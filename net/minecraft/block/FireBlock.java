package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;

public class FireBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
	public static final BooleanProperty UP = ConnectedPlantBlock.UP;
	private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.field_11033)
		.collect(Util.toMap());
	private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap();
	private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap();

	protected FireBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(AGE, Integer.valueOf(0))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(UP, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return this.canPlaceAt(blockState, iWorld, blockPos)
			? this.getStateForPosition(iWorld, blockPos).with(AGE, blockState.get(AGE))
			: Blocks.field_10124.getDefaultState();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getStateForPosition(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos());
	}

	public BlockState getStateForPosition(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11036)) {
			BlockState blockState2 = this.getDefaultState();

			for (Direction direction : Direction.values()) {
				BooleanProperty booleanProperty = (BooleanProperty)DIRECTION_PROPERTIES.get(direction);
				if (booleanProperty != null) {
					blockState2 = blockState2.with(booleanProperty, Boolean.valueOf(this.isFlammable(blockView.getBlockState(blockPos.offset(direction)))));
				}
			}

			return blockState2;
		} else {
			return this.getDefaultState();
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return worldView.getBlockState(blockPos2).isSideSolidFullSquare(worldView, blockPos2, Direction.field_11036)
			|| this.areBlocksAroundFlammable(worldView, blockPos);
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return 30;
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (serverWorld.getGameRules().getBoolean(GameRules.field_19387)) {
			if (!blockState.canPlaceAt(serverWorld, blockPos)) {
				serverWorld.removeBlock(blockPos, false);
			}

			Block block = serverWorld.getBlockState(blockPos.down()).getBlock();
			boolean bl = serverWorld.dimension instanceof TheEndDimension && block == Blocks.field_9987 || block == Blocks.field_10515 || block == Blocks.field_10092;
			int i = (Integer)blockState.get(AGE);
			if (!bl && serverWorld.isRaining() && this.isRainingAround(serverWorld, blockPos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
				serverWorld.removeBlock(blockPos, false);
			} else {
				int j = Math.min(15, i + random.nextInt(3) / 2);
				if (i != j) {
					blockState = blockState.with(AGE, Integer.valueOf(j));
					serverWorld.setBlockState(blockPos, blockState, 4);
				}

				if (!bl) {
					serverWorld.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(serverWorld) + random.nextInt(10));
					if (!this.areBlocksAroundFlammable(serverWorld, blockPos)) {
						BlockPos blockPos2 = blockPos.down();
						if (!serverWorld.getBlockState(blockPos2).isSideSolidFullSquare(serverWorld, blockPos2, Direction.field_11036) || i > 3) {
							serverWorld.removeBlock(blockPos, false);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(serverWorld.getBlockState(blockPos.down()))) {
						serverWorld.removeBlock(blockPos, false);
						return;
					}
				}

				boolean bl2 = serverWorld.hasHighHumidity(blockPos);
				int k = bl2 ? -50 : 0;
				this.trySpreadingFire(serverWorld, blockPos.east(), 300 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.west(), 300 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.down(), 250 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.up(), 250 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.north(), 300 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.south(), 300 + k, random, i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 4; n++) {
							if (l != 0 || n != 0 || m != 0) {
								int o = 100;
								if (n > 1) {
									o += (n - 1) * 100;
								}

								mutable.set(blockPos).setOffset(l, n, m);
								int p = this.getBurnChance(serverWorld, mutable);
								if (p > 0) {
									int q = (p + 40 + serverWorld.getDifficulty().getId() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!serverWorld.isRaining() || !this.isRainingAround(serverWorld, mutable))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										serverWorld.setBlockState(mutable, this.getStateForPosition(serverWorld, mutable).with(AGE, Integer.valueOf(r)), 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isRainingAround(World world, BlockPos blockPos) {
		return world.hasRain(blockPos)
			|| world.hasRain(blockPos.west())
			|| world.hasRain(blockPos.east())
			|| world.hasRain(blockPos.north())
			|| world.hasRain(blockPos.south());
	}

	private int getSpreadChance(BlockState blockState) {
		return blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? 0 : this.spreadChances.getInt(blockState.getBlock());
	}

	private int getBurnChance(BlockState blockState) {
		return blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? 0 : this.burnChances.getInt(blockState.getBlock());
	}

	private void trySpreadingFire(World world, BlockPos blockPos, int i, Random random, int j) {
		int k = this.getSpreadChance(world.getBlockState(blockPos));
		if (random.nextInt(i) < k) {
			BlockState blockState = world.getBlockState(blockPos);
			if (random.nextInt(j + 10) < 5 && !world.hasRain(blockPos)) {
				int l = Math.min(j + random.nextInt(5) / 4, 15);
				world.setBlockState(blockPos, this.getStateForPosition(world, blockPos).with(AGE, Integer.valueOf(l)), 3);
			} else {
				world.removeBlock(blockPos, false);
			}

			Block block = blockState.getBlock();
			if (block instanceof TntBlock) {
				TntBlock.primeTnt(world, blockPos);
			}
		}
	}

	private boolean areBlocksAroundFlammable(BlockView blockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.isFlammable(blockView.getBlockState(blockPos.offset(direction)))) {
				return true;
			}
		}

		return false;
	}

	private int getBurnChance(WorldView worldView, BlockPos blockPos) {
		if (!worldView.isAir(blockPos)) {
			return 0;
		} else {
			int i = 0;

			for (Direction direction : Direction.values()) {
				BlockState blockState = worldView.getBlockState(blockPos.offset(direction));
				i = Math.max(this.getBurnChance(blockState), i);
			}

			return i;
		}
	}

	public boolean isFlammable(BlockState blockState) {
		return this.getBurnChance(blockState) > 0;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.dimension.getType() != DimensionType.field_13072 && world.dimension.getType() != DimensionType.field_13076
				|| !((NetherPortalBlock)Blocks.field_10316).createPortalAt(world, blockPos)) {
				if (!blockState.canPlaceAt(world, blockPos)) {
					world.removeBlock(blockPos, false);
				} else {
					world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world) + world.random.nextInt(10));
				}
			}
		}
	}

	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(24) == 0) {
			world.playSound(
				(double)((float)blockPos.getX() + 0.5F),
				(double)((float)blockPos.getY() + 0.5F),
				(double)((float)blockPos.getZ() + 0.5F),
				SoundEvents.field_14993,
				SoundCategory.field_15245,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (!this.isFlammable(blockState2) && !blockState2.isSideSolidFullSquare(world, blockPos2, Direction.field_11036)) {
			if (this.isFlammable(world.getBlockState(blockPos.west()))) {
				for (int j = 0; j < 2; j++) {
					double g = (double)blockPos.getX() + random.nextDouble() * 0.1F;
					double h = (double)blockPos.getY() + random.nextDouble();
					double k = (double)blockPos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.field_11237, g, h, k, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.east()))) {
				for (int l = 0; l < 2; l++) {
					double m = (double)(blockPos.getX() + 1) - random.nextDouble() * 0.1F;
					double n = (double)blockPos.getY() + random.nextDouble();
					double o = (double)blockPos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.field_11237, m, n, o, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.north()))) {
				for (int p = 0; p < 2; p++) {
					double q = (double)blockPos.getX() + random.nextDouble();
					double r = (double)blockPos.getY() + random.nextDouble();
					double s = (double)blockPos.getZ() + random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.field_11237, q, r, s, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.south()))) {
				for (int t = 0; t < 2; t++) {
					double u = (double)blockPos.getX() + random.nextDouble();
					double v = (double)blockPos.getY() + random.nextDouble();
					double w = (double)(blockPos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.field_11237, u, v, w, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.up()))) {
				for (int x = 0; x < 2; x++) {
					double y = (double)blockPos.getX() + random.nextDouble();
					double z = (double)(blockPos.getY() + 1) - random.nextDouble() * 0.1F;
					double aa = (double)blockPos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.field_11237, y, z, aa, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)blockPos.getX() + random.nextDouble();
				double e = (double)blockPos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)blockPos.getZ() + random.nextDouble();
				world.addParticle(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
	}

	public void registerFlammableBlock(Block block, int i, int j) {
		this.burnChances.put(block, i);
		this.spreadChances.put(block, j);
	}

	public static void registerDefaultFlammables() {
		FireBlock fireBlock = (FireBlock)Blocks.field_10036;
		fireBlock.registerFlammableBlock(Blocks.field_10161, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_9975, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10148, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10334, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10218, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10075, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10119, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10071, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10257, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10617, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10031, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10500, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10188, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10291, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10513, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10041, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10196, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10457, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10620, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10020, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10299, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10319, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10132, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10144, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10563, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10408, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10569, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10122, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10256, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10616, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10431, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10037, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10511, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10306, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10533, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10010, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10519, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10436, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10366, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10254, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10622, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10244, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10250, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10558, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10204, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10084, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10103, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10374, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10126, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10155, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10307, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10303, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_9999, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10178, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10503, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_9988, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10539, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10335, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10098, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10035, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10504, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10375, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10479, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10112, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10428, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10583, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10378, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10430, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10003, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10214, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10313, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10182, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10449, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10086, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10226, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10573, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10270, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10048, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10156, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10315, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10554, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_9995, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10548, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10606, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10446, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10095, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10215, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10294, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10490, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10028, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10459, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10423, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10222, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10619, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10259, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10514, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10113, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10170, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10314, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10146, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10597, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10381, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10359, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10466, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_9977, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10482, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10290, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10512, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10040, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10393, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10591, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10209, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10433, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10510, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10043, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10473, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10338, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10536, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10106, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10342, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10211, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.field_16492, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.field_16330, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.field_17563, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_16999, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_20422, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_20421, 30, 20);
	}
}
