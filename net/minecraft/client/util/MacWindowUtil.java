package net.minecraft.client.util;

import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.util.Optional;
import org.lwjgl.glfw.GLFWNativeCocoa;

public class MacWindowUtil {
	private static final int FULLSCREEN_MASK = 16384;

	public static void toggleFullscreen(long handle) {
		getCocoaWindow(handle).filter(MacWindowUtil::isFullscreen).ifPresent(MacWindowUtil::toggleFullscreen);
	}

	private static Optional<NSObject> getCocoaWindow(long handle) {
		long l = GLFWNativeCocoa.glfwGetCocoaWindow(handle);
		return l != 0L ? Optional.of(new NSObject(new Pointer(l))) : Optional.empty();
	}

	private static boolean isFullscreen(NSObject handle) {
		return ((Long)handle.sendRaw("styleMask", new Object[0]) & 16384L) == 16384L;
	}

	private static void toggleFullscreen(NSObject handle) {
		handle.send("toggleFullScreen:", new Object[0]);
	}
}
