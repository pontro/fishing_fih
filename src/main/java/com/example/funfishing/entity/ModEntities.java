package com.example.funfishing.entity;

import com.example.funfishing.FunFishingMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FihEntity> FIH = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(FunFishingMod.MOD_ID, "fih"),
            FabricEntityTypeBuilder.create(SpawnGroup.WATER_AMBIENT, FihEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.3f))
                    .build()
    );

    public static void registerModEntities() {
        FunFishingMod.LOGGER.info("Registering entities for " + FunFishingMod.MOD_ID);
    }
}
