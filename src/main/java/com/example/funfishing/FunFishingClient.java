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

        // Register the "cast" predicate so the model flips to cast variant when fishing
        registerFishingRodPredicates(com.example.funfishing.item.ModItems.WOODEN_FISHING_ROD);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FishingSkillCheckOverlay.tickClient();
        });
    }

    public static void registerFishingRodPredicates(net.minecraft.item.Item item) {
        net.minecraft.client.item.ModelPredicateProviderRegistry.register(
            item,
            new net.minecraft.util.Identifier("cast"),
            (stack, world, entity, seed) -> {
                if (entity == null) {
                    return 0.0F;
                }
                boolean isMainHand = entity.getMainHandStack() == stack;
                boolean isOffHand = entity.getOffHandStack() == stack;
                if (entity.getMainHandStack().getItem() instanceof net.minecraft.item.FishingRodItem) {
                    isOffHand = false;
                }
                return (isMainHand || isOffHand) && entity instanceof net.minecraft.entity.player.PlayerEntity player && player.fishHook != null ? 1.0F : 0.0F;
            }
        );
    }
}
