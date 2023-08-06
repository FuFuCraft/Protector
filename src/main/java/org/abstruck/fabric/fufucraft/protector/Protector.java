package org.abstruck.fabric.fufucraft.protector;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Astrack
 * @date 2023/7/22
 */
public class Protector implements ModInitializer {
    private Logger logger = LogUtils.getLogger();
    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.isSpectator()){
                return ActionResult.PASS;
            }
            BlockPos blockPos = hitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block instanceof CropBlock cropBlock){
                if (cropBlock.getAge(blockState) != cropBlock.getMaxAge()){
                    return ActionResult.PASS;
                }
                MinecraftServer server = world.getServer();
                if (server == null){
                    return ActionResult.PASS;
                }
                LootTable lootTable = server.getLootManager().getLootTable(cropBlock.getLootTableId());
                ObjectArrayList<ItemStack> itemStacks = lootTable.generateLoot(new LootContextParameterSet.Builder((ServerWorld) world)
                                .add(LootContextParameters.BLOCK_STATE,blockState)
                                .add(LootContextParameters.TOOL,player.getStackInHand(hand))
                                .add(LootContextParameters.ORIGIN,player.getVelocity())
                                .luck(player.getLuck())
                                .build(LootContextTypes.BLOCK));
                itemStacks.forEach(i -> {
                            try {
                                Method getSeedsItemMethod = cropBlock.getClass().getMethod("getSeedsItem");
                                getSeedsItemMethod.setAccessible(true);
                                ItemConvertible seed = (ItemConvertible) getSeedsItemMethod.invoke(cropBlock);
                                if (i.getItem().equals(seed.asItem())){
                                    i.decrement(1);
                                }
                                world.spawnEntity(new ItemEntity(world, blockPos.getX(),blockPos.getY(),blockPos.getZ(),i));
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                logger.warn(e.getMessage());
                            }
                        });
                world.setBlockState(blockPos,cropBlock.withAge(1),2);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
