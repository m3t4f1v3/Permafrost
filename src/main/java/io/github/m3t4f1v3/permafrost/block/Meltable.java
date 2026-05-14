package io.github.m3t4f1v3.permafrost.block;

import org.slf4j.Logger;

import io.github.m3t4f1v3.permafrost.Permafrost;
import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface Meltable {
    public float getMeltThreshold(BlockState state);

    public default boolean shouldMelt(BlockState state, Level level, BlockPos pos) {
        Logger logger = Permafrost.getLogger();
        logger.debug("Checking if block at {} should melt. Current temperature: {}C, melt threshold: {}C",
                pos, ThermodynamicaIntegration.getCurrentTemperature(level, pos), getMeltThreshold(state));
        float temperature = ThermodynamicaIntegration.getCurrentTemperature(level, pos);
        return temperature >= getMeltThreshold(state);
    }
}
