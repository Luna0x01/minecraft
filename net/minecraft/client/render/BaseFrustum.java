package net.minecraft.client.render;

public class BaseFrustum {
	public float[][] homogeneousCoordinates = new float[6][4];
	public float[] projectionMatrix = new float[16];
	public float[] modelMatrix = new float[16];
	public float[] clipMatrix = new float[16];

	private double multiply(float[] frustum, double x, double y, double z) {
		return (double)frustum[0] * x + (double)frustum[1] * y + (double)frustum[2] * z + (double)frustum[3];
	}

	public boolean isInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		for (int i = 0; i < 6; i++) {
			float[] fs = this.homogeneousCoordinates[i];
			if (!(this.multiply(fs, minX, minY, minZ) > 0.0)
				&& !(this.multiply(fs, maxX, minY, minZ) > 0.0)
				&& !(this.multiply(fs, minX, maxY, minZ) > 0.0)
				&& !(this.multiply(fs, maxX, maxY, minZ) > 0.0)
				&& !(this.multiply(fs, minX, minY, maxZ) > 0.0)
				&& !(this.multiply(fs, maxX, minY, maxZ) > 0.0)
				&& !(this.multiply(fs, minX, maxY, maxZ) > 0.0)
				&& !(this.multiply(fs, maxX, maxY, maxZ) > 0.0)) {
				return false;
			}
		}

		return true;
	}
}
