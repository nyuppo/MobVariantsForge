package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfRenderer.class)
public class WolfRendererMixin {
    private static final ResourceLocation DEFAULT_WILD = new ResourceLocation("textures/entity/wolf/wolf.png");
    private static final ResourceLocation DEFAULT_TAMED = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    private static final ResourceLocation DEFAULT_ANGRY = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Wolf wolfEntity, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        wolfEntity.saveWithoutId(nbt);

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            String variant = nbt.getString(MoreMobVariants.NBT_KEY);
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                if (wolfEntity.isTame()) {
                    ci.setReturnValue(DEFAULT_TAMED);
                } else {
                    ci.setReturnValue(wolfEntity.isAngry() ? DEFAULT_ANGRY : DEFAULT_WILD);
                }
            } else {
                String[] split = Variants.splitVariant(variant);
                if (wolfEntity.isTame()) {
                    ci.setReturnValue(new ResourceLocation(split[0], "textures/entity/wolf/" + split[1] + "_tame.png"));
                } else {
                    ci.setReturnValue(wolfEntity.isAngry() ? new ResourceLocation(split[0], "textures/entity/wolf/" + split[1] + "_angry.png") : new ResourceLocation(split[0], "textures/entity/wolf/" + split[1] + "_wild.png"));
                }
            }
        }

        if (wolfEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.WOLF, wolfEntity.getName().getString());
            if (variant != null) {
                ResourceLocation identifier = variant.getIdentifier();
                if (wolfEntity.isTame()) {
                    ci.setReturnValue(new ResourceLocation(identifier.getNamespace(), "textures/entity/wolf/" + identifier.getPath() + "_tame.png"));
                } else {
                    ci.setReturnValue(wolfEntity.isAngry() ? new ResourceLocation(identifier.getNamespace(), "textures/entity/wolf/" + identifier.getPath() + "_angry.png") : new ResourceLocation(identifier.getNamespace(), "textures/entity/wolf/" + identifier.getPath() + "_wild.png"));
                }
            }
        }
    }
}
