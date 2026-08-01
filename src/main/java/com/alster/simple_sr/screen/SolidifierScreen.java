package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.SolidifierMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolidifierScreen extends TooltipContainerScreen<SolidifierMenu> {
    public SolidifierScreen(SolidifierMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 370; imageHeight = 222; inventoryLabelX = 104; inventoryLabelY = 128; titleLabelX = 8; titleLabelY = 6; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        drawPanel(graphics, leftPos + 4, topPos + 14, 166, 112); drawPanel(graphics, leftPos + 186, topPos + 14, 166, 112);
        drawSlots(graphics, leftPos + 8, topPos + 18, 9, 6); drawSlots(graphics, leftPos + 190, topPos + 18, 9, 6);
        drawSlots(graphics, leftPos + 104, topPos + 140, 9, 3); drawSlots(graphics, leftPos + 104, topPos + 198, 9, 1);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, Component.translatable("screen.simple_sr.solidifier.input"), 8, 6, 0x404040, false); graphics.drawString(font, Component.translatable("screen.simple_sr.solidifier.output"), 190, 6, 0x404040, false); Component status = menu.isEndDimension() ? Component.translatable("status.simple_sr.solidifier.running") : Component.translatable("status.simple_sr.solidifier.not_running"); graphics.drawString(font, status, imageWidth - font.width(status) - 5, 6, menu.isEndDimension() ? 0x55FF55 : 0xFF5555, true); graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false); }
    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) { graphics.fill(x, y, x + width, y + height, 0xFF8B8B8B); graphics.fill(x, y, x + width - 1, y + 1, 0xFFFFFFFF); graphics.fill(x, y, x + 1, y + height - 1, 0xFFFFFFFF); graphics.fill(x + width - 1, y, x + width, y + height, 0xFF373737); graphics.fill(x, y + height - 1, x + width, y + height, 0xFF373737); }
    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) { for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) { int slotX = x + column * 18, slotY = y + row * 18; graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF373737); graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF8B8B8B); graphics.fill(slotX + 16, slotY, slotX + 17, slotY + 17, 0xFFFFFFFF); graphics.fill(slotX, slotY + 16, slotX + 17, slotY + 17, 0xFFFFFFFF); } }
}
