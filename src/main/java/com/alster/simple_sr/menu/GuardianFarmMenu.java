package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.GuardianFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GuardianFarmMenu extends AbstractContainerMenu {
    private final GuardianFarmBlockEntity farm;
    private final ContainerData data;
    public GuardianFarmMenu(int id, Inventory inventory, GuardianFarmBlockEntity farm) {
        super(ModMenuTypes.GUARDIAN_FARM_MENU.get(), id); this.farm = farm;
        data = new SimpleContainerData(1);
        data.set(0, farm.isInsideOceanMonument() ? 1 : 0);
        addDataSlot(DataSlot.forContainer(data, 0));
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(farm, col + row * 9, 8 + col * 18, 18 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, 9 + col + row * 9, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 198));
    }
    public static GuardianFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) { BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos()); if (blockEntity instanceof GuardianFarmBlockEntity farm) return new GuardianFarmMenu(id, inventory, farm); throw new IllegalStateException("Guardian farm block entity is missing"); }
    public boolean isInsideOceanMonument() { return data.get(0) != 0; }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.GUARDIAN_FARM.get()); }
    @Override public ItemStack quickMoveStack(Player player, int index) { Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY; ItemStack stack = slot.getItem(); ItemStack copy = stack.copy(); if (index < 54) { if (!moveItemStackTo(stack, 54, slots.size(), true)) return ItemStack.EMPTY; } else if (!moveItemStackTo(stack, 0, 54, false)) return ItemStack.EMPTY; if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged(); return copy; }
}
