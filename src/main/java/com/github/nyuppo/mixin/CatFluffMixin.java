package com.github.nyuppo.mixin;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(OcelotModel.class)
public class CatFluffMixin {
    @Inject(method = "createBodyMesh", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onGetModelData(CubeDeformation p_170769_, CallbackInfoReturnable<MeshDefinition> ci, MeshDefinition meshdefinition, PartDefinition partdefinition) {
        partdefinition.addOrReplaceChild(PartNames.HEAD,
                CubeListBuilder.create()
                        .addBox("main", -2.5f, -2.0f, -3.0f, 5.0f, 4.0f, 5.0f, p_170769_)
                        .addBox(PartNames.NOSE, -1.5f, 0.0f, -4.0f, 3, 2, 2, p_170769_, 0, 24)
                        .addBox("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, p_170769_, 0, 10)
                        .addBox("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, p_170769_, 6, 10)
                        .addBox("scruff1", -4.5f, -2.0f, 1.0f, 9, 4, 0, p_170769_, 40, 28)
                        .addBox("scruff2", -4.5f, -2.0f, -1.0f, 9, 4, 0, p_170769_, 40, 28),
                PartPose.offset(0.0f, 15.0f, -9.0f));

        CubeListBuilder modelpartBuilder3 = CubeListBuilder.create().texOffs(20, 0).addBox(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, p_170769_)
                .texOffs(32, 12).addBox(-4.0f, 6.0f, -9.0f, 8.0f, 0.0f, 8.0f, p_170769_)
                .texOffs(32, 20).addBox(-4.0f, 8.0f, -9.0f, 8.0f, 0.0f, 8.0f, p_170769_)
                .texOffs(32, 12).addBox(-4.0f, 10.0f, -9.0f, 8.0f, 0.0f, 8.0f, p_170769_)
                .texOffs(32, 20).addBox(-4.0f, 12.0f, -9.0f, 8.0f, 0.0f, 8.0f, p_170769_)
                .texOffs(32, 12).addBox(-4.0f, 14.0f, -9.0f, 8.0f, 0.0f, 8.0f, p_170769_)
                .texOffs(32, 20).addBox(-4.0f, 16.0f, -9.0f, 8.0f, 0.0f, 8.0f, p_170769_);
        partdefinition.addOrReplaceChild(PartNames.BODY, modelpartBuilder3, PartPose.offsetAndRotation(0.0f, 12.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
    }
}
