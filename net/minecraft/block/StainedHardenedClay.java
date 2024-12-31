package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class StainedHardenedClay extends WoolBlock {
	private static final MaterialColor[] MATERIAL_COLORS = new MaterialColor[]{
		MaterialColor.field_15838,
		MaterialColor.field_15839,
		MaterialColor.field_15840,
		MaterialColor.field_15841,
		MaterialColor.field_15842,
		MaterialColor.field_15843,
		MaterialColor.field_15844,
		MaterialColor.field_15845,
		MaterialColor.field_15846,
		MaterialColor.field_15847,
		MaterialColor.field_15848,
		MaterialColor.field_15849,
		MaterialColor.field_15850,
		MaterialColor.field_15851,
		MaterialColor.field_15852,
		MaterialColor.field_15853
	};

	public StainedHardenedClay() {
		super(Material.STONE);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return MATERIAL_COLORS[((DyeColor)state.get(COLOR)).getId()];
	}
}
