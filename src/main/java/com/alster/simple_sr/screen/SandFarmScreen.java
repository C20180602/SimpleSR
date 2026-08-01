package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.SandFarmMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SandFarmScreen extends TooltipContainerScreen<SandFarmMenu> {
    private static final int SLOT_STEP = 17;
    private Button toggleButton;

    public SandFarmScreen(SandFarmMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 236;
        imageHeight = 253;
        inventoryLabelX = 122;
        inventoryLabelY = 159;
    }

    @Override protected void init() {
        super.init();
        toggleButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }).bounds(leftPos + 214, topPos + 9, 16, 16).build());
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (toggleButton != null) {
            toggleButton.setMessage(Component.literal(menu.isEnabled() ? "●" : "○"));
            toggleButton.active = menu.hasInput();
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        drawPanel(graphics, leftPos + 7, topPos + 5, 206, 20);
        drawPanel(graphics, leftPos + 7, topPos + 40, 206, 105);
        drawSlots(graphics, leftPos + 10, topPos + 9, 12, 1);
        drawSlots(graphics, leftPos + 10, topPos + 45, 12, 6);
        drawSlot(graphics, leftPos + 214, topPos + 9);
        drawSlots(graphics, leftPos + 32, topPos + 178, 9, 3);
        drawSlots(graphics, leftPos + 32, topPos + 229, 9, 1);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable("screen.simple_sr.sand_farm.input"), 10, 27, 0x404040, false);
        graphics.drawString(font, Component.translatable("screen.simple_sr.sand_farm.output"), 10, 148, 0x404040, false);
        Component status = menu.isEndDimension() ? Component.translatable("status.simple_sr.sand_farm.running") : Component.translatable("status.simple_sr.sand_farm.not_running");
        graphics.drawString(font, status, imageWidth - font.width(status) - 5, 27, menu.isEndDimension() ? 0x55FF55 : 0xFF5555, true);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF8B8B8B);
        graphics.fill(x, y, x + width - 1, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + height - 1, 0xFFFFFFFF);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFF373737);
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFF373737);
    }

    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) {
            int slotX = x + column * SLOT_STEP, slotY = y + row * SLOT_STEP;
            graphics.fill(slotX - 1, slotY - 1, slotX + 16, slotY + 16, 0xFF373737);
            graphics.fill(slotX, slotY, slotX + 15, slotY + 15, 0xFF8B8B8B);
            graphics.fill(slotX + 15, slotY, slotX + 16, slotY + 16, 0xFFFFFFFF);
            graphics.fill(slotX, slotY + 15, slotX + 16, slotY + 16, 0xFFFFFFFF);
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 16, y + 16, 0xFF373737);
        graphics.fill(x, y, x + 15, y + 15, 0xFF8B8B8B);
        graphics.fill(x + 15, y, x + 16, y + 16, 0xFFFFFFFF);
        graphics.fill(x, y + 15, x + 16, y + 16, 0xFFFFFFFF);
    }
}
