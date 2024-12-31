package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
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
		this.setTickRandomly(true);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return collisionBox;
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return this.material != Material.LAVA;
	}

	public static float getHeightPercent(int height) {
		if (height >= 8) {
			height = 0;
		}

		return (float)(height + 1) / 9.0F;
	}

	protected int method_11620(BlockState blockState) {
		return blockState.getMaterial() == this.material ? (Integer)blockState.get(LEVEL) : -1;
	}

	protected int method_11621(BlockState blockState) {
		int i = this.method_11620(blockState);
		return i >= 8 ? 0 : i;
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
	public boolean canCollide(BlockState state, boolean bl) {
		return bl && (Integer)state.get(LEVEL) == 0;
	}

	@Override
	public boolean hasCollision(BlockView blockView, BlockPos pos, Direction direction) {
		Material material = blockView.getBlockState(pos).getMaterial();
		if (material == this.material) {
			return false;
		} else if (direction == Direction.UP) {
			return true;
		} else {
			return material == Material.ICE ? false : super.hasCollision(blockView, pos, direction);
		}
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		if (view.getBlockState(pos.offset(direction)).getMaterial() == this.material) {
			return false;
		} else {
			return direction == Direction.UP ? true : super.method_8654(state, view, pos, direction);
		}
	}

	public boolean shouldDisableCullingSides(BlockView world, BlockPos pos) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockState blockState = world.getBlockState(pos.add(i, 0, j));
				if (blockState.getMaterial() != this.material && !blockState.isFullBlock()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.LIQUID;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.AIR;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	protected Vec3d method_8823(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		int i = this.method_11621(blockState);
		BlockPos.Pooled pooled = BlockPos.Pooled.get();

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			pooled.set(blockPos).move(direction);
			int j = this.method_11621(blockView.getBlockState(pooled));
			if (j < 0) {
				if (!blockView.getBlockState(pooled).getMaterial().blocksMovement()) {
					j = this.method_11621(blockView.getBlockState(pooled.down()));
					if (j >= 0) {
						int k = j - (i - 8);
						d += (double)(direction.getOffsetX() * k);
						e += (double)(direction.getOffsetY() * k);
						f += (double)(direction.getOffsetZ() * k);
					}
				}
			} else if (j >= 0) {
				int l = j - i;
				d += (double)(direction.getOffsetX() * l);
				e += (double)(direction.getOffsetY() * l);
				f += (double)(direction.getOffsetZ() * l);
			}
		}

		Vec3d vec3d = new Vec3d(d, e, f);
		if ((Integer)blockState.get(LEVEL) >= 8) {
			for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
				pooled.set(blockPos).move(direction2);
				if (this.hasCollision(blockView, pooled, direction2) || this.hasCollision(blockView, pooled.up(), direction2)) {
					vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
					break;
				}
			}
		}

		pooled.method_12576();
		return vec3d.normalize();
	}

	@Override
	public Vec3d onEntityCollision(World world, BlockPos pos, Entity entity, Vec3d velocity) {
		return velocity.add(this.method_8823(world, pos, world.getBlockState(pos)));
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
	public int method_11564(BlockState state, BlockView view, BlockPos pos) {
		int i = view.getLight(pos, 0);
		int j = view.getLight(pos.up(), 0);
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
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		if (this.material == Material.WATER) {
			int i = (Integer)state.get(LEVEL);
			if (i > 0 && i < 8) {
				if (random.nextInt(64) == 0) {
					world.playSound(
						d + 0.5, e + 0.5, f + 0.5, Sounds.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false
					);
				}
			} else if (random.nextInt(10) == 0) {
				world.addParticle(ParticleType.SUSPENDED, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.0, 0.0);
			}
		}

		if (this.material == Material.LAVA
			&& world.getBlockState(pos.up()).getMaterial() == Material.AIR
			&& !world.getBlockState(pos.up()).isFullBoundsCubeForCulling()) {
			if (random.nextInt(100) == 0) {
				double g = d + (double)random.nextFloat();
				double h = e + state.getCollisionBox(world, pos).maxY;
				double j = f + (double)random.nextFloat();
				world.addParticle(ParticleType.LAVA, g, h, j, 0.0, 0.0, 0.0);
				world.playSound(g, h, j, Sounds.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				world.playSound(d, e, f, Sounds.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		}

		if (random.nextInt(10) == 0 && world.getBlockState(pos.down()).method_11739()) {
			Material material = world.getBlockState(pos.down(2)).getMaterial();
			if (!material.blocksMovement() && !material.isFluid()) {
				double k = d + (double)random.nextFloat();
				double l = e - 1.05;
				double m = f + (double)random.nextFloat();
				if (this.material == Material.WATER) {
					world.addParticle(ParticleType.WATER_DRIP, k, l, m, 0.0, 0.0, 0.0);
				} else {
					world.addParticle(ParticleType.LAVA_DRIP, k, l, m, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	public static float method_11618(BlockView blockView, BlockPos blockPos, Material material, BlockState blockState) {
		Vec3d vec3d = getFlowingFluidByMaterial(material).method_8823(blockView, blockPos, blockState);
		return vec3d.x == 0.0 && vec3d.z == 0.0 ? -1000.0F : (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.canChangeFromLava(world, pos, state);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		this.canChangeFromLava(world, pos, state);
	}

	public boolean canChangeFromLava(World world, BlockPos pos, BlockState state) {
		if (this.material == Material.LAVA) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getBlockState(pos.offset(direction)).getMaterial() == Material.WATER) {
					bl = true;
					break;
				}
			}

			if (bl) {
				Integer integer = state.get(LEVEL);
				if (integer == 0) {
					world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
					this.method_11619(world, pos);
					return true;
				}

				if (integer <= 4) {
					world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
					this.method_11619(world, pos);
					return true;
				}
			}
		}

		return false;
	}

	protected void method_11619(World world, BlockPos blockPos) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		world.method_11486(
			null, blockPos, Sounds.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
		);

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

	public static float method_13709(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		int i = (Integer)blockState.get(LEVEL);
		return (i & 7) == 0 && blockView.getBlockState(blockPos.up()).getMaterial() == Material.WATER ? 1.0F : 1.0F - getHeightPercent(i);
	}

	public static float method_13710(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return (float)blockPos.getY() + method_13709(blockState, blockView, blockPos);
	}
}
