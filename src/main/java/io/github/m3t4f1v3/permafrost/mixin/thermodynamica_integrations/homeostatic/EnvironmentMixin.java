package io.github.m3t4f1v3.permafrost.mixin.thermodynamica_integrations.homeostatic;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import homeostatic.common.block.BlockRadiation;
import homeostatic.common.temperature.Environment;
import io.github.m3t4f1v3.permafrost.Permafrost;
import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Environment.class)
public class EnvironmentMixin {

    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z"))
    private static boolean permafrost$skipLitCheck(
            BlockState state,
            Property<?> prop) {
        return false;
    }

    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean permafrost$skipBeehiveCheck(
            BlockState state,
            TagKey<Block> tag) {
        return false;
    }

    @Unique
    private static double permafrost$getTemperatureBasedRadiation(
            BlockState state,
            double distance,
            boolean obscured,
            double amount,
            int y,

            ServerLevel world,
            BlockPos blockpos) {

//        Permafrost.getLogger().debug("Calculating temperature-based radiation at {} in world {}, distance {}, obscured {}, amount {}, y {} for block {}", blockpos, world.dimension().location(), distance, obscured, amount, y, state);

        double temperature = ThermodynamicaIntegration.getCurrentTemperature(world, blockpos);
        double ambient = ThermodynamicaIntegration.getAmbientTemperature();

        double deltaT = Math.max(0.0, temperature - ambient);

        // dissipation = °C / tick / face
        double k = Math.max(0.0, ThermodynamicaIntegration.getDissipation(world, blockpos));

        // arbitrary constant to scale the radiation to a reasonable level, since the
        // raw values are very small
        k *= 300.0;

        double heatFlux = k * deltaT;

        double radiation = heatFlux * Math.max(0.0, amount);

        if (distance > 1.) {
            radiation /= distance * distance;
        }

        if (y > 0 && y < 5) {
            radiation = radiation * ((4 - y) * 0.25);
        }

        if (obscured) {
            radiation = radiation * 0.9;
        }

        return Math.max(0.0, radiation);
    }

    @WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lhomeostatic/common/block/BlockRadiationManager;getBlockRadiation(Lnet/minecraft/world/level/block/Block;)Lhomeostatic/common/block/BlockRadiation;"), remap = false)
    private static BlockRadiation permafrost$forceNonNullBlockRadiation(
            Block block,
            Operation<BlockRadiation> original) {
        BlockRadiation existing = original.call(block);

        if (existing != null) {
            return existing;
        }

        var blockId = ForgeRegistries.BLOCKS.getKey(block);
        if (blockId == null) {
            return null;
        }

        return new BlockRadiation(blockId, 1.0);
    }

    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Lhomeostatic/common/block/BlockRadiation;getBlockRadiation(Lnet/minecraft/world/level/block/state/BlockState;DZI)D"), remap = false)
    private static double permafrost$overrideRadiationNoAmount(
            BlockRadiation inst,
            BlockState state,
            double distance,
            boolean obscured,
            int y,

            @Local(argsOnly = true) ServerLevel world,
            @Local(name = "blockpos") BlockPos blockpos) {
        return permafrost$getTemperatureBasedRadiation(state, distance, obscured, 1.0, y, world, blockpos);
    }

    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Lhomeostatic/common/block/BlockRadiation;getBlockRadiation(Lnet/minecraft/world/level/block/state/BlockState;DZDI)D"), remap = false)
    private static double permafrost$overrideRadiationWithAmount(
            BlockRadiation inst,
            BlockState state,
            double distance,
            boolean obscured,
            double amount,
            int y,

            @Local(argsOnly = true) ServerLevel world,
            @Local(name = "blockpos") BlockPos blockpos) {

        return permafrost$getTemperatureBasedRadiation(state, distance, obscured, amount, y, world, blockpos);

    }
}