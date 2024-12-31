package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class class_4117 implements AutoCloseable {
	private static final Logger field_20026 = LogManager.getLogger();
	private final GLFWErrorCallback field_20027 = GLFWErrorCallback.create(this::method_18295);
	private final MinecraftClient field_20028;
	private final class_4116 field_20029;
	private class_4111 field_20030;
	private final long field_20031;
	private int field_20032;
	private int field_20033;
	private int field_20034;
	private int field_20035;
	private Optional<class_4115> field_20036;
	private boolean field_20037;
	private boolean field_20038;
	private int field_20039;
	private int field_20040;
	private int field_20041;
	private int field_20042;
	private int field_20043;
	private int field_20044;
	private int field_20045;
	private int field_20046;
	private double field_20047;
	private String field_20048 = "";
	private boolean field_20049;
	private double field_20050 = Double.MIN_VALUE;

	public class_4117(MinecraftClient minecraftClient, class_4116 arg, RunArgs.WindowInformation windowInformation, String string) {
		this.field_20029 = arg;
		this.method_18327();
		this.method_18299("Pre startup");
		this.field_20028 = minecraftClient;
		Optional<class_4115> optional = class_4115.method_18281(string);
		if (optional.isPresent()) {
			this.field_20036 = optional;
		} else if (windowInformation.field_20508.isPresent() && windowInformation.field_20509.isPresent()) {
			this.field_20036 = Optional.of(new class_4115((Integer)windowInformation.field_20508.get(), (Integer)windowInformation.field_20509.get(), 8, 8, 8, 60));
		} else {
			this.field_20036 = Optional.empty();
		}

		this.field_20038 = this.field_20037 = windowInformation.checkGlErrors;
		this.field_20030 = arg.method_18288(GLFW.glfwGetPrimaryMonitor());
		class_4115 lv = this.field_20030.method_18232(this.field_20037 ? this.field_20036 : Optional.empty());
		this.field_20034 = this.field_20041 = windowInformation.field_20506 > 0 ? windowInformation.field_20506 : 1;
		this.field_20035 = this.field_20042 = windowInformation.field_20507 > 0 ? windowInformation.field_20507 : 1;
		this.field_20032 = this.field_20039 = this.field_20030.method_18235() + lv.method_18280() / 2 - this.field_20041 / 2;
		this.field_20033 = this.field_20040 = this.field_20030.method_18236() + lv.method_18282() / 2 - this.field_20042 / 2;
		GLFW.glfwDefaultWindowHints();
		this.field_20031 = GLFW.glfwCreateWindow(this.field_20041, this.field_20042, "Minecraft 1.13.2", this.field_20037 ? this.field_20030.method_18238() : 0L, 0L);
		minecraftClient.field_19934 = true;
		this.method_18328();
		GLFW.glfwMakeContextCurrent(this.field_20031);
		GL.createCapabilities();
		this.method_18331();
		this.method_18329();
		this.method_18326();
		GLFW.glfwSetFramebufferSizeCallback(this.field_20031, this::method_18305);
		GLFW.glfwSetWindowPosCallback(this.field_20031, this::method_18296);
		GLFW.glfwSetWindowSizeCallback(this.field_20031, this::method_18308);
		GLFW.glfwSetWindowFocusCallback(this.field_20031, this::method_18297);
		minecraftClient.field_19945 = new class_4112(minecraftClient);
		minecraftClient.field_19945.method_18240(this.field_20031);
		minecraftClient.field_19946 = new class_4110(minecraftClient);
		minecraftClient.field_19946.method_18180(this.field_20031);
	}

	public static void method_18300(BiConsumer<Integer, String> biConsumer) {
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var2 = null;

		try {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			int i = GLFW.glfwGetError(pointerBuffer);
			if (i != 0) {
				long l = pointerBuffer.get();
				String string = l != 0L ? MemoryUtil.memUTF8(l) : "";
				biConsumer.accept(i, string);
			}
		} catch (Throwable var15) {
			var2 = var15;
			throw var15;
		} finally {
			if (memoryStack != null) {
				if (var2 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var14) {
						var2.addSuppressed(var14);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	public void method_18293() {
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0, (double)this.method_18317() / this.method_18325(), (double)this.method_18318() / this.method_18325(), 0.0, 1000.0, 3000.0);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
	}

	private void method_18326() {
		try {
			MemoryStack memoryStack = MemoryStack.stackPush();
			Throwable var2 = null;

			try {
				InputStream inputStream = this.field_20028
					.getResourcePackLoader()
					.method_19542()
					.method_5897(class_4455.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png"));
				Throwable var4 = null;

				try {
					InputStream inputStream2 = this.field_20028
						.getResourcePackLoader()
						.method_19542()
						.method_5897(class_4455.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png"));
					Throwable var6 = null;

					try {
						if (inputStream == null) {
							throw new FileNotFoundException("icons/icon_16x16.png");
						}

						if (inputStream2 == null) {
							throw new FileNotFoundException("icons/icon_32x32.png");
						}

						IntBuffer intBuffer = memoryStack.mallocInt(1);
						IntBuffer intBuffer2 = memoryStack.mallocInt(1);
						IntBuffer intBuffer3 = memoryStack.mallocInt(1);
						Buffer buffer = GLFWImage.mallocStack(2, memoryStack);
						ByteBuffer byteBuffer = this.method_18298(inputStream, intBuffer, intBuffer2, intBuffer3);
						if (byteBuffer == null) {
							throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
						}

						buffer.position(0);
						buffer.width(intBuffer.get(0));
						buffer.height(intBuffer2.get(0));
						buffer.pixels(byteBuffer);
						ByteBuffer byteBuffer2 = this.method_18298(inputStream2, intBuffer, intBuffer2, intBuffer3);
						if (byteBuffer2 == null) {
							throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
						}

						buffer.position(1);
						buffer.width(intBuffer.get(0));
						buffer.height(intBuffer2.get(0));
						buffer.pixels(byteBuffer2);
						buffer.position(0);
						GLFW.glfwSetWindowIcon(this.field_20031, buffer);
						STBImage.stbi_image_free(byteBuffer);
						STBImage.stbi_image_free(byteBuffer2);
					} catch (Throwable var58) {
						var6 = var58;
						throw var58;
					} finally {
						if (inputStream2 != null) {
							if (var6 != null) {
								try {
									inputStream2.close();
								} catch (Throwable var57) {
									var6.addSuppressed(var57);
								}
							} else {
								inputStream2.close();
							}
						}
					}
				} catch (Throwable var60) {
					var4 = var60;
					throw var60;
				} finally {
					if (inputStream != null) {
						if (var4 != null) {
							try {
								inputStream.close();
							} catch (Throwable var56) {
								var4.addSuppressed(var56);
							}
						} else {
							inputStream.close();
						}
					}
				}
			} catch (Throwable var62) {
				var2 = var62;
				throw var62;
			} finally {
				if (memoryStack != null) {
					if (var2 != null) {
						try {
							memoryStack.close();
						} catch (Throwable var55) {
							var2.addSuppressed(var55);
						}
					} else {
						memoryStack.close();
					}
				}
			}
		} catch (IOException var64) {
			field_20026.error("Couldn't set icon", var64);
		}
	}

	@Nullable
	private ByteBuffer method_18298(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
		ByteBuffer byteBuffer = null;

		ByteBuffer var6;
		try {
			byteBuffer = TextureUtil.method_19533(inputStream);
			byteBuffer.rewind();
			var6 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, 0);
		} finally {
			if (byteBuffer != null) {
				MemoryUtil.memFree(byteBuffer);
			}
		}

		return var6;
	}

	void method_18299(String string) {
		this.field_20048 = string;
	}

	private void method_18327() {
		GLFW.glfwSetErrorCallback(class_4117::method_18304);
	}

	private static void method_18304(int i, long l) {
		throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
	}

	void method_18295(int i, long l) {
		String string = MemoryUtil.memUTF8(l);
		field_20026.error("########## GL ERROR ##########");
		field_20026.error("@ {}", this.field_20048);
		field_20026.error("{}: {}", i, string);
	}

	void method_18302() {
		GLFW.glfwSetErrorCallback(this.field_20027).free();
	}

	public void method_18306() {
		GLFW.glfwSwapInterval(this.field_20028.options.field_19991 ? 1 : 0);
	}

	public void close() {
		Util.field_21541 = System::nanoTime;
		Callbacks.glfwFreeCallbacks(this.field_20031);
		this.field_20027.close();
		GLFW.glfwDestroyWindow(this.field_20031);
		GLFW.glfwTerminate();
	}

	private void method_18328() {
		this.field_20030 = this.field_20029.method_18290(this);
	}

	private void method_18296(long l, int i, int j) {
		this.field_20039 = i;
		this.field_20040 = j;
		this.method_18328();
	}

	private void method_18305(long l, int i, int j) {
		if (l == this.field_20031) {
			int k = this.method_18317();
			int m = this.method_18318();
			if (i != 0 && j != 0) {
				this.field_20043 = i;
				this.field_20044 = j;
				if (this.method_18317() != k || this.method_18318() != m) {
					this.method_18314();
				}
			}
		}
	}

	private void method_18329() {
		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetFramebufferSize(this.field_20031, is, js);
		this.field_20043 = is[0];
		this.field_20044 = js[0];
	}

	private void method_18308(long l, int i, int j) {
		this.field_20041 = i;
		this.field_20042 = j;
		this.method_18328();
	}

	private void method_18297(long l, boolean bl) {
		if (l == this.field_20031) {
			this.field_20028.field_19934 = bl;
		}
	}

	private int method_18330() {
		return this.field_20028.world == null && this.field_20028.currentScreen != null ? 60 : this.field_20028.options.maxFramerate;
	}

	public boolean method_18309() {
		return (double)this.method_18330() < GameOptions.Option.MAX_FPS.method_18267();
	}

	public void method_18301(boolean bl) {
		this.field_20028.profiler.push("display_update");
		GLFW.glfwSwapBuffers(this.field_20031);
		GLFW.glfwPollEvents();
		if (this.field_20037 != this.field_20038) {
			this.field_20038 = this.field_20037;
			this.method_18292();
		}

		this.field_20028.profiler.pop();
		if (bl && this.method_18309()) {
			this.field_20028.profiler.push("fpslimit_wait");
			double d = this.field_20050 + 1.0 / (double)this.method_18330();

			double e;
			for (e = GLFW.glfwGetTime(); e < d; e = GLFW.glfwGetTime()) {
				GLFW.glfwWaitEventsTimeout(d - e);
			}

			this.field_20050 = e;
			this.field_20028.profiler.pop();
		}
	}

	public Optional<class_4115> method_18310() {
		return this.field_20036;
	}

	public int method_18311() {
		return this.field_20036.isPresent() ? this.field_20030.method_18234(this.field_20036) + 1 : 0;
	}

	public String method_18294(int i) {
		if (this.field_20030.method_18237() <= i) {
			i = this.field_20030.method_18237() - 1;
		}

		return this.field_20030.method_18231(i).toString();
	}

	public void method_18303(int i) {
		Optional<class_4115> optional = this.field_20036;
		if (i == 0) {
			this.field_20036 = Optional.empty();
		} else {
			this.field_20036 = Optional.of(this.field_20030.method_18231(i - 1));
		}

		if (!this.field_20036.equals(optional)) {
			this.field_20049 = true;
		}
	}

	public void method_18312() {
		if (this.field_20037 && this.field_20049) {
			this.field_20049 = false;
			this.method_18331();
			this.method_18314();
		}
	}

	private void method_18331() {
		boolean bl = GLFW.glfwGetWindowMonitor(this.field_20031) != 0L;
		if (this.field_20037) {
			class_4115 lv = this.field_20030.method_18232(this.field_20036);
			if (!bl) {
				this.field_20032 = this.field_20039;
				this.field_20033 = this.field_20040;
				this.field_20034 = this.field_20041;
				this.field_20035 = this.field_20042;
			}

			this.field_20039 = 0;
			this.field_20040 = 0;
			this.field_20041 = lv.method_18280();
			this.field_20042 = lv.method_18282();
			GLFW.glfwSetWindowMonitor(
				this.field_20031, this.field_20030.method_18238(), this.field_20039, this.field_20040, this.field_20041, this.field_20042, lv.method_18286()
			);
		} else {
			class_4115 lv2 = this.field_20030.method_18233();
			this.field_20039 = this.field_20032;
			this.field_20040 = this.field_20033;
			this.field_20041 = this.field_20034;
			this.field_20042 = this.field_20035;
			GLFW.glfwSetWindowMonitor(this.field_20031, 0L, this.field_20039, this.field_20040, this.field_20041, this.field_20042, -1);
		}
	}

	public void method_18313() {
		this.field_20037 = !this.field_20037;
		this.field_20028.options.fullscreen = this.field_20037;
	}

	private void method_18292() {
		try {
			this.method_18331();
			this.method_18314();
			this.method_18306();
			this.method_18301(false);
		} catch (Exception var2) {
			field_20026.error("Couldn't toggle fullscreen", var2);
		}
	}

	public void method_18314() {
		this.field_20047 = (double)this.method_18307(this.field_20028.options.guiScale);
		this.field_20045 = MathHelper.ceil((double)this.field_20043 / this.field_20047);
		this.field_20046 = MathHelper.ceil((double)this.field_20044 / this.field_20047);
		if (this.field_20028.currentScreen != null) {
			this.field_20028.currentScreen.resize(this.field_20028, this.field_20045, this.field_20046);
		}

		Framebuffer framebuffer = this.field_20028.getFramebuffer();
		if (framebuffer != null) {
			framebuffer.resize(this.field_20043, this.field_20044);
		}

		if (this.field_20028.field_3818 != null) {
			this.field_20028.field_3818.method_19063(this.field_20043, this.field_20044);
		}

		if (this.field_20028.field_19945 != null) {
			this.field_20028.field_19945.method_18251();
		}
	}

	public int method_18307(int i) {
		int j = 1;

		while (j != i && j < this.field_20043 && j < this.field_20044 && this.field_20043 / (j + 1) >= 320 && this.field_20044 / (j + 1) >= 240) {
			j++;
		}

		if (this.field_20028.method_18229() && j % 2 != 0) {
			j++;
		}

		return j;
	}

	public long method_18315() {
		return this.field_20031;
	}

	public boolean method_18316() {
		return this.field_20037;
	}

	public int method_18317() {
		return this.field_20043;
	}

	public int method_18318() {
		return this.field_20044;
	}

	public int method_18319() {
		return this.field_20041;
	}

	public int method_18320() {
		return this.field_20042;
	}

	public int method_18321() {
		return this.field_20045;
	}

	public int method_18322() {
		return this.field_20046;
	}

	public int method_18323() {
		return this.field_20039;
	}

	public int method_18324() {
		return this.field_20040;
	}

	public double method_18325() {
		return this.field_20047;
	}
}
