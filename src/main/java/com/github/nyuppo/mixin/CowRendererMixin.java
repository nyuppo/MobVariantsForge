package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowRenderer.class)
public class CowRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/cow/cow.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Cow;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Cow p_114029_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_114029_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals("default") || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                ci.setReturnValue(new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/" + variant + ".png"));
            }
        }
    }
}
