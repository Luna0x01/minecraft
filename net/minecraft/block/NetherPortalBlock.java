package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NetherPortalBlock extends TransparentBlock {
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);

	public NetherPortalBlock() {
		super(Material.PORTAL, false);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
		this.setTickRandomly(true);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.onScheduledTick(world, pos, state, rand);
		if (world.dimension.canPlayersSleep() && world.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < world.getGlobalDifficulty().getId()) {
			int i = pos.getY();
			BlockPos blockPos = pos;

			while (!World.isOpaque(world, blockPos) && blockPos.getY() > 0) {
				blockPos = blockPos.down();
			}

			if (i > 0 && !world.getBlockState(blockPos.up()).getBlock().isFullCube()) {
				Entity entity = SpawnEggItem.spawnEntity(world, 57, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.1, (double)blockPos.getZ() + 0.5);
				if (entity != null) {
					entity.netherPortalCooldown = entity.getDefaultNetherPortalCooldown();
				}
			}
		}
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		Direction.Axis axis = view.getBlockState(pos).get(AXIS);
		float f = 0.125F;
		float g = 0.125F;
		if (axis == Direction.Axis.X) {
			f = 0.5F;
		}

		if (axis == Direction.Axis.Z) {
			g = 0.5F;
		}

		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - g, 0.5F + f, 1.0F, 0.5F + g);
	}

	public static int getDataFromAxis(Direction.Axis axis) {
		if (axis == Direction.Axis.X) {
			return 1;
		} else {
			return axis == Direction.Axis.Z ? 2 : 0;
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean createPortalAt(World world, BlockPos pos) {
		NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(world, pos, Direction.Axis.X);
		if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
			areaHelper.createPortal();
			return true;
		} else {
			NetherPortalBlock.AreaHelper areaHelper2 = new NetherPortalBlock.AreaHelper(world, pos, Direction.Axis.Z);
			if (areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0) {
				areaHelper2.createPortal();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		Direction.Axis axis = state.get(AXIS);
		if (axis == Direction.Axis.X) {
			NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(world, pos, Direction.Axis.X);
			if (!areaHelper.isValid() || areaHelper.foundPortalBlocks < areaHelper.width * areaHelper.height) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		} else if (axis == Direction.Axis.Z) {
			NetherPortalBlock.AreaHelper areaHelper2 = new NetherPortalBlock.AreaHelper(world, pos, Direction.Axis.Z);
			if (!areaHelper2.isValid() || areaHelper2.foundPortalBlocks < areaHelper2.width * areaHelper2.height) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		Direction.Axis axis = null;
		BlockState blockState = view.getBlockState(pos);
		if (view.getBlockState(pos).getBlock() == this) {
			axis = blockState.get(AXIS);
			if (axis == null) {
				return false;
			}

			if (axis == Direction.Axis.Z && facing != Direction.EAST && facing != Direction.WEST) {
				return false;
			}

			if (axis == Direction.Axis.X && facing != Direction.SOUTH && facing != Direction.NORTH) {
				return false;
			}
		}

		boolean bl = view.getBlockState(pos.west()).getBlock() == this && view.getBlockState(pos.west(2)).getBlock() != this;
		boolean bl2 = view.getBlockState(pos.east()).getBlock() == this && view.getBlockState(pos.east(2)).getBlock() != this;
		boolean bl3 = view.getBlockState(pos.north()).getBlock() == this && view.getBlockState(pos.north(2)).getBlock() != this;
		boolean bl4 = view.getBlockState(pos.south()).getBlock() == this && view.getBlockState(pos.south(2)).getBlock() != this;
		boolean bl5 = bl || bl2 || axis == Direction.Axis.X;
		boolean bl6 = bl3 || bl4 || axis == Direction.Axis.Z;
		if (bl5 && facing == Direction.WEST) {
			return true;
		} else if (bl5 && facing == Direction.EAST) {
			return true;
		} else {
			return bl6 && facing == Direction.NORTH ? true : bl6 && facing == Direction.SOUTH;
		}
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity.vehicle == null && entity.rider == null) {
			entity.setInNetherPortal(pos);
		}
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (rand.nextInt(100) == 0) {
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
		}

		for (int i = 0; i < 4; i++) {
			double d = (double)((float)pos.getX() + rand.nextFloat());
			double e = (double)((float)pos.getY() + rand.nextFloat());
			double f = (double)((float)pos.getZ() + rand.nextFloat());
			double g = ((double)rand.nextFloat() - 0.5) * 0.5;
			double h = ((double)rand.nextFloat() - 0.5) * 0.5;
			double j = ((double)rand.nextFloat() - 0.5) * 0.5;
			int k = rand.nextInt(2) * 2 - 1;
			if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
				d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(rand.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(rand.nextFloat() * 2.0F * (float)k);
			}

			world.addParticle(ParticleType.NETHER_PORTAL, d, e, f, g, h, j);
		}
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return null;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(AXIS, (data & 3) == 2 ? Direction.Axis.Z : Direction.Axis.X);
	}

	@Override
	public int getData(BlockState state) {
		return getDataFromAxis(state.get(AXIS));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AXIS);
	}

	public BlockPattern.Result findPortal(World world, BlockPos pos) {
		Direction.Axis axis = Direction.Axis.Z;
		NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(world, pos, Direction.Axis.X);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.createLoadingCache(world, true);
		if (!areaHelper.isValid()) {
			axis = Direction.Axis.X;
			areaHelper = new NetherPortalBlock.AreaHelper(world, pos, Direction.Axis.Z);
		}

		if (!areaHelper.isValid()) {
			return new BlockPattern.Result(pos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = areaHelper.direction1.rotateYCounterclockwise();
			BlockPos blockPos = areaHelper.oppositeCorner.up(areaHelper.getHeight() - 1);

			for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
				BlockPattern.Result result = new BlockPattern.Result(
					direction.getAxisDirection() == axisDirection ? blockPos : blockPos.offset(areaHelper.direction1, areaHelper.getWidth() - 1),
					Direction.get(axisDirection, axis),
					Direction.UP,
					loadingCache,
					areaHelper.getWidth(),
					areaHelper.getHeight(),
					1
				);

				for (int k = 0; k < areaHelper.getWidth(); k++) {
					for (int l = 0; l < areaHelper.getHeight(); l++) {
						CachedBlockPosition cachedBlockPosition = result.translate(k, l, 1);
						if (cachedBlockPosition.getBlockState() != null && cachedBlockPosition.getBlockState().getBlock().getMaterial() != Material.AIR) {
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
				direction.getAxisDirection() == axisDirection2 ? blockPos : blockPos.offset(areaHelper.direction1, areaHelper.getWidth() - 1),
				Direction.get(axisDirection2, axis),
				Direction.UP,
				loadingCache,
				areaHelper.getWidth(),
				areaHelper.getHeight(),
				1
			);
		}
	}

	public static class AreaHelper {
		private final World world;
		private final Direction.Axis axis;
		private final Direction direction1;
		private final Direction direction2;
		private int foundPortalBlocks = 0;
		private BlockPos oppositeCorner;
		private int height;
		private int width;

		public AreaHelper(World world, BlockPos blockPos, Direction.Axis axis) {
			this.world = world;
			this.axis = axis;
			if (axis == Direction.Axis.X) {
				this.direction2 = Direction.EAST;
				this.direction1 = Direction.WEST;
			} else {
				this.direction2 = Direction.NORTH;
				this.direction1 = Direction.SOUTH;
			}

			BlockPos blockPos2 = blockPos;

			while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.canCreatePortal(world.getBlockState(blockPos.down()).getBlock())) {
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
				if (!this.canCreatePortal(this.world.getBlockState(blockPos).getBlock()) || this.world.getBlockState(blockPos.down()).getBlock() != Blocks.OBSIDIAN) {
					break;
				}
			}

			Block block = this.world.getBlockState(pos.offset(dir, i)).getBlock();
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
					Block block = this.world.getBlockState(blockPos).getBlock();
					if (!this.canCreatePortal(block)) {
						break label56;
					}

					if (block == Blocks.NETHER_PORTAL) {
						this.foundPortalBlocks++;
					}

					if (i == 0) {
						block = this.world.getBlockState(blockPos.offset(this.direction2)).getBlock();
						if (block != Blocks.OBSIDIAN) {
							break label56;
						}
					} else if (i == this.width - 1) {
						block = this.world.getBlockState(blockPos.offset(this.direction1)).getBlock();
						if (block != Blocks.OBSIDIAN) {
							break label56;
						}
					}
				}
			}

			for (int j = 0; j < this.width; j++) {
				if (this.world.getBlockState(this.oppositeCorner.offset(this.direction1, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
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

		protected boolean canCreatePortal(Block block) {
			return block.material == Material.AIR || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
		}

		public boolean isValid() {
			return this.oppositeCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void createPortal() {
			for (int i = 0; i < this.width; i++) {
				BlockPos blockPos = this.oppositeCorner.offset(this.direction1, i);

				for (int j = 0; j < this.height; j++) {
					this.world.setBlockState(blockPos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis), 2);
				}
			}
		}
	}
}
