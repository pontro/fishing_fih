package com.example.funfishing.entity.client;

import com.example.funfishing.entity.FihEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class FihModel<T extends FihEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart fih;
    private final ModelPart aletaIzquierda;
    private final ModelPart aletaDerecha;
    private final ModelPart aletaDeArriba;
    private final ModelPart aletaDeAtras;

    public FihModel(ModelPart root) {
        this.root = root;
        this.fih = root.getChild("Fih");
        this.aletaIzquierda = this.fih.getChild("Aleta izquierda");
        this.aletaDerecha = this.fih.getChild("Aleta Derecha");
        this.aletaDeArriba = this.fih.getChild("Aleta DeArriba");
        this.aletaDeAtras = this.fih.getChild("Aleta DeAtras");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData Fih = modelPartData.addChild("Fih", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-12.9176F, -3.5978F, 4.0F, 2.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(-12.9176F, -3.5978F, 2.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(11.9176F, 21.5978F, -8.0F));

        ModelPartData aletaIzquierda = Fih.addChild("Aleta izquierda", ModelPartBuilder.create(), ModelTransform.pivot(-12.9588F, -1.3066F, 9.0F));
        aletaIzquierda.addChild("cube_r1", ModelPartBuilder.create()
                .uv(14, 12).cuboid(-0.2203F, 1.1939F, -2.75F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 17).cuboid(-0.2203F, 2.1939F, -2.75F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -0.6885F, -0.25F, 0.0F, 0.0F, 0.7854F));

        ModelPartData aletaDerecha = Fih.addChild("Aleta Derecha", ModelPartBuilder.create(), ModelTransform.pivot(-10.9588F, -1.3066F, 9.0F));
        aletaDerecha.addChild("cube_r2", ModelPartBuilder.create()
                .uv(2, 17).cuboid(0.2203F, 2.1939F, -2.75F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 15).cuboid(0.2203F, 1.1939F, -2.75F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.0F, -0.6885F, -0.25F, 0.0F, 0.0F, -0.7854F));

        Fih.addChild("Aleta DeArriba", ModelPartBuilder.create()
                .uv(8, 12).cuboid(-1.0F, 2.0F, -4.0F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(8, 16).cuboid(-1.0F, 2.0F, -8.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-10.9176F, -6.5978F, 12.0F));

        Fih.addChild("Aleta DeAtras", ModelPartBuilder.create()
                .uv(4, 17).cuboid(-1.0F, 2.0F, 12.0F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 16).cuboid(-1.0F, 1.0F, 13.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(6, 17).cuboid(-1.0F, 4.0F, 14.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 12).cuboid(-1.0F, 1.0F, 14.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-10.9176F, -4.5978F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float speed = 1.0F;
        float degree = 1.0F;
        if (!entity.isTouchingWater()) {
            speed = 1.5F;
            degree = 1.7F;
        }
        this.aletaDeAtras.yaw = -degree * 0.35F * MathHelper.sin(speed * 0.6F * animationProgress);
    }
}
