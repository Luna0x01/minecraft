package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public abstract class HorizontalFacingBlock extends Block {
	public static final DirectionProperty DIRECTION = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);

	protected HorizontalFacingBlock(Material material) {
		super(material);
	}

	protected HorizontalFacingBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
	}
}
