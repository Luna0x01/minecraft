package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WolfBegGoal extends Goal {
	private WolfEntity wolf;
	private PlayerEntity begFrom;
	private World world;
	private float begDistance;
	private int timer;

	public WolfBegGoal(WolfEntity wolfEntity, float f) {
		this.wolf = wolfEntity;
		this.world = wolfEntity.world;
		this.begDistance = f;
		this.setCategoryBits(2);
	}

	@Override
	public boolean canStart() {
		this.begFrom = this.world.getClosestPlayer(this.wolf, (double)this.begDistance);
		return this.begFrom == null ? false : this.isAttractive(this.begFrom);
	}

	@Override
	public boolean shouldContinue() {
		if (!this.begFrom.isAlive()) {
			return false;
		} else {
			return this.wolf.squaredDistanceTo(this.begFrom) > (double)(this.begDistance * this.begDistance) ? false : this.timer > 0 && this.isAttractive(this.begFrom);
		}
	}

	@Override
	public void start() {
		this.wolf.setBegging(true);
		this.timer = 40 + this.wolf.getRandom().nextInt(40);
	}

	@Override
	public void stop() {
		this.wolf.setBegging(false);
		this.begFrom = null;
	}

	@Override
	public void tick() {
		this.wolf
			.getLookControl()
			.lookAt(this.begFrom.x, this.begFrom.y + (double)this.begFrom.getEyeHeight(), this.begFrom.z, 10.0F, (float)this.wolf.getLookPitchSpeed());
		this.timer--;
	}

	private boolean isAttractive(PlayerEntity player) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack != null) {
				if (this.wolf.isTamed() && itemStack.getItem() == Items.BONE) {
					return true;
				}

				if (this.wolf.isBreedingItem(itemStack)) {
					return true;
				}
			}
		}

		return false;
	}
}
