package com.example.funfishing.mixin;

import com.example.funfishing.minigame.FishingSkillCheckManager;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {
    @Shadow @Final private static TrackedData<Boolean> CAUGHT_FISH;
    @Shadow private int hookCountdown;
    @Shadow public abstract PlayerEntity getPlayerOwner();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        FishingBobberEntity bobber = (FishingBobberEntity)(Object)this;
        if (!bobber.getWorld().isClient) {
            PlayerEntity owner = this.getPlayerOwner();
            if (owner instanceof ServerPlayerEntity serverPlayer) {
                // Instant bite trigger: when fish bites, start skill check
                if (this.hookCountdown > 0 && bobber.getDataTracker().get(CAUGHT_FISH)) {
                    if (!FishingSkillCheckManager.hasActiveSession(serverPlayer)) {
                        this.hookCountdown = 100; // Keep bobber splashing while minigame runs
                        FishingSkillCheckManager.startSession(serverPlayer, bobber);
                    } else {
                        this.hookCountdown = Math.max(this.hookCountdown, 40);
                    }
                }
            }
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        FishingBobberEntity bobber = (FishingBobberEntity)(Object)this;
        if (!bobber.getWorld().isClient) {
            PlayerEntity owner = this.getPlayerOwner();
            if (owner instanceof ServerPlayerEntity serverPlayer) {
                // If minigame is active, completely block vanilla's instant loot and bobber destruction
                if (FishingSkillCheckManager.hasActiveSession(serverPlayer)) {
                    cir.setReturnValue(0);
                }
            }
        }
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
        method = "removeIfInvalid",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean checkFishingRod(ItemStack stack, net.minecraft.item.Item item) {
        return stack.getItem() instanceof net.minecraft.item.FishingRodItem;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(CallbackInfo ci) {
        PlayerEntity owner = this.getPlayerOwner();
        if (owner instanceof ServerPlayerEntity serverPlayer) {
            FishingSkillCheckManager.clear(serverPlayer);
        }
    }
}
