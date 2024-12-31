package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;

public class BreakDoorGoal extends DoorInteractGoal {
	private int breakProgress;
	private int prevBreakProgress = -1;

	public BreakDoorGoal(MobEntity mobEntity) {
		super(mobEntity);
	}

	@Override
	public boolean canStart() {
		if (!super.canStart()) {
			return false;
		} else {
			return !this.mob.world.getGameRules().getBoolean("mobGriefing") ? false : !DoorBlock.isOpen(this.mob.world, this.pos);
		}
	}

	@Override
	public void start() {
		super.start();
		this.breakProgress = 0;
	}

	@Override
	public boolean shouldContinue() {
		double d = this.mob.squaredDistanceTo(this.pos);
		return this.breakProgress <= 240 && !DoorBlock.isOpen(this.mob.world, this.pos) && d < 4.0;
	}

	@Override
	public void stop() {
		super.stop();
		this.mob.world.setBlockBreakingInfo(this.mob.getEntityId(), this.pos, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.mob.getRandom().nextInt(20) == 0) {
			this.mob.world.syncGlobalEvent(1019, this.pos, 0);
		}

		this.breakProgress++;
		int i = (int)((float)this.breakProgress / 240.0F * 10.0F);
		if (i != this.prevBreakProgress) {
			this.mob.world.setBlockBreakingInfo(this.mob.getEntityId(), this.pos, i);
			this.prevBreakProgress = i;
		}

		if (this.breakProgress == 240 && this.mob.world.getGlobalDifficulty() == Difficulty.HARD) {
			this.mob.world.setAir(this.pos);
			this.mob.world.syncGlobalEvent(1021, this.pos, 0);
			this.mob.world.syncGlobalEvent(2001, this.pos, Block.getIdByBlock(this.doorBlock));
		}
	}
}
