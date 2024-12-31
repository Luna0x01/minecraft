package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

public class ScreenshotUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static IntBuffer intBuffer;
	private static int[] field_1035;

	public static Text saveScreenshot(File parent, int textureWidth, int textureHeight, Framebuffer buffer) {
		return method_12154(parent, null, textureWidth, textureHeight, buffer);
	}

	public static Text method_12154(File file, String string, int i, int j, Framebuffer framebuffer) {
		try {
			File file2 = new File(file, "screenshots");
			file2.mkdir();
			BufferedImage bufferedImage = method_12153(i, j, framebuffer);
			File file3;
			if (string == null) {
				file3 = getScreenshotFile(file2);
			} else {
				file3 = new File(file2, string);
			}

			ImageIO.write(bufferedImage, "png", file3);
			Text text = new LiteralText(file3.getName());
			text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath()));
			text.getStyle().setUnderline(true);
			return new TranslatableText("screenshot.success", text);
		} catch (Exception var9) {
			LOGGER.warn("Couldn't save screenshot", var9);
			return new TranslatableText("screenshot.failure", var9.getMessage());
		}
	}

	public static BufferedImage method_12153(int i, int j, Framebuffer framebuffer) {
		if (GLX.supportsFbo()) {
			i = framebuffer.textureWidth;
			j = framebuffer.textureHeight;
		}

		int k = i * j;
		if (intBuffer == null || intBuffer.capacity() < k) {
			intBuffer = BufferUtils.createIntBuffer(k);
			field_1035 = new int[k];
		}

		GlStateManager.method_12314(3333, 1);
		GlStateManager.method_12314(3317, 1);
		intBuffer.clear();
		if (GLX.supportsFbo()) {
			GlStateManager.bindTexture(framebuffer.colorAttachment);
			GlStateManager.method_12278(3553, 0, 32993, 33639, intBuffer);
		} else {
			GlStateManager.method_12277(0, 0, i, j, 32993, 33639, intBuffer);
		}

		intBuffer.get(field_1035);
		TextureUtil.flipXY(field_1035, i, j);
		BufferedImage bufferedImage = null;
		if (GLX.supportsFbo()) {
			bufferedImage = new BufferedImage(framebuffer.viewportWidth, framebuffer.viewportHeight, 1);
			int l = framebuffer.textureHeight - framebuffer.viewportHeight;

			for (int m = l; m < framebuffer.textureHeight; m++) {
				for (int n = 0; n < framebuffer.viewportWidth; n++) {
					bufferedImage.setRGB(n, m - l, field_1035[m * framebuffer.textureWidth + n]);
				}
			}
		} else {
			bufferedImage = new BufferedImage(i, j, 1);
			bufferedImage.setRGB(0, 0, i, j, field_1035, 0, i);
		}

		return bufferedImage;
	}

	private static File getScreenshotFile(File screenshotsDirectory) {
		String string = DATE_FORMAT.format(new Date()).toString();
		int i = 1;

		while (true) {
			File file = new File(screenshotsDirectory, string + (i == 1 ? "" : "_" + i) + ".png");
			if (!file.exists()) {
				return file;
			}

			i++;
		}
	}
}
