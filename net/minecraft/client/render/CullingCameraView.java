package net.minecraft.client.render;

import net.minecraft.util.math.Box;

public class CullingCameraView implements CameraView {
	private final BaseFrustum clipper;
	private double x;
	private double y;
	private double z;

	public CullingCameraView() {
		this(Frustum.getInstance());
	}

	public CullingCameraView(BaseFrustum baseFrustum) {
		this.clipper = baseFrustum;
	}

	@Override
	public void setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean isBoxInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.clipper.isInFrustum(minX - this.x, minY - this.y, minZ - this.z, maxX - this.x, maxY - this.y, maxZ - this.z);
	}

	@Override
	public boolean isBoxInFrustum(Box box) {
		return this.isBoxInFrustum(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}
}
