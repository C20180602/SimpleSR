package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.ZombifiedPiglinTowerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ZombifiedPiglinTowerScreen extends TooltipContainerScreen<ZombifiedPiglinTowerMenu> {
    public ZombifiedPiglinTowerScreen(ZombifiedPiglinTowerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 256;
        imageHeight = 222;
        inventoryLabelY = 128;
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        drawPanel(graphics, leftPos + 4, topPos + 14, 168, 112);
        drawSlots(graphics, 8, 18, 9, 6);
        drawSlot(graphics, 224, 18);
        graphics.fill(leftPos + 224, topPos + 42, leftPos + 248, topPos + 58, 0xFF373737);
        graphics.fill(leftPos + 225, topPos + 43, leftPos + 247, topPos + 57, 0xFF8B8B8B);
        graphics.fill(leftPos + 190, topPos + 82, leftPos + 250, topPos + 90, 0xFF373737);
        int xpWidth = Math.round(MachineExperienceDisplay.progress(menu.getStoredExperience()) * 58);
        graphics.fill(leftPos + 191, topPos + 83, leftPos + 191 + xpWidth, topPos + 89, 0xFF55AAFF);
        drawSlots(graphics, 8, 140, 9, 3);
        drawSlots(graphics, 8, 198, 9, 1);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        Component hint = Component.translatable("hint.simple_sr.zombified_piglin_tower.sword");
        graphics.drawString(font, hint, 8, 6, 0x404040, false);
        Component status = menu.isInNetherDimension()
                ? Component.translatable("status.simple_sr.zombified_piglin_tower.running")
                : Component.translatable("status.simple_sr.zombified_piglin_tower.not_running");
        graphics.drawString(font, status, imageWidth - font.width(status) - 5, 6, menu.isInNetherDimension() ? 0x55FF55 : 0xFF5555, true);
        graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
        graphics.drawString(font, Component.translatable("button.simple_sr.extract_experience"), 226, 46, 0x404040, false);
        graphics.drawString(font, Component.literal("Lv." + MachineExperienceDisplay.level(menu.getStoredExperience())), 190, 94, 0x5555FF, false);
        String owner = menu.getSwordOwnerName();
        if (!owner.isEmpty()) graphics.drawString(font, owner, imageWidth - font.width(owner) - 5, 64, 0x5555FF, false);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isExperienceButton(mouseX, mouseY)) { minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0); return true; }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
        return super.mouseReleased(mouseX, mouseY, button);
    }
    private boolean isExperienceButton(double mouseX, double mouseY) { return mouseX >= leftPos + 224 && mouseX < leftPos + 248 && mouseY >= topPos + 42 && mouseY < topPos + 58; }

    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF8B8B8B);
        graphics.fill(x, y, x + width - 1, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + height - 1, 0xFFFFFFFF);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFF373737);
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFF373737);
    }

    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) drawSlot(graphics, x + column * 18, y + row * 18);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        int slotX = leftPos + x, slotY = topPos + y;
        graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF373737);
        graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF8B8B8B);
        graphics.fill(slotX + 16, slotY, slotX + 17, slotY + 17, 0xFFFFFFFF);
        graphics.fill(slotX, slotY + 16, slotX + 17, slotY + 17, 0xFFFFFFFF);
    }
}
