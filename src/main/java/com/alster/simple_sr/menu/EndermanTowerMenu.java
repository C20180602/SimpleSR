package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.EndermanTowerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EndermanTowerMenu extends ChestMenu {
    private final EndermanTowerBlockEntity tower;
    public EndermanTowerMenu(int id, Inventory inventory, EndermanTowerBlockEntity tower) {
        super(ModMenuTypes.ENDERMAN_TOWER_MENU.get(), id, inventory, tower, 6);
        this.tower = tower;
    }
    public static EndermanTowerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof EndermanTowerBlockEntity tower) return new EndermanTowerMenu(id, inventory, tower);
        throw new IllegalStateException("Enderman tower block entity is missing");
    }
    public boolean isInEndDimension() { return tower.isInEndDimension(); }
    @Override public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(tower.getLevel(), tower.getBlockPos()), player, ModBlocks.ENDERMAN_TOWER.get());
    }
}
