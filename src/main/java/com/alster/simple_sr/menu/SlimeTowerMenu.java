package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.SlimeTowerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.DataSlot;

public class SlimeTowerMenu extends ChestMenu {
    private final SlimeTowerBlockEntity tower;
    private int seedLow;
    private int seedHigh;
    public SlimeTowerMenu(int id, Inventory inventory, SlimeTowerBlockEntity tower) {
        super(ModMenuTypes.SLIME_TOWER_MENU.get(), id, inventory, tower, 6);
        this.tower = tower;
        if (tower.getLevel() instanceof ServerLevel level) {
            long seed = level.getSeed();
            seedLow = (int) seed;
            seedHigh = (int) (seed >>> 32);
        }
        addDataSlot(new DataSlot() { @Override public int get() { return seedLow; } @Override public void set(int value) { seedLow = value; } });
        addDataSlot(new DataSlot() { @Override public int get() { return seedHigh; } @Override public void set(int value) { seedHigh = value; } });
    }
    public static SlimeTowerMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity be = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (be instanceof SlimeTowerBlockEntity tower) return new SlimeTowerMenu(id, inventory, tower);
        throw new IllegalStateException("Slime tower block entity is missing");
    }
    public boolean isSlimeChunk() { return tower.isSlimeChunk(); }
    public net.minecraft.core.BlockPos towerPos() { return tower.getBlockPos(); }
    public long worldSeed() { return Integer.toUnsignedLong(seedLow) | ((long) seedHigh << 32); }
    @Override public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(tower.getLevel(), tower.getBlockPos()), player, ModBlocks.SLIME_TOWER.get());
    }
}
