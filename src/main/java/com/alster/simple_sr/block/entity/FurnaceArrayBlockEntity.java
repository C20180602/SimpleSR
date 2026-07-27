package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.FurnaceArrayMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class FurnaceArrayBlockEntity extends BlockEntity implements net.minecraft.world.WorldlyContainer, MenuProvider {
    public static final int CHEST_SIZE = 54;
    public static final int OUTPUT_START = CHEST_SIZE;
    private static final int TOTAL_SIZE = CHEST_SIZE * 2;
    private static final int TICKS_PER_RESULT = 4;
    private final NonNullList<ItemStack> inputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private final NonNullList<ItemStack> outputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private int ticksUntilSmelt = TICKS_PER_RESULT;

    public FurnaceArrayBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.FURNACE_ARRAY_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.furnace_array"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new FurnaceArrayMenu(id, inventory, this); }
    @Override public int getContainerSize() { return TOTAL_SIZE; }
    @Override public boolean isEmpty() { return inputItems.stream().allMatch(ItemStack::isEmpty) && outputItems.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slot < OUTPUT_START ? inputItems.get(slot) : outputItems.get(slot - OUTPUT_START); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack result = slot < OUTPUT_START
                ? ContainerHelper.removeItem(inputItems, slot, amount)
                : ContainerHelper.removeItem(outputItems, slot - OUTPUT_START, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        return slot < OUTPUT_START ? ContainerHelper.takeItem(inputItems, slot) : ContainerHelper.takeItem(outputItems, slot - OUTPUT_START);
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot < OUTPUT_START) inputItems.set(slot, stack); else outputItems.set(slot - OUTPUT_START, stack);
        setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot < OUTPUT_START; }
    @Override public boolean canTakeItem(Container target, int slot, ItemStack stack) { return true; }
    @Override public int[] getSlotsForFace(Direction side) {
        int[] slots = new int[TOTAL_SIZE];
        for (int i = 0; i < TOTAL_SIZE; i++) slots[i] = i;
        return slots;
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return slot < OUTPUT_START;
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return slot >= OUTPUT_START;
    }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inputItems.replaceAll(stack -> ItemStack.EMPTY); outputItems.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, new SimpleContainerFromList(inputItems));
        Containers.dropContents(level, pos, new SimpleContainerFromList(outputItems));
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inputItems);
        CompoundTag output = new CompoundTag(); ContainerHelper.saveAllItems(output, outputItems); tag.put("output", output);
        tag.putInt("ticks_until_smelt", ticksUntilSmelt);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); ContainerHelper.loadAllItems(tag, inputItems); ContainerHelper.loadAllItems(tag.getCompound("output"), outputItems);
        ticksUntilSmelt = tag.contains("ticks_until_smelt") ? tag.getInt("ticks_until_smelt") : TICKS_PER_RESULT;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FurnaceArrayBlockEntity furnace) {
        if (level.isClientSide || --furnace.ticksUntilSmelt > 0) return;
        furnace.ticksUntilSmelt = TICKS_PER_RESULT;
        furnace.smeltOne(level);
    }

    private void smeltOne(Level level) {
        SimpleContainer recipeInput = new SimpleContainer(1);
        for (int slot = 0; slot < CHEST_SIZE; slot++) {
            ItemStack input = inputItems.get(slot);
            if (input.isEmpty()) continue;
            recipeInput.setItem(0, input);
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
            if (recipe.isEmpty()) continue;
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            if (!canInsertOutput(result)) continue;
            input.shrink(1);
            insertOutput(result);
            setChanged();
            return;
        }
    }

    private boolean canInsertOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (ItemStack stored : outputItems) {
            if (stored.isEmpty()) return true;
            if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                remainder.shrink(Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()));
                if (remainder.isEmpty()) return true;
            }
        }
        return remainder.isEmpty();
    }
    private void insertOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = 0; i < outputItems.size() && !remainder.isEmpty(); i++) {
            ItemStack stored = outputItems.get(i);
            if (stored.isEmpty()) { outputItems.set(i, remainder.copy()); remainder.setCount(0); }
            else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount);
            }
        }
    }

    private static class SimpleContainerFromList extends SimpleContainer {
        SimpleContainerFromList(NonNullList<ItemStack> contents) { super(contents.size()); for (int i = 0; i < contents.size(); i++) setItem(i, contents.get(i)); }
    }
}
