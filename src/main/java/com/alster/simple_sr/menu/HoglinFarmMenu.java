package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.HoglinFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HoglinFarmMenu extends AbstractContainerMenu {
    private static final int PLAYER_START = 54;
    private final HoglinFarmBlockEntity farm;

    public HoglinFarmMenu(int id, Inventory inventory, HoglinFarmBlockEntity farm) {
        super(ModMenuTypes.HOGLIN_FARM_MENU.get(), id);
        this.farm = farm;
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(farm, col + row * 9, 8 + col * 18, 18 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, 9 + col + row * 9, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 198));
    }

    public static HoglinFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof HoglinFarmBlockEntity farm) return new HoglinFarmMenu(id, inventory, farm);
        throw new IllegalStateException("Hoglin farm block entity is missing");
    }

    public boolean isCrimsonForest() { return farm.isCrimsonForest(); }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.HOGLIN_FARM.get()); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem(); ItemStack copy = original.copy();
        if (index < PLAYER_START) { if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(original, 0, PLAYER_START, false)) return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
}
