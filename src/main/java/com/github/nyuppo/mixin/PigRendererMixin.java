package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigRenderer.class)
public class PigRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/pig/pig.png");
    private static final ResourceLocation MOTTLED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/pig/mottled.png");
    private static final ResourceLocation PIEBALD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/pig/piebald.png");
    private static final ResourceLocation PINK_FOOTED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/pig/pink_footed.png");
    private static final ResourceLocation SOOTY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/pig/sooty.png");
    private static final ResourceLocation SPOTTED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/pig/spotted.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Pig;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(Pig p_115697_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_115697_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(MOTTLED);
                    break;
                case 2:
                    ci.setReturnValue(PIEBALD);
                    break;
                case 3:
                    ci.setReturnValue(PINK_FOOTED);
                    break;
                case 4:
                    ci.setReturnValue(SOOTY);
                    break;
                case 5:
                    ci.setReturnValue(SPOTTED);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
