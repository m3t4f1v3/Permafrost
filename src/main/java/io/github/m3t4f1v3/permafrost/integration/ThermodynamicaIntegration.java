package io.github.m3t4f1v3.permafrost.integration;

import java.util.function.Consumer;

import com.Tribulla.thermodynamica.api.HeatAPI;
import com.Tribulla.thermodynamica.api.TemperatureChangeEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;


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

    public static double getAmbientTemperature() {
        return HeatAPI.get().getAmbientTemperature();
    }

    public static double getDissipation(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        return HeatAPI.get().getThermalProperties(blockId).getDissipationRate();
    }
}