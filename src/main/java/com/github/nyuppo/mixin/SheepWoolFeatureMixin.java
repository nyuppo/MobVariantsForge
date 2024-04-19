package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SheepFurLayer.class)
public class SheepWoolFeatureMixin {
    @ModifyArg(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Sheep;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/SheepFurLayer;coloredCutoutModelCopyLayerRender(Lnet/minecraft/client/model/EntityModel;Lnet/minecraft/client/model/EntityModel;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFFFFF)V"),
            index = 2
    )
    private ResourceLocation mixinSheepFurTexture(EntityModel contextModel, EntityModel model, ResourceLocation texture, PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float tickDelta, float red, float green, float blue) {
        CompoundTag nbt = new CompoundTag();
        entity.saveWithoutId(nbt);

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            String variant = nbt.getString(MoreMobVariants.NBT_KEY);
            if (variant.isEmpty()) {
                return texture;
            }

            String[] split = Variants.splitVariant(variant);

            if (Variants.getVariant(EntityType.SHEEP, ResourceLocation.tryBuild(split[0], split[1])).hasCustomWool()) {
                if (entity.hasCustomName()) {
                    MobVariant nametagVariant = Variants.getVariantFromNametag(EntityType.SHEEP, entity.getName().getString());
                    if (nametagVariant != null) {
                        ResourceLocation identifier = nametagVariant.getIdentifier();
                        return new ResourceLocation(identifier.getNamespace(), "textures/entity/sheep/wool/" + identifier.getPath() + ".png");
                    }
                }

                return new ResourceLocation(split[0], "textures/entity/sheep/wool/" + split[1] + ".png");
            }
        }

        return texture;
    }
}
