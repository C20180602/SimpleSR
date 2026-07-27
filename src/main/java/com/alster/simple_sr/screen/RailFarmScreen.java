package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.RailFarmMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RailFarmScreen extends AbstractContainerScreen<RailFarmMenu> {
    private Button toggleButton;
    public RailFarmScreen(RailFarmMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 190; imageHeight = 240; inventoryLabelX = 14; inventoryLabelY = 146;
    }
    @Override protected void init() {
        super.init();
        toggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }).bounds(leftPos + 79, topPos + 9, 48, 20).build());
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (toggleButton != null) {
            toggleButton.setMessage(Component.translatable(menu.isEnabled() ? "button.simple_sr.rail_farm.on" : "button.simple_sr.rail_farm.off"));
            toggleButton.active = menu.hasInput();
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        drawPanel(graphics, leftPos + 53, topPos + 5, 24, 22);
        drawPanel(graphics, leftPos + 10, topPos + 32, 170, 112);
        drawSlots(graphics, leftPos + 57, topPos + 9, 1, 1);
        drawSlots(graphics, leftPos + 14, topPos + 36, 9, 6);
        drawSlots(graphics, leftPos + 14, topPos + 158, 9, 3);
        drawSlots(graphics, leftPos + 14, topPos + 216, 9, 1);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("screen.simple_sr.rail_farm.output"), 14, 20, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 104, inventoryLabelY, 0x404040, false);
    }
    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF8B8B8B);
        graphics.fill(x, y, x + width - 1, y + 1, 0xFFFFFFFF); graphics.fill(x, y, x + 1, y + height - 1, 0xFFFFFFFF);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFF373737); graphics.fill(x, y + height - 1, x + width, y + height, 0xFF373737);
    }
    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) {
            int slotX = x + column * 18, slotY = y + row * 18;
            graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF373737); graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF8B8B8B);
            graphics.fill(slotX + 16, slotY, slotX + 17, slotY + 17, 0xFFFFFFFF); graphics.fill(slotX, slotY + 16, slotX + 17, slotY + 17, 0xFFFFFFFF);
        }
    }
}
