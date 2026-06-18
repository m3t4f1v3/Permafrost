package io.github.m3t4f1v3.permafrost.mixin.thermodynamica_integrations.prototype_pain;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.adinvas.casualties_cubed.compat.TempCompat;
import net.adinvas.casualties_cubed.compat.TempCompat.BiomeTemperatureEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Mixin(TempCompat.class)
public class TempCompatMixin {
    @Inject(method = "getForPosition", at = @At("HEAD"), remap = false, cancellable = true)
    private static void permafrost$overrideTemperature(Level level, BlockPos pos, CallbackInfoReturnable<BiomeTemperatureEntry> cir) {
        float temperature = ThermodynamicaIntegration.getCurrentTemperature(level, pos);
        BiomeTemperatureEntry entry = new BiomeTemperatureEntry();
        entry.temperature = temperature;
        // dgaf about serene seasons, so no need to set others

        cir.setReturnValue(entry);
    }
}
