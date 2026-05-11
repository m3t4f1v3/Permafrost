package io.github.m3t4f1v3.permafrost.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThermalBehaviour.class)
public abstract class ThermalBehaviourMixin extends BlockEntityBehaviour {
    @Shadow
    private float dissipationFactor;

    @Shadow
    private float temperature;

    protected ThermalBehaviourMixin(SmartBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void permafrost$disableDissipation(SmartBlockEntity be, float thermalMass, float dissipationFactor,
            float overheatTemperature, CallbackInfo ci) {
        this.dissipationFactor = 0.0f;
    }

    // @Inject(method = "tick", at = @At("HEAD"), remap = false)
    // private void permafrost$applyExternalHeat(CallbackInfo ci) {
    //     Level level = blockEntity.getLevel();
    //     BlockPos pos = blockEntity.getBlockPos();
    //     if (level == null || level.isClientSide) {
    //         return;
    //     }
    //     float thermodynamicaTemp = ThermodynamicaIntegration.getCurrentTemperature(level, pos);
    //     temperature = thermodynamicaTemp;
    // }

    // @Inject(method = "tick", at = @At("TAIL"), remap = false)
    // private void permafrost$pushHeatToExternal(CallbackInfo ci) {
    //     Level level = blockEntity.getLevel();
    //     BlockPos pos = blockEntity.getBlockPos();
    //     if (level == null || level.isClientSide) {
    //         return;
    //     }
    //     ThermodynamicaIntegration.applyHeatToSimulation(level, pos, temperature);
    // }
}
