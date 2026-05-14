package io.github.m3t4f1v3.permafrost.mixin;

import java.util.List;

import org.patryk3211.powergrid.circuits.circuitboard.BakedCircuit;
import org.patryk3211.powergrid.circuits.circuitboard.CircuitBoardBlockEntity;
import org.patryk3211.powergrid.circuits.thermal.ThermalUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Mixin(BakedCircuit.class)
public class BakedCircuitMixin {

    @Shadow
    private CircuitBoardBlockEntity be;

    @Shadow
    private List<ThermalUnit> thermalUnits;

    @Unique
    private float totalThermalMass;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void permafrost$initialize(CircuitBoardBlockEntity be, CallbackInfo ci) {
        this.totalThermalMass = thermalUnits.stream().map((unit) -> {
            return ((ThermalUnitAccessor) unit).getThermalMass();
        }).reduce(0.0f, Float::sum);
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void permafrost$syncThermalBehaviour(CallbackInfo ci) {
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();
        if (level == null || level.isClientSide) {
            return;
        }

        for (ThermalUnit unit : thermalUnits) {
            unit.setTemperature(ThermodynamicaIntegration.getCurrentTemperature(level, pos)
                    * ((ThermalUnitAccessor) unit).getThermalMass() / totalThermalMass);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    private void permafrost$afterTick(CallbackInfo ci) {
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();
        if (level == null || level.isClientSide) {
            return;
        }
        for (ThermalUnit unit : thermalUnits) {
            ThermodynamicaIntegration.applyHeatToSimulation(level, pos,
                    unit.getTemperature() * ((ThermalUnitAccessor) unit).getThermalMass() / totalThermalMass);
        }
    }
}
