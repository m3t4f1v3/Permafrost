package io.github.m3t4f1v3.permafrost.mixin.thermodynamica_integrations.powergrid;

import com.llamalad7.mixinextras.inheritance.ApplyToSubclasses;
import com.llamalad7.mixinextras.inheritance.InheritedShadow;
import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.kinetics.base.ElectricKineticBlockEntity;
import org.patryk3211.powergrid.kinetics.motor.ConstantSpeedMotorBlockEntity;
import org.patryk3211.powergrid.kinetics.motor.ElectricMotorBlockEntity;
import org.patryk3211.powergrid.kinetics.servo.ServoBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = { ElectricKineticBlockEntity.class, ElectricBlockEntity.class,
        ConstantSpeedMotorBlockEntity.class, ElectricMotorBlockEntity.class, ServoBlockEntity.class })
@ApplyToSubclasses
public abstract class HeatedPowerGridBlockEntityMixin extends BlockEntity {
    @InheritedShadow
    @Nullable
    private ThermalBehaviour thermalBehaviour;

    private HeatedPowerGridBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void permafrost$initialize(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        Level level = getLevel();
        if (level == null || level.isClientSide || thermalBehaviour == null) {
            return;
        }
        float thermodynamicaTemp = ThermodynamicaIntegration.getCurrentTemperature(level, pos);
        thermalBehaviour.setTemperature(thermodynamicaTemp);
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void permafrost$syncThermalBehaviour(CallbackInfo ci) {
        Level level = getLevel();
        BlockPos pos = getBlockPos();
        if (level == null || level.isClientSide || thermalBehaviour == null) {
            return;
        }
        thermalBehaviour.setTemperature(ThermodynamicaIntegration.getCurrentTemperature(level, pos));
    }

    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    private void permafrost$afterTick(CallbackInfo ci) {
        Level level = getLevel();
        BlockPos pos = getBlockPos();
        if (level == null || level.isClientSide || thermalBehaviour == null) {
            return;
        }
        ThermodynamicaIntegration.applyHeatToSimulation(level, pos, thermalBehaviour.getTemperature());
    }
}
