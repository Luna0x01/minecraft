package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;

public class Frustum extends BaseFrustum {
	private static Frustum instance = new Frustum();
	private FloatBuffer projectionBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private FloatBuffer modelBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private FloatBuffer field_2075 = GlAllocationUtils.allocateFloatBuffer(16);

	public static BaseFrustum getInstance() {
		instance.start();
		return instance;
	}

	private void normalize(float[] frustum) {
		float f = MathHelper.sqrt(frustum[0] * frustum[0] + frustum[1] * frustum[1] + frustum[2] * frustum[2]);
		frustum[0] /= f;
		frustum[1] /= f;
		frustum[2] /= f;
		frustum[3] /= f;
	}

	public void start() {
		this.projectionBuffer.clear();
		this.modelBuffer.clear();
		this.field_2075.clear();
		GlStateManager.getFloat(2983, this.projectionBuffer);
		GlStateManager.getFloat(2982, this.modelBuffer);
		float[] fs = this.projectionMatrix;
		float[] gs = this.modelMatrix;
		this.projectionBuffer.flip().limit(16);
		this.projectionBuffer.get(fs);
		this.modelBuffer.flip().limit(16);
		this.modelBuffer.get(gs);
		this.clipMatrix[0] = gs[0] * fs[0] + gs[1] * fs[4] + gs[2] * fs[8] + gs[3] * fs[12];
		this.clipMatrix[1] = gs[0] * fs[1] + gs[1] * fs[5] + gs[2] * fs[9] + gs[3] * fs[13];
		this.clipMatrix[2] = gs[0] * fs[2] + gs[1] * fs[6] + gs[2] * fs[10] + gs[3] * fs[14];
		this.clipMatrix[3] = gs[0] * fs[3] + gs[1] * fs[7] + gs[2] * fs[11] + gs[3] * fs[15];
		this.clipMatrix[4] = gs[4] * fs[0] + gs[5] * fs[4] + gs[6] * fs[8] + gs[7] * fs[12];
		this.clipMatrix[5] = gs[4] * fs[1] + gs[5] * fs[5] + gs[6] * fs[9] + gs[7] * fs[13];
		this.clipMatrix[6] = gs[4] * fs[2] + gs[5] * fs[6] + gs[6] * fs[10] + gs[7] * fs[14];
		this.clipMatrix[7] = gs[4] * fs[3] + gs[5] * fs[7] + gs[6] * fs[11] + gs[7] * fs[15];
		this.clipMatrix[8] = gs[8] * fs[0] + gs[9] * fs[4] + gs[10] * fs[8] + gs[11] * fs[12];
		this.clipMatrix[9] = gs[8] * fs[1] + gs[9] * fs[5] + gs[10] * fs[9] + gs[11] * fs[13];
		this.clipMatrix[10] = gs[8] * fs[2] + gs[9] * fs[6] + gs[10] * fs[10] + gs[11] * fs[14];
		this.clipMatrix[11] = gs[8] * fs[3] + gs[9] * fs[7] + gs[10] * fs[11] + gs[11] * fs[15];
		this.clipMatrix[12] = gs[12] * fs[0] + gs[13] * fs[4] + gs[14] * fs[8] + gs[15] * fs[12];
		this.clipMatrix[13] = gs[12] * fs[1] + gs[13] * fs[5] + gs[14] * fs[9] + gs[15] * fs[13];
		this.clipMatrix[14] = gs[12] * fs[2] + gs[13] * fs[6] + gs[14] * fs[10] + gs[15] * fs[14];
		this.clipMatrix[15] = gs[12] * fs[3] + gs[13] * fs[7] + gs[14] * fs[11] + gs[15] * fs[15];
		float[] hs = this.homogeneousCoordinates[0];
		hs[0] = this.clipMatrix[3] - this.clipMatrix[0];
		hs[1] = this.clipMatrix[7] - this.clipMatrix[4];
		hs[2] = this.clipMatrix[11] - this.clipMatrix[8];
		hs[3] = this.clipMatrix[15] - this.clipMatrix[12];
		this.normalize(hs);
		float[] is = this.homogeneousCoordinates[1];
		is[0] = this.clipMatrix[3] + this.clipMatrix[0];
		is[1] = this.clipMatrix[7] + this.clipMatrix[4];
		is[2] = this.clipMatrix[11] + this.clipMatrix[8];
		is[3] = this.clipMatrix[15] + this.clipMatrix[12];
		this.normalize(is);
		float[] js = this.homogeneousCoordinates[2];
		js[0] = this.clipMatrix[3] + this.clipMatrix[1];
		js[1] = this.clipMatrix[7] + this.clipMatrix[5];
		js[2] = this.clipMatrix[11] + this.clipMatrix[9];
		js[3] = this.clipMatrix[15] + this.clipMatrix[13];
		this.normalize(js);
		float[] ks = this.homogeneousCoordinates[3];
		ks[0] = this.clipMatrix[3] - this.clipMatrix[1];
		ks[1] = this.clipMatrix[7] - this.clipMatrix[5];
		ks[2] = this.clipMatrix[11] - this.clipMatrix[9];
		ks[3] = this.clipMatrix[15] - this.clipMatrix[13];
		this.normalize(ks);
		float[] ls = this.homogeneousCoordinates[4];
		ls[0] = this.clipMatrix[3] - this.clipMatrix[2];
		ls[1] = this.clipMatrix[7] - this.clipMatrix[6];
		ls[2] = this.clipMatrix[11] - this.clipMatrix[10];
		ls[3] = this.clipMatrix[15] - this.clipMatrix[14];
		this.normalize(ls);
		float[] ms = this.homogeneousCoordinates[5];
		ms[0] = this.clipMatrix[3] + this.clipMatrix[2];
		ms[1] = this.clipMatrix[7] + this.clipMatrix[6];
		ms[2] = this.clipMatrix[11] + this.clipMatrix[10];
		ms[3] = this.clipMatrix[15] + this.clipMatrix[14];
		this.normalize(ms);
	}
}
