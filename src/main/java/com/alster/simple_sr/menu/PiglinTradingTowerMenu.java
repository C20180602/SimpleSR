package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.PiglinTradingTowerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PiglinTradingTowerMenu extends AbstractContainerMenu {
    private static final int INPUT_START = 0;
    private static final int OUTPUT_START = 54;
    private static final int PLAYER_START = 108;
    private final PiglinTradingTowerBlockEntity tower;

    public PiglinTradingTowerMenu(int id, Inventory inventory, PiglinTradingTowerBlockEntity tower) {
        super(ModMenuTypes.PIGLIN_TRADING_TOWER_MENU.get(), id); this.tower = tower;
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new InputSlot(tower, col + row * 9, 8 + col * 18, 18 + row * 18));
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new OutputSlot(tower, OUTPUT_START + col + row * 9, 190 + col * 18, 18 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, 9 + col + row * 9, 104 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 104 + col * 18, 198));
    }

    public static PiglinTradingTowerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof PiglinTradingTowerBlockEntity tower) return new PiglinTradingTowerMenu(id, inventory, tower);
        throw new IllegalStateException("Piglin trading tower block entity is missing");
    }
    public boolean isInNetherDimension() { return tower.isInNetherDimension(); }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(tower.getLevel(), tower.getBlockPos()), player, ModBlocks.PIGLIN_TRADING_TOWER.get()); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem(); ItemStack copy = original.copy();
        if (index >= OUTPUT_START && index < PLAYER_START) { if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY; }
        else if (index >= PLAYER_START) { if (!moveItemStackTo(original, INPUT_START, OUTPUT_START, false)) return ItemStack.EMPTY; }
        else if (index >= INPUT_START && index < OUTPUT_START) { if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY; }
        else return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged(); return copy;
    }
    private static class InputSlot extends Slot { InputSlot(PiglinTradingTowerBlockEntity tower, int index, int x, int y) { super(tower, index, x, y); } @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.GOLD_INGOT); } }
    private static class OutputSlot extends Slot { OutputSlot(PiglinTradingTowerBlockEntity tower, int index, int x, int y) { super(tower, index, x, y); } @Override public boolean mayPlace(ItemStack stack) { return false; } }
}
