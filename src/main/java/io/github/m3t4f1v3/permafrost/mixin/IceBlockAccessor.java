package io.github.m3t4f1v3.permafrost.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(IceBlock.class)
public interface IceBlockAccessor {
    @Invoker("melt")
    void invokeMelt(BlockState state, Level level, BlockPos pos);
}
