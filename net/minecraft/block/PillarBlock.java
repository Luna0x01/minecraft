package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public abstract class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class);

	protected PillarBlock(Material material) {
		super(material, material.getColor());
	}

	protected PillarBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
	}
}
