package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.PackerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PackerScreen extends TooltipContainerScreen<PackerMenu> {
    public PackerScreen(PackerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 196;
        imageHeight = 184;
        inventoryLabelY = 74;
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        graphics.fill(leftPos + 4, topPos + 14, leftPos + imageWidth - 4, topPos + 72, 0xFF8B8B8B);
        drawSlots(graphics, 8, 18, 9, 3);
        drawSlots(graphics, 8, 84, 9, 3);
        drawSlots(graphics, 8, 142, 9, 1);
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
        Component machineLabel = Component.translatable("screen.simple_sr.packer.inventory");
        if (menu.materialsShortage()) machineLabel = Component.translatable("status.simple_sr.packer.material_shortage");
        graphics.drawString(font, machineLabel, 8, 4, menu.materialsShortage() ? 0xFF5555 : 0x404040, menu.materialsShortage());
        if (menu.topHasNoExtractableItem()) {
            Component status = Component.translatable("status.simple_sr.packer.top_empty");
            graphics.drawString(font, status, imageWidth - font.width(status) - 4, 4, 0xFF5555, true);
        }
        graphics.drawString(font, Component.translatable("screen.simple_sr.packer.backpack"), 8, inventoryLabelY, 0x404040, false);
    }
}
