package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.util.Util;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
	private static final int field_29850 = 1024;
	private static final float field_29851 = 1024.0F;
	private static final long field_29852 = 61440L;
	private static final long field_29853 = 16384L;
	private static final long field_29854 = -4611686018427387904L;
	private static final long field_29855 = Long.MIN_VALUE;
	public static final float field_29844 = (float) Math.PI;
	public static final float field_29845 = (float) (Math.PI / 2);
	public static final float field_29846 = (float) (Math.PI * 2);
	public static final float field_29847 = (float) (Math.PI / 180.0);
	public static final float field_29848 = 180.0F / (float)Math.PI;
	public static final float field_29849 = 1.0E-5F;
	public static final float SQUARE_ROOT_OF_TWO = sqrt(2.0F);
	private static final float field_29856 = 10430.378F;
	private static final float[] SINE_TABLE = Util.make(new float[65536], sineTable -> {
		for (int ix = 0; ix < sineTable.length; ix++) {
			sineTable[ix] = (float)Math.sin((double)ix * Math.PI * 2.0 / 65536.0);
		}
	});
	private static final Random RANDOM = new Random();
	private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
		0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
	};
	private static final double field_29857 = 0.16666666666666666;
	private static final int field_29858 = 8;
	private static final int field_29859 = 257;
	private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
	private static final double[] ARCSINE_TABLE = new double[257];
	private static final double[] COSINE_TABLE = new double[257];

	public static float sin(float value) {
		return SINE_TABLE[(int)(value * 10430.378F) & 65535];
	}

	public static float cos(float value) {
		return SINE_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt(float value) {
		return (float)Math.sqrt((double)value);
	}

	public static int floor(float value) {
		int i = (int)value;
		return value < (float)i ? i - 1 : i;
	}

	public static int fastFloor(double value) {
		return (int)(value + 1024.0) - 1024;
	}

	public static int floor(double value) {
		int i = (int)value;
		return value < (double)i ? i - 1 : i;
	}

	public static long lfloor(double value) {
		long l = (long)value;
		return value < (double)l ? l - 1L : l;
	}

	public static int method_34953(double d) {
		return (int)(d >= 0.0 ? d : -d + 1.0);
	}

	public static float abs(float value) {
		return Math.abs(value);
	}

	public static int abs(int value) {
		return Math.abs(value);
	}

	public static int ceil(float value) {
		int i = (int)value;
		return value > (float)i ? i + 1 : i;
	}

	public static int ceil(double value) {
		int i = (int)value;
		return value > (double)i ? i + 1 : i;
	}

	public static byte clamp(byte value, byte min, byte max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static long clamp(long value, long min, long max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static double clampedLerp(double start, double end, double delta) {
		if (delta < 0.0) {
			return start;
		} else {
			return delta > 1.0 ? end : lerp(delta, start, end);
		}
	}

	public static float method_37166(float f, float g, float h) {
		if (h < 0.0F) {
			return f;
		} else {
			return h > 1.0F ? g : lerp(h, f, g);
		}
	}

	public static double absMax(double a, double b) {
		if (a < 0.0) {
			a = -a;
		}

		if (b < 0.0) {
			b = -b;
		}

		return a > b ? a : b;
	}

	public static int floorDiv(int dividend, int divisor) {
		return Math.floorDiv(dividend, divisor);
	}

	public static int nextInt(Random random, int min, int max) {
		return min >= max ? min : random.nextInt(max - min + 1) + min;
	}

	public static float nextFloat(Random random, float min, float max) {
		return min >= max ? min : random.nextFloat() * (max - min) + min;
	}

	public static double nextDouble(Random random, double min, double max) {
		return min >= max ? min : random.nextDouble() * (max - min) + min;
	}

	public static double average(long[] array) {
		long l = 0L;

		for (long m : array) {
			l += m;
		}

		return (double)l / (double)array.length;
	}

	public static boolean approximatelyEquals(float a, float b) {
		return Math.abs(b - a) < 1.0E-5F;
	}

	public static boolean approximatelyEquals(double a, double b) {
		return Math.abs(b - a) < 1.0E-5F;
	}

	public static int floorMod(int dividend, int divisor) {
		return Math.floorMod(dividend, divisor);
	}

	public static float floorMod(float dividend, float divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

	public static double floorMod(double dividend, double divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

	public static int wrapDegrees(int degrees) {
		int i = degrees % 360;
		if (i >= 180) {
			i -= 360;
		}

		if (i < -180) {
			i += 360;
		}

		return i;
	}

	public static float wrapDegrees(float degrees) {
		float f = degrees % 360.0F;
		if (f >= 180.0F) {
			f -= 360.0F;
		}

		if (f < -180.0F) {
			f += 360.0F;
		}

		return f;
	}

	public static double wrapDegrees(double degrees) {
		double d = degrees % 360.0;
		if (d >= 180.0) {
			d -= 360.0;
		}

		if (d < -180.0) {
			d += 360.0;
		}

		return d;
	}

	public static float subtractAngles(float start, float end) {
		return wrapDegrees(end - start);
	}

	public static float angleBetween(float first, float second) {
		return abs(subtractAngles(first, second));
	}

	public static float stepAngleTowards(float from, float to, float step) {
		float f = subtractAngles(from, to);
		float g = clamp(f, -step, step);
		return to - g;
	}

	public static float stepTowards(float from, float to, float step) {
		step = abs(step);
		return from < to ? clamp(from + step, from, to) : clamp(from - step, to, from);
	}

	public static float stepUnwrappedAngleTowards(float from, float to, float step) {
		float f = subtractAngles(from, to);
		return stepTowards(from, from + f, step);
	}

	public static int parseInt(String string, int fallback) {
		return NumberUtils.toInt(string, fallback);
	}

	public static int method_34949(String string, int i, int j) {
		return Math.max(j, parseInt(string, i));
	}

	public static double parseDouble(String string, double fallback) {
		try {
			return Double.parseDouble(string);
		} catch (Throwable var4) {
			return fallback;
		}
	}

	public static double method_34948(String string, double d, double e) {
		return Math.max(e, parseDouble(string, d));
	}

	public static int smallestEncompassingPowerOfTwo(int value) {
		int i = value - 1;
		i |= i >> 1;
		i |= i >> 2;
		i |= i >> 4;
		i |= i >> 8;
		i |= i >> 16;
		return i + 1;
	}

	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	public static int log2DeBruijn(int value) {
		value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
		return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 31];
	}

	public static int log2(int value) {
		return log2DeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
	}

	public static int packRgb(float r, float g, float b) {
		return packRgb(floor(r * 255.0F), floor(g * 255.0F), floor(b * 255.0F));
	}

	public static int packRgb(int r, int g, int b) {
		int i = (r << 8) + g;
		return (i << 8) + b;
	}

	public static int method_34952(int i, int j) {
		int k = (i & 0xFF0000) >> 16;
		int l = (j & 0xFF0000) >> 16;
		int m = (i & 0xFF00) >> 8;
		int n = (j & 0xFF00) >> 8;
		int o = (i & 0xFF) >> 0;
		int p = (j & 0xFF) >> 0;
		int q = (int)((float)k * (float)l / 255.0F);
		int r = (int)((float)m * (float)n / 255.0F);
		int s = (int)((float)o * (float)p / 255.0F);
		return i & 0xFF000000 | q << 16 | r << 8 | s;
	}

	public static int method_34943(int i, float f, float g, float h) {
		int j = (i & 0xFF0000) >> 16;
		int k = (i & 0xFF00) >> 8;
		int l = (i & 0xFF) >> 0;
		int m = (int)((float)j * f);
		int n = (int)((float)k * g);
		int o = (int)((float)l * h);
		return i & 0xFF000000 | m << 16 | n << 8 | o;
	}

	public static float fractionalPart(float value) {
		return value - (float)floor(value);
	}

	public static double fractionalPart(double value) {
		return value - (double)lfloor(value);
	}

	public static Vec3d method_34946(Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3, Vec3d vec3d4, double d) {
		double e = ((-d + 2.0) * d - 1.0) * d * 0.5;
		double f = ((3.0 * d - 5.0) * d * d + 2.0) * 0.5;
		double g = ((-3.0 * d + 4.0) * d + 1.0) * d * 0.5;
		double h = (d - 1.0) * d * d * 0.5;
		return new Vec3d(
			vec3d.x * e + vec3d2.x * f + vec3d3.x * g + vec3d4.x * h,
			vec3d.y * e + vec3d2.y * f + vec3d3.y * g + vec3d4.y * h,
			vec3d.z * e + vec3d2.z * f + vec3d3.z * g + vec3d4.z * h
		);
	}

	public static long hashCode(Vec3i vec) {
		return hashCode(vec.getX(), vec.getY(), vec.getZ());
	}

	public static long hashCode(int x, int y, int z) {
		long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
		l = l * l * 42317861L + l * 11L;
		return l >> 16;
	}

	public static UUID randomUuid(Random random) {
		long l = random.nextLong() & -61441L | 16384L;
		long m = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(l, m);
	}

	public static UUID randomUuid() {
		return randomUuid(RANDOM);
	}

	public static double getLerpProgress(double value, double start, double end) {
		return (value - start) / (end - start);
	}

	public static boolean method_34945(Vec3d vec3d, Vec3d vec3d2, Box box) {
		double d = (box.minX + box.maxX) * 0.5;
		double e = (box.maxX - box.minX) * 0.5;
		double f = vec3d.x - d;
		if (Math.abs(f) > e && f * vec3d2.x >= 0.0) {
			return false;
		} else {
			double g = (box.minY + box.maxY) * 0.5;
			double h = (box.maxY - box.minY) * 0.5;
			double i = vec3d.y - g;
			if (Math.abs(i) > h && i * vec3d2.y >= 0.0) {
				return false;
			} else {
				double j = (box.minZ + box.maxZ) * 0.5;
				double k = (box.maxZ - box.minZ) * 0.5;
				double l = vec3d.z - j;
				if (Math.abs(l) > k && l * vec3d2.z >= 0.0) {
					return false;
				} else {
					double m = Math.abs(vec3d2.x);
					double n = Math.abs(vec3d2.y);
					double o = Math.abs(vec3d2.z);
					double p = vec3d2.y * l - vec3d2.z * i;
					if (Math.abs(p) > h * o + k * n) {
						return false;
					} else {
						p = vec3d2.z * f - vec3d2.x * l;
						if (Math.abs(p) > e * o + k * m) {
							return false;
						} else {
							p = vec3d2.x * i - vec3d2.y * f;
							return Math.abs(p) < e * n + h * m;
						}
					}
				}
			}
		}
	}

	public static double atan2(double y, double x) {
		double d = x * x + y * y;
		if (Double.isNaN(d)) {
			return Double.NaN;
		} else {
			boolean bl = y < 0.0;
			if (bl) {
				y = -y;
			}

			boolean bl2 = x < 0.0;
			if (bl2) {
				x = -x;
			}

			boolean bl3 = y > x;
			if (bl3) {
				double e = x;
				x = y;
				y = e;
			}

			double f = fastInverseSqrt(d);
			x *= f;
			y *= f;
			double g = SMALLEST_FRACTION_FREE_DOUBLE + y;
			int i = (int)Double.doubleToRawLongBits(g);
			double h = ARCSINE_TABLE[i];
			double j = COSINE_TABLE[i];
			double k = g - SMALLEST_FRACTION_FREE_DOUBLE;
			double l = y * j - x * k;
			double m = (6.0 + l * l) * l * 0.16666666666666666;
			double n = h + m;
			if (bl3) {
				n = (Math.PI / 2) - n;
			}

			if (bl2) {
				n = Math.PI - n;
			}

			if (bl) {
				n = -n;
			}

			return n;
		}
	}

	public static float fastInverseSqrt(float x) {
		float f = 0.5F * x;
		int i = Float.floatToIntBits(x);
		i = 1597463007 - (i >> 1);
		x = Float.intBitsToFloat(i);
		return x * (1.5F - f * x * x);
	}

	public static double fastInverseSqrt(double x) {
		double d = 0.5 * x;
		long l = Double.doubleToRawLongBits(x);
		l = 6910469410427058090L - (l >> 1);
		x = Double.longBitsToDouble(l);
		return x * (1.5 - d * x * x);
	}

	public static float fastInverseCbrt(float x) {
		int i = Float.floatToIntBits(x);
		i = 1419967116 - i / 3;
		float f = Float.intBitsToFloat(i);
		f = 0.6666667F * f + 1.0F / (3.0F * f * f * x);
		return 0.6666667F * f + 1.0F / (3.0F * f * f * x);
	}

	public static int hsvToRgb(float hue, float saturation, float value) {
		int i = (int)(hue * 6.0F) % 6;
		float f = hue * 6.0F - (float)i;
		float g = value * (1.0F - saturation);
		float h = value * (1.0F - f * saturation);
		float j = value * (1.0F - (1.0F - f) * saturation);
		float k;
		float l;
		float m;
		switch (i) {
			case 0:
				k = value;
				l = j;
				m = g;
				break;
			case 1:
				k = h;
				l = value;
				m = g;
				break;
			case 2:
				k = g;
				l = value;
				m = j;
				break;
			case 3:
				k = g;
				l = h;
				m = value;
				break;
			case 4:
				k = j;
				l = g;
				m = value;
				break;
			case 5:
				k = value;
				l = g;
				m = h;
				break;
			default:
				throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
		}

		int af = clamp((int)(k * 255.0F), 0, 255);
		int ag = clamp((int)(l * 255.0F), 0, 255);
		int ah = clamp((int)(m * 255.0F), 0, 255);
		return af << 16 | ag << 8 | ah;
	}

	public static int idealHash(int value) {
		value ^= value >>> 16;
		value *= -2048144789;
		value ^= value >>> 13;
		value *= -1028477387;
		return value ^ value >>> 16;
	}

	public static long method_34944(long l) {
		l ^= l >>> 33;
		l *= -49064778989728563L;
		l ^= l >>> 33;
		l *= -4265267296055464877L;
		return l ^ l >>> 33;
	}

	public static double[] getCumulativeDistribution(double... values) {
		float f = 0.0F;

		for (double d : values) {
			f = (float)((double)f + d);
		}

		for (int i = 0; i < values.length; i++) {
			values[i] /= (double)f;
		}

		for (int j = 0; j < values.length; j++) {
			values[j] += j == 0 ? 0.0 : values[j - 1];
		}

		return values;
	}

	public static int method_34950(Random random, double[] ds) {
		double d = random.nextDouble();

		for (int i = 0; i < ds.length; i++) {
			if (d < ds[i]) {
				return i;
			}
		}

		return ds.length;
	}

	public static double[] method_34941(double d, double e, double f, int i, int j) {
		double[] ds = new double[j - i + 1];
		int k = 0;

		for (int l = i; l <= j; l++) {
			ds[k] = Math.max(0.0, d * StrictMath.exp(-((double)l - f) * ((double)l - f) / (2.0 * e * e)));
			k++;
		}

		return ds;
	}

	public static double[] method_34940(double d, double e, double f, double g, double h, double i, int j, int k) {
		double[] ds = new double[k - j + 1];
		int l = 0;

		for (int m = j; m <= k; m++) {
			ds[l] = Math.max(
				0.0, d * StrictMath.exp(-((double)m - f) * ((double)m - f) / (2.0 * e * e)) + g * StrictMath.exp(-((double)m - i) * ((double)m - i) / (2.0 * h * h))
			);
			l++;
		}

		return ds;
	}

	public static double[] method_34942(double d, double e, int i, int j) {
		double[] ds = new double[j - i + 1];
		int k = 0;

		for (int l = i; l <= j; l++) {
			ds[k] = Math.max(d * StrictMath.log((double)l) + e, 0.0);
			k++;
		}

		return ds;
	}

	public static int binarySearch(int start, int end, IntPredicate leftPredicate) {
		int i = end - start;

		while (i > 0) {
			int j = i / 2;
			int k = start + j;
			if (leftPredicate.test(k)) {
				i = j;
			} else {
				start = k + 1;
				i -= j + 1;
			}
		}

		return start;
	}

	public static float lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}

	public static double lerp(double delta, double start, double end) {
		return start + delta * (end - start);
	}

	public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
		return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
	}

	public static double lerp3(
		double deltaX,
		double deltaY,
		double deltaZ,
		double x0y0z0,
		double x1y0z0,
		double x0y1z0,
		double x1y1z0,
		double x0y0z1,
		double x1y0z1,
		double x0y1z1,
		double x1y1z1
	) {
		return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
	}

	public static double perlinFade(double value) {
		return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
	}

	public static double perlinFadeDerivative(double value) {
		return 30.0 * value * value * (value - 1.0) * (value - 1.0);
	}

	public static int sign(double value) {
		if (value == 0.0) {
			return 0;
		} else {
			return value > 0.0 ? 1 : -1;
		}
	}

	public static float lerpAngleDegrees(float delta, float start, float end) {
		return start + delta * wrapDegrees(end - start);
	}

	public static float method_34955(float f, float g, float h) {
		return Math.min(f * f * 0.6F + g * g * ((3.0F + g) / 4.0F) + h * h * 0.8F, 1.0F);
	}

	@Deprecated
	public static float lerpAngle(float start, float end, float delta) {
		float f = end - start;

		while (f < -180.0F) {
			f += 360.0F;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return start + delta * f;
	}

	@Deprecated
	public static float fwrapDegrees(double degrees) {
		while (degrees >= 180.0) {
			degrees -= 360.0;
		}

		while (degrees < -180.0) {
			degrees += 360.0;
		}

		return (float)degrees;
	}

	public static float wrap(float value, float maxDeviation) {
		return (Math.abs(value % maxDeviation - maxDeviation * 0.5F) - maxDeviation * 0.25F) / (maxDeviation * 0.25F);
	}

	public static float square(float n) {
		return n * n;
	}

	public static double square(double n) {
		return n * n;
	}

	public static int square(int n) {
		return n * n;
	}

	public static double clampedLerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
		return clampedLerp(start, end, getLerpProgress(lerpValue, lerpStart, lerpEnd));
	}

	public static double lerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
		return lerp(getLerpProgress(lerpValue, lerpStart, lerpEnd), start, end);
	}

	public static double method_34957(double d) {
		return d + (2.0 * new Random((long)floor(d * 3000.0)).nextDouble() - 1.0) * 1.0E-7 / 2.0;
	}

	public static int roundUpToMultiple(int value, int divisor) {
		return (value + divisor - 1) / divisor * divisor;
	}

	public static int nextBetween(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	public static float nextBetween(Random random, float min, float max) {
		return random.nextFloat() * (max - min) + min;
	}

	public static float nextGaussian(Random random, float mean, float deviation) {
		return mean + (float)random.nextGaussian() * deviation;
	}

	public static double magnitude(int x, double y, int z) {
		return Math.sqrt((double)(x * x) + y * y + (double)(z * z));
	}

	static {
		for (int i = 0; i < 257; i++) {
			double d = (double)i / 256.0;
			double e = Math.asin(d);
			COSINE_TABLE[i] = Math.cos(e);
			ARCSINE_TABLE[i] = e;
		}
	}
}
