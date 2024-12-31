package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;

public class FireBlock extends Block {
	public static final IntProperty field_18334 = Properties.AGE_15;
	public static final BooleanProperty field_18335 = ConnectingBlock.NORTH;
	public static final BooleanProperty field_18336 = ConnectingBlock.EAST;
	public static final BooleanProperty field_18337 = ConnectingBlock.SOUTH;
	public static final BooleanProperty field_18338 = ConnectingBlock.WEST;
	public static final BooleanProperty field_18339 = ConnectingBlock.UP;
	private static final Map<Direction, BooleanProperty> field_18340 = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_TO_PROPERTY
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(Util.method_20218());
	private final Object2IntMap<Block> field_18341 = new Object2IntOpenHashMap();
	private final Object2IntMap<Block> field_18342 = new Object2IntOpenHashMap();

	protected FireBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18334, Integer.valueOf(0))
				.withProperty(field_18335, Boolean.valueOf(false))
				.withProperty(field_18336, Boolean.valueOf(false))
				.withProperty(field_18337, Boolean.valueOf(false))
				.withProperty(field_18338, Boolean.valueOf(false))
				.withProperty(field_18339, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return this.canPlaceAt(state, world, pos)
			? this.method_16678(world, pos).withProperty(field_18334, state.getProperty(field_18334))
			: Blocks.AIR.getDefaultState();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.method_16678(context.getWorld(), context.getBlockPos());
	}

	public BlockState method_16678(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos.down());
		if (!blockState.method_16913() && !this.method_16680(blockState)) {
			BlockState blockState2 = this.getDefaultState();

			for (Direction direction : Direction.values()) {
				BooleanProperty booleanProperty = (BooleanProperty)field_18340.get(direction);
				if (booleanProperty != null) {
					blockState2 = blockState2.withProperty(booleanProperty, Boolean.valueOf(this.method_16680(blockView.getBlockState(blockPos.offset(direction)))));
				}
			}

			return blockState2;
		} else {
			return this.getDefaultState();
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_16913() || this.method_8778(world, pos);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 30;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.getGameRules().getBoolean("doFireTick")) {
			if (!state.canPlaceAt(world, pos)) {
				world.method_8553(pos);
			}

			Block block = world.getBlockState(pos.down()).getBlock();
			boolean bl = world.dimension instanceof TheEndDimension && block == Blocks.BEDROCK || block == Blocks.NETHERRACK || block == Blocks.MAGMA_BLOCK;
			int i = (Integer)state.getProperty(field_18334);
			if (!bl && world.isRaining() && this.isRainingAround(world, pos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
				world.method_8553(pos);
			} else {
				int j = Math.min(15, i + random.nextInt(3) / 2);
				if (i != j) {
					state = state.withProperty(field_18334, Integer.valueOf(j));
					world.setBlockState(pos, state, 4);
				}

				if (!bl) {
					world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world) + random.nextInt(10));
					if (!this.method_8778(world, pos)) {
						if (!world.getBlockState(pos.down()).method_16913() || i > 3) {
							world.method_8553(pos);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.method_16680(world.getBlockState(pos.down()))) {
						world.method_8553(pos);
						return;
					}
				}

				boolean bl2 = world.hasHighHumidity(pos);
				int k = bl2 ? -50 : 0;
				this.trySpreadingFire(world, pos.east(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.west(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.down(), 250 + k, random, i);
				this.trySpreadingFire(world, pos.up(), 250 + k, random, i);
				this.trySpreadingFire(world, pos.north(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.south(), 300 + k, random, i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 4; n++) {
							if (l != 0 || n != 0 || m != 0) {
								int o = 100;
								if (n > 1) {
									o += (n - 1) * 100;
								}

								mutable.set(pos).method_19934(l, n, m);
								int p = this.method_8779(world, mutable);
								if (p > 0) {
									int q = (p + 40 + world.method_16346().getId() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.isRainingAround(world, mutable))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										world.setBlockState(mutable, this.method_16678(world, mutable).withProperty(field_18334, Integer.valueOf(r)), 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isRainingAround(World world, BlockPos pos) {
		return world.hasRain(pos) || world.hasRain(pos.west()) || world.hasRain(pos.east()) || world.hasRain(pos.north()) || world.hasRain(pos.south());
	}

	private int getModifier(Block block) {
		return this.field_18342.getInt(block);
	}

	private int getFlammability(Block block) {
		return this.field_18341.getInt(block);
	}

	private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge) {
		int i = this.getModifier(world.getBlockState(pos).getBlock());
		if (rand.nextInt(spreadFactor) < i) {
			BlockState blockState = world.getBlockState(pos);
			if (rand.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
				int j = Math.min(currentAge + rand.nextInt(5) / 4, 15);
				world.setBlockState(pos, this.method_16678(world, pos).withProperty(field_18334, Integer.valueOf(j)), 3);
			} else {
				world.method_8553(pos);
			}

			Block block = blockState.getBlock();
			if (block instanceof TntBlock) {
				((TntBlock)block).method_16751(world, pos);
			}
		}
	}

	private boolean method_8778(BlockView blockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_16680(blockView.getBlockState(blockPos.offset(direction)))) {
				return true;
			}
		}

		return false;
	}

	private int method_8779(RenderBlockView renderBlockView, BlockPos blockPos) {
		if (!renderBlockView.method_8579(blockPos)) {
			return 0;
		} else {
			int i = 0;

			for (Direction direction : Direction.values()) {
				i = Math.max(this.getFlammability(renderBlockView.getBlockState(blockPos.offset(direction)).getBlock()), i);
			}

			return i;
		}
	}

	@Override
	public boolean hasCollision() {
		return false;
	}

	public boolean method_16680(BlockState blockState) {
		return this.getFlammability(blockState.getBlock()) > 0;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			if (world.dimension.method_11789() != DimensionType.OVERWORLD && world.dimension.method_11789() != DimensionType.THE_NETHER
				|| !((NetherPortalBlock)Blocks.NETHER_PORTAL).method_16704(world, pos)) {
				if (!state.canPlaceAt(world, pos)) {
					world.method_8553(pos);
				} else {
					world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world) + world.random.nextInt(10));
				}
			}
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(24) == 0) {
			world.playSound(
				(double)((float)pos.getX() + 0.5F),
				(double)((float)pos.getY() + 0.5F),
				(double)((float)pos.getZ() + 0.5F),
				Sounds.BLOCK_FIRE_AMBIENT,
				SoundCategory.BLOCKS,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		if (!world.getBlockState(pos.down()).method_16913() && !this.method_16680(world.getBlockState(pos.down()))) {
			if (this.method_16680(world.getBlockState(pos.west()))) {
				for (int j = 0; j < 2; j++) {
					double g = (double)pos.getX() + random.nextDouble() * 0.1F;
					double h = (double)pos.getY() + random.nextDouble();
					double k = (double)pos.getZ() + random.nextDouble();
					world.method_16343(class_4342.field_21356, g, h, k, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_16680(world.getBlockState(pos.east()))) {
				for (int l = 0; l < 2; l++) {
					double m = (double)(pos.getX() + 1) - random.nextDouble() * 0.1F;
					double n = (double)pos.getY() + random.nextDouble();
					double o = (double)pos.getZ() + random.nextDouble();
					world.method_16343(class_4342.field_21356, m, n, o, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_16680(world.getBlockState(pos.north()))) {
				for (int p = 0; p < 2; p++) {
					double q = (double)pos.getX() + random.nextDouble();
					double r = (double)pos.getY() + random.nextDouble();
					double s = (double)pos.getZ() + random.nextDouble() * 0.1F;
					world.method_16343(class_4342.field_21356, q, r, s, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_16680(world.getBlockState(pos.south()))) {
				for (int t = 0; t < 2; t++) {
					double u = (double)pos.getX() + random.nextDouble();
					double v = (double)pos.getY() + random.nextDouble();
					double w = (double)(pos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.method_16343(class_4342.field_21356, u, v, w, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_16680(world.getBlockState(pos.up()))) {
				for (int x = 0; x < 2; x++) {
					double y = (double)pos.getX() + random.nextDouble();
					double z = (double)(pos.getY() + 1) - random.nextDouble() * 0.1F;
					double aa = (double)pos.getZ() + random.nextDouble();
					world.method_16343(class_4342.field_21356, y, z, aa, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)pos.getX() + random.nextDouble();
				double e = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)pos.getZ() + random.nextDouble();
				world.method_16343(class_4342.field_21356, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18334, field_18335, field_18336, field_18337, field_18338, field_18339);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	public void registerFlammableBlock(Block block, int flammability, int disappearPercentage) {
		this.field_18341.put(block, flammability);
		this.field_18342.put(block, disappearPercentage);
	}

	public static void registerDefaultFlammables() {
		FireBlock fireBlock = (FireBlock)Blocks.FIRE;
		fireBlock.registerFlammableBlock(Blocks.OAK_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.WOODEN_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.OAK_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BOOKSHELF, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.TNT, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.GRASS, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.FERN, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.DEAD_BUSH, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.SUNFLOWER, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.LILAC, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.ROSE_BUSH, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.PEONY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.TALL_GRASS, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.LARGE_FERN, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.DANDELION, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.POPPY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.BLUE_ORCHID, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.ALLIUM, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.AZURE_BLUET, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.RED_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.ORANGE_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.WHITE_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.PINK_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.OXEYE_DAISY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.WHITE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.ORANGE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.MAGENTA_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_BLUE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.YELLOW_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.LIME_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.PINK_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.GRAY_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_GRAY_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.CYAN_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.PURPLE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BLUE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BROWN_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.GREEN_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.RED_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BLACK_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.VINE, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.COAL_BLOCK, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.HAY_BALE, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.WHITE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.ORANGE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.MAGENTA_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_BLUE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.YELLOW_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.LIME_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.PINK_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.GRAY_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_GRAY_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.CYAN_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.PURPLE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.BLUE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.BROWN_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.GREEN_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.RED_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.BLACK_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.DRIED_KELP_BLOCK, 30, 60);
	}
}
