package net.minecraft.client.gl;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.JsonHelper;
import org.lwjgl.opengl.GL14;

public class GlBlendState {
	private static GlBlendState activeBlendState = null;
	private final int srcRgb;
	private final int srcAlpha;
	private final int dstRgb;
	private final int dstAlpha;
	private final int func;
	private final boolean separateBlend;
	private final boolean blendDisabled;

	private GlBlendState(boolean bl, boolean bl2, int i, int j, int k, int l, int m) {
		this.separateBlend = bl;
		this.srcRgb = i;
		this.dstRgb = j;
		this.srcAlpha = k;
		this.dstAlpha = l;
		this.blendDisabled = bl2;
		this.func = m;
	}

	public GlBlendState() {
		this(false, true, 1, 0, 1, 0, 32774);
	}

	public GlBlendState(int i, int j, int k) {
		this(false, false, i, j, i, j, k);
	}

	public GlBlendState(int i, int j, int k, int l, int m) {
		this(true, false, i, j, k, l, m);
	}

	public void enable() {
		if (!this.equals(activeBlendState)) {
			if (activeBlendState == null || this.blendDisabled != activeBlendState.isBlendDisabled()) {
				activeBlendState = this;
				if (this.blendDisabled) {
					GlStateManager.disableBlend();
					return;
				}

				GlStateManager.enableBlend();
			}

			GL14.glBlendEquation(this.func);
			if (this.separateBlend) {
				GlStateManager.blendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
			} else {
				GlStateManager.blendFunc(this.srcRgb, this.dstRgb);
			}
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof GlBlendState)) {
			return false;
		} else {
			GlBlendState glBlendState = (GlBlendState)object;
			if (this.func != glBlendState.func) {
				return false;
			} else if (this.dstAlpha != glBlendState.dstAlpha) {
				return false;
			} else if (this.dstRgb != glBlendState.dstRgb) {
				return false;
			} else if (this.blendDisabled != glBlendState.blendDisabled) {
				return false;
			} else if (this.separateBlend != glBlendState.separateBlend) {
				return false;
			} else {
				return this.srcAlpha != glBlendState.srcAlpha ? false : this.srcRgb == glBlendState.srcRgb;
			}
		}
	}

	public int hashCode() {
		int i = this.srcRgb;
		i = 31 * i + this.srcAlpha;
		i = 31 * i + this.dstRgb;
		i = 31 * i + this.dstAlpha;
		i = 31 * i + this.func;
		i = 31 * i + (this.separateBlend ? 1 : 0);
		return 31 * i + (this.blendDisabled ? 1 : 0);
	}

	public boolean isBlendDisabled() {
		return this.blendDisabled;
	}

	public static GlBlendState deserializeBlendState(JsonObject json) {
		if (json == null) {
			return new GlBlendState();
		} else {
			int i = 32774;
			int j = 1;
			int k = 0;
			int l = 1;
			int m = 0;
			boolean bl = true;
			boolean bl2 = false;
			if (JsonHelper.hasString(json, "func")) {
				i = getFuncFromString(json.get("func").getAsString());
				if (i != 32774) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "srcrgb")) {
				j = getComponentFromString(json.get("srcrgb").getAsString());
				if (j != 1) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "dstrgb")) {
				k = getComponentFromString(json.get("dstrgb").getAsString());
				if (k != 0) {
					bl = false;
				}
			}

			if (JsonHelper.hasString(json, "srcalpha")) {
				l = getComponentFromString(json.get("srcalpha").getAsString());
				if (l != 1) {
					bl = false;
				}

				bl2 = true;
			}

			if (JsonHelper.hasString(json, "dstalpha")) {
				m = getComponentFromString(json.get("dstalpha").getAsString());
				if (m != 0) {
					bl = false;
				}

				bl2 = true;
			}

			if (bl) {
				return new GlBlendState();
			} else {
				return bl2 ? new GlBlendState(j, k, l, m, i) : new GlBlendState(j, k, i);
			}
		}
	}

	private static int getFuncFromString(String string) {
		String string2 = string.trim().toLowerCase();
		if (string2.equals("add")) {
			return 32774;
		} else if (string2.equals("subtract")) {
			return 32778;
		} else if (string2.equals("reversesubtract")) {
			return 32779;
		} else if (string2.equals("reverse_subtract")) {
			return 32779;
		} else if (string2.equals("min")) {
			return 32775;
		} else {
			return string2.equals("max") ? 32776 : 32774;
		}
	}

	private static int getComponentFromString(String string) {
		String string2 = string.trim().toLowerCase();
		string2 = string2.replaceAll("_", "");
		string2 = string2.replaceAll("one", "1");
		string2 = string2.replaceAll("zero", "0");
		string2 = string2.replaceAll("minus", "-");
		if (string2.equals("0")) {
			return 0;
		} else if (string2.equals("1")) {
			return 1;
		} else if (string2.equals("srccolor")) {
			return 768;
		} else if (string2.equals("1-srccolor")) {
			return 769;
		} else if (string2.equals("dstcolor")) {
			return 774;
		} else if (string2.equals("1-dstcolor")) {
			return 775;
		} else if (string2.equals("srcalpha")) {
			return 770;
		} else if (string2.equals("1-srcalpha")) {
			return 771;
		} else if (string2.equals("dstalpha")) {
			return 772;
		} else {
			return string2.equals("1-dstalpha") ? 773 : -1;
		}
	}
}
