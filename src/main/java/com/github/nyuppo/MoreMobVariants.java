package com.github.nyuppo;

import com.github.nyuppo.config.*;
import com.github.nyuppo.networking.C2SRequestVariantPacket;
import com.github.nyuppo.networking.MMVPacketHandler;
import com.github.nyuppo.variant.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mod(MoreMobVariants.MOD_ID)
public class MoreMobVariants {
    public static final String MOD_ID = "moremobvariants";
    public static final Logger LOGGER = LogUtils.getLogger();

    // NBT keys
    public static final String NBT_KEY = "VariantID";
    public static final String MUDDY_NBT_KEY = "IsMuddy"; // Muddy pigs
    public static final String MUDDY_TIMEOUT_NBT_KEY = "MuddyTimeLeft"; // Muddy pigs
    public static final String SHEEP_HORN_COLOUR_NBT_KEY = "HornColour";

    // Pig mud tag
    public static final TagKey<Block> PIG_MUD_BLOCKS = BlockTags.create(new ResourceLocation(MOD_ID, "pig_mud_blocks"));

    // Biome tags
    public static final TagKey<Biome> SPAWN_MOSSY_SKELETONS = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "spawn_mossy_skeletons"));
    public static final TagKey<Biome> INCREASED_SANDY_SKELETONS = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "increased_sandy_skeletons"));
    public static final TagKey<Biome> SHEEP_SPAWN_WITH_HORNS = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "sheep_spawn_with_horns"));
    public static final TagKey<Biome> SPAWN_PALE_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_pale_spawns"));
    public static final TagKey<Biome> SPAWN_RUSTY_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_rusty_spawns"));
    public static final TagKey<Biome> SPAWN_SPOTTED_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_spotted_spawns"));
    public static final TagKey<Biome> SPAWN_BLACK_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_black_spawns"));
    public static final TagKey<Biome> SPAWN_STRIPED_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_striped_spawns"));
    public static final TagKey<Biome> SPAWN_SNOWY_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_snowy_spawns"));
    public static final TagKey<Biome> SPAWN_ASHEN_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_ashen_spawns"));
    public static final TagKey<Biome> SPAWN_WOODS_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_woods_spawns"));
    public static final TagKey<Biome> SPAWN_CHESTNUT_WOLF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "wolf_chestnut_spawns"));
    public static final TagKey<Biome> ADDITIONAL_WOLF_SPAWNS = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MOD_ID, "additional_wolf_spawns"));

    // Entities that can have variants (clients only request variants of these type)
    Set<EntityType> validEntities = Set.of(EntityType.CAT, EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.SKELETON, EntityType.SPIDER, EntityType.WOLF, EntityType.ZOMBIE);

    public MoreMobVariants() {
        MinecraftForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
        MinecraftForge.EVENT_BUS.addListener(this::onBabyEntitySpawn);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        // Config
        MinecraftForge.EVENT_BUS.addListener(this::onReload);
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && validEntities.contains(event.getEntity().getType())) {
            MMVPacketHandler.sendToServer(new C2SRequestVariantPacket(event.getEntity().getUUID()));
        }
    }

    @SubscribeEvent
    public void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        if (event.getChild() != null && validEntities.contains(event.getChild().getType())) {
            AgeableMob child = event.getChild();

            MobVariant variant = Variants.getChildVariant(child.getType(), (ServerLevel)child.level, (AgeableMob)event.getParentA(), (AgeableMob)event.getParentB());

            // Write variant to child's NBT
            CompoundTag childNbt = new CompoundTag();
            child.saveWithoutId(childNbt);
            childNbt.putString(NBT_KEY, variant.getIdentifier().toString());

            // Special cases
            if (child instanceof Sheep) {
                // Determine horn colour
                CompoundTag nbtParent1 = new CompoundTag();
                event.getParentA().saveWithoutId(nbtParent1);
                CompoundTag nbtParent2 = new CompoundTag();
                event.getParentB().saveWithoutId(nbtParent2);

                String colour = "";
                if (nbtParent1.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
                        && !nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
                        && nbtParent2.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
                        && !nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
                        && child.level.getRandom().nextDouble() <= SheepHornSettings.getInheritChance()) {
                    colour = child.level.getRandom().nextBoolean() ? nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY) : nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);
                } else {
                    SheepHornSettings.SheepHornColour col = SheepHornSettings.getRandomSheepHornColour(child.level.getRandom(), child.level.getBiome(((Sheep)(Object)this).blockPosition()));
                    if (col != null) {
                        colour = col.getId();
                    }
                }

                childNbt.putString(SHEEP_HORN_COLOUR_NBT_KEY, colour);
            }

            child.load(childNbt);
            event.setChild(child);
        }
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Giving mobs a fresh coat of paint...");
        event.enqueueWork(MMVPacketHandler::registerPackets);
    }

    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener(new Gson(), MOD_ID) {
            private final ResourceLocation SETTINGS_ID = new ResourceLocation(MoreMobVariants.MOD_ID, "settings/settings.json");
            private final ResourceLocation SHEEP_HORN_SETTINGS_ID = new ResourceLocation(MoreMobVariants.MOD_ID, "settings/sheep_horn_settings.json");

            @Override
            protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller) {
                LOGGER.info("Reloading config...");

                Variants.clearAllVariants();
                for (ResourceLocation id : manager.listResources("variants", path -> path.getPath().endsWith(".json")).keySet()) {
                    String path = id.getPath().substring("variants/".length(), id.getPath().length() - ".json".length());
                    String[] split = path.split("/");
                    // mob id is stored in split[0] (i.e. "cow"), variant id is stored in split[1] (i.e. "dairy")
                    // this pattern repeats a lot throughout this class and it's related classes, so hopefully you see this comment before all of that lol

                    if (manager.getResource(id).isPresent()) {
                        try (InputStream stream = manager.getResource(id).getInputStream().get().open()) {
                            applyVariant(new InputStreamReader(stream, StandardCharsets.UTF_8), id.getNamespace(), split[0], split[1]);
                        } catch (Exception e) {
                            MoreMobVariants.LOGGER.error("Error occured while loading " + split[0] + " variant '" + split[1] + "' (" + id.toShortLanguageKey() + ")", e);
                        }
                    } else {
                        LOGGER.error(id.toShortLanguageKey() + " was not present.");
                    }
                }
                Variants.validateEmptyVariants();

                VariantBlacklist.clearAllBlacklists();
                for (ResourceLocation id : manager.listResources("blacklist", path -> path.getPath().endsWith(".json")).keySet()) {
                    String mob = id.getPath().substring("blacklist/".length(), id.getPath().length() - ".json".length());

                    if (manager.getResource(id).isPresent()) {
                        try (InputStream stream = manager.getResource(id).get().open()) {
                            applyBlacklist(new InputStreamReader(stream, StandardCharsets.UTF_8), mob);
                        } catch (Exception e) {
                            MoreMobVariants.LOGGER.error("Error occured while loading blacklist config " + id.toShortLanguageKey(), e);
                            VariantBlacklist.clearBlacklist(Variants.getMob(mob));
                        }
                    } else {
                        MoreMobVariants.LOGGER.error(id.toShortLanguageKey() + " was not present.");
                    }
                }
                Variants.applyBlacklists();

                Optional<Resource> settings = manager.getResource(SETTINGS_ID);
                if (settings.isPresent()) {
                    try (InputStream stream = manager.getResource(SETTINGS_ID).get().open()) {
                        applySettings(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        MoreMobVariants.LOGGER.error("Error occured while loading settings config " + SETTINGS_ID.toShortLanguageKey(), e);
                        VariantSettings.resetSettings();
                    }
                }

                Optional<Resource> sheepHornSettings = manager.getResource(SHEEP_HORN_SETTINGS_ID);
                if (sheepHornSettings.isPresent()) {
                    try (InputStream stream = manager.getResource(SHEEP_HORN_SETTINGS_ID).get().open()) {
                        applySheepHornSettings(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        MoreMobVariants.LOGGER.error("Error occured while loading sheep horn settings config " + SHEEP_HORN_SETTINGS_ID.toShortLanguageKey(), e);
                        SheepHornSettings.resetSettings();
                    }
                }
            }

            private void applyVariant(Reader reader, String namespace, String mobId, String variantId) {
                JsonElement element = JsonParser.parseReader(reader);

                int weight = 0;
                List<VariantModifier> modifiers = new ArrayList<>();

                if (element.getAsJsonObject().size() != 0) {
                    if (element.getAsJsonObject().has("weight")) {
                        weight = element.getAsJsonObject().get("weight").getAsInt();
                    } else {
                        // Skip if there is no weight present, UNLESS it is a nametag override, in which case it needs no weight
                        if (!element.getAsJsonObject().has("nametag_override")) {
                            MoreMobVariants.LOGGER.error("Variant " + namespace + ":" + mobId + "/" + variantId + " has no weight, skipping.");
                            return;
                        }
                    }

                    if (element.getAsJsonObject().has("name")) {
                        modifiers.add(new CustomVariantNameModifier(element.getAsJsonObject().get("name").getAsString()));
                    }

                    if (element.getAsJsonObject().has("shiny")) {
                        if (element.getAsJsonObject().get("shiny").getAsBoolean()) {
                            modifiers.add(new ShinyModifier());
                        }
                    }

                    if (element.getAsJsonObject().has("discard_chance")) {
                        modifiers.add(new DiscardableModifier(element.getAsJsonObject().get("discard_chance").getAsDouble()));
                    }

                    if (element.getAsJsonObject().has("biome_tag")) {
                        String[] biomesIdentifier = element.getAsJsonObject().get("biome_tag").getAsString().split(":");
                        TagKey<Biome> biomes = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(biomesIdentifier[0], biomesIdentifier[1]));
                        modifiers.add(new SpawnableBiomesModifier(biomes));
                    }

                    if (element.getAsJsonObject().has("breeding")) {
                        JsonElement breeding = element.getAsJsonObject().get("breeding");
                        if (breeding.getAsJsonObject().has("parent1") &&
                                breeding.getAsJsonObject().has("parent2") &&
                                breeding.getAsJsonObject().has("breeding_chance")) {
                            String[] parent1 = breeding.getAsJsonObject().get("parent1").getAsString().split(":");
                            String[] parent2 = breeding.getAsJsonObject().get("parent2").getAsString().split(":");
                            double breedingChance = breeding.getAsJsonObject().get("breeding_chance").getAsDouble();

                            modifiers.add(new BreedingResultModifier(
                                    new ResourceLocation(parent1[0], parent1[1]),
                                    new ResourceLocation(parent2[0], parent2[1]),
                                    breedingChance));
                        }
                    }

                    if (element.getAsJsonObject().has("custom_wool")) {
                        if (element.getAsJsonObject().get("custom_wool").getAsBoolean()) {
                            modifiers.add(new CustomWoolModifier());
                        }
                    }

                    if (element.getAsJsonObject().has("has_color_when_sheared")) {
                        if (element.getAsJsonObject().get("has_color_when_sheared").getAsBoolean()) {
                            modifiers.add(new ShearedWoolColorModifier());
                        }
                    }

                    if (element.getAsJsonObject().has("custom_eyes")) {
                        if (element.getAsJsonObject().get("custom_eyes").getAsBoolean()) {
                            modifiers.add(new CustomEyesModifier());
                        }
                    }

                    if (element.getAsJsonObject().has("nametag_override")) {
                        modifiers.add(new NametagOverrideModifier(element.getAsJsonObject().get("nametag_override").getAsString()));
                    }

                    if (element.getAsJsonObject().has("minimum_moon_size")) {
                        modifiers.add(new MoonPhaseModifier(element.getAsJsonObject().get("minimum_moon_size").getAsFloat()));
                    }
                }

                Variants.addVariant(Variants.getMob(mobId), new MobVariant(new ResourceLocation(namespace, variantId), weight, modifiers));
            }

            private void applyBlacklist(Reader reader, String mob) {
                JsonElement element = JsonParser.parseReader(reader);

                if (element.getAsJsonObject().size() != 0) {
                    if (element.getAsJsonObject().has("blacklist")) {
                        JsonArray blacklist = element.getAsJsonObject().get("blacklist").getAsJsonArray();
                        for (JsonElement entry : blacklist) {
                            String[] entrySplit = entry.getAsString().split(":");
                            VariantBlacklist.blacklist(Variants.getMob(mob), new ResourceLocation(entrySplit[0], entrySplit[1]));
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
                    if (element.getAsJsonObject().has("muddy_pig_timeout")) {
                        VariantSettings.setMuddyPigTimeout(element.getAsJsonObject().get("muddy_pig_timeout").getAsInt());
                    }
                    if (element.getAsJsonObject().has("child_random_variant_chance")) {
                        VariantSettings.setChildRandomVariantChance(element.getAsJsonObject().get("child_random_variant_chance").getAsDouble());
                    }
                }
            }

            private void applySheepHornSettings(Reader reader) {
                JsonElement element = JsonParser.parseReader(reader);

                if (element.getAsJsonObject().size() != 0) {
                    if (element.getAsJsonObject().has("chance")) {
                        SheepHornSettings.setHornsChance(element.getAsJsonObject().get("chance").getAsDouble());
                    }
                    if (element.getAsJsonObject().has("inherit_parents_chance")) {
                        SheepHornSettings.setInheritChance(element.getAsJsonObject().get("inherit_parents_chance").getAsDouble());
                    }

                    if (element.getAsJsonObject().has("weights")) {
                        JsonElement weights = element.getAsJsonObject().get("weights");

                        if (weights.getAsJsonObject().has("brown")) {
                            SheepHornSettings.setWeight(SheepHornSettings.SheepHornColour.BROWN, weights.getAsJsonObject().get("brown").getAsInt());
                        }
                        if (weights.getAsJsonObject().has("gray")) {
                            SheepHornSettings.setWeight(SheepHornSettings.SheepHornColour.GRAY, weights.getAsJsonObject().get("gray").getAsInt());
                        }
                        if (weights.getAsJsonObject().has("black")) {
                            SheepHornSettings.setWeight(SheepHornSettings.SheepHornColour.BLACK, weights.getAsJsonObject().get("black").getAsInt());
                        }
                        if (weights.getAsJsonObject().has("beige")) {
                            SheepHornSettings.setWeight(SheepHornSettings.SheepHornColour.BEIGE, weights.getAsJsonObject().get("beige").getAsInt());
                        }
                    }
                }
            }
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
