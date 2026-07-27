package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.SwampMobTowerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SwampMobTowerMenu extends ChestMenu {
    private final SwampMobTowerBlockEntity tower;
    public SwampMobTowerMenu(int id, Inventory inventory, SwampMobTowerBlockEntity tower) {
        super(ModMenuTypes.SWAMP_MOB_TOWER_MENU.get(), id, inventory, tower, 6);
        this.tower = tower;
    }
    public static SwampMobTowerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof SwampMobTowerBlockEntity tower) return new SwampMobTowerMenu(id, inventory, tower);
        throw new IllegalStateException("Swamp mob tower block entity is missing");
    }
    public boolean isSwampBiome() { return tower.isSwampBiome(); }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(tower.getLevel(), tower.getBlockPos()), player, ModBlocks.SWAMP_MOB_TOWER.get()); }
}
