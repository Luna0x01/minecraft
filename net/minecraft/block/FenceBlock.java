package net.minecraft.block;

import net.minecraft.class_3703;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceBlock extends class_3703 {
	private final VoxelShape[] field_18319;

	public FenceBlock(Block.Builder builder) {
		super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18265, Boolean.valueOf(false))
				.withProperty(field_18266, Boolean.valueOf(false))
				.withProperty(field_18267, Boolean.valueOf(false))
				.withProperty(field_18268, Boolean.valueOf(false))
				.withProperty(field_18269, Boolean.valueOf(false))
		);
		this.field_18319 = this.method_16656(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
	}

	@Override
	public VoxelShape method_16593(BlockState state, BlockView world, BlockPos pos) {
		return this.field_18319[this.method_16659(state)];
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}

	public boolean method_16676(BlockState blockState, BlockRenderLayer blockRenderLayer) {
		Block block = blockState.getBlock();
		boolean bl = blockRenderLayer == BlockRenderLayer.MIDDLE_POLE && (blockState.getMaterial() == this.material || block instanceof FenceGateBlock);
		return !method_14325(block) && blockRenderLayer == BlockRenderLayer.SOLID || bl;
	}

	public static boolean method_14325(Block block) {
		return Block.method_14309(block)
			|| block == Blocks.BARRIER
			|| block == Blocks.MELON_BLOCK
			|| block == Blocks.PUMPKIN
			|| block == Blocks.CARVED_PUMPKIN
			|| block == Blocks.JACK_O_LANTERN
			|| block == Blocks.FROSTED_ICE
			|| block == Blocks.TNT;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (!world.isClient) {
			return LeadItem.useLead(player, world, pos);
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			return itemStack.getItem() == Items.LEAD || itemStack.isEmpty();
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);
		return super.getPlacementState(context)
			.withProperty(field_18265, Boolean.valueOf(this.method_16676(blockState, blockState.getRenderLayer(blockView, blockPos2, Direction.SOUTH))))
			.withProperty(field_18266, Boolean.valueOf(this.method_16676(blockState2, blockState2.getRenderLayer(blockView, blockPos3, Direction.WEST))))
			.withProperty(field_18267, Boolean.valueOf(this.method_16676(blockState3, blockState3.getRenderLayer(blockView, blockPos4, Direction.NORTH))))
			.withProperty(field_18268, Boolean.valueOf(this.method_16676(blockState4, blockState4.getRenderLayer(blockView, blockPos5, Direction.EAST))))
			.withProperty(field_18269, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18269)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return direction.getAxis().getDirectionType() == Direction.DirectionType.HORIZONTAL
			? state.withProperty(
				(Property)field_18270.get(direction),
				Boolean.valueOf(this.method_16676(neighborState, neighborState.getRenderLayer(world, neighborPos, direction.getOpposite())))
			)
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18265, field_18266, field_18268, field_18267, field_18269);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction != Direction.UP && direction != Direction.DOWN ? BlockRenderLayer.MIDDLE_POLE : BlockRenderLayer.CENTER;
	}
}
