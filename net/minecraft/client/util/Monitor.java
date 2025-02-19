package net.minecraft.client.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class Monitor {
	private final long handle;
	private final List<VideoMode> videoModes;
	private VideoMode currentVideoMode;
	private int x;
	private int y;

	public Monitor(long handle) {
		this.handle = handle;
		this.videoModes = Lists.newArrayList();
		this.populateVideoModes();
	}

	public void populateVideoModes() {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		this.videoModes.clear();
		Buffer buffer = GLFW.glfwGetVideoModes(this.handle);

		for (int i = buffer.limit() - 1; i >= 0; i--) {
			buffer.position(i);
			VideoMode videoMode = new VideoMode(buffer);
			if (videoMode.getRedBits() >= 8 && videoMode.getGreenBits() >= 8 && videoMode.getBlueBits() >= 8) {
				this.videoModes.add(videoMode);
			}
		}

		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetMonitorPos(this.handle, is, js);
		this.x = is[0];
		this.y = js[0];
		GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(this.handle);
		this.currentVideoMode = new VideoMode(gLFWVidMode);
	}

	public VideoMode findClosestVideoMode(Optional<VideoMode> videoMode) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		if (videoMode.isPresent()) {
			VideoMode videoMode2 = (VideoMode)videoMode.get();

			for (VideoMode videoMode3 : this.videoModes) {
				if (videoMode3.equals(videoMode2)) {
					return videoMode3;
				}
			}
		}

		return this.getCurrentVideoMode();
	}

	public int findClosestVideoModeIndex(VideoMode videoMode) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		return this.videoModes.indexOf(videoMode);
	}

	public VideoMode getCurrentVideoMode() {
		return this.currentVideoMode;
	}

	public int getViewportX() {
		return this.x;
	}

	public int getViewportY() {
		return this.y;
	}

	public VideoMode getVideoMode(int index) {
		return (VideoMode)this.videoModes.get(index);
	}

	public int getVideoModeCount() {
		return this.videoModes.size();
	}

	public long getHandle() {
		return this.handle;
	}

	public String toString() {
		return String.format("Monitor[%s %sx%s %s]", this.handle, this.x, this.y, this.currentVideoMode);
	}
}
