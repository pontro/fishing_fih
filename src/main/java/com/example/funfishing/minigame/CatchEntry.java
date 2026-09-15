package com.example.funfishing.minigame;

import net.minecraft.item.ItemStack;

public class CatchEntry {
    private final ItemStack loot;
    private final int weight;
    private final int baseDifficulty;

    public CatchEntry(ItemStack loot, int weight, int baseDifficulty) {
        this.loot = loot;
        this.weight = weight;
        this.baseDifficulty = Math.max(1, Math.min(10, baseDifficulty));
    }

    public ItemStack getLoot() {
        return this.loot.copy();
    }

    public int getWeight() {
        return this.weight;
    }

    public int getBaseDifficulty() {
        return this.baseDifficulty;
    }
}
