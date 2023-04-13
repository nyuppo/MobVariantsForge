package com.github.nyuppo;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(MoreMobVariants.MOD_ID)
public class MoreMobVariants {
    public static final String MOD_ID = "moremobvariants";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Cat variants
    private static final DeferredRegister<CatVariant> CAT_VARIANTS = DeferredRegister.create(Registry.CAT_VARIANT_REGISTRY, MOD_ID);
    public static final RegistryObject<CatVariant> GRAY_TABBY = CAT_VARIANTS.register("gray_tabby", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/gray_tabby.png")));
    public static final RegistryObject<CatVariant> DOUG = CAT_VARIANTS.register("doug", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/doug.png")));
    public static final RegistryObject<CatVariant> HANDSOME = CAT_VARIANTS.register("handsome", () -> new CatVariant(new ResourceLocation(MOD_ID, "textures/entity/cat/handsome.png")));

    public MoreMobVariants() {
        // Register cat variants
        CAT_VARIANTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Giving mobs a fresh coat of paint...");
    }
}
