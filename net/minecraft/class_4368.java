package net.minecraft;

import java.nio.file.Path;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4368 extends class_4370<Fluid> {
	public class_4368(class_4344 arg) {
		super(arg, Registry.FLUID);
	}

	@Override
	protected void method_20081() {
		this.method_20079(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
		this.method_20079(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
	}

	@Override
	protected Path method_20078(Identifier identifier) {
		return this.field_21481.method_19993().resolve("data/" + identifier.getNamespace() + "/tags/fluids/" + identifier.getPath() + ".json");
	}

	@Override
	public String method_19995() {
		return "Fluid Tags";
	}

	@Override
	protected void method_20080(TagContainer<Fluid> tagContainer) {
		FluidTags.setContainer(tagContainer);
	}
}
