package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.HoneyFarmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HoneyFarmBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int CHEST_SIZE = 54;
    public static final int OUTPUT_START = CHEST_SIZE;
    private static final int TOTAL_SIZE = CHEST_SIZE * 2;
    private static final int BOTTLE_INTERVAL = 20 * 30;
    private static final int HONEYCOMB_INTERVAL = 20 * 60;
    private final NonNullList<ItemStack> inputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private final NonNullList<ItemStack> outputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private int bottleTicks = BOTTLE_INTERVAL;
    private int honeycombTicks = HONEYCOMB_INTERVAL;

    public HoneyFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.HONEY_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.honey_farm"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new HoneyFarmMenu(id, inventory, this); }
    @Override public int getContainerSize() { return TOTAL_SIZE; }
    @Override public boolean isEmpty() { return inputItems.stream().allMatch(ItemStack::isEmpty) && outputItems.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slot < OUTPUT_START ? inputItems.get(slot) : outputItems.get(slot - OUTPUT_START); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = slot < OUTPUT_START ? ContainerHelper.removeItem(inputItems, slot, amount) : ContainerHelper.removeItem(outputItems, slot - OUTPUT_START, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return slot < OUTPUT_START ? ContainerHelper.takeItem(inputItems, slot) : ContainerHelper.takeItem(outputItems, slot - OUTPUT_START); }
    @Override public void setItem(int slot, ItemStack stack) { if (slot < OUTPUT_START) inputItems.set(slot, stack); else outputItems.set(slot - OUTPUT_START, stack); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot < OUTPUT_START && stack.is(Items.GLASS_BOTTLE); }
    @Override public boolean canTakeItem(Container target, int slot, ItemStack stack) { return true; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inputItems.replaceAll(stack -> ItemStack.EMPTY); outputItems.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }
    @Override public int[] getSlotsForFace(Direction side) { int[] slots = new int[TOTAL_SIZE]; for (int i = 0; i < TOTAL_SIZE; i++) slots[i] = i; return slots; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) { return slot < OUTPUT_START && stack.is(Items.GLASS_BOTTLE); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) { return slot >= OUTPUT_START; }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, inputItems); CompoundTag output = new CompoundTag(); ContainerHelper.saveAllItems(output, outputItems); tag.put("output", output); tag.putInt("bottle_ticks", bottleTicks); tag.putInt("honeycomb_ticks", honeycombTicks);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); ContainerHelper.loadAllItems(tag, inputItems); ContainerHelper.loadAllItems(tag.getCompound("output"), outputItems); bottleTicks = tag.contains("bottle_ticks") ? tag.getInt("bottle_ticks") : BOTTLE_INTERVAL; honeycombTicks = tag.contains("honeycomb_ticks") ? tag.getInt("honeycomb_ticks") : HONEYCOMB_INTERVAL;
    }
    public static void tick(Level level, BlockPos pos, BlockState state, HoneyFarmBlockEntity farm) {
        if (level.isClientSide) return;
        if (--farm.bottleTicks <= 0) { farm.bottleTicks = BOTTLE_INTERVAL; farm.convertBottle(); }
        if (--farm.honeycombTicks <= 0) { farm.honeycombTicks = HONEYCOMB_INTERVAL; farm.addHoneycomb(); }
    }
    private void convertBottle() {
        for (int slot = 0; slot < CHEST_SIZE; slot++) {
            ItemStack input = inputItems.get(slot);
            if (!input.is(Items.GLASS_BOTTLE) || !canInsertOutput(new ItemStack(Items.HONEY_BOTTLE))) continue;
            input.shrink(1); insertOutput(new ItemStack(Items.HONEY_BOTTLE)); setChanged(); return;
        }
    }
    private void addHoneycomb() {
        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB);
        if (canInsertOutput(honeycomb)) { insertOutput(honeycomb); setChanged(); }
    }
    private boolean canInsertOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (ItemStack stored : outputItems) {
            if (stored.isEmpty()) return true;
            if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) { remainder.shrink(Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount())); if (remainder.isEmpty()) return true; }
        }
        return remainder.isEmpty();
    }
    private void insertOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = 0; i < outputItems.size() && !remainder.isEmpty(); i++) { ItemStack stored = outputItems.get(i); if (stored.isEmpty()) { outputItems.set(i, remainder.copy()); remainder.setCount(0); } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) { int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount); } }
    }
}
