package org.abstruck.fabric.fufucraft.protector.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Astrack
 * @date 2023/7/22
 */
@Mixin(EntityExplosionBehavior.class)
public abstract class EntityExplosionBehaviorMixin {
    @Inject(method = "canDestroyBlock",at = @At("HEAD"), cancellable = true)
    private void cannotDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
