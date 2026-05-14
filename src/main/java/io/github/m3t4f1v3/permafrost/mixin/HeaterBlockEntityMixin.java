package io.github.m3t4f1v3.permafrost.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.heater.HeaterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HeaterBlockEntity.class)
public abstract class HeaterBlockEntityMixin extends ElectricBlockEntity {
    protected HeaterBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyConstant(method = "specifyThermalBehaviour", constant = @Constant(floatValue = 600.0F), remap = false)
    private float modifyMaxTemperature(float original) {
        return 4500F;
    }
}