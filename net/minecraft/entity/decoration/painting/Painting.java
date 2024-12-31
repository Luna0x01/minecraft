package net.minecraft.entity.decoration.painting;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Painting {
	public static final Painting KEBAB = register("kebab", 16, 16, 0, 0);
	public static final Painting AZTEC = register("aztec", 16, 16, 16, 0);
	public static final Painting ALBAN = register("alban", 16, 16, 32, 0);
	public static final Painting AZTEC2 = register("aztec2", 16, 16, 48, 0);
	public static final Painting BOMB = register("bomb", 16, 16, 64, 0);
	public static final Painting PLANT = register("plant", 16, 16, 80, 0);
	public static final Painting WASTELAND = register("wasteland", 16, 16, 96, 0);
	public static final Painting POOL = register("pool", 32, 16, 0, 32);
	public static final Painting COURBET = register("courbet", 32, 16, 32, 32);
	public static final Painting SEA = register("sea", 32, 16, 64, 32);
	public static final Painting SUNSET = register("sunset", 32, 16, 96, 32);
	public static final Painting CREEBET = register("creebet", 32, 16, 128, 32);
	public static final Painting WANDERER = register("wanderer", 16, 32, 0, 64);
	public static final Painting GRAHAM = register("graham", 16, 32, 16, 64);
	public static final Painting MATCH = register("match", 32, 32, 0, 128);
	public static final Painting BUST = register("bust", 32, 32, 32, 128);
	public static final Painting STAGE = register("stage", 32, 32, 64, 128);
	public static final Painting VOID = register("void", 32, 32, 96, 128);
	public static final Painting SKULL_AND_ROSES = register("skull_and_roses", 32, 32, 128, 128);
	public static final Painting WITHER = register("wither", 32, 32, 160, 128);
	public static final Painting FIGHTERS = register("fighters", 64, 32, 0, 96);
	public static final Painting POINTER = register("pointer", 64, 64, 0, 192);
	public static final Painting PIGSCENE = register("pigscene", 64, 64, 64, 192);
	public static final Painting BURNING_SKULL = register("burning_skull", 64, 64, 128, 192);
	public static final Painting SKELETON = register("skeleton", 64, 48, 192, 64);
	public static final Painting DONKEY_KONG = register("donkey_kong", 64, 48, 192, 112);
	private final int field_16993;
	private final int field_16994;
	private final int field_16995;
	private final int field_16996;

	public static void method_15838() {
	}

	public Painting(int i, int j, int k, int l) {
		this.field_16993 = i;
		this.field_16994 = j;
		this.field_16995 = k;
		this.field_16996 = l;
	}

	public int method_15840() {
		return this.field_16993;
	}

	public int method_15841() {
		return this.field_16994;
	}

	public int method_15842() {
		return this.field_16995;
	}

	public int method_15843() {
		return this.field_16996;
	}

	public static Painting register(String string, int i, int j, int k, int l) {
		Painting painting = new Painting(i, j, k, l);
		Registry.PAINTING.add(new Identifier(string), painting);
		return painting;
	}
}
