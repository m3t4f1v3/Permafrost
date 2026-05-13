package io.github.m3t4f1v3.permafrost.integration;

import com.Tribulla.thermodynamica.api.HeatAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class ThermodynamicaIntegration {

    private ThermodynamicaIntegration() {
    }

    public static float getCurrentTemperature(Level level, BlockPos pos) {
        return (float) HeatAPI.get().getSimulatedCelsius(level, pos).orElseGet(() -> HeatAPI.get().getVisualCelsius(level, pos));
    }

    public static float getVisualTemperature(Level level, BlockPos pos) {
        return getCurrentTemperature(level, pos);
    }

    public static float getMeltEternalTemperature() {
        return 3000F;
    }

    public static void applyHeatToSimulation(Level level, BlockPos pos, double celsius) {
        HeatAPI.get().setBlockEnergyOutput(level, pos, celsius);
    }
}