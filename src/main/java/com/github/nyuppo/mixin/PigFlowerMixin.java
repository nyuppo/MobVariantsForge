package com.github.nyuppo.mixin;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PigModel.class)
public class PigFlowerMixin {
    @Inject(method = "createBodyLayer", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onCreateBodyLayer(CubeDeformation p_170801_, CallbackInfoReturnable<LayerDefinition> ci, MeshDefinition meshdefinition, PartDefinition partdefinition) {
        partdefinition.addOrReplaceChild(PartNames.HEAD,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, p_170801_)
                        .texOffs(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4.0f, 3.0f, 1.0f, p_170801_)
                        .texOffs(28, 3).addBox(-1.0f, -5.0f, -7.0f, 4.0f, 1.0f, 4.0f, p_170801_)
                        .texOffs(44, 2).addBox(0.0f, -11.0f, -5.0f,4, 6, 0, p_170801_),
                PartPose.offset(0.0f, 12.0f, -6.0f));
    }
}
