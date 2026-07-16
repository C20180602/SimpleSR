package com.alster.simple_sr.screen;

import com.alster.simple_sr.block.entity.VillagerBreederBlockEntity;
import com.alster.simple_sr.menu.VillagerBreederMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VillagerBreederScreen extends AbstractContainerScreen<VillagerBreederMenu> {
    private static final int BACKGROUND = 0xFFC6C6C6;
    private static final int PANEL_DARK = 0xFF8B8B8B;
    private static final int PANEL_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_DARK = 0xFF373737;
    private static final int SLOT_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_FILL = 0xFF8B8B8B;
    private static final int PROGRESS_EMPTY = 0xFF373737;
    private static final int PROGRESS_GREEN = 0xFF55AA55;

    public VillagerBreederScreen(VillagerBreederMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        drawRaisedPanel(graphics, x, y, imageWidth, imageHeight);
        drawSunkenPanel(graphics, x + 6, y + 14, 164, 54);
        drawSunkenPanel(graphics, x + 6, y + 70, 164, 90);
        drawSlots(graphics, x + 80, y + 35, 1, 1);
        drawSlots(graphics, x + 8, y + 84, 9, 3);
        drawSlots(graphics, x + 8, y + 142, 9, 1);

        int progressWidth = 36;
        int filledWidth = Math.round(progressWidth * menu.getProgressTicks() / (float) VillagerBreederBlockEntity.GENERATION_INTERVAL);
        graphics.fill(x + 70, y + 55, x + 70 + progressWidth, y + 61, PROGRESS_EMPTY);
        if (filledWidth > 0) {
            graphics.fill(x + 70, y + 55, x + 70 + filledWidth, y + 61, PROGRESS_GREEN);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
        String seconds = menu.getRemainingSeconds() + "s";
        graphics.drawString(font, seconds, 88 - font.width(seconds) / 2, 62, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, 72, 0x404040, false);
    }

    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int slotX = x + column * 18;
                int slotY = y + row * 18;
                graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, SLOT_DARK);
                graphics.fill(slotX, slotY, slotX + 16, slotY + 16, SLOT_FILL);
                graphics.fill(slotX + 16, slotY, slotX + 17, slotY + 17, SLOT_LIGHT);
                graphics.fill(slotX, slotY + 16, slotX + 17, slotY + 17, SLOT_LIGHT);
            }
        }
    }

    private void drawRaisedPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND);
        graphics.fill(x, y, x + width - 1, y + 1, PANEL_LIGHT);
        graphics.fill(x, y, x + 1, y + height - 1, PANEL_LIGHT);
        graphics.fill(x + width - 1, y, x + width, y + height, PANEL_DARK);
        graphics.fill(x, y + height - 1, x + width, y + height, PANEL_DARK);
    }

    private void drawSunkenPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND);
        graphics.fill(x, y, x + width - 1, y + 1, PANEL_DARK);
        graphics.fill(x, y, x + 1, y + height - 1, PANEL_DARK);
        graphics.fill(x + width - 1, y, x + width, y + height, PANEL_LIGHT);
        graphics.fill(x, y + height - 1, x + width, y + height, PANEL_LIGHT);
    }
}
