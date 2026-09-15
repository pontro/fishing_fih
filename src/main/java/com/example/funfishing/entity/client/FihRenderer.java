package com.example.funfishing.entity.client;

import com.example.funfishing.FunFishingMod;
import com.example.funfishing.entity.FihEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class FihRenderer extends MobEntityRenderer<FihEntity, FihModel<FihEntity>> {
    private static final Identifier TEXTURE = new Identifier(FunFishingMod.MOD_ID, "textures/entity/fish/fih.png");

    public FihRenderer(EntityRendererFactory.Context context) {
        super(context, new FihModel<>(context.getPart(ModModelLayers.FIH)), 0.3F);
    }

    @Override
    public Identifier getTexture(FihEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(FihEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        float roll = 4.3F * MathHelper.sin(0.6F * animationProgress);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(roll));
        if (!entity.isTouchingWater()) {
            matrices.translate(0.1F, 0.1F, -0.1F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
        }
    }
}
