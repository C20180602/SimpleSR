package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.SolidifierBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

public class SolidifierMenu extends AbstractContainerMenu {
    private static final int INPUT_START = 0, OUTPUT_START = 54, PLAYER_START = 108;
    private final SolidifierBlockEntity solidifier;
    public SolidifierMenu(int id, Inventory inventory, SolidifierBlockEntity solidifier) {
        super(ModMenuTypes.SOLIDIFIER_MENU.get(), id); this.solidifier = solidifier;
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new InputSlot(solidifier, col + row * 9, 8 + col * 18, 18 + row * 18));
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new OutputSlot(solidifier, OUTPUT_START + col + row * 9, 190 + col * 18, 18 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, 9 + col + row * 9, 104 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 104 + col * 18, 198));
    }
    public static SolidifierMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) { BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos()); if (blockEntity instanceof SolidifierBlockEntity solidifier) return new SolidifierMenu(id, inventory, solidifier); throw new IllegalStateException("Solidifier block entity is missing"); }
    public boolean isEndDimension() { return solidifier.getLevel() != null && solidifier.getLevel().dimension() == Level.END; }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(solidifier.getLevel(), solidifier.getBlockPos()), player, ModBlocks.SOLIDIFIER.get()); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY; ItemStack original = slot.getItem(), copy = original.copy();
        if (index >= OUTPUT_START && index < PLAYER_START) { if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY; }
        else if (index >= PLAYER_START) { if (!moveItemStackTo(original, INPUT_START, OUTPUT_START, false)) return ItemStack.EMPTY; }
        else if (index < OUTPUT_START) { if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY; }
        else return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged(); return copy;
    }
    private static class InputSlot extends Slot { InputSlot(SolidifierBlockEntity c, int i, int x, int y) { super(c, i, x, y); } @Override public boolean mayPlace(ItemStack stack) { return ((SolidifierBlockEntity)container).canPlaceItem(getSlotIndex(), stack); } }
    private static class OutputSlot extends Slot { OutputSlot(SolidifierBlockEntity c, int i, int x, int y) { super(c, i, x, y); } @Override public boolean mayPlace(ItemStack stack) { return false; } }
}
