package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChorusPlantBlock extends Block {
	public static final BooleanProperty field_12626 = BooleanProperty.of("north");
	public static final BooleanProperty field_12627 = BooleanProperty.of("east");
	public static final BooleanProperty field_12628 = BooleanProperty.of("south");
	public static final BooleanProperty field_12629 = BooleanProperty.of("west");
	public static final BooleanProperty field_12630 = BooleanProperty.of("up");
	public static final BooleanProperty field_12631 = BooleanProperty.of("down");

	protected ChorusPlantBlock() {
		super(Material.PLANT);
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(field_12626, false)
				.with(field_12627, false)
				.with(field_12628, false)
				.with(field_12629, false)
				.with(field_12630, false)
				.with(field_12631, false)
		);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		Block block = view.getBlockState(pos.down()).getBlock();
		Block block2 = view.getBlockState(pos.up()).getBlock();
		Block block3 = view.getBlockState(pos.north()).getBlock();
		Block block4 = view.getBlockState(pos.east()).getBlock();
		Block block5 = view.getBlockState(pos.south()).getBlock();
		Block block6 = view.getBlockState(pos.west()).getBlock();
		return state.with(field_12631, block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)
			.with(field_12630, block2 == this || block2 == Blocks.CHORUS_FLOWER)
			.with(field_12626, block3 == this || block3 == Blocks.CHORUS_FLOWER)
			.with(field_12627, block4 == this || block4 == Blocks.CHORUS_FLOWER)
			.with(field_12628, block5 == this || block5 == Blocks.CHORUS_FLOWER)
			.with(field_12629, block6 == this || block6 == Blocks.CHORUS_FLOWER);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = state.getBlockState(view, pos);
		float f = 0.1875F;
		float g = state.get(field_12629) ? 0.0F : 0.1875F;
		float h = state.get(field_12631) ? 0.0F : 0.1875F;
		float i = state.get(field_12626) ? 0.0F : 0.1875F;
		float j = state.get(field_12627) ? 1.0F : 0.8125F;
		float k = state.get(field_12630) ? 1.0F : 0.8125F;
		float l = state.get(field_12628) ? 1.0F : 0.8125F;
		return new Box((double)g, (double)h, (double)i, (double)j, (double)k, (double)l);
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		state = state.getBlockState(world, pos);
		float f = 0.1875F;
		float g = 0.8125F;
		appendCollisionBoxes(pos, entityBox, boxes, new Box(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125));
		if ((Boolean)state.get(field_12629)) {
			appendCollisionBoxes(pos, entityBox, boxes, new Box(0.0, 0.1875, 0.1875, 0.1875, 0.8125, 0.8125));
		}

		if ((Boolean)state.get(field_12627)) {
			appendCollisionBoxes(pos, entityBox, boxes, new Box(0.8125, 0.1875, 0.1875, 1.0, 0.8125, 0.8125));
		}

		if ((Boolean)state.get(field_12630)) {
			appendCollisionBoxes(pos, entityBox, boxes, new Box(0.1875, 0.8125, 0.1875, 0.8125, 1.0, 0.8125));
		}

		if ((Boolean)state.get(field_12631)) {
			appendCollisionBoxes(pos, entityBox, boxes, new Box(0.1875, 0.0, 0.1875, 0.8125, 0.1875, 0.8125));
		}

		if ((Boolean)state.get(field_12626)) {
			appendCollisionBoxes(pos, entityBox, boxes, new Box(0.1875, 0.1875, 0.0, 0.8125, 0.8125, 0.1875));
		}

		if ((Boolean)state.get(field_12628)) {
			appendCollisionBoxes(pos, entityBox, boxes, new Box(0.1875, 0.1875, 0.8125, 0.8125, 0.8125, 1.0));
		}
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!this.method_11590(world, pos)) {
			world.removeBlock(pos, true);
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.CHORUS_FRUIT;
	}

	@Override
	public int getDropCount(Random rand) {
		return rand.nextInt(2);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) ? this.method_11590(world, pos) : false;
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!this.method_11590(world, blockPos)) {
			world.createAndScheduleBlockTick(blockPos, this, 1);
		}
	}

	public boolean method_11590(World world, BlockPos blockPos) {
		boolean bl = world.isAir(blockPos.up());
		boolean bl2 = world.isAir(blockPos.down());

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			Block block = world.getBlockState(blockPos2).getBlock();
			if (block == this) {
				if (!bl && !bl2) {
					return false;
				}

				Block block2 = world.getBlockState(blockPos2.down()).getBlock();
				if (block2 == this || block2 == Blocks.END_STONE) {
					return true;
				}
			}
		}

		Block block3 = world.getBlockState(blockPos.down()).getBlock();
		return block3 == this || block3 == Blocks.END_STONE;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		Block block = view.getBlockState(pos.offset(direction)).getBlock();
		return block != this && block != Blocks.CHORUS_FLOWER && (direction != Direction.DOWN || block != Blocks.END_STONE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, field_12626, field_12627, field_12628, field_12629, field_12630, field_12631);
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return false;
	}
}
