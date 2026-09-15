package com.example.funfishing.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModFishingRodItem extends FishingRodItem {
    public static final String NBT_CHARM_KEY = "FunFishingCharm";

    public ModFishingRodItem(Settings settings) {
        super(settings);
    }

    /**
     * Check if this rod has an attached charm.
     */
    public static boolean hasCharm(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(NBT_CHARM_KEY, NbtElement.STRING_TYPE) 
                && !stack.getNbt().getString(NBT_CHARM_KEY).isEmpty();
    }

    /**
     * Get the identifier/name of the attached charm, or null if none.
     */
    @Nullable
    public static String getCharm(ItemStack stack) {
        if (hasCharm(stack)) {
            return stack.getNbt().getString(NBT_CHARM_KEY);
        }
        return null;
    }

    /**
     * Attach a charm to this rod.
     */
    public static void setCharm(ItemStack stack, @Nullable String charmId) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (charmId == null || charmId.isEmpty()) {
            nbt.remove(NBT_CHARM_KEY);
        } else {
            nbt.putString(NBT_CHARM_KEY, charmId);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        String charm = getCharm(stack);
        if (charm != null) {
            tooltip.add(Text.translatable("tooltip.fun_fishing.charm_prefix")
                    .append(Text.translatable("charm.fun_fishing." + charm).formatted(Formatting.AQUA))
                    .formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.fun_fishing.no_charm").formatted(Formatting.DARK_GRAY));
        }
    }
}
