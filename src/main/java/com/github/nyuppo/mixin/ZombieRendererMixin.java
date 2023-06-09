package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractZombieRenderer.class)
public class ZombieRendererMixin {
    private static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/zombie/zombie.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Zombie;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTextureLocation(Zombie p_113771_, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        p_113771_.saveWithoutId(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals("default")) {
                ci.setReturnValue(DEFAULT);
            } else {
                ci.setReturnValue(new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/zombie/" + variant + ".png"));
            }
        }
    }
}
