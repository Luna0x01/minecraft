package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.minecraft.class_4277;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

public class TextureUtil {
	private static final Logger LOGGER = LogManager.getLogger();

	public static int getTexLevelParameter() {
		return GlStateManager.getTexLevelParameter();
	}

	public static void deleteTexture(int id) {
		GlStateManager.deleteTexture(id);
	}

	public static void prepareImage(int id, int width, int height) {
		method_19532(class_4277.class_4279.RGBA, id, 0, width, height);
	}

	public static void method_19531(class_4277.class_4279 arg, int i, int j, int k) {
		method_19532(arg, i, 0, j, k);
	}

	public static void prepareImage(int id, int maxLevel, int width, int height) {
		method_19532(class_4277.class_4279.RGBA, id, maxLevel, width, height);
	}

	public static void method_19532(class_4277.class_4279 arg, int i, int j, int k, int l) {
		bindTexture(i);
		if (j >= 0) {
			GlStateManager.method_12294(3553, 33085, j);
			GlStateManager.method_12294(3553, 33082, 0);
			GlStateManager.method_12294(3553, 33083, j);
			GlStateManager.method_12293(3553, 34049, 0.0F);
		}

		for (int m = 0; m <= j; m++) {
			GlStateManager.method_12276(3553, m, arg.method_19499(), k >> m, l >> m, 0, 6408, 5121, null);
		}
	}

	private static void bindTexture(int texture) {
		GlStateManager.bindTexture(texture);
	}

	@Deprecated
	public static int[] method_19534(ResourceManager resourceManager, Identifier identifier) throws IOException {
		Resource resource = resourceManager.getResource(identifier);
		Throwable var3 = null;

		int[] var6;
		try (class_4277 lv = class_4277.method_19472(resource.getInputStream())) {
			var6 = lv.method_19482();
		} catch (Throwable var31) {
			var3 = var31;
			throw var31;
		} finally {
			if (resource != null) {
				if (var3 != null) {
					try {
						resource.close();
					} catch (Throwable var27) {
						var3.addSuppressed(var27);
					}
				} else {
					resource.close();
				}
			}
		}

		return var6;
	}

	public static ByteBuffer method_19533(InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer;
		if (inputStream instanceof FileInputStream) {
			FileInputStream fileInputStream = (FileInputStream)inputStream;
			FileChannel fileChannel = fileInputStream.getChannel();
			byteBuffer = MemoryUtil.memAlloc((int)fileChannel.size() + 1);

			while (fileChannel.read(byteBuffer) != -1) {
			}
		} else {
			byteBuffer = MemoryUtil.memAlloc(8192);
			ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);

			while (readableByteChannel.read(byteBuffer) != -1) {
				if (byteBuffer.remaining() == 0) {
					byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
				}
			}
		}

		return byteBuffer;
	}
}
