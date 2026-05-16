package io.github.m3t4f1v3.permafrost.integration;

import java.util.function.Consumer;

import com.Tribulla.thermodynamica.api.HeatAPI;
import com.Tribulla.thermodynamica.api.TemperatureChangeEvent;

import io.github.m3t4f1v3.permafrost.Permafrost;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;


public final class ThermodynamicaIntegration {

    private ThermodynamicaIntegration() {
    }

    public static float getCurrentTemperature(Level level, BlockPos pos) {
        return (float) HeatAPI.get().getVisualCelsius(level, pos);
    }

    public static void applyHeatToSimulation(Level level, BlockPos pos, double celsius) {
        HeatAPI.get().setTransientTemperature(level, pos, celsius);
    }

    public static void registerTemperatureChangeListener() {
        HeatAPI.get().onTemperatureChange(ThermodynamicaIntegration::onThermodynamicaTemperatureUpdate);
    }

    public static double getAmbientTemperature() {
        return HeatAPI.get().getAmbientTemperature();
    }

    public static double getDissipation(@NotNull Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        return HeatAPI.get().getThermalProperties(blockId).getDissipationRate();
    }


    private static void onThermodynamicaTemperatureUpdate(@NotNull TemperatureChangeEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        BlockState state = level.getBlockState(event.getPos());
        Permafrost.melt(level, state, (float) event.getNewCelsius(), pos);
    }
}