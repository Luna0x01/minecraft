package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;

public class MathHelper {
	public static final float SQUARE_ROOT_OF_TWO = sqrt(2.0F);
	private static final float[] SINE_TABLE = new float[65536];
	private static final Random RANDOM = new Random();
	private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION;
	private static final double SMALLEST_FRACTION_FREE_DOUBLE;
	private static final double[] ARCSINE_TABLE;
	private static final double[] COSINE_TABLE;

	public static float sin(float f) {
		return SINE_TABLE[(int)(f * 10430.378F) & 65535];
	}

	public static float cos(float f) {
		return SINE_TABLE[(int)(f * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt(float f) {
		return (float)Math.sqrt((double)f);
	}

	public static float sqrt(double d) {
		return (float)Math.sqrt(d);
	}

	public static int floor(float f) {
		int i = (int)f;
		return f < (float)i ? i - 1 : i;
	}

	public static int fastFloor(double d) {
		return (int)(d + 1024.0) - 1024;
	}

	public static int floor(double d) {
		int i = (int)d;
		return d < (double)i ? i - 1 : i;
	}

	public static long lfloor(double d) {
		long l = (long)d;
		return d < (double)l ? l - 1L : l;
	}

	public static int absFloor(double d) {
		return (int)(d >= 0.0 ? d : -d + 1.0);
	}

	public static float abs(float f) {
		return f >= 0.0F ? f : -f;
	}

	public static int abs(int i) {
		return i >= 0 ? i : -i;
	}

	public static int ceil(float f) {
		int i = (int)f;
		return f > (float)i ? i + 1 : i;
	}

	public static int ceil(double d) {
		int i = (int)d;
		return d > (double)i ? i + 1 : i;
	}

	public static int clamp(int value, int min, int max) {
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
			return delta > 1.0 ? end : start + (end - start) * delta;
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
		return dividend < 0 ? -((-dividend - 1) / divisor) - 1 : dividend / divisor;
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
		return abs(b - a) < 1.0E-5F;
	}

	public static int floorMod(int dividend, int divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

	public static float floorMod(float dividend, float divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

	public static float wrapDegrees(float degrees) {
		degrees %= 360.0F;
		if (degrees >= 180.0F) {
			degrees -= 360.0F;
		}

		if (degrees < -180.0F) {
			degrees += 360.0F;
		}

		return degrees;
	}

	public static double wrapDegrees(double degrees) {
		degrees %= 360.0;
		if (degrees >= 180.0) {
			degrees -= 360.0;
		}

		if (degrees < -180.0) {
			degrees += 360.0;
		}

		return degrees;
	}

	public static int wrapDegrees(int degrees) {
		degrees %= 360;
		if (degrees >= 180) {
			degrees -= 360;
		}

		if (degrees < -180) {
			degrees += 360;
		}

		return degrees;
	}

	public static int parseInt(String string, int fallback) {
		try {
			return Integer.parseInt(string);
		} catch (Throwable var3) {
			return fallback;
		}
	}

	public static int parseInt(String string, int fallback, int minimum) {
		return Math.max(minimum, parseInt(string, fallback));
	}

	public static double parseDouble(String string, double fallback) {
		try {
			return Double.parseDouble(string);
		} catch (Throwable var4) {
			return fallback;
		}
	}

	public static double parseDouble(String string, double fallback, double minimum) {
		return Math.max(minimum, parseDouble(string, fallback));
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

	private static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	public static int log2DeBruijn(int value) {
		value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
		return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 31];
	}

	public static int log2(int value) {
		return log2DeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
	}

	public static int roundUp(int value, int stepSize) {
		if (stepSize == 0) {
			return 0;
		} else if (value == 0) {
			return stepSize;
		} else {
			if (value < 0) {
				stepSize *= -1;
			}

			int i = value % stepSize;
			return i == 0 ? value : value + stepSize - i;
		}
	}

	public static int packRgb(float r, float g, float b) {
		return packRgb(floor(r * 255.0F), floor(g * 255.0F), floor(b * 255.0F));
	}

	public static int packRgb(int r, int g, int b) {
		int i = (r << 8) + g;
		return (i << 8) + b;
	}

	public static int multiplyColors(int color1, int color2) {
		int i = (color1 & 0xFF0000) >> 16;
		int j = (color2 & 0xFF0000) >> 16;
		int k = (color1 & 0xFF00) >> 8;
		int l = (color2 & 0xFF00) >> 8;
		int m = (color1 & 0xFF) >> 0;
		int n = (color2 & 0xFF) >> 0;
		int o = (int)((float)i * (float)j / 255.0F);
		int p = (int)((float)k * (float)l / 255.0F);
		int q = (int)((float)m * (float)n / 255.0F);
		return color1 & 0xFF000000 | o << 16 | p << 8 | q;
	}

	public static double fractionalPart(double value) {
		return value - Math.floor(value);
	}

	public static long hashCode(Vec3i vec) {
		return hashCode(vec.getX(), vec.getY(), vec.getZ());
	}

	public static long hashCode(int x, int y, int z) {
		long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
		return l * l * 42317861L + l * 11L;
	}

	public static UUID randomUuid(Random random) {
		long l = random.nextLong() & -61441L | 16384L;
		long m = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(l, m);
	}

	public static UUID randomUuid() {
		return randomUuid(RANDOM);
	}

	public static double minusDiv(double numerator, double delta, double denominator) {
		return (numerator - delta) / (denominator - delta);
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

	public static double fastInverseSqrt(double x) {
		double d = 0.5 * x;
		long l = Double.doubleToRawLongBits(x);
		l = 6910469410427058090L - (l >> 1);
		x = Double.longBitsToDouble(l);
		return x * (1.5 - d * x * x);
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

		int n = clamp((int)(k * 255.0F), 0, 255);
		int o = clamp((int)(l * 255.0F), 0, 255);
		int p = clamp((int)(m * 255.0F), 0, 255);
		return n << 16 | o << 8 | p;
	}

	public static int idealHash(int value) {
		value ^= value >>> 16;
		value *= -2048144789;
		value ^= value >>> 13;
		value *= -1028477387;
		return value ^ value >>> 16;
	}

	static {
		for (int i = 0; i < 65536; i++) {
			SINE_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
		}

		MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
			0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
		};
		SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
		ARCSINE_TABLE = new double[257];
		COSINE_TABLE = new double[257];

		for (int j = 0; j < 257; j++) {
			double d = (double)j / 256.0;
			double e = Math.asin(d);
			COSINE_TABLE[j] = Math.cos(e);
			ARCSINE_TABLE[j] = e;
		}
	}
}
