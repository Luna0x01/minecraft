package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock extends PillarBlock {
	public HayBlock() {
		super(Material.GRASS, MaterialColor.YELLOW);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		entity.handleFallDamage(distance, 0.2F);
	}
}
