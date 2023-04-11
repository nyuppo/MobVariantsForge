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
    private static final ResourceLocation ASHEN = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/ashen.png");
    private static final ResourceLocation COOKIE = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/cookie.png");
    private static final ResourceLocation DAIRY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/dairy.png");
    private static final ResourceLocation PINTO = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/pinto.png");
    private static final ResourceLocation SUNSET = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/sunset.png");
    private static final ResourceLocation WOOLY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/wooly.png");
    private static final ResourceLocation UMBRA = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/cow/umbra.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Cow;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Cow p_114029_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_114029_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(ASHEN);
                    break;
                case 2:
                    ci.setReturnValue(COOKIE);
                    break;
                case 3:
                    ci.setReturnValue(DAIRY);
                    break;
                case 4:
                    ci.setReturnValue(PINTO);
                    break;
                case 5:
                    ci.setReturnValue(SUNSET);
                    break;
                case 6:
                    ci.setReturnValue(WOOLY);
                    break;
                case 7:
                    ci.setReturnValue(UMBRA);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
