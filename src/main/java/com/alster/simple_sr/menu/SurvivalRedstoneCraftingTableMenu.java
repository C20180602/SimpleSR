package com.alster.simple_sr.menu;

import com.alster.simple_sr.Config;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.SurvivalRedstoneCraftingTableBlockEntity;
import com.alster.simple_sr.level.SurvivalRedstoneLevel;
import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class SurvivalRedstoneCraftingTableMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_COUNT = 54;
    public static final int OUTPUT_SLOT = 54;
    public static final int PLAYER_INVENTORY_START = 55;

    private final SurvivalRedstoneCraftingTableBlockEntity blockEntity;
    private final Level level;
    private final SimpleContainer outputContainer = new SimpleContainer(1);
    private final ContainerData data;
    private final int playerSurvivalRedstoneLevel;

    public SurvivalRedstoneCraftingTableMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, getBlockEntity(inventory, extraData));
    }

    public SurvivalRedstoneCraftingTableMenu(int containerId, Inventory inventory, SurvivalRedstoneCraftingTableBlockEntity blockEntity) {
        super(ModMenuTypes.SURVIVAL_REDSTONE_CRAFTING_TABLE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.playerSurvivalRedstoneLevel = inventory.player instanceof ServerPlayer serverPlayer ? SurvivalRedstoneLevel.get(serverPlayer) : 1;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getSelectedRecipe();
                    case 1 -> playerSurvivalRedstoneLevel;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    blockEntity.setSelectedRecipe(value);
                    updateOutput();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        addDataSlot(DataSlot.forContainer(data, 0));
        addDataSlot(DataSlot.forContainer(data, 1));

        ItemStackHandler handler = blockEntity.getItemHandler();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new SurvivalRedstoneInputSlot(this, handler, column + row * 9, 90 + column * 18, 18 + row * 18));
            }
        }

        addSlot(new SurvivalRedstoneResultSlot(this, outputContainer, 0, 281, 63));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 90 + column * 18, 140 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 90 + column * 18, 198));
        }

        updateOutput();
    }

    private static SurvivalRedstoneCraftingTableBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf extraData) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof SurvivalRedstoneCraftingTableBlockEntity table) {
            return table;
        }
        throw new IllegalStateException("Survival Redstone Crafting Table block entity is missing");
    }

    public List<SurvivalRedstoneRecipe> recipes() {
        return Config.survivalRedstoneRecipes;
    }

    public SurvivalRedstoneRecipe getSelectedRecipe() {
        List<SurvivalRedstoneRecipe> recipes = recipes();
        int selected = data.get(0);
        if (selected < 0 || selected >= recipes.size()) {
            return null;
        }
        return recipes.get(selected);
    }

    public int getSelectedRecipeIndex() {
        return data.get(0);
    }

    public int getPlayerSurvivalRedstoneLevel() {
        return data.get(1);
    }

    public boolean canSelectRecipe(Player player, int recipeIndex) {
        List<SurvivalRedstoneRecipe> recipes = recipes();
        if (recipeIndex < 0 || recipeIndex >= recipes.size() || !blockEntity.inputsEmpty()) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            return recipes.get(recipeIndex).level() <= SurvivalRedstoneLevel.get(serverPlayer);
        }
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (canSelectRecipe(player, id)) {
            data.set(0, id);
            broadcastChanges();
            return true;
        }
        return false;
    }

    public int countStored(Item item) {
        int count = 0;
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            ItemStack stack = slots.get(i).getItem();
            if (!stack.isEmpty() && stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public boolean hasCompleteInputs() {
        SurvivalRedstoneRecipe recipe = getSelectedRecipe();
        if (recipe == null) {
            return false;
        }
        for (SurvivalRedstoneRecipe.IngredientCount ingredient : recipe.ingredients()) {
            if (countStored(ingredient.item()) < ingredient.count()) {
                return false;
            }
        }
        return true;
    }

    public void consumeInputs() {
        blockEntity.clearInputs();
        updateOutput();
    }

    private void updateOutput() {
        SurvivalRedstoneRecipe recipe = getSelectedRecipe();
        outputContainer.setItem(0, recipe != null && hasCompleteInputs() ? recipe.result().copy() : ItemStack.EMPTY);
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        super.slotsChanged(container);
        updateOutput();
    }

    @Override
    public void broadcastChanges() {
        updateOutput();
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return empty;
        }

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index == OUTPUT_SLOT) {
            if (!moveItemStackTo(original, PLAYER_INVENTORY_START, slots.size(), true)) {
                return empty;
            }
            slot.onQuickCraft(original, copy);
        } else if (index >= PLAYER_INVENTORY_START) {
            if (!moveItemStackTo(original, 0, INPUT_SLOT_COUNT, false)) {
                return empty;
            }
        } else if (!moveItemStackTo(original, PLAYER_INVENTORY_START, slots.size(), false)) {
            return empty;
        }

        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (original.getCount() == copy.getCount()) {
            return empty;
        }

        slot.onTake(player, original);
        updateOutput();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get());
    }
}
