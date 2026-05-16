package io.github.m3t4f1v3.permafrost.mixin.thermodynamica_integrations.powergrid;

import io.github.m3t4f1v3.permafrost.Permafrost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.heater.HeaterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeaterBlockEntity.class)
public abstract class NonThermoHeaterBlockEntityMixin extends ElectricBlockEntity {

    protected NonThermoHeaterBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    void permafrost$meltNearby(CallbackInfo ci) {
        Level level = getLevel();
        BlockPos entPos = getBlockPos();
        if (level == null || level.isClientSide) {
            return;
        }

        for (BlockPos pos : BlockPos.withinManhattan(entPos, 2, 2, 2)) {
            float distance = pos.distManhattan(entPos);
            float temperature = thermalBehaviour.getTemperature();
            if (distance > 0.) {
                temperature /= distance;
            }

            Permafrost.melt(level, pos, temperature);
        }
    }
}
