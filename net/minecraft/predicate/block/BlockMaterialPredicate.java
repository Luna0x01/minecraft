package net.minecraft.predicate.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

public class BlockMaterialPredicate implements Predicate<BlockState> {
	private static final BlockMaterialPredicate field_18699 = new BlockMaterialPredicate(Material.AIR) {
		@Override
		public boolean test(@Nullable BlockState blockState) {
			return blockState != null && blockState.isAir();
		}
	};
	private final Material field_14844;

	private BlockMaterialPredicate(Material material) {
		this.field_14844 = material;
	}

	public static BlockMaterialPredicate create(Material material) {
		return material == Material.AIR ? field_18699 : new BlockMaterialPredicate(material);
	}

	public boolean test(@Nullable BlockState blockState) {
		return blockState != null && blockState.getMaterial() == this.field_14844;
	}
}
