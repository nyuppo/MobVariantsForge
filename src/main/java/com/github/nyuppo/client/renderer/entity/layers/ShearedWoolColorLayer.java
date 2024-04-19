package com.github.nyuppo.client.renderer.entity.layers;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShearedWoolColorLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
    public ShearedWoolColorLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> context) {
        super(context);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Sheep sheepEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        /*
        if (!sheepEntity.isSheared()) {
            return;
        }
        */
        // It actually looks better without this check lol

        // Check for custom name override
        if (sheepEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.SHEEP, sheepEntity.getName().getString());
            if (variant != null) {
                if (variant.hasColorWhenSheared()) {
                    float[] hs = Sheep.getColorArray(sheepEntity.getColor());

                    RenderType FUR_OVERLAY = RenderType.entityCutoutNoCull(MoreMobVariants.id("textures/entity/sheep/sheared_color_overlay/" + variant.getIdentifier().getPath() + ".png"));
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(FUR_OVERLAY);
                    ((Model)this.getParentModel()).renderToBuffer(matrices, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, hs[0], hs[1], hs[2], 1.0f);
                }

                return;
            }
        }

        CompoundTag nbt = new CompoundTag();
        sheepEntity.addAdditionalSaveData(nbt);

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            ResourceLocation variant = new ResourceLocation(nbt.getString(MoreMobVariants.NBT_KEY));
            if (Variants.getVariant(EntityType.SHEEP, variant).hasColorWhenSheared()) {
                float[] hs = Sheep.getColorArray(sheepEntity.getColor());

                RenderType FUR_OVERLAY = RenderType.entityCutoutNoCull(MoreMobVariants.id("textures/entity/sheep/sheared_color_overlay/" + variant.getPath() + ".png"));
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(FUR_OVERLAY);
                ((Model)this.getParentModel()).renderToBuffer(matrices, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, hs[0], hs[1], hs[2], 1.0f);
            }
        }
    }
}
