package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.VillagerBreederBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class VillagerBreederMenu extends AbstractContainerMenu {
    private final VillagerBreederBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data = new ContainerData() {
        private int syncedProgress;

        @Override public int get(int index) {
            if (index != 0) return 0;
            return level.isClientSide ? syncedProgress : blockEntity.getProgressTicks();
        }

        @Override public void set(int index, int value) {
            if (index == 0) syncedProgress = value;
        }
        @Override public int getCount() { return 1; }
    };

    public VillagerBreederMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, getBlockEntity(inventory, extraData));
    }

    public VillagerBreederMenu(int containerId, Inventory inventory, VillagerBreederBlockEntity blockEntity) {
        super(ModMenuTypes.VILLAGER_BREEDER_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        addDataSlot(DataSlot.forContainer(data, 0));
        addSlot(new Slot(blockEntity, 0, 80, 35) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    public int getProgressTicks() {
        return data.get(0);
    }

    public int getRemainingSeconds() {
        return Math.max(0, (VillagerBreederBlockEntity.GENERATION_INTERVAL - getProgressTicks() + 19) / 20);
    }

    private static VillagerBreederBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf extraData) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof VillagerBreederBlockEntity breeder) return breeder;
        throw new IllegalStateException("Villager breeder block entity is missing");
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return empty;
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (index == 0) {
            if (!moveItemStackTo(original, 1, slots.size(), true)) return empty;
        } else {
            return empty;
        }
        if (original.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, original);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.VILLAGER_BREEDER.get());
    }
}
