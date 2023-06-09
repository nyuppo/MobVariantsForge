package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
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
    private void onGetTextureLocation(Wolf p_116526_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_116526_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals("default")) {
                if (p_116526_.isTame()) {
                    ci.setReturnValue(DEFAULT_TAMED);
                } else {
                    ci.setReturnValue(p_116526_.isAngry() ? DEFAULT_ANGRY : DEFAULT_WILD);
                }
            } else {
                if (p_116526_.isTame()) {
                    ci.setReturnValue(new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/" + variant + "_tame.png"));
                } else {
                    ci.setReturnValue(p_116526_.isAngry() ? new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/" + variant + "_angry.png") : new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/" + variant + "_wild.png"));
                }
            }
        }
    }
}
