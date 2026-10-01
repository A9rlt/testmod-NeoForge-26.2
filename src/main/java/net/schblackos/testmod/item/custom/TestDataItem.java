package net.schblackos.testmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.schblackos.testmod.datacomponents.ModDataComponents;

import java.util.function.Consumer;

public class TestDataItem extends Item {
    public TestDataItem(Properties properties) {
        super(properties);
    }

    //Removes pre saved coordinate data
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        if (player.getItemInHand(hand).has(ModDataComponents.COORDINATES)){
            player.getItemInHand(hand).remove(ModDataComponents.COORDINATES);
        }

        if (player.getItemInHand(hand).has(ModDataComponents.ORES)){
            player.getItemInHand(hand).remove(ModDataComponents.ORES);
        }


        return InteractionResult.SUCCESS;
    }

    //When isFoil method returns true, the enchantment glint appears on the item
    @Override
    public boolean isFoil(ItemStack itemStack) {
        return itemStack.has(ModDataComponents.COORDINATES);
    }

    //Gets the coordinates data to the item
    //This can be saved as a custom class to get more data from the collected block like blockstate and the block itself
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context,
                                TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {

        if(itemStack.has(ModDataComponents.COORDINATES)){
            BlockPos position = itemStack.get(ModDataComponents.COORDINATES);
            String foundPositionString = "(" +
                    position.getX() + ", " +
                    position.getY() + ", " +
                    position.getZ() + ")" ;


            builder.accept(Component.literal(foundPositionString));
        }

        if(itemStack.has(ModDataComponents.ORES)){
            BlockState blockState = itemStack.get(ModDataComponents.ORES);
            String foundBlockString = blockState.getBlock().getName().getString();


            builder.accept(Component.literal(foundBlockString));
        }
    }
}
