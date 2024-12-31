package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SoulSandBlock extends Block {
	protected static final Box field_12758 = new Box(0.0, 0.0, 0.0, 1.0, 0.875, 1.0);

	public SoulSandBlock() {
		super(Material.SAND, MaterialColor.BROWN);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return field_12758;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		entity.velocityX *= 0.4;
		entity.velocityZ *= 0.4;
	}
}
