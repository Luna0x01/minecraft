package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.class_4277;
import net.minecraft.class_4469;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScreenshotUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void method_18271(File file, int i, int j, Framebuffer framebuffer, Consumer<Text> consumer) {
		method_18273(file, null, i, j, framebuffer, consumer);
	}

	public static void method_18273(File file, @Nullable String string, int i, int j, Framebuffer framebuffer, Consumer<Text> consumer) {
		class_4277 lv = method_18269(i, j, framebuffer);
		File file2 = new File(file, "screenshots");
		file2.mkdir();
		File file3;
		if (string == null) {
			file3 = getScreenshotFile(file2);
		} else {
			file3 = new File(file2, string);
		}

		class_4469.field_21928
			.execute(
				() -> {
					try {
						lv.method_19471(file3);
						Text text = new LiteralText(file3.getName())
							.formatted(Formatting.UNDERLINE)
							.styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath())));
						consumer.accept(new TranslatableText("screenshot.success", text));
					} catch (Exception var7x) {
						LOGGER.warn("Couldn't save screenshot", var7x);
						consumer.accept(new TranslatableText("screenshot.failure", var7x.getMessage()));
					} finally {
						lv.close();
					}
				}
			);
	}

	public static class_4277 method_18269(int i, int j, Framebuffer framebuffer) {
		if (GLX.supportsFbo()) {
			i = framebuffer.textureWidth;
			j = framebuffer.textureHeight;
		}

		class_4277 lv = new class_4277(i, j, false);
		if (GLX.supportsFbo()) {
			GlStateManager.bindTexture(framebuffer.colorAttachment);
			lv.method_19467(0, true);
		} else {
			lv.method_19476(true);
		}

		lv.method_19484();
		return lv;
	}

	private static File getScreenshotFile(File screenshotsDirectory) {
		String string = DATE_FORMAT.format(new Date());
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
