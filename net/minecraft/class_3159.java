package net.minecraft;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class class_3159 extends TameableEntity {
	private int field_15569;

	public class_3159(World world) {
		super(world);
	}

	public boolean method_14115(PlayerEntity playerEntity) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", this.getSavedEntityId());
		this.toNbt(nbtCompound);
		if (playerEntity.method_14160(nbtCompound)) {
			this.world.removeEntity(this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		this.field_15569++;
		super.tick();
	}

	public boolean method_14114() {
		return this.field_15569 > 100;
	}
}
