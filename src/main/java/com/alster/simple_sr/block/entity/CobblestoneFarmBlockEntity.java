package com.alster.simple_sr.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CobblestoneFarmBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 54;
    private static final int DROP_INTERVAL = 20;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private long ticksUntilDrop = DROP_INTERVAL;

    public CobblestoneFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.COBBLESTONE_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.cobblestone_farm"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return ChestMenu.sixRows(id, inventory, this); }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putLong("ticks_until_drop", ticksUntilDrop);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); ContainerHelper.loadAllItems(tag, items); ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getLong("ticks_until_drop") : DROP_INTERVAL;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CobblestoneFarmBlockEntity farm) {
        if (level.isClientSide) return;
        if (--farm.ticksUntilDrop <= 0) { farm.ticksUntilDrop = DROP_INTERVAL; farm.generateCobblestone(); }
    }

    private void generateCobblestone() {
        NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < SLOT_COUNT; i++) backup.set(i, items.get(i).copy());
        if (!insertStack(new ItemStack(Items.COBBLESTONE, 12)).isEmpty()) {
            for (int i = 0; i < SLOT_COUNT; i++) items.set(i, backup.get(i));
            return;
        }
        setChanged();
    }

    private ItemStack insertStack(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) {
            ItemStack stored = items.get(slot);
            if (stored.isEmpty()) { items.set(slot, remainder.copy()); remainder.setCount(0); }
            else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount);
            }
        }
        return remainder;
    }
}
