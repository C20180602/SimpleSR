package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.TreeFarmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TreeFarmBlockEntity extends BlockEntity implements net.minecraft.world.WorldlyContainer, MenuProvider {
    public static final int CHEST_SIZE = 54;
    public static final int OUTPUT_START = CHEST_SIZE;
    private static final int TOTAL_SIZE = CHEST_SIZE * 2;
    private static final int TICKS_PER_RESULT = 40;
    private static final ItemStack EMPTY = ItemStack.EMPTY;
    private static final List<TreeRecipe> RECIPES = List.of(
            new TreeRecipe(Items.OAK_SAPLING, Items.OAK_LOG, 6, Items.OAK_SAPLING, 3),
            new TreeRecipe(Items.SPRUCE_SAPLING, Items.SPRUCE_LOG, 6, Items.SPRUCE_SAPLING, 3),
            new TreeRecipe(Items.BIRCH_SAPLING, Items.BIRCH_LOG, 6, Items.BIRCH_SAPLING, 3),
            new TreeRecipe(Items.JUNGLE_SAPLING, Items.JUNGLE_LOG, 6, Items.JUNGLE_SAPLING, 3),
            new TreeRecipe(Items.ACACIA_SAPLING, Items.ACACIA_LOG, 6, Items.ACACIA_SAPLING, 3),
            new TreeRecipe(Items.DARK_OAK_SAPLING, Items.DARK_OAK_LOG, 6, Items.DARK_OAK_SAPLING, 3),
            new TreeRecipe(Items.CHERRY_SAPLING, Items.CHERRY_LOG, 6, Items.CHERRY_SAPLING, 3),
            new TreeRecipe(Items.MANGROVE_PROPAGULE, Items.MANGROVE_LOG, 6, Items.MANGROVE_PROPAGULE, 3),
            new TreeRecipe(Items.CRIMSON_FUNGUS, Items.CRIMSON_STEM, 6, Items.CRIMSON_FUNGUS, 2),
            new TreeRecipe(Items.CRIMSON_STEM, Items.CRIMSON_STEM, 6, Items.CRIMSON_FUNGUS, 2),
            new TreeRecipe(Items.WARPED_FUNGUS, Items.WARPED_STEM, 6, Items.WARPED_FUNGUS, 2),
            new TreeRecipe(Items.WARPED_STEM, Items.WARPED_STEM, 6, Items.WARPED_FUNGUS, 2),
            new TreeRecipe(Items.BROWN_MUSHROOM, Items.BROWN_MUSHROOM, 5, null, 0),
            new TreeRecipe(Items.RED_MUSHROOM, Items.RED_MUSHROOM, 5, null, 0)
    );
    private final NonNullList<ItemStack> inputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private final NonNullList<ItemStack> outputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private int ticksUntilProduce = TICKS_PER_RESULT;

    public TreeFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.TREE_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.tree_farm"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new TreeFarmMenu(id, inventory, this); }

    @Override public int getContainerSize() { return TOTAL_SIZE; }
    @Override public boolean isEmpty() { return inputItems.stream().allMatch(ItemStack::isEmpty) && outputItems.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slot < OUTPUT_START ? inputItems.get(slot) : outputItems.get(slot - OUTPUT_START); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack result = slot < OUTPUT_START ? ContainerHelper.removeItem(inputItems, slot, amount) : ContainerHelper.removeItem(outputItems, slot - OUTPUT_START, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return slot < OUTPUT_START ? ContainerHelper.takeItem(inputItems, slot) : ContainerHelper.takeItem(outputItems, slot - OUTPUT_START); }
    @Override public void setItem(int slot, ItemStack stack) { if (slot < OUTPUT_START) inputItems.set(slot, stack); else outputItems.set(slot - OUTPUT_START, stack); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot < OUTPUT_START; }
    @Override public boolean canTakeItem(Container target, int slot, ItemStack stack) { return true; }
    @Override public int[] getSlotsForFace(Direction side) { int[] slots = new int[TOTAL_SIZE]; for (int i = 0; i < TOTAL_SIZE; i++) slots[i] = i; return slots; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) { return slot < OUTPUT_START; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) { return slot >= OUTPUT_START; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inputItems.replaceAll(stack -> ItemStack.EMPTY); outputItems.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, new ListContainer(inputItems)); Containers.dropContents(level, pos, new ListContainer(outputItems)); }

    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, inputItems); CompoundTag output = new CompoundTag(); ContainerHelper.saveAllItems(output, outputItems); tag.put("output", output); tag.putInt("ticks_until_produce", ticksUntilProduce); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, inputItems); ContainerHelper.loadAllItems(tag.getCompound("output"), outputItems); ticksUntilProduce = tag.contains("ticks_until_produce") ? tag.getInt("ticks_until_produce") : TICKS_PER_RESULT; }

    public static void tick(Level level, BlockPos pos, BlockState state, TreeFarmBlockEntity farm) {
        if (level.isClientSide || --farm.ticksUntilProduce > 0) return;
        farm.ticksUntilProduce = TICKS_PER_RESULT;
        farm.produce(level);
    }

    private void produce(Level level) {
        for (int plantSlot = 0; plantSlot < CHEST_SIZE; plantSlot++) {
            ItemStack plant = inputItems.get(plantSlot);
            TreeRecipe recipe = recipeFor(plant.getItem());
            if (recipe == null) continue;
            int[] boneMeal = findBoneMealSlots();
            if (boneMeal == null) continue;
            NonNullList<ItemStack> backup = copyContents(outputItems);
            if (!insertOutput(new ItemStack(recipe.log(), recipe.logCount())).isEmpty()
                    || (recipe.secondary() != null && !insertOutput(new ItemStack(recipe.secondary(), recipe.secondaryCount())).isEmpty())) {
                restore(outputItems, backup);
                continue;
            }
            plant.shrink(1);
            removeBoneMeal(boneMeal, 5);
            setChanged();
            return;
        }
    }

    private TreeRecipe recipeFor(Item item) { return RECIPES.stream().filter(recipe -> recipe.input() == item).findFirst().orElse(null); }
    private int[] findBoneMealSlots() {
        int first = -1, second = -1, third = -1, fourth = -1, fifth = -1, remaining = 5;
        for (int slot = 0; slot < CHEST_SIZE && remaining > 0; slot++) {
            ItemStack stack = inputItems.get(slot);
            if (!stack.is(Items.BONE_MEAL) || stack.isEmpty()) continue;
            int amount = Math.min(stack.getCount(), remaining);
            for (int i = 0; i < amount; i++) { if (first < 0) first = slot; else if (second < 0) second = slot; else if (third < 0) third = slot; else if (fourth < 0) fourth = slot; else fifth = slot; }
            remaining -= amount;
        }
        return remaining == 0 ? new int[]{first, second, third, fourth, fifth} : null;
    }
    private void removeBoneMeal(int[] slots, int amount) { for (int slot : slots) inputItems.get(slot).shrink(1); }
    private ItemStack insertOutput(ItemStack stack) { ItemStack remainder = stack.copy(); for (int slot = 0; slot < CHEST_SIZE && !remainder.isEmpty(); slot++) { ItemStack stored = outputItems.get(slot); if (stored.isEmpty()) { outputItems.set(slot, remainder.copy()); remainder.setCount(0); } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) { int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount); } } return remainder; }
    private static NonNullList<ItemStack> copyContents(NonNullList<ItemStack> source) { NonNullList<ItemStack> copy = NonNullList.withSize(source.size(), ItemStack.EMPTY); for (int i = 0; i < source.size(); i++) copy.set(i, source.get(i).copy()); return copy; }
    private static void restore(NonNullList<ItemStack> target, NonNullList<ItemStack> source) { for (int i = 0; i < target.size(); i++) target.set(i, source.get(i)); }

    private record TreeRecipe(Item input, Item log, int logCount, @Nullable Item secondary, int secondaryCount) { }
    private static class ListContainer extends SimpleContainer { ListContainer(NonNullList<ItemStack> contents) { super(contents.size()); for (int i = 0; i < contents.size(); i++) setItem(i, contents.get(i)); } }
}
