package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class NetherPortalBlock extends Block {
	public static final EnumProperty<Direction.Axis> field_18409 = Properties.HORIZONTAL_AXIS;
	protected static final VoxelShape field_18410 = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape field_18411 = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public NetherPortalBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18409, Direction.Axis.X));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((Direction.Axis)state.getProperty(field_18409)) {
			case Z:
				return field_18411;
			case X:
			default:
				return field_18410;
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.dimension.canPlayersSleep() && world.getGameRules().getBoolean("doMobSpawning") && random.nextInt(2000) < world.method_16346().getId()) {
			int i = pos.getY();
			BlockPos blockPos = pos;

			while (!world.getBlockState(blockPos).method_16913() && blockPos.getY() > 0) {
				blockPos = blockPos.down();
			}

			if (i > 0 && !world.getBlockState(blockPos.up()).method_16907()) {
				Entity entity = EntityType.ZOMBIE_PIGMAN.method_15620(world, null, null, null, blockPos.up(), false, false);
				if (entity != null) {
					entity.netherPortalCooldown = entity.getDefaultNetherPortalCooldown();
				}
			}
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	public boolean method_16704(IWorld iWorld, BlockPos blockPos) {
		NetherPortalBlock.AreaHelper areaHelper = this.method_16705(iWorld, blockPos);
		if (areaHelper != null) {
			areaHelper.createPortal();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public NetherPortalBlock.AreaHelper method_16705(IWorld iWorld, BlockPos blockPos) {
		NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.X);
		if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
			return areaHelper;
		} else {
			NetherPortalBlock.AreaHelper areaHelper2 = new NetherPortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.Z);
			return areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0 ? areaHelper2 : null;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = state.getProperty(field_18409);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && neighborState.getBlock() != this && !new NetherPortalBlock.AreaHelper(world, pos, axis2).method_16707()
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.hasMount() && !entity.hasPassengers() && entity.canUsePortals()) {
			entity.setInNetherPortal(pos);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(100) == 0) {
			world.playSound(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				Sounds.BLOCK_PORTAL_AMBIENT,
				SoundCategory.BLOCKS,
				0.5F,
				random.nextFloat() * 0.4F + 0.8F,
				false
			);
		}

		for (int i = 0; i < 4; i++) {
			double d = (double)((float)pos.getX() + random.nextFloat());
			double e = (double)((float)pos.getY() + random.nextFloat());
			double f = (double)((float)pos.getZ() + random.nextFloat());
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = ((double)random.nextFloat() - 0.5) * 0.5;
			double j = ((double)random.nextFloat() - 0.5) * 0.5;
			int k = random.nextInt(2) * 2 - 1;
			if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
				d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			world.method_16343(class_4342.field_21361, d, e, f, g, h, j);
		}
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis)state.getProperty(field_18409)) {
					case Z:
						return state.withProperty(field_18409, Direction.Axis.X);
					case X:
						return state.withProperty(field_18409, Direction.Axis.Z);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18409);
	}

	public BlockPattern.Result method_8848(IWorld iWorld, BlockPos blockPos) {
		Direction.Axis axis = Direction.Axis.Z;
		NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.X);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.method_16939(iWorld, true);
		if (!areaHelper.isValid()) {
			axis = Direction.Axis.X;
			areaHelper = new NetherPortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.Z);
		}

		if (!areaHelper.isValid()) {
			return new BlockPattern.Result(blockPos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = areaHelper.direction1.rotateYCounterclockwise();
			BlockPos blockPos2 = areaHelper.oppositeCorner.up(areaHelper.getHeight() - 1);

			for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
				BlockPattern.Result result = new BlockPattern.Result(
					direction.getAxisDirection() == axisDirection ? blockPos2 : blockPos2.offset(areaHelper.direction1, areaHelper.getWidth() - 1),
					Direction.get(axisDirection, axis),
					Direction.UP,
					loadingCache,
					areaHelper.getWidth(),
					areaHelper.getHeight(),
					1
				);

				for (int i = 0; i < areaHelper.getWidth(); i++) {
					for (int j = 0; j < areaHelper.getHeight(); j++) {
						CachedBlockPosition cachedBlockPosition = result.translate(i, j, 1);
						if (!cachedBlockPosition.getBlockState().isAir()) {
							is[axisDirection.ordinal()]++;
						}
					}
				}
			}

			Direction.AxisDirection axisDirection2 = Direction.AxisDirection.POSITIVE;

			for (Direction.AxisDirection axisDirection3 : Direction.AxisDirection.values()) {
				if (is[axisDirection3.ordinal()] < is[axisDirection2.ordinal()]) {
					axisDirection2 = axisDirection3;
				}
			}

			return new BlockPattern.Result(
				direction.getAxisDirection() == axisDirection2 ? blockPos2 : blockPos2.offset(areaHelper.direction1, areaHelper.getWidth() - 1),
				Direction.get(axisDirection2, axis),
				Direction.UP,
				loadingCache,
				areaHelper.getWidth(),
				areaHelper.getHeight(),
				1
			);
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	public static class AreaHelper {
		private final IWorld field_18412;
		private final Direction.Axis axis;
		private final Direction direction1;
		private final Direction direction2;
		private int foundPortalBlocks;
		private BlockPos oppositeCorner;
		private int height;
		private int width;

		public AreaHelper(IWorld iWorld, BlockPos blockPos, Direction.Axis axis) {
			this.field_18412 = iWorld;
			this.axis = axis;
			if (axis == Direction.Axis.X) {
				this.direction2 = Direction.EAST;
				this.direction1 = Direction.WEST;
			} else {
				this.direction2 = Direction.NORTH;
				this.direction1 = Direction.SOUTH;
			}

			BlockPos blockPos2 = blockPos;

			while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.method_16706(iWorld.getBlockState(blockPos.down()))) {
				blockPos = blockPos.down();
			}

			int i = this.distanceToPortalEdge(blockPos, this.direction2) - 1;
			if (i >= 0) {
				this.oppositeCorner = blockPos.offset(this.direction2, i);
				this.width = this.distanceToPortalEdge(this.oppositeCorner, this.direction1);
				if (this.width < 2 || this.width > 21) {
					this.oppositeCorner = null;
					this.width = 0;
				}
			}

			if (this.oppositeCorner != null) {
				this.height = this.findHeight();
			}
		}

		protected int distanceToPortalEdge(BlockPos pos, Direction dir) {
			int i;
			for (i = 0; i < 22; i++) {
				BlockPos blockPos = pos.offset(dir, i);
				if (!this.method_16706(this.field_18412.getBlockState(blockPos)) || this.field_18412.getBlockState(blockPos.down()).getBlock() != Blocks.OBSIDIAN) {
					break;
				}
			}

			Block block = this.field_18412.getBlockState(pos.offset(dir, i)).getBlock();
			return block == Blocks.OBSIDIAN ? i : 0;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		protected int findHeight() {
			label56:
			for (this.height = 0; this.height < 21; this.height++) {
				for (int i = 0; i < this.width; i++) {
					BlockPos blockPos = this.oppositeCorner.offset(this.direction1, i).up(this.height);
					BlockState blockState = this.field_18412.getBlockState(blockPos);
					if (!this.method_16706(blockState)) {
						break label56;
					}

					Block block = blockState.getBlock();
					if (block == Blocks.NETHER_PORTAL) {
						this.foundPortalBlocks++;
					}

					if (i == 0) {
						block = this.field_18412.getBlockState(blockPos.offset(this.direction2)).getBlock();
						if (block != Blocks.OBSIDIAN) {
							break label56;
						}
					} else if (i == this.width - 1) {
						block = this.field_18412.getBlockState(blockPos.offset(this.direction1)).getBlock();
						if (block != Blocks.OBSIDIAN) {
							break label56;
						}
					}
				}
			}

			for (int j = 0; j < this.width; j++) {
				if (this.field_18412.getBlockState(this.oppositeCorner.offset(this.direction1, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
					this.height = 0;
					break;
				}
			}

			if (this.height <= 21 && this.height >= 3) {
				return this.height;
			} else {
				this.oppositeCorner = null;
				this.width = 0;
				this.height = 0;
				return 0;
			}
		}

		protected boolean method_16706(BlockState blockState) {
			Block block = blockState.getBlock();
			return blockState.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
		}

		public boolean isValid() {
			return this.oppositeCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void createPortal() {
			for (int i = 0; i < this.width; i++) {
				BlockPos blockPos = this.oppositeCorner.offset(this.direction1, i);

				for (int j = 0; j < this.height; j++) {
					this.field_18412.setBlockState(blockPos.up(j), Blocks.NETHER_PORTAL.getDefaultState().withProperty(NetherPortalBlock.field_18409, this.axis), 18);
				}
			}
		}

		private boolean method_16708() {
			return this.foundPortalBlocks >= this.width * this.height;
		}

		public boolean method_16707() {
			return this.isValid() && this.method_16708();
		}
	}
}
