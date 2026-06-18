package io.github.m3t4f1v3.permafrost.mixin.homeostatic;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import homeostatic.common.block.BlockRadiation;
import homeostatic.common.block.BlockRadiationManager;
import homeostatic.common.temperature.EnvironmentInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import homeostatic.common.temperature.Environment;

@Mixin(Environment.class)
public class EnvironmentMixin {
    @WrapMethod(method = "get", remap = false)
    private static EnvironmentInfo inventoryRadiation(
            ServerLevel world, ServerPlayer sp, Operation<EnvironmentInfo> original
    ) {
        EnvironmentInfo env = original.call(world, sp);
        double radiation = env.getRadiation();

        for (ItemStack stack : sp.getInventory().items) {
            if (stack.getItem() instanceof BlockItem blockItem) {
                radiation += permafrost$getRadiationFromBlock(blockItem.getBlock()) * stack.getCount();
            } else if (stack.getItem() instanceof BucketItem bucketItem) {
                radiation += permafrost$getRadiationFromBlock(bucketItem.getFluid().defaultFluidState().createLegacyBlock().getBlock()) * stack.getCount();
            }
        }

        return new EnvironmentInfo(
                env.isUnderground(),
                env.isSheltered(),
                radiation, // skips the reduction
                env.getWaterVolume()
        );
    }

    @Unique
    private static double permafrost$getRadiationFromBlock(Block block) {
        BlockRadiation blockRadiation = BlockRadiationManager.getBlockRadiation(block);
        if (blockRadiation != null) {
            return blockRadiation.maxRadiation();
        }
        return 0.0;
    }
}
