package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SoulSandBlock extends Block {
	public SoulSandBlock() {
		super(Material.SAND, MaterialColor.BROWN);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		float f = 0.125F;
		return new Box(
			(double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)((float)(pos.getY() + 1) - f), (double)(pos.getZ() + 1)
		);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		entity.velocityX *= 0.4;
		entity.velocityZ *= 0.4;
	}
}
