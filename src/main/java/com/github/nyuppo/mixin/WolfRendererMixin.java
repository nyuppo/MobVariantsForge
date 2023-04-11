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
    private static final ResourceLocation JUPITER_WILD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/jupiter_wild.png");
    private static final ResourceLocation JUPITER_TAMED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/jupiter_tame.png");
    private static final ResourceLocation JUPITER_ANGRY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/jupiter_angry.png");
    private static final ResourceLocation HUSKY_WILD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/husky_wild.png");
    private static final ResourceLocation HUSKY_TAMED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/husky_tame.png");
    private static final ResourceLocation HUSKY_ANGRY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/husky_angry.png");
    private static final ResourceLocation GERMAN_SHEPHERD_WILD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/german_shepherd_wild.png");
    private static final ResourceLocation GERMAN_SHEPHERD_TAMED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/german_shepherd_tame.png");
    private static final ResourceLocation GERMAN_SHEPHERD_ANGRY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/german_shepherd_angry.png");
    private static final ResourceLocation GOLDEN_RETRIEVER_WILD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/golden_retriever_wild.png");
    private static final ResourceLocation GOLDEN_RETRIEVER_TAMED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/golden_retriever_tame.png");
    private static final ResourceLocation GOLDEN_RETRIEVER_ANGRY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/golden_retriever_angry.png");
    private static final ResourceLocation FRENCH_BULLDOG_WILD = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/french_bulldog_wild.png");
    private static final ResourceLocation FRENCH_BULLDOG_TAMED = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/french_bulldog_tame.png");
    private static final ResourceLocation FRENCH_BULLDOG_ANGRY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/wolf/french_bulldog_angry.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Wolf p_116526_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_116526_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    if (p_116526_.isTame()) {
                        ci.setReturnValue(JUPITER_TAMED);
                    } else {
                        ci.setReturnValue(p_116526_.isAngry() ? JUPITER_ANGRY : JUPITER_WILD);
                    }
                    break;
                case 2:
                    if (p_116526_.isTame()) {
                        ci.setReturnValue(HUSKY_TAMED);
                    } else {
                        ci.setReturnValue(p_116526_.isAngry() ? HUSKY_ANGRY : HUSKY_WILD);
                    }
                    break;
                case 3:
                    if (p_116526_.isTame()) {
                        ci.setReturnValue(GERMAN_SHEPHERD_TAMED);
                    } else {
                        ci.setReturnValue(p_116526_.isAngry() ? GERMAN_SHEPHERD_ANGRY : GERMAN_SHEPHERD_WILD);
                    }
                    break;
                case 4:
                    if (p_116526_.isTame()) {
                        ci.setReturnValue(GOLDEN_RETRIEVER_TAMED);
                    } else {
                        ci.setReturnValue(p_116526_.isAngry() ? GOLDEN_RETRIEVER_ANGRY : GOLDEN_RETRIEVER_WILD);
                    }
                    break;
                case 5:
                    if (p_116526_.isTame()) {
                        ci.setReturnValue(FRENCH_BULLDOG_TAMED);
                    } else {
                        ci.setReturnValue(p_116526_.isAngry() ? FRENCH_BULLDOG_ANGRY : FRENCH_BULLDOG_WILD);
                    }
                    break;
                case 0:
                default:
                    if (p_116526_.isTame()) {
                        ci.setReturnValue(DEFAULT_TAMED);
                    } else {
                        ci.setReturnValue(p_116526_.isAngry() ? DEFAULT_ANGRY : DEFAULT_WILD);
                    }
                    break;
            }
        }
    }
}
