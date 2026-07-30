package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.BambooFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BambooFarmMenu extends ChestMenu {
    private final BambooFarmBlockEntity farm;
    private int modeValue;
    public BambooFarmMenu(int id, Inventory inventory, BambooFarmBlockEntity farm) { super(ModMenuTypes.BAMBOO_FARM_MENU.get(), id, inventory, farm, 6); this.farm = farm; addDataSlot(new DataSlot() { @Override public int get() { return modeValue; } @Override public void set(int value) { modeValue = value; } }); modeValue = farm.isBambooMode() ? 1 : 0; }
    public static BambooFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) { BlockEntity be = inventory.player.level().getBlockEntity(data.readBlockPos()); if (be instanceof BambooFarmBlockEntity farm) return new BambooFarmMenu(id, inventory, farm); throw new IllegalStateException("Bamboo farm block entity is missing"); }
    public boolean isBambooMode() { return modeValue != 0; }
    @Override public boolean clickMenuButton(Player player, int id) { if (id != 0) return false; farm.setBambooMode(!farm.isBambooMode()); modeValue = farm.isBambooMode() ? 1 : 0; broadcastChanges(); return true; }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.BAMBOO_FARM.get()); }
}
