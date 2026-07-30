package com.alster.simple_sr.screen;

import com.alster.simple_sr.menu.SurvivalRedstoneCraftingTableMenu;
import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SurvivalRedstoneCraftingTableScreen extends AbstractContainerScreen<SurvivalRedstoneCraftingTableMenu> {
    private static final int BACKGROUND = 0xFFC6C6C6;
    private static final int PANEL_DARK = 0xFF8B8B8B;
    private static final int PANEL_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_DARK = 0xFF373737;
    private static final int SLOT_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_FILL = 0xFF8B8B8B;
    private static final int SELECTED = 0xFFE0E0E0;
    private static final int BUTTON_FILL = 0xFFC6C6C6;
    private static final int LOCKED = 0xFF9F9F9F;
    private static final int RED_TEXT = 0xFFFF5555;
    private static final int WHITE_TEXT = 0xFFFFFFFF;
    private static final int TEXT_SHADOW = 0xFF3F3F3F;
    private static final float MIN_COUNT_SCALE = 0.55F;
    private static final int VISIBLE_RECIPE_COUNT = 6;
    private int recipeScroll;

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
        drawRaisedPanel(graphics, x, y, imageWidth, imageHeight);
        drawSunkenPanel(graphics, x + 6, y + 14, 76, 112);
        drawSunkenPanel(graphics, x + 88, y + 14, 166, 112);
        drawSunkenPanel(graphics, x + 279, y + 61, 22, 22);

        drawRecipeButtons(graphics, x, y);
        drawRecipeScrollbar(graphics, x, y);
        drawSlots(graphics, x + 90, y + 18, 9, 6);
        drawSlots(graphics, x + 90, y + 140, 9, 3);
        drawSlots(graphics, x + 90, y + 198, 9, 1);
        drawSlots(graphics, x + 281, y + 63, 1, 1);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderGhostIngredients(graphics);
        renderScaledStackCounts(graphics);
        renderRecipeCounts(graphics);
        renderGhostTooltips(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void drawRecipeButtons(GuiGraphics graphics, int x, int y) {
        List<SurvivalRedstoneRecipe> recipes = menu.recipes();
        for (int i = 0; i < Math.min(VISIBLE_RECIPE_COUNT, recipes.size() - recipeScroll); i++) {
            int recipeIndex = recipeScroll + i;
            int buttonX = x + 10;
            int buttonY = y + 18 + i * 18;
            boolean selected = recipeIndex == menu.getSelectedRecipeIndex();
            boolean locked = recipes.get(recipeIndex).level() > menu.getPlayerSurvivalRedstoneLevel();
            drawRaisedPanel(graphics, buttonX, buttonY, 68, 18);
            graphics.fill(buttonX + 1, buttonY + 1, buttonX + 67, buttonY + 17, locked ? LOCKED : selected ? SELECTED : BUTTON_FILL);
        }
    }

    private void drawRecipeScrollbar(GuiGraphics graphics, int x, int y) {
        int total = menu.recipes().size();
        if (total <= VISIBLE_RECIPE_COUNT) return;
        int trackX = x + 78;
        int trackY = y + 18;
        int trackHeight = 108;
        int thumbHeight = Math.max(18, trackHeight * VISIBLE_RECIPE_COUNT / total);
        int maxScroll = total - VISIBLE_RECIPE_COUNT;
        int thumbY = trackY + (trackHeight - thumbHeight) * recipeScroll / maxScroll;
        graphics.fill(trackX, trackY, trackX + 3, trackY + trackHeight, PANEL_DARK);
        graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbHeight, PANEL_LIGHT);
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

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);

        List<SurvivalRedstoneRecipe> recipes = menu.recipes();
        for (int i = 0; i < Math.min(VISIBLE_RECIPE_COUNT, recipes.size() - recipeScroll); i++) {
            SurvivalRedstoneRecipe recipe = recipes.get(recipeScroll + i);
            int buttonY = 20 + i * 18;
            graphics.renderItem(recipe.icon(), 12, buttonY);
            String name = Component.translatable(recipe.nameKey()).getString();
            if (font.width(name) > 40) {
                name = font.plainSubstrByWidth(name, 40);
            }
            int color = recipe.level() > menu.getPlayerSurvivalRedstoneLevel() ? 0x555555 : 0x404040;
            graphics.drawString(font, name, 30, buttonY + 5, color, false);
            if (recipe.level() > 1) {
                drawScaledString(graphics, String.valueOf(recipe.level()), 70, buttonY + 5, 8, RED_TEXT, true);
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
            if (menu.countStored(ingredient) <= 0) {
                Slot slot = menu.slots.get(i);
                long cycle = minecraft != null && minecraft.level != null ? minecraft.level.getGameTime() / 20L : 0L;
                graphics.setColor(1.0F, 1.0F, 1.0F, 0.5F);
                graphics.renderItem(ingredient.stackAt(cycle), leftPos + slot.x, topPos + slot.y);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderGhostTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        SurvivalRedstoneRecipe recipe = menu.getSelectedRecipe();
        if (recipe == null) return;
        long cycle = minecraft != null && minecraft.level != null ? minecraft.level.getGameTime() / 20L : 0L;
        for (int i = 0; i < recipe.ingredients().size() && i < SurvivalRedstoneCraftingTableMenu.INPUT_SLOT_COUNT; i++) {
            SurvivalRedstoneRecipe.IngredientCount ingredient = recipe.ingredients().get(i);
            Slot slot = menu.slots.get(i);
            if (!slot.hasItem() && menu.countStored(ingredient) <= 0 && isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                ItemStack displayStack = ingredient.stackAt(cycle);
                if (!displayStack.isEmpty()) graphics.renderTooltip(font, displayStack, mouseX, mouseY);
                return;
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
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 200.0F);
            drawScaledString(graphics, String.valueOf(ingredient.count()), leftPos + slot.x + 1, topPos + slot.y + 1, 14, RED_TEXT, true);
            graphics.pose().popPose();
        }
    }

    private void renderScaledStackCounts(GuiGraphics graphics) {
        for (Slot slot : menu.slots) {
            if (!slot.hasItem() || slot.getItem().getCount() <= 1) {
                continue;
            }
            String count = String.valueOf(slot.getItem().getCount());
            if (font.width(count) <= 14) {
                continue;
            }
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 210.0F);
            drawScaledStringRight(graphics, count, leftPos + slot.x + 17, topPos + slot.y + 10, 14, WHITE_TEXT, true);
            graphics.pose().popPose();
        }
    }

    private void drawScaledString(GuiGraphics graphics, String text, int x, int y, int maxWidth, int color, boolean shadow) {
        float scale = countScale(text, maxWidth);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.drawString(font, text, 0, 0, color, shadow);
        graphics.pose().popPose();
    }

    private void drawScaledStringRight(GuiGraphics graphics, String text, int right, int y, int maxWidth, int color, boolean shadow) {
        float scale = countScale(text, maxWidth);
        int x = right - (int) Math.ceil(font.width(text) * scale);
        if (shadow && color == WHITE_TEXT) {
            drawScaledString(graphics, text, x + 1, y + 1, maxWidth, TEXT_SHADOW, false);
        }
        drawScaledString(graphics, text, x, y, maxWidth, color, shadow);
    }

    private float countScale(String text, int maxWidth) {
        int width = font.width(text);
        if (width <= maxWidth) {
            return 1.0F;
        }
        return Math.max(MIN_COUNT_SCALE, maxWidth / (float) width);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
            int relativeX = (int) mouseX - leftPos;
            int relativeY = (int) mouseY - topPos;
            if (relativeX >= 10 && relativeX < 78 && relativeY >= 18 && relativeY < 126) {
                int recipeIndex = recipeScroll + (relativeY - 18) / 18;
                if (recipeIndex >= recipeScroll && recipeIndex < Math.min(recipeScroll + VISIBLE_RECIPE_COUNT, menu.recipes().size())) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, recipeIndex);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int relativeX = (int) mouseX - leftPos;
        int relativeY = (int) mouseY - topPos;
        if (relativeX >= 6 && relativeX < 82 && relativeY >= 14 && relativeY < 126 && menu.recipes().size() > VISIBLE_RECIPE_COUNT) {
            int maxScroll = menu.recipes().size() - VISIBLE_RECIPE_COUNT;
            recipeScroll = Math.max(0, Math.min(maxScroll, recipeScroll - (int) Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
