package net.minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.lwjgl.system.Pointer;

public class class_4105 {
	@Nullable
	private static final MethodHandle field_19904 = Util.make(() -> {
		try {
			Lookup lookup = MethodHandles.lookup();
			Class<?> class_ = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
			Method method = class_.getDeclaredMethod("untrack", long.class);
			method.setAccessible(true);
			Field field = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
			field.setAccessible(true);
			Object object = field.get(null);
			return class_.isInstance(object) ? lookup.unreflect(method) : null;
		} catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException var5) {
			throw new RuntimeException(var5);
		}
	});

	public static void method_18147(long l) {
		if (field_19904 != null) {
			try {
				field_19904.invoke(l);
			} catch (Throwable var3) {
				throw new RuntimeException(var3);
			}
		}
	}

	public static void method_18148(Pointer pointer) {
		method_18147(pointer.address());
	}
}
