package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepRenderer.class)
public class SheepRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/sheep/sheep.png");
    private static final ResourceLocation PATCHED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/sheep/patched.png");
    private static final ResourceLocation FUZZY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/sheep/fuzzy.png");
    private static final ResourceLocation ROCKY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/sheep/rocky.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Sheep;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Sheep p_115840_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_115840_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(PATCHED);
                    break;
                case 2:
                    ci.setReturnValue(FUZZY);
                    break;
                case 3:
                    ci.setReturnValue(ROCKY);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
