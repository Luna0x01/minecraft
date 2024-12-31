package net.minecraft.client.util;

public class SmoothUtil {
	private double field_22256;
	private double field_22257;
	private double field_22258;

	public double method_21530(double d, double e) {
		this.field_22256 += d;
		double f = this.field_22256 - this.field_22257;
		double g = this.field_22258 + (f - this.field_22258) * 0.5;
		double h = Math.signum(f);
		if (h * f > h * this.field_22258) {
			f = g;
		}

		this.field_22258 = g;
		this.field_22257 += f * e;
		return f * e;
	}

	public void clear() {
		this.field_22256 = 0.0;
		this.field_22257 = 0.0;
		this.field_22258 = 0.0;
	}
}
