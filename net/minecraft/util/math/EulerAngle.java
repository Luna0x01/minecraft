package net.minecraft.util.math;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

public class EulerAngle {
	protected final float pitch;
	protected final float yaw;
	protected final float roll;

	public EulerAngle(float f, float g, float h) {
		this.pitch = !Float.isInfinite(f) && !Float.isNaN(f) ? f % 360.0F : 0.0F;
		this.yaw = !Float.isInfinite(g) && !Float.isNaN(g) ? g % 360.0F : 0.0F;
		this.roll = !Float.isInfinite(h) && !Float.isNaN(h) ? h % 360.0F : 0.0F;
	}

	public EulerAngle(NbtList nbtList) {
		this(nbtList.getFloat(0), nbtList.getFloat(1), nbtList.getFloat(2));
	}

	public NbtList serialize() {
		NbtList nbtList = new NbtList();
		nbtList.add((NbtElement)(new NbtFloat(this.pitch)));
		nbtList.add((NbtElement)(new NbtFloat(this.yaw)));
		nbtList.add((NbtElement)(new NbtFloat(this.roll)));
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
