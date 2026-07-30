package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.ShulkerFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ShulkerFarmMenu extends ChestMenu {
    private final ShulkerFarmBlockEntity farm;
    public ShulkerFarmMenu(int id, Inventory inventory, ShulkerFarmBlockEntity farm) { super(ModMenuTypes.SHULKER_FARM_MENU.get(), id, inventory, farm, 6); this.farm = farm; }
    public static ShulkerFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) { BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos()); if (blockEntity instanceof ShulkerFarmBlockEntity farm) return new ShulkerFarmMenu(id, inventory, farm); throw new IllegalStateException("Shulker farm block entity is missing"); }
    public boolean isNearEndCity() { return farm.isNearEndCity(); }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.SHULKER_FARM.get()); }
}
