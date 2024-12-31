package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FallingBlock extends Block {
	public static boolean instantFall;

	public FallingBlock() {
		super(Material.SAND);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	public FallingBlock(Material material) {
		super(material);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		world.createAndScheduleBlockTick(blockPos, this, this.getTickRate(world));
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			this.scheduledTick(world, pos);
		}
	}

	private void scheduledTick(World world, BlockPos pos) {
		if (canFallThough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
			int i = 32;
			if (instantFall || !world.isRegionLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				world.setAir(pos);
				BlockPos blockPos = pos.down();

				while (canFallThough(world.getBlockState(blockPos)) && blockPos.getY() > 0) {
					blockPos = blockPos.down();
				}

				if (blockPos.getY() > 0) {
					world.setBlockState(blockPos.up(), this.getDefaultState());
				}
			} else if (!world.isClient) {
				FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
					world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, world.getBlockState(pos)
				);
				this.configureFallingBlockEntity(fallingBlockEntity);
				world.spawnEntity(fallingBlockEntity);
			}
		}
	}

	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
	}

	@Override
	public int getTickRate(World world) {
		return 2;
	}

	public static boolean canFallThough(BlockState pos) {
		Block block = pos.getBlock();
		Material material = pos.getMaterial();
		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	public void onDestroyedOnLanding(World world, BlockPos pos) {
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos blockPos = pos.down();
			if (canFallThough(world.getBlockState(blockPos))) {
				double d = (double)((float)pos.getX() + random.nextFloat());
				double e = (double)pos.getY() - 0.05;
				double f = (double)((float)pos.getZ() + random.nextFloat());
				world.addParticle(ParticleType.FALLING_DUST, d, e, f, 0.0, 0.0, 0.0, Block.getByBlockState(state));
			}
		}
	}

	public int getColor(BlockState state) {
		return -16777216;
	}
}
