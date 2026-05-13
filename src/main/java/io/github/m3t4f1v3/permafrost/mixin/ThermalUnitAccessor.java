package io.github.m3t4f1v3.permafrost.mixin;

import org.patryk3211.powergrid.circuits.thermal.ThermalUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThermalUnit.class)
public interface ThermalUnitAccessor {
    @Accessor("thermalMass")
    float getThermalMass();
}
