package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
	public MinecartEntity(World world) {
		super(world);
	}

	public MinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		AbstractMinecartEntity.method_13302(dataFixer, "MinecartRideable");
	}

	@Override
	public boolean method_6100(PlayerEntity playerEntity, @Nullable ItemStack itemStack, Hand hand) {
		if (playerEntity.isSneaking()) {
			return false;
		} else if (this.hasPassengers()) {
			return true;
		} else {
			if (!this.world.isClient) {
				playerEntity.ride(this);
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
