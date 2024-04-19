package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyesLayer.class)
public abstract class CustomEyesLayerMixin<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public CustomEyesLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderEyesLayer(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_, CallbackInfo ci) {
        if (p_116986_ instanceof Spider) {
            Spider spider = (Spider)p_116986_;
            CompoundTag nbt = new CompoundTag();
            spider.addAdditionalSaveData(nbt);

            if (nbt.contains(MoreMobVariants.NBT_KEY)) {
                String variant = nbt.getString(MoreMobVariants.NBT_KEY);
                if (!variant.isEmpty()) {
                    String[] split = Variants.splitVariant(variant);

                    if (Variants.getVariant(EntityType.SPIDER, ResourceLocation.tryBuild(split[0], split[1])).hasCustomEyes()) {
                        VertexConsumer vertexconsumer = p_116984_.getBuffer(RenderType.eyes(new ResourceLocation(split[0], "textures/entity/spider/eyes/" + split[1] + ".png")));
                        this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        ci.cancel();
                    }
                }
            }
        }
    }
}
