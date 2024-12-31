package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DragonEggBlock extends Block {
	protected static final Box field_12650 = new Box(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);

	public DragonEggBlock() {
		super(Material.EGG, MaterialColor.BLACK);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12650;
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.scheduledTick(world, pos);
	}

	private void scheduledTick(World world, BlockPos pos) {
		if (FallingBlock.canFallThough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
			int i = 32;
			if (!FallingBlock.instantFall && world.isRegionLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				world.spawnEntity(
					new FallingBlockEntity(world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), this.getDefaultState())
				);
			} else {
				world.setAir(pos);
				BlockPos blockPos = pos;

				while (FallingBlock.canFallThough(world.getBlockState(blockPos)) && blockPos.getY() > 0) {
					blockPos = blockPos.down();
				}

				if (blockPos.getY() > 0) {
					world.setBlockState(blockPos, this.getDefaultState(), 2);
				}
			}
		}
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		this.teleport(world, pos);
		return true;
	}

	@Override
	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		this.teleport(world, pos);
	}

	private void teleport(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this) {
			for (int i = 0; i < 1000; i++) {
				BlockPos blockPos = pos.add(
					world.random.nextInt(16) - world.random.nextInt(16),
					world.random.nextInt(8) - world.random.nextInt(8),
					world.random.nextInt(16) - world.random.nextInt(16)
				);
				if (world.getBlockState(blockPos).getBlock().material == Material.AIR) {
					if (world.isClient) {
						for (int j = 0; j < 128; j++) {
							double d = world.random.nextDouble();
							float f = (world.random.nextFloat() - 0.5F) * 0.2F;
							float g = (world.random.nextFloat() - 0.5F) * 0.2F;
							float h = (world.random.nextFloat() - 0.5F) * 0.2F;
							double e = (double)blockPos.getX() + (double)(pos.getX() - blockPos.getX()) * d + (world.random.nextDouble() - 0.5) + 0.5;
							double k = (double)blockPos.getY() + (double)(pos.getY() - blockPos.getY()) * d + world.random.nextDouble() - 0.5;
							double l = (double)blockPos.getZ() + (double)(pos.getZ() - blockPos.getZ()) * d + (world.random.nextDouble() - 0.5) + 0.5;
							world.addParticle(ParticleType.NETHER_PORTAL, e, k, l, (double)f, (double)g, (double)h);
						}
					} else {
						world.setBlockState(blockPos, blockState, 2);
						world.setAir(pos);
					}

					return;
				}
			}
		}
	}

	@Override
	public int getTickRate(World world) {
		return 5;
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
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
