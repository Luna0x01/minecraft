package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractFluidBlock extends Block {
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 15);

	protected AbstractFluidBlock(Material material) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setTickRandomly(true);
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return this.material != Material.LAVA;
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return this.material == Material.WATER ? BiomeColors.getWaterColor(view, pos) : 16777215;
	}

	public static float getHeightPercent(int height) {
		if (height >= 8) {
			height = 0;
		}

		return (float)(height + 1) / 9.0F;
	}

	protected int getFluidLevel(BlockView world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().getMaterial() == this.material ? (Integer)world.getBlockState(pos).get(LEVEL) : -1;
	}

	protected int getFlowReduction(BlockView world, BlockPos pos) {
		int i = this.getFluidLevel(world, pos);
		return i >= 8 ? 0 : i;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean canCollide(BlockState state, boolean bl) {
		return bl && (Integer)state.get(LEVEL) == 0;
	}

	@Override
	public boolean hasCollision(BlockView blockView, BlockPos pos, Direction direction) {
		Material material = blockView.getBlockState(pos).getBlock().getMaterial();
		if (material == this.material) {
			return false;
		} else if (direction == Direction.UP) {
			return true;
		} else {
			return material == Material.ICE ? false : super.hasCollision(blockView, pos, direction);
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		if (view.getBlockState(pos).getBlock().getMaterial() == this.material) {
			return false;
		} else {
			return facing == Direction.UP ? true : super.isSideInvisible(view, pos, facing);
		}
	}

	public boolean shouldDisableCullingSides(BlockView world, BlockPos pos) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockState blockState = world.getBlockState(pos.add(i, 0, j));
				Block block = blockState.getBlock();
				Material material = block.getMaterial();
				if (material != this.material && !block.isFullBlock()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public int getBlockType() {
		return 1;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	protected Vec3d getFluidVec(BlockView world, BlockPos pos) {
		Vec3d vec3d = new Vec3d(0.0, 0.0, 0.0);
		int i = this.getFlowReduction(world, pos);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			int j = this.getFlowReduction(world, blockPos);
			if (j < 0) {
				if (!world.getBlockState(blockPos).getBlock().getMaterial().blocksMovement()) {
					j = this.getFlowReduction(world, blockPos.down());
					if (j >= 0) {
						int k = j - (i - 8);
						vec3d = vec3d.add(
							(double)((blockPos.getX() - pos.getX()) * k), (double)((blockPos.getY() - pos.getY()) * k), (double)((blockPos.getZ() - pos.getZ()) * k)
						);
					}
				}
			} else if (j >= 0) {
				int l = j - i;
				vec3d = vec3d.add((double)((blockPos.getX() - pos.getX()) * l), (double)((blockPos.getY() - pos.getY()) * l), (double)((blockPos.getZ() - pos.getZ()) * l));
			}
		}

		if ((Integer)world.getBlockState(pos).get(LEVEL) >= 8) {
			for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
				BlockPos blockPos2 = pos.offset(direction2);
				if (this.hasCollision(world, blockPos2, direction2) || this.hasCollision(world, blockPos2.up(), direction2)) {
					vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
					break;
				}
			}
		}

		return vec3d.normalize();
	}

	@Override
	public Vec3d onEntityCollision(World world, BlockPos pos, Entity entity, Vec3d velocity) {
		return velocity.add(this.getFluidVec(world, pos));
	}

	@Override
	public int getTickRate(World world) {
		if (this.material == Material.WATER) {
			return 5;
		} else if (this.material == Material.LAVA) {
			return world.dimension.hasNoSkylight() ? 10 : 30;
		} else {
			return 0;
		}
	}

	@Override
	public int getBrightness(BlockView blockView, BlockPos pos) {
		int i = blockView.getLight(pos, 0);
		int j = blockView.getLight(pos.up(), 0);
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return this.material == Material.WATER ? RenderLayer.TRANSLUCENT : RenderLayer.SOLID;
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		if (this.material == Material.WATER) {
			int i = (Integer)state.get(LEVEL);
			if (i > 0 && i < 8) {
				if (rand.nextInt(64) == 0) {
					world.playSound(d + 0.5, e + 0.5, f + 0.5, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F, false);
				}
			} else if (rand.nextInt(10) == 0) {
				world.addParticle(ParticleType.SUSPENDED, d + (double)rand.nextFloat(), e + (double)rand.nextFloat(), f + (double)rand.nextFloat(), 0.0, 0.0, 0.0);
			}
		}

		if (this.material == Material.LAVA
			&& world.getBlockState(pos.up()).getBlock().getMaterial() == Material.AIR
			&& !world.getBlockState(pos.up()).getBlock().hasTransparency()) {
			if (rand.nextInt(100) == 0) {
				double g = d + (double)rand.nextFloat();
				double h = e + this.boundingBoxMaxY;
				double j = f + (double)rand.nextFloat();
				world.addParticle(ParticleType.LAVA, g, h, j, 0.0, 0.0, 0.0);
				world.playSound(g, h, j, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}

			if (rand.nextInt(200) == 0) {
				world.playSound(d, e, f, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}
		}

		if (rand.nextInt(10) == 0 && World.isOpaque(world, pos.down())) {
			Material material = world.getBlockState(pos.down(2)).getBlock().getMaterial();
			if (!material.blocksMovement() && !material.isFluid()) {
				double k = d + (double)rand.nextFloat();
				double l = e - 1.05;
				double m = f + (double)rand.nextFloat();
				if (this.material == Material.WATER) {
					world.addParticle(ParticleType.WATER_DRIP, k, l, m, 0.0, 0.0, 0.0);
				} else {
					world.addParticle(ParticleType.LAVA_DRIP, k, l, m, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	public static double getDirection(BlockView world, BlockPos pos, Material material) {
		Vec3d vec3d = getFlowingFluidByMaterial(material).getFluidVec(world, pos);
		return vec3d.x == 0.0 && vec3d.z == 0.0 ? -1000.0 : MathHelper.atan2(vec3d.z, vec3d.x) - (Math.PI / 2);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.canChangeFromLava(world, pos, state);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		this.canChangeFromLava(world, pos, state);
	}

	public boolean canChangeFromLava(World world, BlockPos pos, BlockState state) {
		if (this.material == Material.LAVA) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getBlockState(pos.offset(direction)).getBlock().getMaterial() == Material.WATER) {
					bl = true;
					break;
				}
			}

			if (bl) {
				Integer integer = state.get(LEVEL);
				if (integer == 0) {
					world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
					this.playExtinguishEffects(world, pos);
					return true;
				}

				if (integer <= 4) {
					world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
					this.playExtinguishEffects(world, pos);
					return true;
				}
			}
		}

		return false;
	}

	protected void playExtinguishEffects(World world, BlockPos pos) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		world.playSound(d + 0.5, e + 0.5, f + 0.5, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

		for (int i = 0; i < 8; i++) {
			world.addParticle(ParticleType.SMOKE_LARGE, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(LEVEL, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(LEVEL);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, LEVEL);
	}

	public static FlowingFluidBlock getFlowingFluidByMaterial(Material material) {
		if (material == Material.WATER) {
			return Blocks.FLOWING_WATER;
		} else if (material == Material.LAVA) {
			return Blocks.FLOWING_LAVA;
		} else {
			throw new IllegalArgumentException("Invalid material");
		}
	}

	public static FluidBlock getFluidByMaterial(Material material) {
		if (material == Material.WATER) {
			return Blocks.WATER;
		} else if (material == Material.LAVA) {
			return Blocks.LAVA;
		} else {
			throw new IllegalArgumentException("Invalid material");
		}
	}
}
