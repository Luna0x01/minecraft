package net.minecraft.world.dimension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum DimensionType {
	OVERWORLD(0, "overworld", "", OverworldDimension.class),
	NETHER(-1, "the_nether", "_nether", TheNetherDimension.class),
	THE_END(1, "the_end", "_end", TheEndDimension.class);

	private final int id;
	private final String name;
	private final String suffix;
	private final Class<? extends Dimension> dimensionClass;

	private DimensionType(int j, String string2, String string3, Class<? extends Dimension> class_) {
		this.id = j;
		this.name = string2;
		this.suffix = string3;
		this.dimensionClass = class_;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public Dimension create() {
		try {
			Constructor<? extends Dimension> constructor = this.dimensionClass.getConstructor();
			return (Dimension)constructor.newInstance();
		} catch (NoSuchMethodException var2) {
			throw new Error("Could not create new dimension", var2);
		} catch (InvocationTargetException var3) {
			throw new Error("Could not create new dimension", var3);
		} catch (InstantiationException var4) {
			throw new Error("Could not create new dimension", var4);
		} catch (IllegalAccessException var5) {
			throw new Error("Could not create new dimension", var5);
		}
	}

	public static DimensionType fromId(int id) {
		for (DimensionType dimensionType : values()) {
			if (dimensionType.getId() == id) {
				return dimensionType;
			}
		}

		throw new IllegalArgumentException("Invalid dimension id " + id);
	}

	public static DimensionType fromName(String name) {
		for (DimensionType dimensionType : values()) {
			if (dimensionType.getName().equals(name)) {
				return dimensionType;
			}
		}

		throw new IllegalArgumentException("Invalid dimension " + name);
	}
}
