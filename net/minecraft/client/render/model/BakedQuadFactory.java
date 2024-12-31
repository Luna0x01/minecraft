package net.minecraft.client.render.model;

import javax.annotation.Nullable;
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
	private static final BakedQuadFactory.class_2873[] field_13555 = new BakedQuadFactory.class_2873[ModelRotation.values().length * Direction.values().length];
	private static final BakedQuadFactory.class_2873 field_13556 = new BakedQuadFactory.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{f, g, h, i}, 0);
		}
	};
	private static final BakedQuadFactory.class_2873 field_13557 = new BakedQuadFactory.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{i, 16.0F - f, g, 16.0F - h}, 270);
		}
	};
	private static final BakedQuadFactory.class_2873 field_13558 = new BakedQuadFactory.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{16.0F - f, 16.0F - g, 16.0F - h, 16.0F - i}, 0);
		}
	};
	private static final BakedQuadFactory.class_2873 field_13559 = new BakedQuadFactory.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{16.0F - g, h, 16.0F - i, f}, 90);
		}
	};

	public BakedQuad bake(
		Vector3f from,
		Vector3f to,
		ModelElementFace face,
		Sprite texture,
		Direction side,
		ModelRotation rotation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation rotation2,
		boolean lockUv,
		boolean shade
	) {
		ModelElementTexture modelElementTexture = face.textureReference;
		if (lockUv) {
			modelElementTexture = this.method_12363(face.textureReference, side, rotation);
		}

		int[] is = this.method_10050(modelElementTexture, texture, side, this.getPositionMatrix(from, to), rotation, rotation2, shade);
		Direction direction = decodeDirection(is);
		if (rotation2 == null) {
			this.encodeDirection(is, direction);
		}

		return new BakedQuad(is, face.tintIndex, direction, texture);
	}

	private ModelElementTexture method_12363(ModelElementTexture modelElementTexture, Direction direction, ModelRotation modelRotation) {
		return field_13555[method_12364(modelRotation, direction)].method_12367(modelElementTexture);
	}

	private int[] method_10050(
		ModelElementTexture modelElementTexture,
		Sprite sprite,
		Direction direction,
		float[] fs,
		ModelRotation modelRotation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation2,
		boolean bl
	) {
		int[] is = new int[28];

		for (int i = 0; i < 4; i++) {
			this.method_10059(is, i, direction, modelElementTexture, fs, sprite, modelRotation, modelRotation2, bl);
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

	private void method_10059(
		int[] is,
		int i,
		Direction direction,
		ModelElementTexture modelElementTexture,
		float[] fs,
		Sprite sprite,
		ModelRotation modelRotation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation2,
		boolean bl
	) {
		Direction direction2 = modelRotation.rotate(direction);
		int j = bl ? this.method_10051(direction2) : -1;
		CubeFace.Corner corner = CubeFace.getFace(direction).getCorner(i);
		Vector3f vector3f = new Vector3f(fs[corner.sideX], fs[corner.sideY], fs[corner.sideZ]);
		this.rotateVertex(vector3f, modelRotation2);
		int k = this.method_10053(vector3f, direction, i, modelRotation);
		this.packVertexData(is, k, i, vector3f, j, sprite, modelElementTexture);
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

	private void rotateVertex(Vector3f vec, @Nullable net.minecraft.client.render.model.json.ModelRotation rotation) {
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

	public int method_10053(Vector3f vector3f, Direction direction, int i, ModelRotation modelRotation) {
		if (modelRotation == ModelRotation.X0_Y0) {
			return i;
		} else {
			this.transformVertex(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), modelRotation.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
			return modelRotation.rotate(direction, i);
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

	private static void method_12365(ModelRotation modelRotation, Direction direction, BakedQuadFactory.class_2873 arg) {
		field_13555[method_12364(modelRotation, direction)] = arg;
	}

	private static int method_12364(ModelRotation modelRotation, Direction direction) {
		return ModelRotation.values().length * direction.ordinal() + modelRotation.ordinal();
	}

	static {
		method_12365(ModelRotation.X0_Y0, Direction.DOWN, field_13556);
		method_12365(ModelRotation.X0_Y0, Direction.EAST, field_13556);
		method_12365(ModelRotation.X0_Y0, Direction.NORTH, field_13556);
		method_12365(ModelRotation.X0_Y0, Direction.SOUTH, field_13556);
		method_12365(ModelRotation.X0_Y0, Direction.UP, field_13556);
		method_12365(ModelRotation.X0_Y0, Direction.WEST, field_13556);
		method_12365(ModelRotation.X0_Y90, Direction.EAST, field_13556);
		method_12365(ModelRotation.X0_Y90, Direction.NORTH, field_13556);
		method_12365(ModelRotation.X0_Y90, Direction.SOUTH, field_13556);
		method_12365(ModelRotation.X0_Y90, Direction.WEST, field_13556);
		method_12365(ModelRotation.X0_Y180, Direction.EAST, field_13556);
		method_12365(ModelRotation.X0_Y180, Direction.NORTH, field_13556);
		method_12365(ModelRotation.X0_Y180, Direction.SOUTH, field_13556);
		method_12365(ModelRotation.X0_Y180, Direction.WEST, field_13556);
		method_12365(ModelRotation.X0_Y270, Direction.EAST, field_13556);
		method_12365(ModelRotation.X0_Y270, Direction.NORTH, field_13556);
		method_12365(ModelRotation.X0_Y270, Direction.SOUTH, field_13556);
		method_12365(ModelRotation.X0_Y270, Direction.WEST, field_13556);
		method_12365(ModelRotation.X90_Y0, Direction.DOWN, field_13556);
		method_12365(ModelRotation.X90_Y0, Direction.SOUTH, field_13556);
		method_12365(ModelRotation.X90_Y90, Direction.DOWN, field_13556);
		method_12365(ModelRotation.X90_Y180, Direction.DOWN, field_13556);
		method_12365(ModelRotation.X90_Y180, Direction.NORTH, field_13556);
		method_12365(ModelRotation.X90_Y270, Direction.DOWN, field_13556);
		method_12365(ModelRotation.X180_Y0, Direction.DOWN, field_13556);
		method_12365(ModelRotation.X180_Y0, Direction.UP, field_13556);
		method_12365(ModelRotation.X270_Y0, Direction.SOUTH, field_13556);
		method_12365(ModelRotation.X270_Y0, Direction.UP, field_13556);
		method_12365(ModelRotation.X270_Y90, Direction.UP, field_13556);
		method_12365(ModelRotation.X270_Y180, Direction.NORTH, field_13556);
		method_12365(ModelRotation.X270_Y180, Direction.UP, field_13556);
		method_12365(ModelRotation.X270_Y270, Direction.UP, field_13556);
		method_12365(ModelRotation.X0_Y270, Direction.UP, field_13557);
		method_12365(ModelRotation.X0_Y90, Direction.DOWN, field_13557);
		method_12365(ModelRotation.X90_Y0, Direction.WEST, field_13557);
		method_12365(ModelRotation.X90_Y90, Direction.WEST, field_13557);
		method_12365(ModelRotation.X90_Y180, Direction.WEST, field_13557);
		method_12365(ModelRotation.X90_Y270, Direction.NORTH, field_13557);
		method_12365(ModelRotation.X90_Y270, Direction.SOUTH, field_13557);
		method_12365(ModelRotation.X90_Y270, Direction.WEST, field_13557);
		method_12365(ModelRotation.X180_Y90, Direction.UP, field_13557);
		method_12365(ModelRotation.X180_Y270, Direction.DOWN, field_13557);
		method_12365(ModelRotation.X270_Y0, Direction.EAST, field_13557);
		method_12365(ModelRotation.X270_Y90, Direction.EAST, field_13557);
		method_12365(ModelRotation.X270_Y90, Direction.NORTH, field_13557);
		method_12365(ModelRotation.X270_Y90, Direction.SOUTH, field_13557);
		method_12365(ModelRotation.X270_Y180, Direction.EAST, field_13557);
		method_12365(ModelRotation.X270_Y270, Direction.EAST, field_13557);
		method_12365(ModelRotation.X0_Y180, Direction.DOWN, field_13558);
		method_12365(ModelRotation.X0_Y180, Direction.UP, field_13558);
		method_12365(ModelRotation.X90_Y0, Direction.NORTH, field_13558);
		method_12365(ModelRotation.X90_Y0, Direction.UP, field_13558);
		method_12365(ModelRotation.X90_Y90, Direction.UP, field_13558);
		method_12365(ModelRotation.X90_Y180, Direction.SOUTH, field_13558);
		method_12365(ModelRotation.X90_Y180, Direction.UP, field_13558);
		method_12365(ModelRotation.X90_Y270, Direction.UP, field_13558);
		method_12365(ModelRotation.X180_Y0, Direction.EAST, field_13558);
		method_12365(ModelRotation.X180_Y0, Direction.NORTH, field_13558);
		method_12365(ModelRotation.X180_Y0, Direction.SOUTH, field_13558);
		method_12365(ModelRotation.X180_Y0, Direction.WEST, field_13558);
		method_12365(ModelRotation.X180_Y90, Direction.EAST, field_13558);
		method_12365(ModelRotation.X180_Y90, Direction.NORTH, field_13558);
		method_12365(ModelRotation.X180_Y90, Direction.SOUTH, field_13558);
		method_12365(ModelRotation.X180_Y90, Direction.WEST, field_13558);
		method_12365(ModelRotation.X180_Y180, Direction.DOWN, field_13558);
		method_12365(ModelRotation.X180_Y180, Direction.EAST, field_13558);
		method_12365(ModelRotation.X180_Y180, Direction.NORTH, field_13558);
		method_12365(ModelRotation.X180_Y180, Direction.SOUTH, field_13558);
		method_12365(ModelRotation.X180_Y180, Direction.UP, field_13558);
		method_12365(ModelRotation.X180_Y180, Direction.WEST, field_13558);
		method_12365(ModelRotation.X180_Y270, Direction.EAST, field_13558);
		method_12365(ModelRotation.X180_Y270, Direction.NORTH, field_13558);
		method_12365(ModelRotation.X180_Y270, Direction.SOUTH, field_13558);
		method_12365(ModelRotation.X180_Y270, Direction.WEST, field_13558);
		method_12365(ModelRotation.X270_Y0, Direction.DOWN, field_13558);
		method_12365(ModelRotation.X270_Y0, Direction.NORTH, field_13558);
		method_12365(ModelRotation.X270_Y90, Direction.DOWN, field_13558);
		method_12365(ModelRotation.X270_Y180, Direction.DOWN, field_13558);
		method_12365(ModelRotation.X270_Y180, Direction.SOUTH, field_13558);
		method_12365(ModelRotation.X270_Y270, Direction.DOWN, field_13558);
		method_12365(ModelRotation.X0_Y90, Direction.UP, field_13559);
		method_12365(ModelRotation.X0_Y270, Direction.DOWN, field_13559);
		method_12365(ModelRotation.X90_Y0, Direction.EAST, field_13559);
		method_12365(ModelRotation.X90_Y90, Direction.EAST, field_13559);
		method_12365(ModelRotation.X90_Y90, Direction.NORTH, field_13559);
		method_12365(ModelRotation.X90_Y90, Direction.SOUTH, field_13559);
		method_12365(ModelRotation.X90_Y180, Direction.EAST, field_13559);
		method_12365(ModelRotation.X90_Y270, Direction.EAST, field_13559);
		method_12365(ModelRotation.X270_Y0, Direction.WEST, field_13559);
		method_12365(ModelRotation.X180_Y90, Direction.DOWN, field_13559);
		method_12365(ModelRotation.X180_Y270, Direction.UP, field_13559);
		method_12365(ModelRotation.X270_Y90, Direction.WEST, field_13559);
		method_12365(ModelRotation.X270_Y180, Direction.WEST, field_13559);
		method_12365(ModelRotation.X270_Y270, Direction.NORTH, field_13559);
		method_12365(ModelRotation.X270_Y270, Direction.SOUTH, field_13559);
		method_12365(ModelRotation.X270_Y270, Direction.WEST, field_13559);
	}

	abstract static class class_2873 {
		private class_2873() {
		}

		public ModelElementTexture method_12367(ModelElementTexture modelElementTexture) {
			float f = modelElementTexture.method_9999(modelElementTexture.getDirectionIndex(0));
			float g = modelElementTexture.method_10001(modelElementTexture.getDirectionIndex(0));
			float h = modelElementTexture.method_9999(modelElementTexture.getDirectionIndex(2));
			float i = modelElementTexture.method_10001(modelElementTexture.getDirectionIndex(2));
			return this.method_12366(f, g, h, i);
		}

		abstract ModelElementTexture method_12366(float f, float g, float h, float i);
	}
}
