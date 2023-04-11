package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractZombieRenderer.class)
public class ZombieRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation ALEX = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_alex.png");
    private static final ResourceLocation ARI = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_ari.png");
    private static final ResourceLocation EFE = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_efe.png");
    private static final ResourceLocation KAI = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_kai.png");
    private static final ResourceLocation MAKENA = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_makena.png");
    private static final ResourceLocation NOOR = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_noor.png");
    private static final ResourceLocation SUNNY = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_sunny.png");
    private static final ResourceLocation ZURI = new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_zuri.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Zombie;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Zombie p_113771_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_113771_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(ALEX);
                    break;
                case 2:
                    ci.setReturnValue(ARI);
                    break;
                case 3:
                    ci.setReturnValue(EFE);
                    break;
                case 4:
                    ci.setReturnValue(KAI);
                    break;
                case 5:
                    ci.setReturnValue(MAKENA);
                    break;
                case 6:
                    ci.setReturnValue(NOOR);
                    break;
                case 7:
                    ci.setReturnValue(SUNNY);
                    break;
                case 8:
                    ci.setReturnValue(ZURI);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
