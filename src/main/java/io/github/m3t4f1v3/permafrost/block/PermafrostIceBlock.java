package io.github.m3t4f1v3.permafrost.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Unbreakable Permafrost Ice Block
 * Can only be melted by:
 * - Power Grid Heating Coils (electric heating)
 * - Ars Nouveau Melt Eternal spell (magical heating)
 */
public class PermafrostIceBlock extends IceBlock {

    public PermafrostIceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        // Prevent natural melting in warm biomes
        // Does nothing - stays frozen forever unless affected by heating
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        // Make it unbreakable by normal means
        return -1.0f;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        // Entities cannot destroy this
        return false;
    }

    /**
     * Called when affected by Melt Eternal spell
     */
    public void meltBySpell(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            melt(null, level, pos);
        }
    }
}
