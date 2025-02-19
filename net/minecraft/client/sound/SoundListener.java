package net.minecraft.client.sound;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.openal.AL10;

public class SoundListener {
	private float volume = 1.0F;
	private Vec3d pos = Vec3d.ZERO;

	public void setPosition(Vec3d position) {
		this.pos = position;
		AL10.alListener3f(4100, (float)position.x, (float)position.y, (float)position.z);
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public void setOrientation(Vec3f at, Vec3f up) {
		AL10.alListenerfv(4111, new float[]{at.getX(), at.getY(), at.getZ(), up.getX(), up.getY(), up.getZ()});
	}

	public void setVolume(float volume) {
		AL10.alListenerf(4106, volume);
		this.volume = volume;
	}

	public float getVolume() {
		return this.volume;
	}

	public void init() {
		this.setPosition(Vec3d.ZERO);
		this.setOrientation(Vec3f.NEGATIVE_Z, Vec3f.POSITIVE_Y);
	}
}
