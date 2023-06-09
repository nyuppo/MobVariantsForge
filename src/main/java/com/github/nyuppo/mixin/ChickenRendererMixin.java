package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenRenderer.class)
public class ChickenRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/chicken.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Chicken;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Chicken p_113998_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_113998_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals("default")) {
                ci.setReturnValue(DEFAULT);
            } else {
                ci.setReturnValue(new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/" + variant + ".png"));
            }
        }
    }
}
