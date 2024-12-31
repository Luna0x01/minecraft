package net.minecraft.client.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

public class MonitorTracker {
	private final Long2ObjectMap<Monitor> pointerToMonitorMap = new Long2ObjectOpenHashMap();
	private final MonitorFactory monitorFactory;

	public MonitorTracker(MonitorFactory monitorFactory) {
		this.monitorFactory = monitorFactory;
		GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
		PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
		if (pointerBuffer != null) {
			for (int i = 0; i < pointerBuffer.limit(); i++) {
				long l = pointerBuffer.get(i);
				this.pointerToMonitorMap.put(l, monitorFactory.createMonitor(l));
			}
		}
	}

	private void handleMonitorEvent(long l, int i) {
		if (i == 262145) {
			this.pointerToMonitorMap.put(l, this.monitorFactory.createMonitor(l));
		} else if (i == 262146) {
			this.pointerToMonitorMap.remove(l);
		}
	}

	@Nullable
	public Monitor getMonitor(long l) {
		return (Monitor)this.pointerToMonitorMap.get(l);
	}

	@Nullable
	public Monitor getMonitor(Window window) {
		long l = GLFW.glfwGetWindowMonitor(window.getHandle());
		if (l != 0L) {
			return this.getMonitor(l);
		} else {
			int i = window.getPositionY();
			int j = i + window.getWidth();
			int k = window.getPositionX();
			int m = k + window.getHeight();
			int n = -1;
			Monitor monitor = null;
			ObjectIterator var10 = this.pointerToMonitorMap.values().iterator();

			while (var10.hasNext()) {
				Monitor monitor2 = (Monitor)var10.next();
				int o = monitor2.getViewportX();
				int p = o + monitor2.getCurrentVideoMode().getWidth();
				int q = monitor2.getViewportY();
				int r = q + monitor2.getCurrentVideoMode().getHeight();
				int s = clamp(i, o, p);
				int t = clamp(j, o, p);
				int u = clamp(k, q, r);
				int v = clamp(m, q, r);
				int w = Math.max(0, t - s);
				int x = Math.max(0, v - u);
				int y = w * x;
				if (y > n) {
					monitor = monitor2;
					n = y;
				}
			}

			return monitor;
		}
	}

	public static int clamp(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	public void stop() {
		GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
		if (gLFWMonitorCallback != null) {
			gLFWMonitorCallback.free();
		}
	}
}
