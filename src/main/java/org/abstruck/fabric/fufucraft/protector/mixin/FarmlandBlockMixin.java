package org.abstruck.fabric.fufucraft.protector.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Astrack
 * @date 2023/7/22
 */
@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {
    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onLandedUpon",at = @At("HEAD"), cancellable = true)
    private void cannotSetToDirt(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci){
        super.onLandedUpon(world, state, pos, entity, fallDistance);
        ci.cancel();
    }
}
