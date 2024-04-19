package com.github.nyuppo.client.renderer.entity.layers;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.mixin.QuadrupedModelPartAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class SheepHornsLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
    private static final RenderType HORNS_BROWN = RenderType.entityCutoutNoCull(MoreMobVariants.id("textures/entity/sheep/horns/horns_brown.png"));
    private static final RenderType HORNS_GRAY = RenderType.entityCutoutNoCull(MoreMobVariants.id("textures/entity/sheep/horns/horns_gray.png"));
    private static final RenderType HORNS_BLACK = RenderType.entityCutoutNoCull(MoreMobVariants.id("textures/entity/sheep/horns/horns_black.png"));
    private static final RenderType HORNS_BEIGE = RenderType.entityCutoutNoCull(MoreMobVariants.id("textures/entity/sheep/horns/horns_beige.png"));
    private final ModelPart horns = getLayerDefintiion().bakeRoot();

    public SheepHornsLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> context) {
        super(context);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Sheep entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        CompoundTag nbt = new CompoundTag();
        entity.addAdditionalSaveData(nbt);

        if (nbt.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)) {
            if (!nbt.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()) {
                matrices.pushPose();

                ModelPart sheepHead = ((QuadrupedModelPartAccessor) this.getParentModel()).getHead();
                horns.copyFrom(sheepHead);

                String hornColour = nbt.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);

                if (entity.isBaby()) {
                    matrices.pushPose();
                    matrices.translate(0.0f, 0.5f, 0.25f);

                    if (hornColour.equalsIgnoreCase("brown")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BROWN), light, OverlayTexture.NO_OVERLAY);
                    } else if (hornColour.equalsIgnoreCase("gray")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_GRAY), light, OverlayTexture.NO_OVERLAY);
                    } else if (hornColour.equalsIgnoreCase("black")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BLACK), light, OverlayTexture.NO_OVERLAY);
                    } else if (hornColour.equalsIgnoreCase("beige")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BEIGE), light, OverlayTexture.NO_OVERLAY);
                    }

                    matrices.popPose();
                } else {
                    if (hornColour.equalsIgnoreCase("brown")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BROWN), light, OverlayTexture.NO_OVERLAY);
                    } else if (hornColour.equalsIgnoreCase("gray")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_GRAY), light, OverlayTexture.NO_OVERLAY);
                    } else if (hornColour.equalsIgnoreCase("black")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BLACK), light, OverlayTexture.NO_OVERLAY);
                    } else if (hornColour.equalsIgnoreCase("beige")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BEIGE), light, OverlayTexture.NO_OVERLAY);
                    }
                }

                matrices.popPose();
            }
        }
    }

    private static LayerDefinition getLayerDefintiion() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        modelPartData.addOrReplaceChild(PartNames.HEAD,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(3.0f, -3.5f, -3.0f, 4.0f, 7.0f, 6.0f)
                        .texOffs(0, 13).addBox(3.0f, 0.5f, -6.0f, 4.0f, 3.0f, 3.0f)
                        .texOffs(0, 19).addBox(-7.0f, -3.5f, -3.0f, 4.0f, 7.0f, 6.0f)
                        .texOffs(14, 13).addBox(-7.0f, 0.5f, -6.0f, 4.0f, 3.0f, 3.0f),
                PartPose.offset(0.0f, -1.5f, -2.1f));
        return LayerDefinition.create(modelData, 32, 32);
    }
}
