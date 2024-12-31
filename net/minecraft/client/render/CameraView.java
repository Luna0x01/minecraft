package net.minecraft.client.render;

import net.minecraft.util.math.Box;

public interface CameraView {
	boolean isBoxInFrustum(Box box);

	void setPos(double x, double y, double z);
}
