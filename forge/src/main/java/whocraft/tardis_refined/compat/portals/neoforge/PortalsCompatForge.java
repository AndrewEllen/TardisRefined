package whocraft.tardis_refined.compat.portals.neoforge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import qouteall.q_misc_util.forge.networking.Dim_Sync;
import qouteall.q_misc_util.forge.networking.Message;
import whocraft.tardis_refined.api.event.TardisCommonEvents;
import whocraft.tardis_refined.common.capability.tardis.TardisLevelOperator;
import whocraft.tardis_refined.compat.portals.ImmersivePortals;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

public class PortalsCompatForge {

    public static void init() {
        EVENT_BUS.register(new PortalsCompatForge());
        TardisCommonEvents.DOOR_OPENED_EVENT.register(PortalsCompatForge::onDoorOpened);
    }

    public static void syncDimensionIds(ServerPlayer player) {
        if (player == null) {
            return;
        }
        Message.sendToPlayer(new Dim_Sync(), player);
    }

    private static void syncDimensionIds(ServerLevel level) {
        if (level == null) {
            return;
        }
        Dim_Sync sync = new Dim_Sync();
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            Message.sendToPlayer(sync, player);
        }
    }

    private static void onDoorOpened(TardisLevelOperator operator) {
        if (operator == null) {
            return;
        }
        if (operator.getLevel() instanceof ServerLevel serverLevel) {
            syncDimensionIds(serverLevel);
        }
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent breakEvent) {
        BlockEntity blockEntity = breakEvent.getLevel().getExistingBlockEntity(breakEvent.getPos());
        ImmersivePortals.onDoorRemoved(breakEvent.getPlayer().level(), breakEvent.getPlayer(), breakEvent.getPos(), breakEvent.getState(), blockEntity);
    }


    @SubscribeEvent
    public void onServerShutdown(ServerStoppedEvent serverStoppedEvent) {
        ImmersivePortals.clearPortalCache();
    }

}
