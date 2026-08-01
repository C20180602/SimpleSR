package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.WitherSkeletonFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WitherSkeletonFarmMenu extends AbstractContainerMenu {
    private final WitherSkeletonFarmBlockEntity farm;
    private final ContainerData experienceData;
    private int syncedExperience;
    public WitherSkeletonFarmMenu(int id, Inventory inventory, WitherSkeletonFarmBlockEntity farm) {
        this(id, inventory, farm, farm.getSwordOwnerName());
    }
    private WitherSkeletonFarmMenu(int id, Inventory inventory, WitherSkeletonFarmBlockEntity farm, String ownerName) {
        super(ModMenuTypes.WITHER_SKELETON_FARM_MENU.get(), id); this.farm = farm;
        if (!ownerName.isEmpty() && farm.getSwordOwnerName().isEmpty()) farm.setClientSwordOwnerName(ownerName);
        experienceData = new ContainerData() {
            @Override public int get(int index) { int value = farm.getLevel() != null && !farm.getLevel().isClientSide ? farm.getStoredExperience() : syncedExperience; return index == 0 ? value & 0xFFFF : (value >>> 16) & 0xFFFF; }
            @Override public void set(int index, int value) { if (index == 0) syncedExperience = (syncedExperience & 0xFFFF0000) | (value & 0xFFFF); else syncedExperience = (syncedExperience & 0xFFFF) | ((value & 0xFFFF) << 16); }
            @Override public int getCount() { return 2; }
        };
        addDataSlot(DataSlot.forContainer(experienceData, 0)); addDataSlot(DataSlot.forContainer(experienceData, 1));
        for (int row = 0; row < 6; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(farm, column + row * 9, 8 + column * 18, 18 + row * 18));
        addSlot(new Slot(farm, WitherSkeletonFarmBlockEntity.SWORD_SLOT, 224, 18) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof SwordItem; }
            @Override public int getMaxStackSize() { return 1; }
            @Override public void set(ItemStack stack) { super.set(stack); if (stack.isEmpty()) { if (farm.getLevel().isClientSide) farm.setClientSwordOwnerName(""); else farm.clearSwordOwner(); } else if (farm.getLevel().isClientSide) { if (farm.getSwordOwnerName().isEmpty()) farm.setClientSwordOwnerName(inventory.player.getGameProfile().getName()); } else farm.setSwordOwner(inventory.player); }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 140 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 198));
    }
    public static WitherSkeletonFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buffer) { BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos()); if (blockEntity instanceof WitherSkeletonFarmBlockEntity farm) return new WitherSkeletonFarmMenu(id, inventory, farm, buffer.readUtf(16)); throw new IllegalStateException("Wither skeleton farm block entity is missing"); }
    public boolean isInsideNetherFortress() { return farm.isInsideNetherFortress(); }
    public int getStoredExperience() { return syncedExperience; }
    public String getSwordOwnerName() { return farm.getSwordOwnerName(); }
    @Override public boolean clickMenuButton(Player player, int buttonId) { if (buttonId == 0) { farm.startExperienceExtraction(player); return true; } if (buttonId == 1) { farm.stopExperienceExtraction(player); return true; } return false; }
    @Override public void removed(Player player) { farm.stopExperienceExtraction(player); super.removed(player); }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.WITHER_SKELETON_FARM.get()); }
    @Override public ItemStack quickMoveStack(Player player, int index) { Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY; ItemStack stack = slot.getItem(); ItemStack copy = stack.copy(); if (index < 55) { if (!moveItemStackTo(stack, 55, slots.size(), true)) return ItemStack.EMPTY; } else if (stack.getItem() instanceof SwordItem && !moveItemStackTo(stack, WitherSkeletonFarmBlockEntity.SWORD_SLOT, WitherSkeletonFarmBlockEntity.SWORD_SLOT + 1, false)) return ItemStack.EMPTY; else if (!moveItemStackTo(stack, 0, WitherSkeletonFarmBlockEntity.STORAGE_SLOTS, false)) return ItemStack.EMPTY; if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged(); return copy; }
}
