package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public abstract class FacingBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);

	protected FacingBlock(Material material) {
		super(material);
	}

	protected FacingBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
	}
}
