package net.minecraft.predicate.block;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

public class BlockMaterialPredicate implements Predicate<BlockState> {
	private final Material field_14844;

	private BlockMaterialPredicate(Material material) {
		this.field_14844 = material;
	}

	public static BlockMaterialPredicate create(Material material) {
		return new BlockMaterialPredicate(material);
	}

	public boolean apply(@Nullable BlockState blockState) {
		return blockState != null && blockState.getMaterial() == this.field_14844;
	}
}
