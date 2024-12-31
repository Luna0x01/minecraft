package net.minecraft;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.util.Identifier;

public class class_4329 {
	public static void method_19910() {
		class_4323.method_19899(new Identifier("brigadier:bool"), BoolArgumentType.class, new class_4326(BoolArgumentType::bool));
		class_4323.method_19899(new Identifier("brigadier:float"), FloatArgumentType.class, new class_4331());
		class_4323.method_19899(new Identifier("brigadier:double"), DoubleArgumentType.class, new class_4330());
		class_4323.method_19899(new Identifier("brigadier:integer"), IntegerArgumentType.class, new class_4332());
		class_4323.method_19899(new Identifier("brigadier:string"), StringArgumentType.class, new class_4333());
	}

	public static byte method_19912(boolean bl, boolean bl2) {
		byte b = 0;
		if (bl) {
			b = (byte)(b | 1);
		}

		if (bl2) {
			b = (byte)(b | 2);
		}

		return b;
	}

	public static boolean method_19911(byte b) {
		return (b & 1) != 0;
	}

	public static boolean method_19913(byte b) {
		return (b & 2) != 0;
	}
}
