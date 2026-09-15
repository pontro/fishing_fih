package com.example.funfishing.network;

import com.example.funfishing.FunFishingMod;
import com.example.funfishing.minigame.FishingSkillCheckManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FunFishingNetworking {
    public static final Identifier START_SKILL_CHECK = new Identifier(FunFishingMod.MOD_ID, "start_skill_check");
    public static final Identifier SKILL_CHECK_RESULT = new Identifier(FunFishingMod.MOD_ID, "skill_check_result");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SKILL_CHECK_RESULT, (server, player, handler, buf, responseSender) -> {
            int result = buf.readInt();
            boolean allCleared = buf.readBoolean();
            server.execute(() -> {
                FishingSkillCheckManager.handleResult(player, result, allCleared);
            });
        });
    }

    public static void sendStartSkillCheck(ServerPlayerEntity player, int difficulty, int totalStages, float needleSpeed, float greatZoneWidth, float goodZoneWidth) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(difficulty);
        buf.writeInt(totalStages);
        buf.writeFloat(needleSpeed);
        buf.writeFloat(greatZoneWidth);
        buf.writeFloat(goodZoneWidth);
        ServerPlayNetworking.send(player, START_SKILL_CHECK, buf);
    }
}
