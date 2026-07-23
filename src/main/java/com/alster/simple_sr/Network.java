package com.alster.simple_sr;

import com.alster.simple_sr.block.entity.VillagerTradingHallBlockEntity;
import com.alster.simple_sr.menu.VillagerTradingHallMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class Network {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            SimpleSR.id("main"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static int id;
    private Network() {}
    public static void register() {
        CHANNEL.registerMessage(id++, RenameVillager.class, RenameVillager::encode, RenameVillager::decode, RenameVillager::handle);
        CHANNEL.registerMessage(id++, SyncTradingHall.class, SyncTradingHall::encode, SyncTradingHall::decode, SyncTradingHall::handle);
        CHANNEL.registerMessage(id++, SubmitVillager.class, SubmitVillager::encode, SubmitVillager::decode, SubmitVillager::handle);
    }
    public static void rename(int index, String name) { CHANNEL.sendToServer(new RenameVillager(index, name)); }
    public static void submitVillager(BlockPos pos) { CHANNEL.sendToServer(new SubmitVillager(pos)); }
    public static void syncTradingHall(ServerPlayer player, VillagerTradingHallBlockEntity hall) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncTradingHall(hall.getBlockPos(), hall.saveForItem()));
    }
    public record RenameVillager(int index, String name) {
        static void encode(RenameVillager msg, FriendlyByteBuf buf) { buf.writeVarInt(msg.index); buf.writeUtf(msg.name, 32); }
        static RenameVillager decode(FriendlyByteBuf buf) { return new RenameVillager(buf.readVarInt(), buf.readUtf(32)); }
        static void handle(RenameVillager msg, Supplier<NetworkEvent.Context> supplier) { NetworkEvent.Context ctx=supplier.get(); ctx.enqueueWork(() -> { ServerPlayer p=ctx.getSender(); if(p!=null && p.containerMenu instanceof VillagerTradingHallMenu m) { m.rename(msg.index,msg.name); Network.syncTradingHall(p, m.getHall()); } }); ctx.setPacketHandled(true); }
    }
    public record SyncTradingHall(BlockPos pos, CompoundTag tag) {
        static void encode(SyncTradingHall msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.pos);
            buf.writeNbt(msg.tag);
        }
        static SyncTradingHall decode(FriendlyByteBuf buf) {
            return new SyncTradingHall(buf.readBlockPos(), buf.readNbt());
        }
        static void handle(SyncTradingHall msg, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().level != null) {
                    if (Minecraft.getInstance().player != null
                            && Minecraft.getInstance().player.containerMenu instanceof VillagerTradingHallMenu menu
                            && menu.getHall().getBlockPos().equals(msg.pos)) {
                        menu.applyHallSync(msg.tag);
                    } else {
                        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(msg.pos);
                        if (blockEntity instanceof VillagerTradingHallBlockEntity hall) hall.load(msg.tag);
                    }
                }
            }));
            ctx.setPacketHandled(true);
        }
    }
    public record SubmitVillager(BlockPos pos) {
        static void encode(SubmitVillager msg, FriendlyByteBuf buf) { buf.writeBlockPos(msg.pos); }
        static SubmitVillager decode(FriendlyByteBuf buf) { return new SubmitVillager(buf.readBlockPos()); }
        static void handle(SubmitVillager msg, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player != null && player.level().getBlockEntity(msg.pos) instanceof VillagerTradingHallBlockEntity hall
                        && player.distanceToSqr(msg.pos.getX() + 0.5D, msg.pos.getY() + 0.5D, msg.pos.getZ() + 0.5D) <= 64.0D) {
                    if (player.containerMenu instanceof VillagerTradingHallMenu menu
                            && menu.getHall().getBlockPos().equals(msg.pos)) {
                        menu.submitFromNetwork(player);
                    } else if (hall.submit()) {
                        player.connection.send(ClientboundBlockEntityDataPacket.create(hall));
                        syncTradingHall(player, hall);
                    }
                }
            });
            ctx.setPacketHandled(true);
        }
    }
}
