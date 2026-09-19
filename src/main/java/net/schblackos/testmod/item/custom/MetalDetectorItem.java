package net.schblackos.testmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MetalDetectorItem extends Item {

    public MetalDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos positionClicked = context.getClickedPos();
        Player player = context.getPlayer();

        if (!level.isClientSide()){
            //Important to be on the server side

            boolean foundBlock = false;

            for(int i = 0; i <= positionClicked.getY() + 64; i++) {
                BlockState blockState = level.getBlockState(positionClicked.below(i));

                if (isValuableBlock(blockState)){
                    outPutValuableCoordinates(positionClicked.below(i), player, blockState.getBlock());

                    foundBlock = true;

                    //Damages the item on use
                    context.getItemInHand().hurtAndBreak(1 , player, context.getHand());

                    //Play sound
                    level.playSound(null,
                            positionClicked,
                            SoundEvents.BONE_BLOCK_BREAK,
                            SoundSource.BLOCKS, 1.5f, 1f);

                    //Particles
                    spawnFoundParticles(level, positionClicked, blockState);



                    break;
                }
                
            }

            if(!foundBlock){
                outPutValuablesFound(player);
            }
        }



        return InteractionResult.SUCCESS;
    }

    private void spawnFoundParticles(Level level, BlockPos positionClicked, BlockState blockState) {
        for(int i = 0; i < 20; i++) {
            ServerLevel serverLevel = (ServerLevel) level;

            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    positionClicked.getX() + 0.5d, positionClicked.getY() + 1, positionClicked.getZ() + 0.5d, 1,
                    Math.cos(i * 18) * 0.15d, 0.15d, Math.sin(i * 18) * 0.15d, 0.1);
        }
    }

    private void outPutValuablesFound(Player player) {

        player.sendSystemMessage(Component.translatable("item.testmod.metal_detector.no_valuables"));

    }

    private void outPutValuableCoordinates(BlockPos position, Player player, Block block) {

        player.sendSystemMessage(Component.literal("Valuables Found at: ")
                .append(block.getName())
                .append(Component.literal(" at ("
                        + position.getX()+ ", "
                        + position.getY()+ ", "
                        + position.getZ()+ ")")));

    }

    private boolean isValuableBlock(BlockState blockState) {
        return  blockState.is(Blocks.IRON_ORE) ||
                blockState.is(Blocks.DEEPSLATE_IRON_ORE)||
                blockState.is(Blocks.GOLD_ORE)||
                blockState.is(Blocks.DEEPSLATE_GOLD_ORE);

    }
}
