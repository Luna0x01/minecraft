package net.minecraft.entity;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class LightningBoltEntity extends WeatherEntity {
	private int ambientTick;
	public long seed;
	private int remainingActions;

	public LightningBoltEntity(World world, double d, double e, double f) {
		super(world);
		this.refreshPositionAndAngles(d, e, f, 0.0F, 0.0F);
		this.ambientTick = 2;
		this.seed = this.random.nextLong();
		this.remainingActions = this.random.nextInt(3) + 1;
		BlockPos blockPos = new BlockPos(this);
		if (!world.isClient
			&& world.getGameRules().getBoolean("doFireTick")
			&& (world.getGlobalDifficulty() == Difficulty.NORMAL || world.getGlobalDifficulty() == Difficulty.HARD)
			&& world.isRegionLoaded(blockPos, 10)) {
			if (world.getBlockState(blockPos).getBlock().getMaterial() == Material.AIR && Blocks.FIRE.canBePlacedAtPos(world, blockPos)) {
				world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
			}

			for (int i = 0; i < 4; i++) {
				BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
				if (world.getBlockState(blockPos2).getBlock().getMaterial() == Material.AIR && Blocks.FIRE.canBePlacedAtPos(world, blockPos2)) {
					world.setBlockState(blockPos2, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.ambientTick == 2) {
			this.world.playSound(this.x, this.y, this.z, "ambient.weather.thunder", 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
			this.world.playSound(this.x, this.y, this.z, "random.explode", 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
		}

		this.ambientTick--;
		if (this.ambientTick < 0) {
			if (this.remainingActions == 0) {
				this.remove();
			} else if (this.ambientTick < -this.random.nextInt(10)) {
				this.remainingActions--;
				this.ambientTick = 1;
				this.seed = this.random.nextLong();
				BlockPos blockPos = new BlockPos(this);
				if (!this.world.isClient
					&& this.world.getGameRules().getBoolean("doFireTick")
					&& this.world.isRegionLoaded(blockPos, 10)
					&& this.world.getBlockState(blockPos).getBlock().getMaterial() == Material.AIR
					&& Blocks.FIRE.canBePlacedAtPos(this.world, blockPos)) {
					this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
				}
			}
		}

		if (this.ambientTick >= 0) {
			if (this.world.isClient) {
				this.world.setLightningTicksLeft(2);
			} else {
				double d = 3.0;
				List<Entity> list = this.world.getEntitiesIn(this, new Box(this.x - d, this.y - d, this.z - d, this.x + d, this.y + 6.0 + d, this.z + d));

				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					entity.onLightningStrike(this);
				}
			}
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}
}
