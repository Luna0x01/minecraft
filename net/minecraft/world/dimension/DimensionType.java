package net.minecraft.world.dimension;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.class_3794;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DimensionType {
	public static final DimensionType OVERWORLD = method_17198("overworld", new DimensionType(1, "", "", class_3794::new));
	public static final DimensionType THE_NETHER = method_17198("the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new));
	public static final DimensionType THE_END = method_17198("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new));
	private final int field_18957;
	private final String field_18958;
	private final String field_18959;
	private final Supplier<? extends Dimension> field_18960;

	public static void method_17194() {
	}

	private static DimensionType method_17198(String string, DimensionType dimensionType) {
		Registry.DIMENSION_TYPE.set(dimensionType.field_18957, new Identifier(string), dimensionType);
		return dimensionType;
	}

	protected DimensionType(int i, String string, String string2, Supplier<? extends Dimension> supplier) {
		this.field_18957 = i;
		this.field_18958 = string;
		this.field_18959 = string2;
		this.field_18960 = supplier;
	}

	public static Iterable<DimensionType> method_17200() {
		return Registry.DIMENSION_TYPE;
	}

	public int method_17201() {
		return this.field_18957 + -1;
	}

	public String method_17202() {
		return this.field_18958;
	}

	public File method_17197(File file) {
		return this.field_18959.isEmpty() ? file : new File(file, this.field_18959);
	}

	public Dimension method_17203() {
		return (Dimension)this.field_18960.get();
	}

	public String toString() {
		return method_17196(this).toString();
	}

	@Nullable
	public static DimensionType method_17195(int i) {
		return Registry.DIMENSION_TYPE.getByRawId(i - -1);
	}

	@Nullable
	public static DimensionType method_17199(Identifier identifier) {
		return Registry.DIMENSION_TYPE.getByIdentifier(identifier);
	}

	@Nullable
	public static Identifier method_17196(DimensionType dimensionType) {
		return Registry.DIMENSION_TYPE.getId(dimensionType);
	}
}
