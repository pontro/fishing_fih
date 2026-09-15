package com.example.funfishing.minigame;

import net.minecraft.server.network.ServerPlayerEntity;

public class FishingDifficultyEngine {
    public static class DifficultyProfile {
        public final int difficulty;
        public final int totalStages;
        public final float needleSpeed;
        public final float greatZoneWidth;
        public final float goodZoneWidth;

        public DifficultyProfile(int difficulty, int totalStages, float needleSpeed, float greatZoneWidth, float goodZoneWidth) {
            this.difficulty = difficulty;
            this.totalStages = totalStages;
            this.needleSpeed = needleSpeed;
            this.greatZoneWidth = greatZoneWidth;
            this.goodZoneWidth = goodZoneWidth;
        }
    }

    public static DifficultyProfile calculate(ServerPlayerEntity player, int baseDifficulty) {
        int rodModifier = getRodModifier(player);
        int enchantModifier = getEnchantmentModifier(player);

        int finalDifficulty = Math.max(1, Math.min(10, baseDifficulty - rodModifier - enchantModifier));

        // Multi-stage tiers: 1-5 = 1 stage, 6-8 = 2 stages, 9-10 = 3 stages
        int totalStages;
        if (finalDifficulty <= 5) {
            totalStages = 1;
        } else if (finalDifficulty <= 8) {
            totalStages = 2;
        } else {
            totalStages = 3;
        }

        float needleSpeed = 3.5f + (finalDifficulty * 0.45f);
        float goodZoneWidth = Math.max(22.0f, 75.0f - (finalDifficulty * 5.0f));
        float greatZoneWidth = Math.max(6.0f, 22.0f - (finalDifficulty * 1.6f));

        return new DifficultyProfile(finalDifficulty, totalStages, needleSpeed, greatZoneWidth, goodZoneWidth);
    }

    private static int getRodModifier(ServerPlayerEntity player) {
        return 0; // Ready for custom rod tiers
    }

    private static int getEnchantmentModifier(ServerPlayerEntity player) {
        return 0; // Ready for custom enchantments
    }
}
