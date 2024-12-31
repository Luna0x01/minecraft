package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilBlock extends FallingBlock {
	private static final Logger field_12557 = LogManager.getLogger();
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final VoxelShape X_STEP_SHAPE = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
	private static final VoxelShape X_STEM_SHAPE = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
	private static final VoxelShape X_FACE_SHAPE = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
	private static final VoxelShape Z_STEP_SHAPE = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
	private static final VoxelShape Z_STEM_SHAPE = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
	private static final VoxelShape Z_FACE_SHAPE = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
	private static final VoxelShape X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, VoxelShapes.union(X_STEP_SHAPE, VoxelShapes.union(X_STEM_SHAPE, X_FACE_SHAPE)));
	private static final VoxelShape Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, VoxelShapes.union(Z_STEP_SHAPE, VoxelShapes.union(Z_STEM_SHAPE, Z_FACE_SHAPE)));

	public AnvilBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH));
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16145().rotateYClockwise());
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (!world.isClient) {
			player.openHandledScreen(new AnvilBlock.AnvilNameableHandler(world, pos));
		}

		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = state.getProperty(FACING);
		return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
		entity.setHurtingEntities(true);
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
		world.syncGlobalEvent(1031, pos, 0);
	}

	@Override
	public void method_13705(World world, BlockPos blockPos) {
		world.syncGlobalEvent(1029, blockPos, 0);
	}

	@Nullable
	public static BlockState getLandingState(BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.ANVIL) {
			return Blocks.CHIPPED_ANVIL.getDefaultState().withProperty(FACING, state.getProperty(FACING));
		} else {
			return block == Blocks.CHIPPED_ANVIL ? Blocks.DAMAGED_ANVIL.getDefaultState().withProperty(FACING, state.getProperty(FACING)) : null;
		}
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING);
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}

	public static class AnvilNameableHandler implements NamedScreenHandlerFactory {
		private final World world;
		private final BlockPos pos;

		public AnvilNameableHandler(World world, BlockPos blockPos) {
			this.world = world;
			this.pos = blockPos;
		}

		@Override
		public Text method_15540() {
			return new TranslatableText(Blocks.ANVIL.getTranslationKey());
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Nullable
		@Override
		public Text method_15541() {
			return null;
		}

		@Override
		public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
			return new AnvilScreenHandler(inventory, this.world, this.pos, player);
		}

		@Override
		public String getId() {
			return "minecraft:anvil";
		}
	}
}
