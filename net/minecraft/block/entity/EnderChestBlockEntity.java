package net.minecraft.block.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class EnderChestBlockEntity extends BlockEntity implements Tickable {
	public float animationProgress;
	public float lastAnimationProgress;
	public int viewerCount;
	private int ticks;

	@Override
	public void tick() {
		if (++this.ticks % 20 * 4 == 0) {
			this.world.addBlockAction(this.pos, Blocks.ENDERCHEST, 1, this.viewerCount);
		}

		this.lastAnimationProgress = this.animationProgress;
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		float f = 0.1F;
		if (this.viewerCount > 0 && this.animationProgress == 0.0F) {
			double d = (double)i + 0.5;
			double e = (double)k + 0.5;
			this.world.playSound(d, (double)j + 0.5, e, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (this.viewerCount == 0 && this.animationProgress > 0.0F || this.viewerCount > 0 && this.animationProgress < 1.0F) {
			float g = this.animationProgress;
			if (this.viewerCount > 0) {
				this.animationProgress += f;
			} else {
				this.animationProgress -= f;
			}

			if (this.animationProgress > 1.0F) {
				this.animationProgress = 1.0F;
			}

			float h = 0.5F;
			if (this.animationProgress < h && g >= h) {
				double l = (double)i + 0.5;
				double m = (double)k + 0.5;
				this.world.playSound(l, (double)j + 0.5, m, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (this.animationProgress < 0.0F) {
				this.animationProgress = 0.0F;
			}
		}
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		if (code == 1) {
			this.viewerCount = data;
			return true;
		} else {
			return super.onBlockAction(code, data);
		}
	}

	@Override
	public void markRemoved() {
		this.resetBlock();
		super.markRemoved();
	}

	public void onOpen() {
		this.viewerCount++;
		this.world.addBlockAction(this.pos, Blocks.ENDERCHEST, 1, this.viewerCount);
	}

	public void onClose() {
		this.viewerCount--;
		this.world.addBlockAction(this.pos, Blocks.ENDERCHEST, 1, this.viewerCount);
	}

	public boolean canPlayerUse(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}
}
