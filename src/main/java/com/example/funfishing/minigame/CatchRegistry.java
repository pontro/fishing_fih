package com.example.funfishing.minigame;

import com.example.funfishing.item.ModItems;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class CatchRegistry {
    private static final List<CatchEntry> CATCH_ENTRIES = new ArrayList<>();
    private static int totalWeight = 0;

    public static void init() {
        CATCH_ENTRIES.clear();
        totalWeight = 0;

        // Default catch: Fih (Weight: 100, Base Difficulty: 4)
        register(ModItems.FIH, 1, 100, 4);
    }

    public static void register(ItemConvertible item, int count, int weight, int baseDifficulty) {
        register(new CatchEntry(new ItemStack(item, count), weight, baseDifficulty));
    }

    public static void register(CatchEntry entry) {
        CATCH_ENTRIES.add(entry);
        totalWeight += entry.getWeight();
    }

    public static CatchEntry rollCatch(Random random) {
        if (CATCH_ENTRIES.isEmpty() || totalWeight <= 0) {
            return new CatchEntry(new ItemStack(ModItems.FIH), 100, 4);
        }

        int roll = random.nextInt(totalWeight);
        int current = 0;
        for (CatchEntry entry : CATCH_ENTRIES) {
            current += entry.getWeight();
            if (roll < current) {
                return entry;
            }
        }
        return CATCH_ENTRIES.get(0);
    }
}
