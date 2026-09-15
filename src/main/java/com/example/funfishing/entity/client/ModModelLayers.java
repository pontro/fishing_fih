package com.example.funfishing.entity.client;

import com.example.funfishing.FunFishingMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer FIH =
            new EntityModelLayer(new Identifier(FunFishingMod.MOD_ID, "fih"), "main");
}
