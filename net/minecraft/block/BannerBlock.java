package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class BannerBlock extends AbstractBannerBlock {
	public static final IntProperty ROTATION = Properties.ROTATION;
	private static final Map<DyeColor, Block> COLORED_BANNERS = Maps.newHashMap();
	private static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	public BannerBlock(DyeColor dyeColor, Block.Builder builder) {
		super(dyeColor, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(ROTATION, Integer.valueOf(0)));
		COLORED_BANNERS.put(dyeColor, this);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState()
			.withProperty(ROTATION, Integer.valueOf(MathHelper.floor((double)((180.0F + context.method_16147()) * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(ROTATION, Integer.valueOf(rotation.rotate((Integer)state.getProperty(ROTATION), 16)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withProperty(ROTATION, Integer.valueOf(mirror.mirror((Integer)state.getProperty(ROTATION), 16)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(ROTATION);
	}

	public static Block getForColor(DyeColor color) {
		return (Block)COLORED_BANNERS.getOrDefault(color, Blocks.WHITE_BANNER);
	}
}
