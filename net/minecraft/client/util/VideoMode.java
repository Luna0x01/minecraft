package net.minecraft.client.util;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class VideoMode {
	private final int width;
	private final int height;
	private final int redBits;
	private final int greenBits;
	private final int blueBits;
	private final int refreshRate;
	private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

	public VideoMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
		this.width = width;
		this.height = height;
		this.redBits = redBits;
		this.greenBits = greenBits;
		this.blueBits = blueBits;
		this.refreshRate = refreshRate;
	}

	public VideoMode(Buffer buffer) {
		this.width = buffer.width();
		this.height = buffer.height();
		this.redBits = buffer.redBits();
		this.greenBits = buffer.greenBits();
		this.blueBits = buffer.blueBits();
		this.refreshRate = buffer.refreshRate();
	}

	public VideoMode(GLFWVidMode gLFWVidMode) {
		this.width = gLFWVidMode.width();
		this.height = gLFWVidMode.height();
		this.redBits = gLFWVidMode.redBits();
		this.greenBits = gLFWVidMode.greenBits();
		this.blueBits = gLFWVidMode.blueBits();
		this.refreshRate = gLFWVidMode.refreshRate();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getRedBits() {
		return this.redBits;
	}

	public int getGreenBits() {
		return this.greenBits;
	}

	public int getBlueBits() {
		return this.blueBits;
	}

	public int getRefreshRate() {
		return this.refreshRate;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			VideoMode videoMode = (VideoMode)o;
			return this.width == videoMode.width
				&& this.height == videoMode.height
				&& this.redBits == videoMode.redBits
				&& this.greenBits == videoMode.greenBits
				&& this.blueBits == videoMode.blueBits
				&& this.refreshRate == videoMode.refreshRate;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate});
	}

	public String toString() {
		return String.format("%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
	}

	public static Optional<VideoMode> fromString(@Nullable String string) {
		if (string == null) {
			return Optional.empty();
		} else {
			try {
				Matcher matcher = PATTERN.matcher(string);
				if (matcher.matches()) {
					int i = Integer.parseInt(matcher.group(1));
					int j = Integer.parseInt(matcher.group(2));
					String string2 = matcher.group(3);
					int k;
					if (string2 == null) {
						k = 60;
					} else {
						k = Integer.parseInt(string2);
					}

					String string3 = matcher.group(4);
					int m;
					if (string3 == null) {
						m = 24;
					} else {
						m = Integer.parseInt(string3);
					}

					int o = m / 3;
					return Optional.of(new VideoMode(i, j, o, o, o, k));
				}
			} catch (Exception var9) {
			}

			return Optional.empty();
		}
	}

	public String asString() {
		return String.format("%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
	}
}
