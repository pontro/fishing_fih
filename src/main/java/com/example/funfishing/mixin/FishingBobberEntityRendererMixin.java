package com.example.funfishing.mixin;

import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {

    /**
     * Fixes vanilla hardcoded isOf(Items.FISHING_ROD) check in line rendering.
     * When holding a custom rod in the main hand, vanilla thought it was the offhand
     * and inverted the rendering side to the left.
     */
    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean isCustomFishingRod(ItemStack stack, Item item) {
        return stack.getItem() instanceof FishingRodItem;
    }
}
