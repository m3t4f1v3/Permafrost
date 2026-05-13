package io.github.m3t4f1v3.permafrost.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

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

    protected ThermalBehaviourMixin(SmartBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void permafrost$disableDissipation(SmartBlockEntity be, float thermalMass, float dissipationFactor,
            float overheatTemperature, CallbackInfo ci) {
        this.dissipationFactor = 0.0f;
    }

    @Inject(method = "setDissipationFactor", at = @At("HEAD"), remap = false, cancellable = true)
    private void permafrost$disableDissipationFactor(float dissipationFactor, CallbackInfo ci) {
        ci.cancel();
    }
}
