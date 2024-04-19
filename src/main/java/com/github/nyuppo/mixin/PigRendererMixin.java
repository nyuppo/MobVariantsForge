package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigRenderer.class)
public class PigRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/pig/pig.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Pig;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(Pig pigEntity, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        pigEntity.saveWithoutId(nbt);

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            String variant = nbt.getString(MoreMobVariants.NBT_KEY);
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                String[] split = Variants.splitVariant(variant);
                ci.setReturnValue(new ResourceLocation(split[0], "textures/entity/pig/" + split[1] + ".png"));
            }
        }

        if (pigEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.PIG, pigEntity.getName().getString());
            if (variant != null) {
                ResourceLocation identifier = variant.getIdentifier();
                ci.setReturnValue(new ResourceLocation(identifier.getNamespace(), "textures/entity/pig/" + identifier.getPath() + ".png"));
            }
        }
    }
}
