package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EatGrassGoal extends Goal {
	private static final Predicate<BlockState> GRASS_PREDICATE = BlockStatePredicate.create(Blocks.TALLGRASS)
		.setProperty(TallPlantBlock.TYPE, Predicates.equalTo(TallPlantBlock.GrassType.GRASS));
	private final MobEntity mob;
	private final World world;
	int timer;

	public EatGrassGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.world = mobEntity.world;
		this.setCategoryBits(7);
	}

	@Override
	public boolean canStart() {
		if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y, this.mob.z);
			return GRASS_PREDICATE.apply(this.world.getBlockState(blockPos)) ? true : this.world.getBlockState(blockPos.down()).getBlock() == Blocks.GRASS;
		}
	}

	@Override
	public void start() {
		this.timer = 40;
		this.world.sendEntityStatus(this.mob, (byte)10);
		this.mob.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.timer = 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.timer > 0;
	}

	public int getTimer() {
		return this.timer;
	}

	@Override
	public void tick() {
		this.timer = Math.max(0, this.timer - 1);
		if (this.timer == 4) {
			BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y, this.mob.z);
			if (GRASS_PREDICATE.apply(this.world.getBlockState(blockPos))) {
				if (this.world.getGameRules().getBoolean("mobGriefing")) {
					this.world.removeBlock(blockPos, false);
				}

				this.mob.onEatingGrass();
			} else {
				BlockPos blockPos2 = blockPos.down();
				if (this.world.getBlockState(blockPos2).getBlock() == Blocks.GRASS) {
					if (this.world.getGameRules().getBoolean("mobGriefing")) {
						this.world.syncGlobalEvent(2001, blockPos2, Block.getIdByBlock(Blocks.GRASS));
						this.world.setBlockState(blockPos2, Blocks.DIRT.getDefaultState(), 2);
					}

					this.mob.onEatingGrass();
				}
			}
		}
	}
}
