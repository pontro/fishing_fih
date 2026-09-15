package com.example.funfishing.minigame;

import com.example.funfishing.network.FunFishingNetworking;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FishingSkillCheckManager {
    public static class Session {
        public final FishingBobberEntity bobber;
        public final CatchEntry catchEntry;
        public final FishingDifficultyEngine.DifficultyProfile profile;
        public boolean completed = false;

        public Session(FishingBobberEntity bobber, CatchEntry catchEntry, FishingDifficultyEngine.DifficultyProfile profile) {
            this.bobber = bobber;
            this.catchEntry = catchEntry;
            this.profile = profile;
        }
    }

    private static final Map<UUID, Session> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    public static boolean hasActiveSession(ServerPlayerEntity player) {
        Session session = ACTIVE_SESSIONS.get(player.getUuid());
        return session != null && !session.completed && session.bobber.isAlive();
    }

    public static void startSession(ServerPlayerEntity player, FishingBobberEntity bobber) {
        if (hasActiveSession(player)) return;

        CatchEntry catchEntry = CatchRegistry.rollCatch(player.getRandom());
        FishingDifficultyEngine.DifficultyProfile profile =
                FishingDifficultyEngine.calculate(player, catchEntry.getBaseDifficulty());

        Session session = new Session(bobber, catchEntry, profile);
        ACTIVE_SESSIONS.put(player.getUuid(), session);

        FunFishingNetworking.sendStartSkillCheck(
                player,
                profile.difficulty,
                profile.totalStages,
                profile.needleSpeed,
                profile.greatZoneWidth,
                profile.goodZoneWidth
        );
    }

    public static void handleResult(ServerPlayerEntity player, int result, boolean allCleared) {
        Session session = ACTIVE_SESSIONS.remove(player.getUuid());
        if (session == null || !session.bobber.isAlive()) return;

        session.completed = true;
        FishingBobberEntity bobber = session.bobber;
        ServerWorld world = (ServerWorld) player.getWorld();

        damageFishingRod(player);

        if (allCleared) {
            ItemStack loot = session.catchEntry.getLoot();
            if (result == 2) {
                loot.setCount(loot.getCount() * 2);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8f, 1.4f);
                player.sendMessage(Text.literal("★ GREAT CATCH! ★").formatted(Formatting.AQUA, Formatting.BOLD), true);
            } else {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.8f, 1.0f);
                player.sendMessage(Text.literal("Fish Caught!").formatted(Formatting.GREEN), true);
            }

            player.increaseStat(Stats.FISH_CAUGHT, 1);

            ItemEntity itemEntity = new ItemEntity(world, bobber.getX(), bobber.getY() + 0.5, bobber.getZ(), loot);
            double dx = player.getX() - bobber.getX();
            double dy = player.getY() - bobber.getY();
            double dz = player.getZ() - bobber.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            itemEntity.setVelocity(dx * 0.1, dy * 0.1 + Math.sqrt(dist) * 0.08, dz * 0.1);
            world.spawnEntity(itemEntity);

            int expAmount = Math.max(2, session.profile.difficulty * (result == 2 ? 2 : 1));
            world.spawnEntity(new ExperienceOrbEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), expAmount));
            bobber.discard();

        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.6f, 0.8f);

            player.sendMessage(Text.literal("The fish got away!").formatted(Formatting.RED), true);

            world.spawnParticles(ParticleTypes.SPLASH, bobber.getX(), bobber.getY() + 0.2, bobber.getZ(), 20, 0.3, 0.1, 0.3, 0.2);
            bobber.discard();
        }
    }

    private static void damageFishingRod(ServerPlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof FishingRodItem) {
            player.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
        } else if (player.getOffHandStack().getItem() instanceof FishingRodItem) {
            player.getOffHandStack().damage(1, player, p -> p.sendToolBreakStatus(Hand.OFF_HAND));
        }
    }

    public static void clear(ServerPlayerEntity player) {
        ACTIVE_SESSIONS.remove(player.getUuid());
    }
}
