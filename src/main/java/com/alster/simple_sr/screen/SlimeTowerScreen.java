package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.SlimeTowerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SlimeTowerScreen extends AbstractContainerScreen<SlimeTowerMenu> {
    public SlimeTowerScreen(SlimeTowerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 256;
        imageHeight = 222;
        inventoryLabelY = 128;
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        graphics.fill(leftPos + 4, topPos + 14, leftPos + 172, topPos + 126, 0xFF8B8B8B);
        drawSlots(graphics, 8, 18, 9, 6);
        drawSlots(graphics, 8, 140, 9, 3);
        drawSlots(graphics, 8, 198, 9, 1);
    }

    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) {
            int slotX = leftPos + x + column * 18;
            int slotY = topPos + y + row * 18;
            graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF373737);
            graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF8B8B8B);
            graphics.fill(slotX + 16, slotY, slotX + 17, slotY + 17, 0xFFFFFFFF);
            graphics.fill(slotX, slotY + 16, slotX + 17, slotY + 17, 0xFFFFFFFF);
        }
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
        boolean running = isSlimeChunkClient();
        Component status = running
                ? Component.translatable("status.simple_sr.slime_tower.running")
                : Component.translatable("status.simple_sr.slime_tower.not_running");
        graphics.drawString(font, status, imageWidth - font.width(status) - 5, 6, running ? 0x55FF55 : 0xFF5555, true);
    }

    private boolean isSlimeChunkClient() {
        BlockPos pos = menu.towerPos();
        ChunkPos chunk = new ChunkPos(pos);
        long seed = menu.worldSeed() + (long) (chunk.x * chunk.x * 4987142)
                + (long) (chunk.x * 5947611) + (long) (chunk.z * chunk.z) * 4392871L
                + (long) (chunk.z * 389711) ^ 987234911L;
        return new LegacyRandomSource(seed).nextInt(10) == 0;
    }
}
