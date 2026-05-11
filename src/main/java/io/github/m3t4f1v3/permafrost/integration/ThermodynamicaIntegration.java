package io.github.m3t4f1v3.permafrost.integration;

import com.Tribulla.thermodynamica.Thermodynamica;
import com.Tribulla.thermodynamica.api.HeatAPI;
import com.Tribulla.thermodynamica.api.HeatTier;
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

    public static float getTierCelsius(HeatTier tier) {
        return (float) HeatAPI.get().getTierCelsius(tier);
    }

    public static float getMeltEternalTemperature() {
        return getTierCelsius(HeatTier.POS5) + 0.25F;
    }

    public static void applyHeatToSimulation(Level level, BlockPos pos, double celsius) {
        HeatAPI.get().setBlockEnergyOutput(level, pos, celsius);
    }
}