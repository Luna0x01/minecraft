package net.minecraft.util.math;

import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

public class EulerAngle {
	protected final float pitch;
	protected final float yaw;
	protected final float roll;

	public EulerAngle(float f, float g, float h) {
		this.pitch = f;
		this.yaw = g;
		this.roll = h;
	}

	public EulerAngle(NbtList nbtList) {
		this.pitch = nbtList.getFloat(0);
		this.yaw = nbtList.getFloat(1);
		this.roll = nbtList.getFloat(2);
	}

	public NbtList serialize() {
		NbtList nbtList = new NbtList();
		nbtList.add(new NbtFloat(this.pitch));
		nbtList.add(new NbtFloat(this.yaw));
		nbtList.add(new NbtFloat(this.roll));
		return nbtList;
	}

	public boolean equals(Object object) {
		if (!(object instanceof EulerAngle)) {
			return false;
		} else {
			EulerAngle eulerAngle = (EulerAngle)object;
			return this.pitch == eulerAngle.pitch && this.yaw == eulerAngle.yaw && this.roll == eulerAngle.roll;
		}
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getRoll() {
		return this.roll;
	}
}
