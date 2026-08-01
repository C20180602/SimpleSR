package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.ZombifiedPiglinTowerBlockEntity;
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

public class ZombifiedPiglinTowerMenu extends AbstractContainerMenu {
    private final ZombifiedPiglinTowerBlockEntity tower;
    private final ContainerData experienceData;
    private int syncedExperience;

    public ZombifiedPiglinTowerMenu(int id, Inventory inventory, ZombifiedPiglinTowerBlockEntity tower) {
        this(id, inventory, tower, tower.getSwordOwnerName());
    }
    private ZombifiedPiglinTowerMenu(int id, Inventory inventory, ZombifiedPiglinTowerBlockEntity tower, String ownerName) {
        super(ModMenuTypes.ZOMBIFIED_PIGLIN_TOWER_MENU.get(), id);
        this.tower = tower;
        if (!ownerName.isEmpty() && tower.getSwordOwnerName().isEmpty()) tower.setClientSwordOwnerName(ownerName);
        experienceData = new ContainerData() {
            @Override public int get(int index) {
                int value = tower.getLevel() != null && !tower.getLevel().isClientSide ? tower.getStoredExperience() : syncedExperience;
                return index == 0 ? value & 0xFFFF : (value >>> 16) & 0xFFFF;
            }
            @Override public void set(int index, int value) {
                if (index == 0) syncedExperience = (syncedExperience & 0xFFFF0000) | (value & 0xFFFF);
                else syncedExperience = (syncedExperience & 0xFFFF) | ((value & 0xFFFF) << 16);
            }
            @Override public int getCount() { return 2; }
        };
        addDataSlot(DataSlot.forContainer(experienceData, 0));
        addDataSlot(DataSlot.forContainer(experienceData, 1));
        for (int row = 0; row < 6; row++) for (int column = 0; column < 9; column++) {
            addSlot(new Slot(tower, column + row * 9, 8 + column * 18, 18 + row * 18));
        }
        addSlot(new Slot(tower, ZombifiedPiglinTowerBlockEntity.SWORD_SLOT, 224, 18) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof SwordItem; }
            @Override public int getMaxStackSize() { return 1; }
            @Override public void set(ItemStack stack) {
                super.set(stack);
                if (stack.isEmpty()) { if (tower.getLevel().isClientSide) tower.setClientSwordOwnerName(""); else tower.clearSwordOwner(); }
                else if (tower.getLevel().isClientSide) { if (tower.getSwordOwnerName().isEmpty()) tower.setClientSwordOwnerName(inventory.player.getGameProfile().getName()); }
                else tower.setSwordOwner(inventory.player);
            }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 140 + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 198));
    }

    public static ZombifiedPiglinTowerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof ZombifiedPiglinTowerBlockEntity tower) return new ZombifiedPiglinTowerMenu(id, inventory, tower, data.readUtf(16));
        throw new IllegalStateException("Zombified piglin tower block entity is missing");
    }

    public boolean isInNetherDimension() { return tower.isInNetherDimension(); }
    public int getStoredExperience() { return syncedExperience; }
    public String getSwordOwnerName() { return tower.getSwordOwnerName(); }

    @Override public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == 0) { tower.startExperienceExtraction(player); return true; }
        if (buttonId == 1) { tower.stopExperienceExtraction(player); return true; }
        return false;
    }
    @Override public void removed(Player player) { tower.stopExperienceExtraction(player); super.removed(player); }

    @Override public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(tower.getLevel(), tower.getBlockPos()), player, ModBlocks.ZOMBIFIED_PIGLIN_TOWER.get());
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        ItemStack source = ItemStack.EMPTY;
        Slot sourceSlot = slots.get(index);
        if (sourceSlot != null && sourceSlot.hasItem()) {
            ItemStack stack = sourceSlot.getItem();
            source = stack.copy();
            if (index < ZombifiedPiglinTowerBlockEntity.STORAGE_SLOTS + 1) {
                if (!moveItemStackTo(stack, 55, slots.size(), true)) return ItemStack.EMPTY;
            } else if (stack.getItem() instanceof SwordItem && !moveItemStackTo(stack, ZombifiedPiglinTowerBlockEntity.SWORD_SLOT, ZombifiedPiglinTowerBlockEntity.SWORD_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, 0, ZombifiedPiglinTowerBlockEntity.STORAGE_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) sourceSlot.set(ItemStack.EMPTY); else sourceSlot.setChanged();
        }
        return source;
    }
}
