package net.minecraft.block.entity;

import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.util.Tickable;

public class DaylightDetectorBlockEntity extends BlockEntity implements Tickable {
	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient && this.world.getLastUpdateTime() % 20L == 0L) {
			this.block = this.getBlock();
			if (this.block instanceof DaylightDetectorBlock) {
				((DaylightDetectorBlock)this.block).updateState(this.world, this.pos);
			}
		}
	}
}
