package net.minecraft.client.render.model;

import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class BakedQuadFactory {
	private static final float MIN_SCALE = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
	private static final float MAX_SCALE = 1.0F / (float)Math.cos((float) (Math.PI / 4)) - 1.0F;

	public BakedQuad bake(
		Vector3f from,
		Vector3f to,
		ModelElementFace face,
		Sprite texture,
		Direction side,
		ModelRotation rotation,
		net.minecraft.client.render.model.json.ModelRotation rotation2,
		boolean lockUv,
		boolean shade
	) {
		int[] is = this.packVertexData(face, texture, side, this.getPositionMatrix(from, to), rotation, rotation2, lockUv, shade);
		Direction direction = decodeDirection(is);
		if (lockUv) {
			this.uvLock(is, direction, face.textureReference, texture);
		}

		if (rotation2 == null) {
			this.encodeDirection(is, direction);
		}

		return new BakedQuad(is, face.tintIndex, direction);
	}

	private int[] packVertexData(
		ModelElementFace face,
		Sprite sprite,
		Direction dir,
		float[] matrix,
		ModelRotation rotation1,
		net.minecraft.client.render.model.json.ModelRotation rotation2,
		boolean lockUv,
		boolean shade
	) {
		int[] is = new int[28];

		for (int i = 0; i < 4; i++) {
			this.packVertexData(is, i, dir, face, matrix, sprite, rotation1, rotation2, lockUv, shade);
		}

		return is;
	}

	private int method_10051(Direction dir) {
		float f = this.getBrightness(dir);
		int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
		return 0xFF000000 | i << 16 | i << 8 | i;
	}

	private float getBrightness(Direction dir) {
		switch (dir) {
			case DOWN:
				return 0.5F;
			case UP:
				return 1.0F;
			case NORTH:
			case SOUTH:
				return 0.8F;
			case WEST:
			case EAST:
				return 0.6F;
			default:
				return 1.0F;
		}
	}

	private float[] getPositionMatrix(Vector3f from, Vector3f to) {
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = from.x / 16.0F;
		fs[CubeFace.DirectionIds.DOWN] = from.y / 16.0F;
		fs[CubeFace.DirectionIds.NORTH] = from.z / 16.0F;
		fs[CubeFace.DirectionIds.EAST] = to.x / 16.0F;
		fs[CubeFace.DirectionIds.UP] = to.y / 16.0F;
		fs[CubeFace.DirectionIds.SOUTH] = to.z / 16.0F;
		return fs;
	}

	private void packVertexData(
		int[] vertices,
		int cornerIndex,
		Direction direction,
		ModelElementFace face,
		float[] positionMatrix,
		Sprite sprite,
		ModelRotation rotation,
		net.minecraft.client.render.model.json.ModelRotation rotation2,
		boolean lockUv,
		boolean shade
	) {
		Direction direction2 = rotation.rotate(direction);
		int i = shade ? this.method_10051(direction2) : -1;
		CubeFace.Corner corner = CubeFace.getFace(direction).getCorner(cornerIndex);
		Vector3f vector3f = new Vector3f(positionMatrix[corner.sideX], positionMatrix[corner.sideY], positionMatrix[corner.sideZ]);
		this.rotateVertex(vector3f, rotation2);
		int j = this.transformVertex(vector3f, direction, cornerIndex, rotation, lockUv);
		this.packVertexData(vertices, j, cornerIndex, vector3f, i, sprite, face.textureReference);
	}

	private void packVertexData(int[] vertices, int cornerIndex1, int cornerIndex2, Vector3f position, int color, Sprite sprite, ModelElementTexture texture) {
		int i = cornerIndex1 * 7;
		vertices[i] = Float.floatToRawIntBits(position.x);
		vertices[i + 1] = Float.floatToRawIntBits(position.y);
		vertices[i + 2] = Float.floatToRawIntBits(position.z);
		vertices[i + 3] = color;
		vertices[i + 4] = Float.floatToRawIntBits(sprite.getFrameU((double)texture.method_9999(cornerIndex2)));
		vertices[i + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV((double)texture.method_10001(cornerIndex2)));
	}

	private void rotateVertex(Vector3f vec, net.minecraft.client.render.model.json.ModelRotation rotation) {
		if (rotation != null) {
			Matrix4f matrix4f = this.method_10048();
			Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
			switch (rotation.axis) {
				case X:
					Matrix4f.rotate(rotation.angle * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
					vector3f.set(0.0F, 1.0F, 1.0F);
					break;
				case Y:
					Matrix4f.rotate(rotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
					vector3f.set(1.0F, 0.0F, 1.0F);
					break;
				case Z:
					Matrix4f.rotate(rotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
					vector3f.set(1.0F, 1.0F, 0.0F);
			}

			if (rotation.rescale) {
				if (Math.abs(rotation.angle) == 22.5F) {
					vector3f.scale(MIN_SCALE);
				} else {
					vector3f.scale(MAX_SCALE);
				}

				Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
			} else {
				vector3f.set(1.0F, 1.0F, 1.0F);
			}

			this.transformVertex(vec, new Vector3f(rotation.rotation), matrix4f, vector3f);
		}
	}

	public int transformVertex(Vector3f vertex, Direction direction, int cornerIndex, ModelRotation rotation, boolean shade) {
		if (rotation == ModelRotation.X0_Y0) {
			return cornerIndex;
		} else {
			this.transformVertex(vertex, new Vector3f(0.5F, 0.5F, 0.5F), rotation.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
			return rotation.rotate(direction, cornerIndex);
		}
	}

	private void transformVertex(Vector3f vertex, Vector3f origin, Matrix4f transformationMatrix, Vector3f scale) {
		Vector4f vector4f = new Vector4f(vertex.x - origin.x, vertex.y - origin.y, vertex.z - origin.z, 1.0F);
		Matrix4f.transform(transformationMatrix, vector4f, vector4f);
		vector4f.x = vector4f.x * scale.x;
		vector4f.y = vector4f.y * scale.y;
		vector4f.z = vector4f.z * scale.z;
		vertex.set(vector4f.x + origin.x, vector4f.y + origin.y, vector4f.z + origin.z);
	}

	private Matrix4f method_10048() {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();
		return matrix4f;
	}

	public static Direction decodeDirection(int[] rotationMatrix) {
		Vector3f vector3f = new Vector3f(Float.intBitsToFloat(rotationMatrix[0]), Float.intBitsToFloat(rotationMatrix[1]), Float.intBitsToFloat(rotationMatrix[2]));
		Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(rotationMatrix[7]), Float.intBitsToFloat(rotationMatrix[8]), Float.intBitsToFloat(rotationMatrix[9]));
		Vector3f vector3f3 = new Vector3f(
			Float.intBitsToFloat(rotationMatrix[14]), Float.intBitsToFloat(rotationMatrix[15]), Float.intBitsToFloat(rotationMatrix[16])
		);
		Vector3f vector3f4 = new Vector3f();
		Vector3f vector3f5 = new Vector3f();
		Vector3f vector3f6 = new Vector3f();
		Vector3f.sub(vector3f, vector3f2, vector3f4);
		Vector3f.sub(vector3f3, vector3f2, vector3f5);
		Vector3f.cross(vector3f5, vector3f4, vector3f6);
		float f = (float)Math.sqrt((double)(vector3f6.x * vector3f6.x + vector3f6.y * vector3f6.y + vector3f6.z * vector3f6.z));
		vector3f6.x /= f;
		vector3f6.y /= f;
		vector3f6.z /= f;
		Direction direction = null;
		float g = 0.0F;

		for (Direction direction2 : Direction.values()) {
			Vec3i vec3i = direction2.getVector();
			Vector3f vector3f7 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			float h = Vector3f.dot(vector3f6, vector3f7);
			if (h >= 0.0F && h > g) {
				g = h;
				direction = direction2;
			}
		}

		return direction == null ? Direction.UP : direction;
	}

	public void uvLock(int[] rotationMatrix, Direction dir, ModelElementTexture texture, Sprite sprite) {
		for (int i = 0; i < 4; i++) {
			this.lockVertex(i, rotationMatrix, dir, texture, sprite);
		}
	}

	private void encodeDirection(int[] data, Direction direction) {
		int[] is = new int[data.length];
		System.arraycopy(data, 0, is, 0, data.length);
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = 999.0F;
		fs[CubeFace.DirectionIds.DOWN] = 999.0F;
		fs[CubeFace.DirectionIds.NORTH] = 999.0F;
		fs[CubeFace.DirectionIds.EAST] = -999.0F;
		fs[CubeFace.DirectionIds.UP] = -999.0F;
		fs[CubeFace.DirectionIds.SOUTH] = -999.0F;

		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			float f = Float.intBitsToFloat(is[j]);
			float g = Float.intBitsToFloat(is[j + 1]);
			float h = Float.intBitsToFloat(is[j + 2]);
			if (f < fs[CubeFace.DirectionIds.WEST]) {
				fs[CubeFace.DirectionIds.WEST] = f;
			}

			if (g < fs[CubeFace.DirectionIds.DOWN]) {
				fs[CubeFace.DirectionIds.DOWN] = g;
			}

			if (h < fs[CubeFace.DirectionIds.NORTH]) {
				fs[CubeFace.DirectionIds.NORTH] = h;
			}

			if (f > fs[CubeFace.DirectionIds.EAST]) {
				fs[CubeFace.DirectionIds.EAST] = f;
			}

			if (g > fs[CubeFace.DirectionIds.UP]) {
				fs[CubeFace.DirectionIds.UP] = g;
			}

			if (h > fs[CubeFace.DirectionIds.SOUTH]) {
				fs[CubeFace.DirectionIds.SOUTH] = h;
			}
		}

		CubeFace cubeFace = CubeFace.getFace(direction);

		for (int k = 0; k < 4; k++) {
			int l = 7 * k;
			CubeFace.Corner corner = cubeFace.getCorner(k);
			float m = fs[corner.sideX];
			float n = fs[corner.sideY];
			float o = fs[corner.sideZ];
			data[l] = Float.floatToRawIntBits(m);
			data[l + 1] = Float.floatToRawIntBits(n);
			data[l + 2] = Float.floatToRawIntBits(o);

			for (int p = 0; p < 4; p++) {
				int q = 7 * p;
				float r = Float.intBitsToFloat(is[q]);
				float s = Float.intBitsToFloat(is[q + 1]);
				float t = Float.intBitsToFloat(is[q + 2]);
				if (MathHelper.approximatelyEquals(m, r) && MathHelper.approximatelyEquals(n, s) && MathHelper.approximatelyEquals(o, t)) {
					data[l + 4] = is[q + 4];
					data[l + 4 + 1] = is[q + 4 + 1];
				}
			}
		}
	}

	private void lockVertex(int index, int[] data, Direction direction, ModelElementTexture texture, Sprite sprite) {
		int i = 7 * index;
		float f = Float.intBitsToFloat(data[i]);
		float g = Float.intBitsToFloat(data[i + 1]);
		float h = Float.intBitsToFloat(data[i + 2]);
		if (f < -0.1F || f >= 1.1F) {
			f -= (float)MathHelper.floor(f);
		}

		if (g < -0.1F || g >= 1.1F) {
			g -= (float)MathHelper.floor(g);
		}

		if (h < -0.1F || h >= 1.1F) {
			h -= (float)MathHelper.floor(h);
		}

		float j = 0.0F;
		float k = 0.0F;
		switch (direction) {
			case DOWN:
				j = f * 16.0F;
				k = (1.0F - h) * 16.0F;
				break;
			case UP:
				j = f * 16.0F;
				k = h * 16.0F;
				break;
			case NORTH:
				j = (1.0F - f) * 16.0F;
				k = (1.0F - g) * 16.0F;
				break;
			case SOUTH:
				j = f * 16.0F;
				k = (1.0F - g) * 16.0F;
				break;
			case WEST:
				j = h * 16.0F;
				k = (1.0F - g) * 16.0F;
				break;
			case EAST:
				j = (1.0F - h) * 16.0F;
				k = (1.0F - g) * 16.0F;
		}

		int l = texture.getDirectionIndex(index) * 7;
		data[l + 4] = Float.floatToRawIntBits(sprite.getFrameU((double)j));
		data[l + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV((double)k));
	}
}
