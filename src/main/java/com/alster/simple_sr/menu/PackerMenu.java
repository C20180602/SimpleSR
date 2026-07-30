package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.PackerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PackerMenu extends ChestMenu {
    private final PackerBlockEntity packer;
    private int materialShortage;
    private int topEmpty;

    public PackerMenu(int id, Inventory inventory, PackerBlockEntity packer) {
        super(ModMenuTypes.PACKER_MENU.get(), id, inventory, packer, 3);
        this.packer = packer;
        addDataSlot(new DataSlot() { @Override public int get() { return packer.getLevel() != null && !packer.getLevel().isClientSide ? (packer.materialsShortage() ? 1 : 0) : materialShortage; } @Override public void set(int value) { materialShortage = value; } });
        addDataSlot(new DataSlot() { @Override public int get() { return packer.getLevel() != null && !packer.getLevel().isClientSide ? (packer.hasExtractableTopItem() ? 0 : 1) : topEmpty; } @Override public void set(int value) { topEmpty = value; } });
        materialShortage = packer.materialsShortage() ? 1 : 0;
        topEmpty = packer.hasExtractableTopItem() ? 0 : 1;
    }

    public static PackerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof PackerBlockEntity packer) return new PackerMenu(id, inventory, packer);
        throw new IllegalStateException("Packer block entity is missing");
    }

    public boolean materialsShortage() { return materialShortage != 0; }
    public boolean topHasNoExtractableItem() { return topEmpty != 0; }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(packer.getLevel(), packer.getBlockPos()), player, ModBlocks.PACKER.get()); }
}
