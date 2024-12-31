package net.minecraft.dragon;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class class_2993<T extends class_2987> {
	private static class_2993<?>[] field_14709 = new class_2993[0];
	public static final class_2993<class_2983> HOLDING_PATTERN = method_13198(class_2983.class, "HoldingPattern");
	public static final class_2993<class_2991> STRAFE_PLAYER = method_13198(class_2991.class, "StrafePlayer");
	public static final class_2993<class_2985> LANDING_APPROACH = method_13198(class_2985.class, "LandingApproach");
	public static final class_2993<class_2986> LANDING = method_13198(class_2986.class, "Landing");
	public static final class_2993<class_2992> TAKEOFF = method_13198(class_2992.class, "Takeoff");
	public static final class_2993<class_2989> SITTING_FLAMING = method_13198(class_2989.class, "SittingFlaming");
	public static final class_2993<class_2990> SITTING_SCANNING = method_13198(class_2990.class, "SittingScanning");
	public static final class_2993<class_2988> SITTING_ATTACKING = method_13198(class_2988.class, "SittingAttacking");
	public static final class_2993<class_2981> CHARGING_PLAYER = method_13198(class_2981.class, "ChargingPlayer");
	public static final class_2993<class_2982> DYING = method_13198(class_2982.class, "Dying");
	public static final class_2993<class_2984> HOVER = method_13198(class_2984.class, "Hover");
	private final Class<? extends class_2987> field_14710;
	private final int field_14711;
	private final String field_14712;

	private class_2993(int i, Class<? extends class_2987> class_, String string) {
		this.field_14711 = i;
		this.field_14710 = class_;
		this.field_14712 = string;
	}

	public class_2987 method_13199(EnderDragonEntity enderDragonEntity) {
		try {
			Constructor<? extends class_2987> constructor = this.method_13196();
			return (class_2987)constructor.newInstance(enderDragonEntity);
		} catch (Exception var3) {
			throw new Error(var3);
		}
	}

	protected Constructor<? extends class_2987> method_13196() throws NoSuchMethodException {
		return this.field_14710.getConstructor(EnderDragonEntity.class);
	}

	public int method_13200() {
		return this.field_14711;
	}

	public String toString() {
		return this.field_14712 + " (#" + this.field_14711 + ")";
	}

	public static class_2993<?> method_13197(int i) {
		return i >= 0 && i < field_14709.length ? field_14709[i] : HOLDING_PATTERN;
	}

	public static int method_13201() {
		return field_14709.length;
	}

	private static <T extends class_2987> class_2993<T> method_13198(Class<T> class_, String string) {
		class_2993<T> lv = new class_2993<>(field_14709.length, class_, string);
		field_14709 = (class_2993<?>[])Arrays.copyOf(field_14709, field_14709.length + 1);
		field_14709[lv.method_13200()] = lv;
		return lv;
	}
}
