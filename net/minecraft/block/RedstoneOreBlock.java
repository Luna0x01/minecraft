package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock extends Block {
	private final boolean lit;

	public RedstoneOreBlock(boolean bl) {
		super(Material.STONE);
		if (bl) {
			this.setTickRandomly(true);
		}

		this.lit = bl;
	}

	@Override
	public int getTickRate(World world) {
		return 30;
	}

	@Override
	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		this.activateGlowing(world, pos);
		super.onBlockBreakStart(world, pos, player);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.activateGlowing(world, pos);
		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		this.activateGlowing(world, pos);
		return super.onUse(world, pos, state, player, direction, posX, posY, posZ);
	}

	private void activateGlowing(World world, BlockPos pos) {
		this.emitParticles(world, pos);
		if (this == Blocks.REDSTONE_ORE) {
			world.setBlockState(pos, Blocks.LIT_REDSTONE_ORE.getDefaultState());
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this == Blocks.LIT_REDSTONE_ORE) {
			world.setBlockState(pos, Blocks.REDSTONE_ORE.getDefaultState());
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.REDSTONE;
	}

	@Override
	public int getBonusDrops(int id, Random rand) {
		return this.getDropCount(rand) + rand.nextInt(id + 1);
	}

	@Override
	public int getDropCount(Random rand) {
		return 4 + rand.nextInt(2);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, id);
		if (this.getDropItem(state, world.random, id) != Item.fromBlock(this)) {
			int i = 1 + world.random.nextInt(5);
			this.dropExperience(world, pos, i);
		}
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this.lit) {
			this.emitParticles(world, pos);
		}
	}

	private void emitParticles(World world, BlockPos pos) {
		Random random = world.random;
		double d = 0.0625;

		for (int i = 0; i < 6; i++) {
			double e = (double)((float)pos.getX() + random.nextFloat());
			double f = (double)((float)pos.getY() + random.nextFloat());
			double g = (double)((float)pos.getZ() + random.nextFloat());
			if (i == 0 && !world.getBlockState(pos.up()).getBlock().hasTransparency()) {
				f = (double)pos.getY() + d + 1.0;
			}

			if (i == 1 && !world.getBlockState(pos.down()).getBlock().hasTransparency()) {
				f = (double)pos.getY() - d;
			}

			if (i == 2 && !world.getBlockState(pos.south()).getBlock().hasTransparency()) {
				g = (double)pos.getZ() + d + 1.0;
			}

			if (i == 3 && !world.getBlockState(pos.north()).getBlock().hasTransparency()) {
				g = (double)pos.getZ() - d;
			}

			if (i == 4 && !world.getBlockState(pos.east()).getBlock().hasTransparency()) {
				e = (double)pos.getX() + d + 1.0;
			}

			if (i == 5 && !world.getBlockState(pos.west()).getBlock().hasTransparency()) {
				e = (double)pos.getX() - d;
			}

			if (e < (double)pos.getX()
				|| e > (double)(pos.getX() + 1)
				|| f < 0.0
				|| f > (double)(pos.getY() + 1)
				|| g < (double)pos.getZ()
				|| g > (double)(pos.getZ() + 1)) {
				world.addParticle(ParticleType.REDSTONE, e, f, g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(Blocks.REDSTONE_ORE);
	}
}
