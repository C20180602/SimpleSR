package com.alster.simple_sr.screen;

import com.alster.simple_sr.Config;
import com.alster.simple_sr.menu.SurvivalRedstoneCraftingTableMenu;
import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class SurvivalRedstoneCraftingTableScreen extends AbstractContainerScreen<SurvivalRedstoneCraftingTableMenu> {
    private static final int PANEL = 0xFF2D3136;
    private static final int SLOT = 0xFF8B8B8B;
    private static final int SLOT_DARK = 0xFF373B40;
    private static final int SELECTED = 0xFFC43A34;
    private static final int LOCKED = 0xAA1F1F1F;

    public SurvivalRedstoneCraftingTableScreen(SurvivalRedstoneCraftingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 328;
        imageHeight = 222;
        inventoryLabelX = 90;
        inventoryLabelY = 128;
        titleLabelX = 90;
        titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF16191D);
        graphics.fill(x + 6, y + 14, x + 82, y + 126, PANEL);
        graphics.fill(x + 88, y + 14, x + 254, y + 126, PANEL);
        graphics.fill(x + 268, y + 50, x + 318, y + 104, PANEL);

        drawRecipeButtons(graphics, x, y);
        drawSlots(graphics, x + 90, y + 18, 9, 6);
        drawSlots(graphics, x + 90, y + 140, 9, 3);
        drawSlots(graphics, x + 90, y + 198, 9, 1);
        drawSlots(graphics, x + 272, y + 54, 2, 2);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderGhostIngredients(graphics);
        renderRecipeCounts(graphics);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void drawRecipeButtons(GuiGraphics graphics, int x, int y) {
        List<SurvivalRedstoneRecipe> recipes = menu.recipes();
        for (int i = 0; i < Math.min(6, recipes.size()); i++) {
            int buttonX = x + 10;
            int buttonY = y + 18 + i * 18;
            boolean selected = i == menu.getSelectedRecipeIndex();
            boolean locked = recipes.get(i).level() > menu.getPlayerSurvivalRedstoneLevel();
            graphics.fill(buttonX, buttonY, buttonX + 68, buttonY + 18, selected ? SELECTED : 0xFF444A50);
            graphics.fill(buttonX + 1, buttonY + 1, buttonX + 67, buttonY + 17, locked ? 0xFF292D31 : 0xFF20242A);
        }
    }

    private void drawSlots(GuiGraphics graphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int slotX = x + column * 18;
                int slotY = y + row * 18;
                graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, SLOT);
                graphics.fill(slotX, slotY, slotX + 16, slotY + 16, SLOT_DARK);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xE8E8E8, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xE8E8E8, false);

        List<SurvivalRedstoneRecipe> recipes = menu.recipes();
        for (int i = 0; i < Math.min(6, recipes.size()); i++) {
            SurvivalRedstoneRecipe recipe = recipes.get(i);
            int buttonY = 20 + i * 18;
            graphics.renderItem(recipe.icon(), 12, buttonY);
            String name = Component.translatable(recipe.nameKey()).getString();
            if (font.width(name) > 40) {
                name = font.plainSubstrByWidth(name, 40);
            }
            int color = recipe.level() > menu.getPlayerSurvivalRedstoneLevel() ? 0x777777 : 0xF0F0F0;
            graphics.drawString(font, name, 30, buttonY + 5, color, false);
            if (recipe.level() > 1) {
                graphics.drawString(font, String.valueOf(recipe.level()), 70, buttonY + 5, 0xFF7777, false);
            }
        }
    }

    private void renderGhostIngredients(GuiGraphics graphics) {
        SurvivalRedstoneRecipe recipe = menu.getSelectedRecipe();
        if (recipe == null) {
            return;
        }
        for (int i = 0; i < recipe.ingredients().size() && i < SurvivalRedstoneCraftingTableMenu.INPUT_SLOT_COUNT; i++) {
            SurvivalRedstoneRecipe.IngredientCount ingredient = recipe.ingredients().get(i);
            if (menu.countStored(ingredient.item()) <= 0) {
                Slot slot = menu.slots.get(i);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
                graphics.renderItem(ingredient.stack(), leftPos + slot.x, topPos + slot.y);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
            }
        }
    }

    private void renderRecipeCounts(GuiGraphics graphics) {
        SurvivalRedstoneRecipe recipe = menu.getSelectedRecipe();
        if (recipe == null) {
            return;
        }
        for (int i = 0; i < recipe.ingredients().size() && i < SurvivalRedstoneCraftingTableMenu.INPUT_SLOT_COUNT; i++) {
            SurvivalRedstoneRecipe.IngredientCount ingredient = recipe.ingredients().get(i);
            Slot slot = menu.slots.get(i);
            int stored = menu.countStored(ingredient.item());
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 200.0F);
            graphics.drawString(font, String.valueOf(ingredient.count()), leftPos + slot.x + 1, topPos + slot.y + 1, ChatFormatting.RED.getColor(), false);
            if (stored > 0) {
                graphics.drawString(font, String.valueOf(stored), leftPos + slot.x + 1, topPos + slot.y + 9, ChatFormatting.GREEN.getColor(), false);
            }
            graphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int relativeX = (int) mouseX - leftPos;
            int relativeY = (int) mouseY - topPos;
            if (relativeX >= 10 && relativeX < 78 && relativeY >= 18 && relativeY < 126) {
                int recipeIndex = (relativeY - 18) / 18;
                if (recipeIndex >= 0 && recipeIndex < Math.min(6, menu.recipes().size())) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, recipeIndex);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
