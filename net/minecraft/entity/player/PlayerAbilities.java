package net.minecraft.entity.player;

import net.minecraft.nbt.NbtCompound;

public class PlayerAbilities {
	public boolean invulnerable;
	public boolean flying;
	public boolean allowFlying;
	public boolean creativeMode;
	public boolean allowModifyWorld = true;
	private double field_17089 = 0.05F;
	private float walkSpeed = 0.1F;

	public void serialize(NbtCompound nbt) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putBoolean("invulnerable", this.invulnerable);
		nbtCompound.putBoolean("flying", this.flying);
		nbtCompound.putBoolean("mayfly", this.allowFlying);
		nbtCompound.putBoolean("instabuild", this.creativeMode);
		nbtCompound.putBoolean("mayBuild", this.allowModifyWorld);
		nbtCompound.putFloat("flySpeed", (float)this.field_17089);
		nbtCompound.putFloat("walkSpeed", this.walkSpeed);
		nbt.put("abilities", nbtCompound);
	}

	public void deserialize(NbtCompound nbt) {
		if (nbt.contains("abilities", 10)) {
			NbtCompound nbtCompound = nbt.getCompound("abilities");
			this.invulnerable = nbtCompound.getBoolean("invulnerable");
			this.flying = nbtCompound.getBoolean("flying");
			this.allowFlying = nbtCompound.getBoolean("mayfly");
			this.creativeMode = nbtCompound.getBoolean("instabuild");
			if (nbtCompound.contains("flySpeed", 99)) {
				this.field_17089 = (double)nbtCompound.getFloat("flySpeed");
				this.walkSpeed = nbtCompound.getFloat("walkSpeed");
			}

			if (nbtCompound.contains("mayBuild", 1)) {
				this.allowModifyWorld = nbtCompound.getBoolean("mayBuild");
			}
		}
	}

	public float getFlySpeed() {
		return (float)this.field_17089;
	}

	public void method_15919(double d) {
		this.field_17089 = d;
	}

	public float getWalkSpeed() {
		return this.walkSpeed;
	}

	public void setWalkSpeed(float walkSpeed) {
		this.walkSpeed = walkSpeed;
	}
}
