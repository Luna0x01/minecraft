package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class SignType {
	private static final Set<SignType> VALUES = new ObjectArraySet();
	public static final SignType OAK = register(new SignType("oak"));
	public static final SignType SPRUCE = register(new SignType("spruce"));
	public static final SignType BIRCH = register(new SignType("birch"));
	public static final SignType ACACIA = register(new SignType("acacia"));
	public static final SignType JUNGLE = register(new SignType("jungle"));
	public static final SignType DARK_OAK = register(new SignType("dark_oak"));
	private final String name;

	protected SignType(String string) {
		this.name = string;
	}

	private static SignType register(SignType signType) {
		VALUES.add(signType);
		return signType;
	}

	public static Stream<SignType> stream() {
		return VALUES.stream();
	}

	public String getName() {
		return this.name;
	}
}
