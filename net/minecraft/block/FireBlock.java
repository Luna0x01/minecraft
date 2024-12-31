package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class FireBlock extends Block {
	public static final IntProperty AGE = IntProperty.of("age", 0, 15);
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty field_12678 = BooleanProperty.of("up");
	private final Map<Block, Integer> BLOCKS_BY_FLAMMABILITY = Maps.newIdentityHashMap();
	private final Map<Block, Integer> BLOCKS_BY_MODIFIER = Maps.newIdentityHashMap();

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return !view.getBlockState(pos.down()).method_11739() && !Blocks.FIRE.isFlammable(view, pos.down())
			? state.with(NORTH, this.isFlammable(view, pos.north()))
				.with(EAST, this.isFlammable(view, pos.east()))
				.with(SOUTH, this.isFlammable(view, pos.south()))
				.with(WEST, this.isFlammable(view, pos.west()))
				.with(field_12678, this.isFlammable(view, pos.up()))
			: this.getDefaultState();
	}

	protected FireBlock() {
		super(Material.FIRE);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(AGE, 0).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(field_12678, false)
		);
		this.setTickRandomly(true);
	}

	public static void registerDefaultFlammables() {
		Blocks.FIRE.registerFlammableBlock(Blocks.PLANKS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.DOUBLE_WOODEN_SLAB, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.WOODEN_SLAB, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.OAK_FENCE_GATE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.SPRUCE_FENCE_GATE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.BIRCH_FENCE_GATE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.JUNGLE_FENCE_GATE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.ACACIA_FENCE_GATE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.OAK_FENCE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.SPRUCE_FENCE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.BIRCH_FENCE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.JUNGLE_FENCE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.DARK_OAK_FENCE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.ACACIA_FENCE, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.WOODEN_STAIRS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.BIRCH_STAIRS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.SPRUCE_STAIRS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.JUNGLE_STAIRS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.ACACIA_STAIRS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.DARK_OAK_STAIRS, 5, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.LOG, 5, 5);
		Blocks.FIRE.registerFlammableBlock(Blocks.LOG2, 5, 5);
		Blocks.FIRE.registerFlammableBlock(Blocks.LEAVES, 30, 60);
		Blocks.FIRE.registerFlammableBlock(Blocks.LEAVES2, 30, 60);
		Blocks.FIRE.registerFlammableBlock(Blocks.BOOKSHELF, 30, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.TNT, 15, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.TALLGRASS, 60, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.DOUBLE_PLANT, 60, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.YELLOW_FLOWER, 60, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.RED_FLOWER, 60, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.DEADBUSH, 60, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.WOOL, 30, 60);
		Blocks.FIRE.registerFlammableBlock(Blocks.VINE, 15, 100);
		Blocks.FIRE.registerFlammableBlock(Blocks.COAL_BLOCK, 5, 5);
		Blocks.FIRE.registerFlammableBlock(Blocks.HAY_BALE, 60, 20);
		Blocks.FIRE.registerFlammableBlock(Blocks.CARPET, 60, 20);
	}

	public void registerFlammableBlock(Block block, int flammability, int disappearPercentage) {
		this.BLOCKS_BY_FLAMMABILITY.put(block, flammability);
		this.BLOCKS_BY_MODIFIER.put(block, disappearPercentage);
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public int getTickRate(World world) {
		return 30;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.getGameRules().getBoolean("doFireTick")) {
			if (!this.canBePlacedAtPos(world, pos)) {
				world.setAir(pos);
			}

			Block block = world.getBlockState(pos.down()).getBlock();
			boolean bl = block == Blocks.NETHERRACK;
			if (world.dimension instanceof TheEndDimension && block == Blocks.BEDROCK) {
				bl = true;
			}

			int i = (Integer)state.get(AGE);
			if (!bl && world.isRaining() && this.isRainingAround(world, pos) && rand.nextFloat() < 0.2F + (float)i * 0.03F) {
				world.setAir(pos);
			} else {
				if (i < 15) {
					state = state.with(AGE, i + rand.nextInt(3) / 2);
					world.setBlockState(pos, state, 4);
				}

				world.createAndScheduleBlockTick(pos, this, this.getTickRate(world) + rand.nextInt(10));
				if (!bl) {
					if (!this.isAdjacentFlammable(world, pos)) {
						if (!world.getBlockState(pos.down()).method_11739() || i > 3) {
							world.setAir(pos);
						}

						return;
					}

					if (!this.isFlammable(world, pos.down()) && i == 15 && rand.nextInt(4) == 0) {
						world.setAir(pos);
						return;
					}
				}

				boolean bl2 = world.hasHighHumidity(pos);
				int j = 0;
				if (bl2) {
					j = -50;
				}

				this.trySpreadingFire(world, pos.east(), 300 + j, rand, i);
				this.trySpreadingFire(world, pos.west(), 300 + j, rand, i);
				this.trySpreadingFire(world, pos.down(), 250 + j, rand, i);
				this.trySpreadingFire(world, pos.up(), 250 + j, rand, i);
				this.trySpreadingFire(world, pos.north(), 300 + j, rand, i);
				this.trySpreadingFire(world, pos.south(), 300 + j, rand, i);

				for (int k = -1; k <= 1; k++) {
					for (int l = -1; l <= 1; l++) {
						for (int m = -1; m <= 4; m++) {
							if (k != 0 || m != 0 || l != 0) {
								int n = 100;
								if (m > 1) {
									n += (m - 1) * 100;
								}

								BlockPos blockPos = pos.add(k, m, l);
								int o = this.getBurnChance(world, blockPos);
								if (o > 0) {
									int p = (o + 40 + world.getGlobalDifficulty().getId() * 7) / (i + 30);
									if (bl2) {
										p /= 2;
									}

									if (p > 0 && rand.nextInt(n) <= p && (!world.isRaining() || !this.isRainingAround(world, blockPos))) {
										int q = i + rand.nextInt(5) / 4;
										if (q > 15) {
											q = 15;
										}

										world.setBlockState(blockPos, state.with(AGE, q), 3);
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

	@Override
	public boolean doImmediateUpdates() {
		return false;
	}

	private int getModifier(Block block) {
		Integer integer = (Integer)this.BLOCKS_BY_MODIFIER.get(block);
		return integer == null ? 0 : integer;
	}

	private int getFlammability(Block block) {
		Integer integer = (Integer)this.BLOCKS_BY_FLAMMABILITY.get(block);
		return integer == null ? 0 : integer;
	}

	private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge) {
		int i = this.getModifier(world.getBlockState(pos).getBlock());
		if (rand.nextInt(spreadFactor) < i) {
			BlockState blockState = world.getBlockState(pos);
			if (rand.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
				int j = currentAge + rand.nextInt(5) / 4;
				if (j > 15) {
					j = 15;
				}

				world.setBlockState(pos, this.getDefaultState().with(AGE, j), 3);
			} else {
				world.setAir(pos);
			}

			if (blockState.getBlock() == Blocks.TNT) {
				Blocks.TNT.onBreakByPlayer(world, pos, blockState.with(TntBlock.EXPLODE, true));
			}
		}
	}

	private boolean isAdjacentFlammable(World world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.isFlammable(world, pos.offset(direction))) {
				return true;
			}
		}

		return false;
	}

	private int getBurnChance(World world, BlockPos pos) {
		if (!world.isAir(pos)) {
			return 0;
		} else {
			int i = 0;

			for (Direction direction : Direction.values()) {
				i = Math.max(this.getFlammability(world.getBlockState(pos.offset(direction)).getBlock()), i);
			}

			return i;
		}
	}

	@Override
	public boolean hasCollision() {
		return false;
	}

	public boolean isFlammable(BlockView view, BlockPos pos) {
		return this.getFlammability(view.getBlockState(pos).getBlock()) > 0;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_11739() || this.isAdjacentFlammable(world, pos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.getBlockState(pos.down()).method_11739() && !this.isAdjacentFlammable(world, pos)) {
			world.setAir(pos);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (world.dimension.getDimensionType().getId() > 0 || !Blocks.NETHER_PORTAL.createPortalAt(world, pos)) {
			if (!world.getBlockState(pos.down()).method_11739() && !this.isAdjacentFlammable(world, pos)) {
				world.setAir(pos);
			} else {
				world.createAndScheduleBlockTick(pos, this, this.getTickRate(world) + world.random.nextInt(10));
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

		if (!world.getBlockState(pos.down()).method_11739() && !Blocks.FIRE.isFlammable(world, pos.down())) {
			if (Blocks.FIRE.isFlammable(world, pos.west())) {
				for (int j = 0; j < 2; j++) {
					double g = (double)pos.getX() + random.nextDouble() * 0.1F;
					double h = (double)pos.getY() + random.nextDouble();
					double k = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleType.SMOKE_LARGE, g, h, k, 0.0, 0.0, 0.0);
				}
			}

			if (Blocks.FIRE.isFlammable(world, pos.east())) {
				for (int l = 0; l < 2; l++) {
					double m = (double)(pos.getX() + 1) - random.nextDouble() * 0.1F;
					double n = (double)pos.getY() + random.nextDouble();
					double o = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleType.SMOKE_LARGE, m, n, o, 0.0, 0.0, 0.0);
				}
			}

			if (Blocks.FIRE.isFlammable(world, pos.north())) {
				for (int p = 0; p < 2; p++) {
					double q = (double)pos.getX() + random.nextDouble();
					double r = (double)pos.getY() + random.nextDouble();
					double s = (double)pos.getZ() + random.nextDouble() * 0.1F;
					world.addParticle(ParticleType.SMOKE_LARGE, q, r, s, 0.0, 0.0, 0.0);
				}
			}

			if (Blocks.FIRE.isFlammable(world, pos.south())) {
				for (int t = 0; t < 2; t++) {
					double u = (double)pos.getX() + random.nextDouble();
					double v = (double)pos.getY() + random.nextDouble();
					double w = (double)(pos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.addParticle(ParticleType.SMOKE_LARGE, u, v, w, 0.0, 0.0, 0.0);
				}
			}

			if (Blocks.FIRE.isFlammable(world, pos.up())) {
				for (int x = 0; x < 2; x++) {
					double y = (double)pos.getX() + random.nextDouble();
					double z = (double)(pos.getY() + 1) - random.nextDouble() * 0.1F;
					double aa = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleType.SMOKE_LARGE, y, z, aa, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)pos.getX() + random.nextDouble();
				double e = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)pos.getZ() + random.nextDouble();
				world.addParticle(ParticleType.SMOKE_LARGE, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.LAVA;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(AGE, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(AGE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AGE, NORTH, EAST, SOUTH, WEST, field_12678);
	}
}
