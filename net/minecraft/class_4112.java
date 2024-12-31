package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class class_4112 {
	private final MinecraftClient field_19955;
	private boolean field_19956;
	private boolean field_19957;
	private boolean field_19958;
	private double field_19959;
	private double field_19960;
	private int field_19961;
	private int field_19962 = -1;
	private boolean field_19963 = true;
	private int field_19964;
	private double field_19965;
	private final SmoothUtil field_19966 = new SmoothUtil();
	private final SmoothUtil field_19967 = new SmoothUtil();
	private double field_19968;
	private double field_19969;
	private double field_19970;
	private double field_19971 = Double.MIN_VALUE;
	private boolean field_19972;

	public class_4112(MinecraftClient minecraftClient) {
		this.field_19955 = minecraftClient;
	}

	private void method_18242(long l, int i, int j, int k) {
		if (l == this.field_19955.field_19944.method_18315()) {
			boolean bl = j == 1;
			if (MinecraftClient.IS_MAC && i == 0) {
				if (bl) {
					if ((k & 2) == 2) {
						i = 1;
						this.field_19961++;
					}
				} else if (this.field_19961 > 0) {
					i = 1;
					this.field_19961--;
				}
			}

			int m = i;
			if (bl) {
				if (this.field_19955.options.touchscreen && this.field_19964++ > 0) {
					return;
				}

				this.field_19962 = m;
				this.field_19965 = GLFW.glfwGetTime();
			} else if (this.field_19962 != -1) {
				if (this.field_19955.options.touchscreen && --this.field_19964 > 0) {
					return;
				}

				this.field_19962 = -1;
			}

			boolean[] bls = new boolean[]{false};
			if (this.field_19955.currentScreen == null) {
				if (!this.field_19972 && bl) {
					this.method_18253();
				}
			} else {
				double d = this.field_19959 * (double)this.field_19955.field_19944.method_18321() / (double)this.field_19955.field_19944.method_18319();
				double e = this.field_19960 * (double)this.field_19955.field_19944.method_18322() / (double)this.field_19955.field_19944.method_18320();
				if (bl) {
					Screen.method_18605(
						() -> bls[0] = this.field_19955.currentScreen.mouseClicked(d, e, m),
						"mouseClicked event handler",
						this.field_19955.currentScreen.getClass().getCanonicalName()
					);
				} else {
					Screen.method_18605(
						() -> bls[0] = this.field_19955.currentScreen.mouseReleased(d, e, m),
						"mouseReleased event handler",
						this.field_19955.currentScreen.getClass().getCanonicalName()
					);
				}
			}

			if (!bls[0] && (this.field_19955.currentScreen == null || this.field_19955.currentScreen.passEvents)) {
				if (m == 0) {
					this.field_19956 = bl;
				} else if (m == 2) {
					this.field_19957 = bl;
				} else if (m == 1) {
					this.field_19958 = bl;
				}

				KeyBinding.method_18168(class_4107.class_4109.MOUSE.method_18162(m), bl);
				if (bl) {
					if (this.field_19955.player.isSpectator() && m == 2) {
						this.field_19955.inGameHud.getSpectatorHud().useSelectedCommand();
					} else {
						KeyBinding.method_18167(class_4107.class_4109.MOUSE.method_18162(m));
					}
				}
			}
		}
	}

	private void method_18241(long l, double d, double e) {
		if (l == MinecraftClient.getInstance().field_19944.method_18315()) {
			double f = e * this.field_19955.options.field_19980;
			if (this.field_19955.currentScreen != null) {
				this.field_19955.currentScreen.mouseScrolled(f);
			} else if (this.field_19955.player != null) {
				if (this.field_19970 != 0.0 && Math.signum(f) != Math.signum(this.field_19970)) {
					this.field_19970 = 0.0;
				}

				this.field_19970 += f;
				double g = (double)((int)this.field_19970);
				if (g == 0.0) {
					return;
				}

				this.field_19970 -= g;
				if (this.field_19955.player.isSpectator()) {
					if (this.field_19955.inGameHud.getSpectatorHud().isOpen()) {
						this.field_19955.inGameHud.getSpectatorHud().method_18430(-g);
					} else {
						double h = MathHelper.clamp((double)this.field_19955.player.abilities.getFlySpeed() + g * 0.005F, 0.0, 0.2F);
						this.field_19955.player.abilities.method_15919(h);
					}
				} else {
					this.field_19955.player.inventory.method_15920(g);
				}
			}
		}
	}

	public void method_18240(long l) {
		GLFW.glfwSetCursorPosCallback(l, this::method_18246);
		GLFW.glfwSetMouseButtonCallback(l, this::method_18242);
		GLFW.glfwSetScrollCallback(l, this::method_18241);
	}

	private void method_18246(long l, double d, double e) {
		if (l == MinecraftClient.getInstance().field_19944.method_18315()) {
			if (this.field_19963) {
				this.field_19959 = d;
				this.field_19960 = e;
				this.field_19963 = false;
			}

			class_4122 lv = this.field_19955.currentScreen;
			if (this.field_19962 != -1 && this.field_19965 > 0.0 && lv != null) {
				double f = d * (double)this.field_19955.field_19944.method_18321() / (double)this.field_19955.field_19944.method_18319();
				double g = e * (double)this.field_19955.field_19944.method_18322() / (double)this.field_19955.field_19944.method_18320();
				double h = (d - this.field_19959) * (double)this.field_19955.field_19944.method_18321() / (double)this.field_19955.field_19944.method_18319();
				double i = (e - this.field_19960) * (double)this.field_19955.field_19944.method_18322() / (double)this.field_19955.field_19944.method_18320();
				Screen.method_18605(() -> lv.mouseDragged(f, g, this.field_19962, h, i), "mouseDragged event handler", lv.getClass().getCanonicalName());
			}

			this.field_19955.profiler.push("mouse");
			if (this.method_18252() && this.field_19955.isFullscreen()) {
				this.field_19968 = this.field_19968 + (d - this.field_19959);
				this.field_19969 = this.field_19969 + (e - this.field_19960);
			}

			this.method_18239();
			this.field_19959 = d;
			this.field_19960 = e;
			this.field_19955.profiler.pop();
		}
	}

	public void method_18239() {
		double d = GLFW.glfwGetTime();
		double e = d - this.field_19971;
		this.field_19971 = d;
		if (this.method_18252() && this.field_19955.isFullscreen()) {
			double f = this.field_19955.options.field_19988 * 0.6F + 0.2F;
			double g = f * f * f * 8.0;
			double j;
			double k;
			if (this.field_19955.options.smoothCameraEnabled) {
				double h = this.field_19966.method_21530(this.field_19968 * g, e * g);
				double i = this.field_19967.method_21530(this.field_19969 * g, e * g);
				j = h;
				k = i;
			} else {
				this.field_19966.clear();
				this.field_19967.clear();
				j = this.field_19968 * g;
				k = this.field_19969 * g;
			}

			this.field_19968 = 0.0;
			this.field_19969 = 0.0;
			int n = 1;
			if (this.field_19955.options.invertYMouse) {
				n = -1;
			}

			this.field_19955.method_14463().method_19639(j, k);
			if (this.field_19955.player != null) {
				this.field_19955.player.method_15558(j, k * (double)n);
			}
		} else {
			this.field_19968 = 0.0;
			this.field_19969 = 0.0;
		}
	}

	public boolean method_18245() {
		return this.field_19956;
	}

	public boolean method_18248() {
		return this.field_19958;
	}

	public double method_18249() {
		return this.field_19959;
	}

	public double method_18250() {
		return this.field_19960;
	}

	public void method_18251() {
		this.field_19963 = true;
	}

	public boolean method_18252() {
		return this.field_19972;
	}

	public void method_18253() {
		if (this.field_19955.isFullscreen()) {
			if (!this.field_19972) {
				if (!MinecraftClient.IS_MAC) {
					KeyBinding.method_12137();
				}

				this.field_19972 = true;
				this.field_19959 = (double)(this.field_19955.field_19944.method_18319() / 2);
				this.field_19960 = (double)(this.field_19955.field_19944.method_18320() / 2);
				GLFW.glfwSetCursorPos(this.field_19955.field_19944.method_18315(), this.field_19959, this.field_19960);
				GLFW.glfwSetInputMode(this.field_19955.field_19944.method_18315(), 208897, 212995);
				this.field_19955.setScreen(null);
				this.field_19955.attackCooldown = 10000;
			}
		}
	}

	public void method_18254() {
		if (this.field_19972) {
			this.field_19972 = false;
			GLFW.glfwSetInputMode(this.field_19955.field_19944.method_18315(), 208897, 212993);
			this.field_19959 = (double)(this.field_19955.field_19944.method_18319() / 2);
			this.field_19960 = (double)(this.field_19955.field_19944.method_18320() / 2);
			GLFW.glfwSetCursorPos(this.field_19955.field_19944.method_18315(), this.field_19959, this.field_19960);
		}
	}
}
