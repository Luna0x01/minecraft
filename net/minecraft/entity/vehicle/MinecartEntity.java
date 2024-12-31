package net.minecraft.entity.vehicle;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
	public MinecartEntity(World world) {
		super(EntityType.MINECART, world);
	}

	public MinecartEntity(World world, double d, double e, double f) {
		super(EntityType.MINECART, world, d, e, f);
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if (player.isSneaking()) {
			return false;
		} else if (this.hasPassengers()) {
			return true;
		} else {
			if (!this.world.isClient) {
				player.ride(this);
			}

			return true;
		}
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered) {
			if (this.hasPassengers()) {
				this.removeAllPassengers();
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
