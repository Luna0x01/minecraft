package net.minecraft;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RenderBlockView;

public class class_3559 extends BlockItem {
	protected final Block field_17384;

	public class_3559(Block block, Block block2, Item.Settings settings) {
		super(block, settings);
		this.field_17384 = block2;
	}

	@Nullable
	@Override
	protected BlockState method_16016(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.field_17384.getPlacementState(itemPlacementContext);
		BlockState blockState2 = null;
		RenderBlockView renderBlockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();

		for (Direction direction : itemPlacementContext.method_16021()) {
			if (direction != Direction.UP) {
				BlockState blockState3 = direction == Direction.DOWN ? this.getBlock().getPlacementState(itemPlacementContext) : blockState;
				if (blockState3 != null && blockState3.canPlaceAt(renderBlockView, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && renderBlockView.method_16371(blockState2, blockPos) ? blockState2 : null;
	}

	@Override
	public void method_16015(Map<Block, Item> map, Item item) {
		super.method_16015(map, item);
		map.put(this.field_17384, item);
	}
}
