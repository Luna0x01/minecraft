package net.minecraft.entity.vehicle;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
	public MinecartEntity(World world) {
		super(world);
	}

	public MinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		if (this.rider != null && this.rider instanceof PlayerEntity && this.rider != player) {
			return true;
		} else if (this.rider != null && this.rider != player) {
			return false;
		} else {
			if (!this.world.isClient) {
				player.startRiding(this);
			}

			return true;
		}
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered) {
			if (this.rider != null) {
				this.rider.startRiding(null);
			}

			if (this.getDamageWobbleTicks() == 0) {
				this.setDamageWobbleSide(-this.getDamageWobbleSide());
				this.setDamageWobbleTicks(10);
				this.setDamageWobbleStrength(50.0F);
				this.scheduleVelocityUpdate();
			}
		}
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.RIDEABLE;
	}
}
