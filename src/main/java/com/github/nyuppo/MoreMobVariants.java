package com.github.nyuppo;

import com.github.nyuppo.client.renderer.entity.layers.PigMudLayer;
import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantSettings;
import com.github.nyuppo.config.VariantWeights;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mod(MoreMobVariants.MOD_ID)
public class MoreMobVariants {
    public static final String MOD_ID = "moremobvariants";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Cat variants
    private static final DeferredRegister<CatVariant> CAT_VARIANTS = DeferredRegister.create(Registries.CAT_VARIANT, MOD_ID);
    public static final RegistryObject<CatVariant> GRAY_TABBY = CAT_VARIANTS.register("gray_tabby", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/gray_tabby.png")));
    public static final RegistryObject<CatVariant> DOUG = CAT_VARIANTS.register("doug", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/doug.png")));
    public static final RegistryObject<CatVariant> HANDSOME = CAT_VARIANTS.register("handsome", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/handsome.png")));
    public static final RegistryObject<CatVariant> TORTOISESHELL = CAT_VARIANTS.register("tortoiseshell", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/tortoiseshell.png")));

    // Pig mud tag
    public static final TagKey<Block> PIG_MUD_BLOCKS = BlockTags.create(new ResourceLocation(MOD_ID, "pig_mud_blocks"));

    public MoreMobVariants() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        // Config
        MinecraftForge.EVENT_BUS.addListener(this::onReload);

        // Register cat variants
        CAT_VARIANTS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Add render layers
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addLayers);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Giving mobs a fresh coat of paint...");
    }

    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener(new Gson(), MOD_ID) {
            private final ResourceLocation SETTINGS_ID = new ResourceLocation(MoreMobVariants.MOD_ID, "settings/settings.json");

            @Override
            protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller) {
                LOGGER.info("Reloading config...");
                VariantWeights.clearWeights();
                VariantBlacklist.resetBlacklists();

                for (ResourceLocation id : manager.listResources("weights", path -> path.getPath().endsWith(".json")).keySet()) {
                    String target = id.getPath().substring(8, id.getPath().length() - 5);
                    try (InputStream stream = manager.getResource(id).get().open()) {
                        applyWeight(id, new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        LOGGER.error("Error occured while loading weight config " + id.toShortLanguageKey(), e);
                        VariantWeights.resetWeight(target);
                    }
                }

                for (ResourceLocation id : manager.listResources("blacklist", path -> path.getPath().endsWith(".json")).keySet()) {
                    String target = id.getPath().substring(10, id.getPath().length() - 5);
                    try (InputStream stream = manager.getResource(id).get().open()) {
                        applyBlacklist(id, new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        MoreMobVariants.LOGGER.error("Error occured while loading blacklist config " + id.toShortLanguageKey(), e);
                        VariantBlacklist.resetBlacklist(target);
                    }
                }

                Optional<Resource> settings = manager.getResource(SETTINGS_ID);
                if (settings.isPresent()) {
                    try (InputStream stream = manager.getResource(SETTINGS_ID).get().open()) {
                        applySettings(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        MoreMobVariants.LOGGER.error("Error occured while loading settings config " + SETTINGS_ID.toShortLanguageKey(), e);
                        VariantSettings.resetSettings();
                    }
                }

                VariantWeights.applyBlacklists();
            }

            private void applyWeight(ResourceLocation identifier, Reader reader) {
                String target = identifier.getPath().substring(8, identifier.getPath().length() - 5);
                JsonElement element = JsonParser.parseReader(reader);

                if (element.getAsJsonObject().size() != 0) {
                    if (element.getAsJsonObject().has("weights")) {
                        Map<String, JsonElement> weights = element.getAsJsonObject().get("weights").getAsJsonObject().asMap();
                        HashMap<String, Integer> weightsConverted = new HashMap<String, Integer>();
                        for (Map.Entry entry : weights.entrySet()) {
                            weightsConverted.put(entry.getKey().toString(), ((JsonElement)entry.getValue()).getAsInt());
                        }
                        VariantWeights.setWeight(target, weightsConverted);
                    }
                }
            }

            private void applyBlacklist(ResourceLocation identifier, Reader reader) {
                String target = identifier.getPath().substring(10, identifier.getPath().length() - 5);
                JsonElement element = JsonParser.parseReader(reader);

                if (element.getAsJsonObject().size() != 0) {
                    if (element.getAsJsonObject().has("blacklist")) {
                        JsonArray blacklist = element.getAsJsonObject().get("blacklist").getAsJsonArray();
                        for (JsonElement entry : blacklist) {
                            VariantBlacklist.blacklistVariant(target, entry.getAsString());
                        }
                    }
                }
            }

            private void applySettings(Reader reader) {
                JsonElement element = JsonParser.parseReader(reader);

                if (element.getAsJsonObject().size() != 0) {
                    if (element.getAsJsonObject().has("enable_muddy_pigs")) {
                        VariantSettings.setEnableMuddyPigs(element.getAsJsonObject().get("enable_muddy_pigs").getAsBoolean());
                    }
                    if (element.getAsJsonObject().has("wolf_breeding_chance")) {
                        VariantSettings.setWolfBreedingChance(element.getAsJsonObject().get("wolf_breeding_chance").getAsInt());
                    }
                }
            }
        });
    }

    public void addLayers(EntityRenderersEvent.AddLayers event) {
        addLayerToRenderer(event, EntityType.PIG, PigMudLayer::new);
    }

    // Thanks gigaherz
    private static <T extends LivingEntity, R extends LivingEntityRenderer<T, M>, M extends EntityModel<T>> void addLayerToRenderer(EntityRenderersEvent.AddLayers event, EntityType<T> entityType, Function<R, ? extends RenderLayer<T,M>> factory)
    {
        R renderer = event.getRenderer(entityType);
        if (renderer != null) renderer.addLayer(factory.apply(renderer));
    }
}
