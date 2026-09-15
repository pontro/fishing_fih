package com.example.funfishing.item;

import com.example.funfishing.FunFishingMod;
import com.example.funfishing.entity.ModEntities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FIH = registerItem("fih",
            new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build())));

    public static final Item COOKED_FIH = registerItem("cooked_fih",
            new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).build())));

    public static final Item FIH_BUCKET = registerItem("fih_bucket",
            new EntityBucketItem(ModEntities.FIH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, new Item.Settings().maxCount(1)));

    public static final Item FIH_SPAWN_EGG = registerItem("fih_spawn_egg",
            new SpawnEggItem(ModEntities.FIH, 0xD2B48C, 0x33B2FF, new Item.Settings()));

    public static final Item WOODEN_FISHING_ROD = registerItem("wooden_fishing_rod",
            new ModFishingRodItem(new Item.Settings().maxDamage(64)));

    public static final RegistryKey<ItemGroup> FUN_FISHING_GROUP_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(),
            new Identifier(FunFishingMod.MOD_ID, "fun_fishing_group")
    );

    public static final ItemGroup FUN_FISHING_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(WOODEN_FISHING_ROD))
            .displayName(Text.translatable("itemGroup.fun_fishing.fun_fishing_group"))
            .entries((context, entries) -> {
                entries.add(WOODEN_FISHING_ROD);
                entries.add(FIH);
                entries.add(COOKED_FIH);
                entries.add(FIH_BUCKET);
                entries.add(FIH_SPAWN_EGG);
            })
            .build();

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(FunFishingMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        FunFishingMod.LOGGER.info("Registering items for " + FunFishingMod.MOD_ID);
        Registry.register(Registries.ITEM_GROUP, FUN_FISHING_GROUP_KEY, FUN_FISHING_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(FIH);
            entries.add(COOKED_FIH);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.getDisplayStacks().removeIf(stack -> stack.isOf(Items.FISHING_ROD));
            entries.getSearchTabStacks().removeIf(stack -> stack.isOf(Items.FISHING_ROD));
            entries.add(WOODEN_FISHING_ROD);
            entries.add(FIH_BUCKET);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(FIH_SPAWN_EGG);
        });
    }
}
