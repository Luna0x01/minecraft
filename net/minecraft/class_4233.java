package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class class_4233 {
	private static final float field_20799 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
	private static final float field_20800 = 1.0F / (float)Math.cos((float) (Math.PI / 4)) - 1.0F;
	private static final class_4233.class_2873[] field_20801 = new class_4233.class_2873[ModelRotation.values().length * Direction.values().length];
	private static final class_4233.class_2873 field_20802 = new class_4233.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{f, g, h, i}, 0);
		}
	};
	private static final class_4233.class_2873 field_20803 = new class_4233.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{i, 16.0F - f, g, 16.0F - h}, 270);
		}
	};
	private static final class_4233.class_2873 field_20804 = new class_4233.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{16.0F - f, 16.0F - g, 16.0F - h, 16.0F - i}, 0);
		}
	};
	private static final class_4233.class_2873 field_20805 = new class_4233.class_2873() {
		@Override
		ModelElementTexture method_12366(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{16.0F - g, h, 16.0F - i, f}, 90);
		}
	};

	public BakedQuad method_19242(
		class_4306 arg,
		class_4306 arg2,
		ModelElementFace modelElementFace,
		Sprite sprite,
		Direction direction,
		ModelRotation modelRotation,
		@Nullable class_4230 arg3,
		boolean bl,
		boolean bl2
	) {
		ModelElementTexture modelElementTexture = modelElementFace.textureReference;
		if (bl) {
			modelElementTexture = this.method_19237(modelElementFace.textureReference, direction, modelRotation);
		}

		int[] is = this.method_19236(modelElementTexture, sprite, direction, this.method_19241(arg, arg2), modelRotation, arg3, bl2);
		Direction direction2 = method_19246(is);
		if (arg3 == null) {
			this.method_19249(is, direction2);
		}

		return new BakedQuad(is, modelElementFace.tintIndex, direction2, sprite);
	}

	private ModelElementTexture method_19237(ModelElementTexture modelElementTexture, Direction direction, ModelRotation modelRotation) {
		return field_20801[method_19238(modelRotation, direction)].method_12367(modelElementTexture);
	}

	private int[] method_19236(
		ModelElementTexture modelElementTexture, Sprite sprite, Direction direction, float[] fs, ModelRotation modelRotation, @Nullable class_4230 arg, boolean bl
	) {
		int[] is = new int[28];

		for (int i = 0; i < 4; i++) {
			this.method_19248(is, i, direction, modelElementTexture, fs, sprite, modelRotation, arg, bl);
		}

		return is;
	}

	private int method_19245(Direction direction) {
		float f = this.method_19250(direction);
		int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
		return 0xFF000000 | i << 16 | i << 8 | i;
	}

	private float method_19250(Direction direction) {
		switch (direction) {
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

	private float[] method_19241(class_4306 arg, class_4306 arg2) {
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = arg.method_19662() / 16.0F;
		fs[CubeFace.DirectionIds.DOWN] = arg.method_19667() / 16.0F;
		fs[CubeFace.DirectionIds.NORTH] = arg.method_19670() / 16.0F;
		fs[CubeFace.DirectionIds.EAST] = arg2.method_19662() / 16.0F;
		fs[CubeFace.DirectionIds.UP] = arg2.method_19667() / 16.0F;
		fs[CubeFace.DirectionIds.SOUTH] = arg2.method_19670() / 16.0F;
		return fs;
	}

	private void method_19248(
		int[] is,
		int i,
		Direction direction,
		ModelElementTexture modelElementTexture,
		float[] fs,
		Sprite sprite,
		ModelRotation modelRotation,
		@Nullable class_4230 arg,
		boolean bl
	) {
		Direction direction2 = modelRotation.rotate(direction);
		int j = bl ? this.method_19245(direction2) : -1;
		CubeFace.Corner corner = CubeFace.getFace(direction).getCorner(i);
		class_4306 lv = new class_4306(fs[corner.sideX], fs[corner.sideY], fs[corner.sideZ]);
		this.method_19240(lv, arg);
		int k = this.method_19244(lv, direction, i, modelRotation);
		this.method_19247(is, k, i, lv, j, sprite, modelElementTexture);
	}

	private void method_19247(int[] is, int i, int j, class_4306 arg, int k, Sprite sprite, ModelElementTexture modelElementTexture) {
		int l = i * 7;
		is[l] = Float.floatToRawIntBits(arg.method_19662());
		is[l + 1] = Float.floatToRawIntBits(arg.method_19667());
		is[l + 2] = Float.floatToRawIntBits(arg.method_19670());
		is[l + 3] = k;
		is[l + 4] = Float.floatToRawIntBits(sprite.getFrameU((double)modelElementTexture.method_9999(j)));
		is[l + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV((double)modelElementTexture.method_10001(j)));
	}

	private void method_19240(class_4306 arg, @Nullable class_4230 arg2) {
		if (arg2 != null) {
			class_4306 lv;
			class_4306 lv2;
			switch (arg2.field_20781) {
				case X:
					lv = new class_4306(1.0F, 0.0F, 0.0F);
					lv2 = new class_4306(0.0F, 1.0F, 1.0F);
					break;
				case Y:
					lv = new class_4306(0.0F, 1.0F, 0.0F);
					lv2 = new class_4306(1.0F, 0.0F, 1.0F);
					break;
				case Z:
					lv = new class_4306(0.0F, 0.0F, 1.0F);
					lv2 = new class_4306(1.0F, 1.0F, 0.0F);
					break;
				default:
					throw new IllegalArgumentException("There are only 3 axes");
			}

			class_4305 lv9 = new class_4305(lv, arg2.field_20782, true);
			if (arg2.field_20783) {
				if (Math.abs(arg2.field_20782) == 22.5F) {
					lv2.method_19663(field_20799);
				} else {
					lv2.method_19663(field_20800);
				}

				lv2.method_19668(1.0F, 1.0F, 1.0F);
			} else {
				lv2.method_19665(1.0F, 1.0F, 1.0F);
			}

			this.method_19243(arg, new class_4306(arg2.field_20780), lv9, lv2);
		}
	}

	public int method_19244(class_4306 arg, Direction direction, int i, ModelRotation modelRotation) {
		if (modelRotation == ModelRotation.X0_Y0) {
			return i;
		} else {
			this.method_19243(arg, new class_4306(0.5F, 0.5F, 0.5F), modelRotation.method_10378(), new class_4306(1.0F, 1.0F, 1.0F));
			return modelRotation.rotate(direction, i);
		}
	}

	private void method_19243(class_4306 arg, class_4306 arg2, class_4305 arg3, class_4306 arg4) {
		class_4307 lv = new class_4307(
			arg.method_19662() - arg2.method_19662(), arg.method_19667() - arg2.method_19667(), arg.method_19670() - arg2.method_19670(), 1.0F
		);
		lv.method_19676(arg3);
		lv.method_19677(arg4);
		arg.method_19665(lv.method_19673() + arg2.method_19662(), lv.method_19678() + arg2.method_19667(), lv.method_19679() + arg2.method_19670());
	}

	public static Direction method_19246(int[] is) {
		class_4306 lv = new class_4306(Float.intBitsToFloat(is[0]), Float.intBitsToFloat(is[1]), Float.intBitsToFloat(is[2]));
		class_4306 lv2 = new class_4306(Float.intBitsToFloat(is[7]), Float.intBitsToFloat(is[8]), Float.intBitsToFloat(is[9]));
		class_4306 lv3 = new class_4306(Float.intBitsToFloat(is[14]), Float.intBitsToFloat(is[15]), Float.intBitsToFloat(is[16]));
		class_4306 lv4 = new class_4306(lv);
		lv4.method_19666(lv2);
		class_4306 lv5 = new class_4306(lv3);
		lv5.method_19666(lv2);
		class_4306 lv6 = new class_4306(lv5);
		lv6.method_19671(lv4);
		lv6.method_19672();
		Direction direction = null;
		float f = 0.0F;

		for (Direction direction2 : Direction.values()) {
			Vec3i vec3i = direction2.getVector();
			class_4306 lv7 = new class_4306((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			float g = lv6.method_19669(lv7);
			if (g >= 0.0F && g > f) {
				f = g;
				direction = direction2;
			}
		}

		return direction == null ? Direction.UP : direction;
	}

	private void method_19249(int[] is, Direction direction) {
		int[] js = new int[is.length];
		System.arraycopy(is, 0, js, 0, is.length);
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = 999.0F;
		fs[CubeFace.DirectionIds.DOWN] = 999.0F;
		fs[CubeFace.DirectionIds.NORTH] = 999.0F;
		fs[CubeFace.DirectionIds.EAST] = -999.0F;
		fs[CubeFace.DirectionIds.UP] = -999.0F;
		fs[CubeFace.DirectionIds.SOUTH] = -999.0F;

		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			float f = Float.intBitsToFloat(js[j]);
			float g = Float.intBitsToFloat(js[j + 1]);
			float h = Float.intBitsToFloat(js[j + 2]);
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
			is[l] = Float.floatToRawIntBits(m);
			is[l + 1] = Float.floatToRawIntBits(n);
			is[l + 2] = Float.floatToRawIntBits(o);

			for (int p = 0; p < 4; p++) {
				int q = 7 * p;
				float r = Float.intBitsToFloat(js[q]);
				float s = Float.intBitsToFloat(js[q + 1]);
				float t = Float.intBitsToFloat(js[q + 2]);
				if (MathHelper.approximatelyEquals(m, r) && MathHelper.approximatelyEquals(n, s) && MathHelper.approximatelyEquals(o, t)) {
					is[l + 4] = js[q + 4];
					is[l + 4 + 1] = js[q + 4 + 1];
				}
			}
		}
	}

	private static void method_19239(ModelRotation modelRotation, Direction direction, class_4233.class_2873 arg) {
		field_20801[method_19238(modelRotation, direction)] = arg;
	}

	private static int method_19238(ModelRotation modelRotation, Direction direction) {
		return ModelRotation.values().length * direction.ordinal() + modelRotation.ordinal();
	}

	static {
		method_19239(ModelRotation.X0_Y0, Direction.DOWN, field_20802);
		method_19239(ModelRotation.X0_Y0, Direction.EAST, field_20802);
		method_19239(ModelRotation.X0_Y0, Direction.NORTH, field_20802);
		method_19239(ModelRotation.X0_Y0, Direction.SOUTH, field_20802);
		method_19239(ModelRotation.X0_Y0, Direction.UP, field_20802);
		method_19239(ModelRotation.X0_Y0, Direction.WEST, field_20802);
		method_19239(ModelRotation.X0_Y90, Direction.EAST, field_20802);
		method_19239(ModelRotation.X0_Y90, Direction.NORTH, field_20802);
		method_19239(ModelRotation.X0_Y90, Direction.SOUTH, field_20802);
		method_19239(ModelRotation.X0_Y90, Direction.WEST, field_20802);
		method_19239(ModelRotation.X0_Y180, Direction.EAST, field_20802);
		method_19239(ModelRotation.X0_Y180, Direction.NORTH, field_20802);
		method_19239(ModelRotation.X0_Y180, Direction.SOUTH, field_20802);
		method_19239(ModelRotation.X0_Y180, Direction.WEST, field_20802);
		method_19239(ModelRotation.X0_Y270, Direction.EAST, field_20802);
		method_19239(ModelRotation.X0_Y270, Direction.NORTH, field_20802);
		method_19239(ModelRotation.X0_Y270, Direction.SOUTH, field_20802);
		method_19239(ModelRotation.X0_Y270, Direction.WEST, field_20802);
		method_19239(ModelRotation.X90_Y0, Direction.DOWN, field_20802);
		method_19239(ModelRotation.X90_Y0, Direction.SOUTH, field_20802);
		method_19239(ModelRotation.X90_Y90, Direction.DOWN, field_20802);
		method_19239(ModelRotation.X90_Y180, Direction.DOWN, field_20802);
		method_19239(ModelRotation.X90_Y180, Direction.NORTH, field_20802);
		method_19239(ModelRotation.X90_Y270, Direction.DOWN, field_20802);
		method_19239(ModelRotation.X180_Y0, Direction.DOWN, field_20802);
		method_19239(ModelRotation.X180_Y0, Direction.UP, field_20802);
		method_19239(ModelRotation.X270_Y0, Direction.SOUTH, field_20802);
		method_19239(ModelRotation.X270_Y0, Direction.UP, field_20802);
		method_19239(ModelRotation.X270_Y90, Direction.UP, field_20802);
		method_19239(ModelRotation.X270_Y180, Direction.NORTH, field_20802);
		method_19239(ModelRotation.X270_Y180, Direction.UP, field_20802);
		method_19239(ModelRotation.X270_Y270, Direction.UP, field_20802);
		method_19239(ModelRotation.X0_Y270, Direction.UP, field_20803);
		method_19239(ModelRotation.X0_Y90, Direction.DOWN, field_20803);
		method_19239(ModelRotation.X90_Y0, Direction.WEST, field_20803);
		method_19239(ModelRotation.X90_Y90, Direction.WEST, field_20803);
		method_19239(ModelRotation.X90_Y180, Direction.WEST, field_20803);
		method_19239(ModelRotation.X90_Y270, Direction.NORTH, field_20803);
		method_19239(ModelRotation.X90_Y270, Direction.SOUTH, field_20803);
		method_19239(ModelRotation.X90_Y270, Direction.WEST, field_20803);
		method_19239(ModelRotation.X180_Y90, Direction.UP, field_20803);
		method_19239(ModelRotation.X180_Y270, Direction.DOWN, field_20803);
		method_19239(ModelRotation.X270_Y0, Direction.EAST, field_20803);
		method_19239(ModelRotation.X270_Y90, Direction.EAST, field_20803);
		method_19239(ModelRotation.X270_Y90, Direction.NORTH, field_20803);
		method_19239(ModelRotation.X270_Y90, Direction.SOUTH, field_20803);
		method_19239(ModelRotation.X270_Y180, Direction.EAST, field_20803);
		method_19239(ModelRotation.X270_Y270, Direction.EAST, field_20803);
		method_19239(ModelRotation.X0_Y180, Direction.DOWN, field_20804);
		method_19239(ModelRotation.X0_Y180, Direction.UP, field_20804);
		method_19239(ModelRotation.X90_Y0, Direction.NORTH, field_20804);
		method_19239(ModelRotation.X90_Y0, Direction.UP, field_20804);
		method_19239(ModelRotation.X90_Y90, Direction.UP, field_20804);
		method_19239(ModelRotation.X90_Y180, Direction.SOUTH, field_20804);
		method_19239(ModelRotation.X90_Y180, Direction.UP, field_20804);
		method_19239(ModelRotation.X90_Y270, Direction.UP, field_20804);
		method_19239(ModelRotation.X180_Y0, Direction.EAST, field_20804);
		method_19239(ModelRotation.X180_Y0, Direction.NORTH, field_20804);
		method_19239(ModelRotation.X180_Y0, Direction.SOUTH, field_20804);
		method_19239(ModelRotation.X180_Y0, Direction.WEST, field_20804);
		method_19239(ModelRotation.X180_Y90, Direction.EAST, field_20804);
		method_19239(ModelRotation.X180_Y90, Direction.NORTH, field_20804);
		method_19239(ModelRotation.X180_Y90, Direction.SOUTH, field_20804);
		method_19239(ModelRotation.X180_Y90, Direction.WEST, field_20804);
		method_19239(ModelRotation.X180_Y180, Direction.DOWN, field_20804);
		method_19239(ModelRotation.X180_Y180, Direction.EAST, field_20804);
		method_19239(ModelRotation.X180_Y180, Direction.NORTH, field_20804);
		method_19239(ModelRotation.X180_Y180, Direction.SOUTH, field_20804);
		method_19239(ModelRotation.X180_Y180, Direction.UP, field_20804);
		method_19239(ModelRotation.X180_Y180, Direction.WEST, field_20804);
		method_19239(ModelRotation.X180_Y270, Direction.EAST, field_20804);
		method_19239(ModelRotation.X180_Y270, Direction.NORTH, field_20804);
		method_19239(ModelRotation.X180_Y270, Direction.SOUTH, field_20804);
		method_19239(ModelRotation.X180_Y270, Direction.WEST, field_20804);
		method_19239(ModelRotation.X270_Y0, Direction.DOWN, field_20804);
		method_19239(ModelRotation.X270_Y0, Direction.NORTH, field_20804);
		method_19239(ModelRotation.X270_Y90, Direction.DOWN, field_20804);
		method_19239(ModelRotation.X270_Y180, Direction.DOWN, field_20804);
		method_19239(ModelRotation.X270_Y180, Direction.SOUTH, field_20804);
		method_19239(ModelRotation.X270_Y270, Direction.DOWN, field_20804);
		method_19239(ModelRotation.X0_Y90, Direction.UP, field_20805);
		method_19239(ModelRotation.X0_Y270, Direction.DOWN, field_20805);
		method_19239(ModelRotation.X90_Y0, Direction.EAST, field_20805);
		method_19239(ModelRotation.X90_Y90, Direction.EAST, field_20805);
		method_19239(ModelRotation.X90_Y90, Direction.NORTH, field_20805);
		method_19239(ModelRotation.X90_Y90, Direction.SOUTH, field_20805);
		method_19239(ModelRotation.X90_Y180, Direction.EAST, field_20805);
		method_19239(ModelRotation.X90_Y270, Direction.EAST, field_20805);
		method_19239(ModelRotation.X270_Y0, Direction.WEST, field_20805);
		method_19239(ModelRotation.X180_Y90, Direction.DOWN, field_20805);
		method_19239(ModelRotation.X180_Y270, Direction.UP, field_20805);
		method_19239(ModelRotation.X270_Y90, Direction.WEST, field_20805);
		method_19239(ModelRotation.X270_Y180, Direction.WEST, field_20805);
		method_19239(ModelRotation.X270_Y270, Direction.NORTH, field_20805);
		method_19239(ModelRotation.X270_Y270, Direction.SOUTH, field_20805);
		method_19239(ModelRotation.X270_Y270, Direction.WEST, field_20805);
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
