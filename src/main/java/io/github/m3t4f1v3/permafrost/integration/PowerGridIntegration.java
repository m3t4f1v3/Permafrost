package io.github.m3t4f1v3.permafrost.integration;

import io.github.m3t4f1v3.permafrost.Permafrost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

public class PowerGridIntegration {

    private static final int HEATER_MELT_RADIUS = 4;

    private PowerGridIntegration() {
    }

    public static void meltNearbyPermafrost(Level level, BlockPos heaterPos) {
        if (level.isClientSide) {
            return;
        }

        for (BlockPos checkPos : BlockPos.betweenClosed(
                heaterPos.offset(-HEATER_MELT_RADIUS, -HEATER_MELT_RADIUS, -HEATER_MELT_RADIUS),
                heaterPos.offset(HEATER_MELT_RADIUS, HEATER_MELT_RADIUS, HEATER_MELT_RADIUS))) {
            BlockState state = level.getBlockState(checkPos);
            Permafrost.melt(heaterPos, state, level, checkPos);
        }
    }
}
