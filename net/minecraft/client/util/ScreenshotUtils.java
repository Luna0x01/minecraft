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
import org.lwjgl.opengl.GL11;

public class ScreenshotUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static IntBuffer intBuffer;
	private static int[] field_1035;

	public static Text saveScreenshot(File parent, int textureWidth, int textureHeight, Framebuffer buffer) {
		return saveScreenshot(parent, null, textureWidth, textureHeight, buffer);
	}

	public static Text saveScreenshot(File parent, String name, int textureWidth, int textureHeight, Framebuffer buffer) {
		try {
			File file = new File(parent, "screenshots");
			file.mkdir();
			if (GLX.supportsFbo()) {
				textureWidth = buffer.textureWidth;
				textureHeight = buffer.textureHeight;
			}

			int i = textureWidth * textureHeight;
			if (intBuffer == null || intBuffer.capacity() < i) {
				intBuffer = BufferUtils.createIntBuffer(i);
				field_1035 = new int[i];
			}

			GL11.glPixelStorei(3333, 1);
			GL11.glPixelStorei(3317, 1);
			intBuffer.clear();
			if (GLX.supportsFbo()) {
				GlStateManager.bindTexture(buffer.colorAttachment);
				GL11.glGetTexImage(3553, 0, 32993, 33639, intBuffer);
			} else {
				GL11.glReadPixels(0, 0, textureWidth, textureHeight, 32993, 33639, intBuffer);
			}

			intBuffer.get(field_1035);
			TextureUtil.flipXY(field_1035, textureWidth, textureHeight);
			BufferedImage bufferedImage = null;
			if (GLX.supportsFbo()) {
				bufferedImage = new BufferedImage(buffer.viewportWidth, buffer.viewportHeight, 1);
				int j = buffer.textureHeight - buffer.viewportHeight;

				for (int k = j; k < buffer.textureHeight; k++) {
					for (int l = 0; l < buffer.viewportWidth; l++) {
						bufferedImage.setRGB(l, k - j, field_1035[k * buffer.textureWidth + l]);
					}
				}
			} else {
				bufferedImage = new BufferedImage(textureWidth, textureHeight, 1);
				bufferedImage.setRGB(0, 0, textureWidth, textureHeight, field_1035, 0, textureWidth);
			}

			File file2;
			if (name == null) {
				file2 = getScreenshotFile(file);
			} else {
				file2 = new File(file, name);
			}

			ImageIO.write(bufferedImage, "png", file2);
			Text text = new LiteralText(file2.getName());
			text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
			text.getStyle().setUnderline(true);
			return new TranslatableText("screenshot.success", text);
		} catch (Exception var11) {
			LOGGER.warn("Couldn't save screenshot", var11);
			return new TranslatableText("screenshot.failure", var11.getMessage());
		}
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
