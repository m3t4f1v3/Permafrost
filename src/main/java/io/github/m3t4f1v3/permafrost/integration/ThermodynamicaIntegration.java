package io.github.m3t4f1v3.permafrost.integration;

import java.util.function.Consumer;

import com.Tribulla.thermodynamica.api.HeatAPI;
import com.Tribulla.thermodynamica.api.TemperatureChangeEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class ThermodynamicaIntegration {

    private ThermodynamicaIntegration() {
    }

    public static float getCurrentTemperature(Level level, BlockPos pos) {
        return (float) HeatAPI.get().getVisualCelsius(level, pos);
    }

    public static void applyHeatToSimulation(Level level, BlockPos pos, double celsius) {
        HeatAPI.get().setTransientTemperature(level, pos, celsius);
    }

    public static void registerTemperatureChangeListener(Consumer<TemperatureChangeEvent> listener) {
        HeatAPI.get().onTemperatureChange(listener);
    }
}