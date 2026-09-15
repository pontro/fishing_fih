package com.example.funfishing.client;

import com.example.funfishing.FunFishingMod;
import com.example.funfishing.network.FunFishingNetworking;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Random;

public class FishingSkillCheckOverlay implements HudRenderCallback {
    private static final Identifier RING_TEXTURE = new Identifier(FunFishingMod.MOD_ID, "textures/gui/skill_check_ring.png");
    private static final Identifier NEEDLE_TEXTURE = new Identifier(FunFishingMod.MOD_ID, "textures/gui/needle.png");
    private static final Random RANDOM = new Random();

    private static boolean active = false;
    private static float currentAngle = 0.0f;
    private static int difficulty = 4;
    private static int currentStage = 1;
    private static int totalStages = 1;
    private static float needleSpeed = 4.5f;
    private static float greatZoneWidth = 18.0f;
    private static float goodZoneWidth = 55.0f;

    private static float greatStart = 130.0f;
    private static float greatEnd = 148.0f;
    private static float goodStart = 148.0f;
    private static float goodEnd = 203.0f;

    private static int feedbackTimer = 0;
    private static String feedbackText = "";
    private static int feedbackColor = 0xFFFFFF;

    public static void init() {
        HudRenderCallback.EVENT.register(new FishingSkillCheckOverlay());

        ClientPlayNetworking.registerGlobalReceiver(FunFishingNetworking.START_SKILL_CHECK, (client, handler, buf, responseSender) -> {
            int diff = buf.readInt();
            int stages = buf.readInt();
            float spd = buf.readFloat();
            float greatW = buf.readFloat();
            float goodW = buf.readFloat();

            client.execute(() -> {
                start(diff, stages, spd, greatW, goodW);
            });
        });
    }

    public static void start(int diff, int stages, float spd, float greatW, float goodW) {
        active = true;
        difficulty = diff;
        totalStages = stages;
        currentStage = 1;
        needleSpeed = spd;
        greatZoneWidth = greatW;
        goodZoneWidth = goodW;
        feedbackTimer = 0;

        rollNewZones();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 0.7f));
        }
    }

    private static void rollNewZones() {
        currentAngle = 0.0f;
        greatStart = 90.0f + RANDOM.nextFloat() * 140.0f;
        greatEnd = greatStart + greatZoneWidth;
        goodStart = greatEnd;
        goodEnd = goodStart + goodZoneWidth;
    }

    public static void tickClient() {
        if (!active) {
            if (feedbackTimer > 0) feedbackTimer--;
            return;
        }

        currentAngle += needleSpeed;
        if (currentAngle > 360.0f) {
            finish(0, false);
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static void onTriggerKey() {
        if (!active) return;

        MinecraftClient client = MinecraftClient.getInstance();

        if (currentAngle >= greatStart && currentAngle <= greatEnd) {
            if (currentStage < totalStages) {
                currentStage++;
                rollNewZones();
                if (client.player != null) {
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 1.6f, 0.9f));
                }
            } else {
                finish(2, true);
            }
        } else if (currentAngle >= goodStart && currentAngle <= goodEnd) {
            if (currentStage < totalStages) {
                currentStage++;
                rollNewZones();
                if (client.player != null) {
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1.3f, 0.8f));
                }
            } else {
                finish(1, true);
            }
        } else {
            finish(0, false);
        }
    }

    private static void finish(int result, boolean allCleared) {
        active = false;
        feedbackTimer = 30;

        MinecraftClient client = MinecraftClient.getInstance();
        if (result == 2) {
            feedbackText = "★ GREAT! ★";
            feedbackColor = 0x55FFFF;
            if (client.player != null) {
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.4f, 0.9f));
            }
        } else if (result == 1) {
            feedbackText = "FISH CAUGHT!";
            feedbackColor = 0x55FF55;
            if (client.player != null) {
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.2f, 0.8f));
            }
        } else {
            feedbackText = "LINE SNAPPED!";
            feedbackColor = 0xFF5555;
            if (client.player != null) {
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ITEM_BREAK, 1.0f, 0.7f));
            }
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(result);
        buf.writeBoolean(allCleared);
        ClientPlayNetworking.send(FunFishingNetworking.SKILL_CHECK_RESULT, buf);
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2 + 30;

        if (feedbackTimer > 0 && !active) {
            drawContext.drawCenteredTextWithShadow(client.textRenderer, feedbackText, centerX, centerY - 45, feedbackColor);
        }

        if (!active) return;

        MatrixStack matrices = drawContext.getMatrices();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1. Draw Ring Background (64x64)
        drawContext.drawTexture(RING_TEXTURE, centerX - 32, centerY - 32, 0, 0, 64, 64, 64, 64);

        // 2. Draw Good Zone (white arc slices)
        for (float a = goodStart; a <= goodEnd; a += 1.5f) {
            matrices.push();
            matrices.translate(centerX, centerY, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(a));
            drawContext.fill(-1, -26, 1, -20, 0xAAFFFFFF);
            matrices.pop();
        }

        // 3. Draw Great Zone (cyan arc slices)
        for (float a = greatStart; a <= greatEnd; a += 1.5f) {
            matrices.push();
            matrices.translate(centerX, centerY, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(a));
            drawContext.fill(-1, -27, 1, -19, 0xFF33FFFF);
            matrices.pop();
        }

        // 4. Draw Rotating Needle
        matrices.push();
        matrices.translate(centerX, centerY, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentAngle));
        drawContext.drawTexture(NEEDLE_TEXTURE, -32, -32, 0, 0, 64, 64, 64, 64);
        matrices.pop();

        RenderSystem.disableBlend();

        // 5. Multi-stage progress pips (e.g. [ ● ○ ])
        if (totalStages > 1) {
            StringBuilder pips = new StringBuilder();
            for (int i = 1; i <= totalStages; i++) {
                if (i <= currentStage) {
                    pips.append("● ");
                } else {
                    pips.append("○ ");
                }
            }
            drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("[ " + pips.toString().trim() + " ]").formatted(Formatting.AQUA), centerX, centerY - 42, 0xFFFFFF);
        }

        // 6. Prompt text
        drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal("[RIGHT CLICK] Reel In!").formatted(Formatting.YELLOW, Formatting.BOLD), centerX, centerY + 38, 0xFFFFFF);
    }
}
