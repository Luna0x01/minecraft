package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.state.property.DirectionProperty;

public abstract class FacingBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing");

	protected FacingBlock(Material material) {
		super(material);
	}
}
