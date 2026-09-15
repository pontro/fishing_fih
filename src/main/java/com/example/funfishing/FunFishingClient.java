package com.example.funfishing;

import com.example.funfishing.client.FishingSkillCheckOverlay;
import com.example.funfishing.entity.ModEntities;
import com.example.funfishing.entity.client.FihModel;
import com.example.funfishing.entity.client.FihRenderer;
import com.example.funfishing.entity.client.ModModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FunFishingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FIH, FihModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.FIH, FihRenderer::new);

        FishingSkillCheckOverlay.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FishingSkillCheckOverlay.tickClient();
        });
    }
}
