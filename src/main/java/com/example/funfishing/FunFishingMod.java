package com.example.funfishing;

import com.example.funfishing.entity.ModEntities;
import com.example.funfishing.item.ModItems;
import com.example.funfishing.minigame.CatchRegistry;
import com.example.funfishing.network.FunFishingNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunFishingMod implements ModInitializer {
    public static final String MOD_ID = "fun_fishing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Fun Fishing Mod!");
        ModEntities.registerModEntities();
        ModItems.registerModItems();
        CatchRegistry.init();
        FunFishingNetworking.registerServerReceivers();

        FabricDefaultAttributeRegistry.register(ModEntities.FIH, FishEntity.createFishAttributes());

        SpawnRestriction.register(
                ModEntities.FIH,
                SpawnRestriction.Location.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                FishEntity::canSpawn
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.OCEAN,
                        BiomeKeys.DEEP_OCEAN,
                        BiomeKeys.WARM_OCEAN,
                        BiomeKeys.LUKEWARM_OCEAN,
                        BiomeKeys.DEEP_LUKEWARM_OCEAN,
                        BiomeKeys.RIVER
                ),
                SpawnGroup.WATER_AMBIENT,
                ModEntities.FIH,
                10,
                2,
                6
        );
    }
}
