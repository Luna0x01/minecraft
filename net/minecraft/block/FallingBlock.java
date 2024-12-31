package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
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
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			this.scheduledTick(world, pos);
		}
	}

	private void scheduledTick(World world, BlockPos pos) {
		if (canFallThrough(world, pos.down()) && pos.getY() >= 0) {
			int i = 32;
			if (instantFall || !world.isRegionLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
				world.setAir(pos);
				BlockPos blockPos = pos.down();

				while (canFallThrough(world, blockPos) && blockPos.getY() > 0) {
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

	public static boolean canFallThrough(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		Material material = block.material;
		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	public void onDestroyedOnLanding(World world, BlockPos pos) {
	}
}
