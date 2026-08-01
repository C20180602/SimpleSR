package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.RaidTowerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RaidTowerScreen extends TooltipContainerScreen<RaidTowerMenu> {
    public RaidTowerScreen(RaidTowerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 176; imageHeight = 230; inventoryLabelY = 136; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6); drawPanel(graphics, leftPos + 4, topPos + 14, 168, 112); drawSlots(graphics, leftPos + 8, topPos + 18, 9, 6); drawSlots(graphics, leftPos + 8, topPos + 148, 9, 3); drawSlots(graphics, leftPos + 8, topPos + 206, 9, 1);
        graphics.fill(leftPos + 8, topPos + 128, leftPos + 168, topPos + 134, 0xFF373737); int width = menu.isRaidActive() ? Math.max(1, (menu.waveTotalRaiders() - menu.remainingRaiders()) * 160 / Math.max(1, menu.waveTotalRaiders())) : 0; if (width > 0) graphics.fill(leftPos + 8, topPos + 128, leftPos + 8 + width, topPos + 134, 0xFFAA3333);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false); Component status = menu.isOceanBiome() ? Component.translatable("status.simple_sr.raid_tower.running") : Component.translatable("status.simple_sr.raid_tower.not_running"); graphics.drawString(font, status, imageWidth - font.width(status) - 5, 6, menu.isOceanBiome() ? 0x55FF55 : 0xFF5555, true);
        if (menu.isRaidActive()) { String wave = "" + menu.currentWave() + "/" + menu.totalWaves(); graphics.drawString(font, Component.literal(wave), 78 - font.width(wave) / 2, 119, 0x404040, false); } graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
    }
    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) { graphics.fill(x, y, x + width, y + height, 0xFF8B8B8B); graphics.fill(x, y, x + width - 1, y + 1, 0xFFFFFFFF); graphics.fill(x, y, x + 1, y + height - 1, 0xFFFFFFFF); graphics.fill(x + width - 1, y, x + width, y + height, 0xFF373737); graphics.fill(x, y + height - 1, x + width, y + height, 0xFF373737); }
    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) { for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) { int sx = x + column * 18, sy = y + row * 18; graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF373737); graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B); graphics.fill(sx + 16, sy, sx + 17, sy + 17, 0xFFFFFFFF); graphics.fill(sx, sy + 16, sx + 17, sy + 17, 0xFFFFFFFF); } }
}
