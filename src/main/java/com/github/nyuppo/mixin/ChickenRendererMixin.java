package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenRenderer.class)
public class ChickenRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/chicken.png");
    private static final ResourceLocation AMBER = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/amber.png");
    private static final ResourceLocation GOLD_CRESTED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/gold_crested.png");
    private static final ResourceLocation BRONZED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/bronzed.png");
    private static final ResourceLocation SKEWBALD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/skewbald.png");
    private static final ResourceLocation STORMY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/stormy.png");
    private static final ResourceLocation MIDNIGHT = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/midnight.png");
    private static final ResourceLocation BONE = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/chicken/bone.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Chicken;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Chicken p_113998_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_113998_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(AMBER);
                    break;
                case 2:
                    ci.setReturnValue(GOLD_CRESTED);
                    break;
                case 3:
                    ci.setReturnValue(BRONZED);
                    break;
                case 4:
                    ci.setReturnValue(SKEWBALD);
                    break;
                case 5:
                    ci.setReturnValue(STORMY);
                    break;
                case 6:
                    ci.setReturnValue(MIDNIGHT);
                    break;
                case 7:
                    ci.setReturnValue(BONE);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
