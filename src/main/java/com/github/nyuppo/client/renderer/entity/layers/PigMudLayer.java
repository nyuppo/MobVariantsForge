package com.github.nyuppo.client.renderer.entity.layers;

import com.github.nyuppo.MoreMobVariants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PigMudLayer extends RenderLayer<Pig, PigModel<Pig>> {
    private static final RenderType MUD_SKIN = RenderType.entityTranslucent(new ResourceLocation(MoreMobVariants.MOD_ID, "textures/entity/pig/mud/mud_overlay.png"));
    private static final String MUDDY_NBT_KEY = "IsMuddy";

    public PigMudLayer(RenderLayerParent<Pig, PigModel<Pig>> p_117507_) {
        super(p_117507_);
    }

    @Override
    public void render(PoseStack p_117349_, MultiBufferSource p_117350_, int p_117351_, Pig p_117352_, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
        CompoundTag nbt = new CompoundTag();
        p_117352_.addAdditionalSaveData(nbt);

        if (nbt.contains(MUDDY_NBT_KEY)) {
            if (nbt.getBoolean(MUDDY_NBT_KEY)) {
                VertexConsumer vertexConsumer = p_117350_.getBuffer(this.getMudTexture());
                ((Model)this.getParentModel()).renderToBuffer(p_117349_, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private RenderType getMudTexture() {
        return MUD_SKIN;
    }
}
