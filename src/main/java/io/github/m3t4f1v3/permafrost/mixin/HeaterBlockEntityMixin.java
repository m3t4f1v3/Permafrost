package io.github.m3t4f1v3.permafrost.mixin;

import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.heater.HeaterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeaterBlockEntity.class)
public abstract class HeaterBlockEntityMixin extends ElectricBlockEntity {
    protected HeaterBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyConstant(method = "specifyThermalBehaviour", constant = @Constant(floatValue = 600.0F), remap = false)
    private float modifyMaxTemperature(float original) {
        return ThermodynamicaIntegration.getMeltEternalTemperature() * 1.5f;
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void permafrost$applyExternalHeat(CallbackInfo ci) {
        Level level = getLevel();
        BlockPos pos = getBlockPos();
        if (level == null || level.isClientSide) {
            return;
        }
        float thermodynamicaTemp = ThermodynamicaIntegration.getCurrentTemperature(level, pos);
        thermalBehaviour.setTemperature(thermodynamicaTemp);
    }

    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    private void permafrost$pushHeatToExternal(CallbackInfo ci) {
        Level level = getLevel();
        BlockPos pos = getBlockPos();
        if (level == null || level.isClientSide) {
            return;
        }
        double currentTemp = thermalBehaviour.getTemperature();
        ThermodynamicaIntegration.applyHeatToSimulation(level, pos, currentTemp);
    }
}