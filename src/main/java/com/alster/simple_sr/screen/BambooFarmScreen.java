package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.BambooFarmMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BambooFarmScreen extends AbstractContainerScreen<BambooFarmMenu> {
    private Button modeButton;
    public BambooFarmScreen(BambooFarmMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 256; imageHeight = 222; inventoryLabelX = 8; inventoryLabelY = 128; }
    @Override protected void init() { super.init(); modeButton = addRenderableWidget(Button.builder(Component.empty(), button -> { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0); }).bounds(leftPos + 184, topPos + 9, 64, 20).build()); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { if (modeButton != null) modeButton.setMessage(Component.translatable(menu.isBambooMode() ? "button.simple_sr.bamboo_farm.bamboo" : "button.simple_sr.bamboo_farm.sugar_cane")); super.render(graphics, mouseX, mouseY, partialTick); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) { graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6); drawPanel(graphics, leftPos + 4, topPos + 14, 168, 112); drawSlots(graphics, 8, 18, 9, 6); drawSlots(graphics, 8, 140, 9, 3); drawSlots(graphics, 8, 198, 9, 1); }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, title, 8, 6, 0x404040, false); graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false); }
    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) { graphics.fill(x, y, x + width, y + height, 0xFF8B8B8B); graphics.fill(x, y, x + width - 1, y + 1, 0xFFFFFFFF); graphics.fill(x, y, x + 1, y + height - 1, 0xFFFFFFFF); graphics.fill(x + width - 1, y, x + width, y + height, 0xFF373737); graphics.fill(x, y + height - 1, x + width, y + height, 0xFF373737); }
    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) { for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) { int sx = leftPos + x + column * 18, sy = topPos + y + row * 18; graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF373737); graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B); graphics.fill(sx + 16, sy, sx + 17, sy + 17, 0xFFFFFFFF); graphics.fill(sx, sy + 16, sx + 17, sy + 17, 0xFFFFFFFF); } }
}
