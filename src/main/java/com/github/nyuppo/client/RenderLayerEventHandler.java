package com.github.nyuppo.client;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.client.renderer.entity.layers.PigMudLayer;
import com.github.nyuppo.client.renderer.entity.layers.ShearedWoolColorLayer;
import com.github.nyuppo.client.renderer.entity.layers.SheepHornsLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = MoreMobVariants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderLayerEventHandler {
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        addLayerToRenderer(event, EntityType.PIG, PigMudLayer::new);
        addLayerToRenderer(event, EntityType.SHEEP, ShearedWoolColorLayer::new);
        addLayerToRenderer(event, EntityType.SHEEP, SheepHornsLayer::new);
    }

    // Thanks gigaherz
    private static <T extends LivingEntity, R extends LivingEntityRenderer<T, M>, M extends EntityModel<T>> void addLayerToRenderer(EntityRenderersEvent.AddLayers event, EntityType<T> entityType, Function<R, ? extends RenderLayer<T,M>> factory)
    {
        R renderer = event.getRenderer(entityType);
        if (renderer != null) renderer.addLayer(factory.apply(renderer));
    }
}
