package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.RaidTowerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RaidTowerMenu extends AbstractContainerMenu {
    private final RaidTowerBlockEntity tower;
    private final ContainerData data;
    public RaidTowerMenu(int id, Inventory inventory, RaidTowerBlockEntity tower) {
        super(ModMenuTypes.RAID_TOWER_MENU.get(), id); this.tower = tower;
        data = new ContainerData() {
            @Override public int get(int index) { return switch (index) { case 0 -> tower.getCurrentWave(); case 1 -> tower.getTotalWaves(); case 2 -> tower.getRemainingRaiders(); case 3 -> tower.isRaidActive() ? 1 : 0; case 4 -> tower.getWaveTotalRaiders(); default -> 0; }; }
            @Override public void set(int index, int value) { }
            @Override public int getCount() { return 5; }
        };
        for (int i = 0; i < 5; i++) addDataSlot(DataSlot.forContainer(data, i));
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(tower, col + row * 9, 8 + col * 18, 18 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, 9 + col + row * 9, 8 + col * 18, 148 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 206));
    }
    public static RaidTowerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) { BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos()); if (blockEntity instanceof RaidTowerBlockEntity tower) return new RaidTowerMenu(id, inventory, tower); throw new IllegalStateException("Raid tower block entity is missing"); }
    public boolean isOceanBiome() { return tower.isOceanBiome(); }
    public boolean isRaidActive() { return data.get(3) != 0; }
    public int currentWave() { return data.get(0); }
    public int totalWaves() { return data.get(1); }
    public int remainingRaiders() { return data.get(2); }
    public int waveTotalRaiders() { return data.get(4); }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(tower.getLevel(), tower.getBlockPos()), player, ModBlocks.RAID_TOWER.get()); }
    @Override public ItemStack quickMoveStack(Player player, int index) { Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY; ItemStack stack = slot.getItem(); ItemStack copy = stack.copy(); if (index < 54) { if (!moveItemStackTo(stack, 54, slots.size(), true)) return ItemStack.EMPTY; } else if (!moveItemStackTo(stack, 0, 54, false)) return ItemStack.EMPTY; if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged(); return copy; }
}
