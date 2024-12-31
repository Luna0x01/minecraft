package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class PlantBlock extends Block {
	protected PlantBlock() {
		this(Material.PLANT);
	}

	protected PlantBlock(Material material) {
		this(material, material.getColor());
	}

	protected PlantBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.setTickRandomly(true);
		float f = 0.2F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && this.canPlantOnTop(world.getBlockState(pos.down()).getBlock());
	}

	protected boolean canPlantOnTop(Block block) {
		return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		super.neighborUpdate(world, pos, state, block);
		this.plantAt(world, pos, state);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.plantAt(world, pos, state);
	}

	protected void plantAt(World world, BlockPos pos, BlockState state) {
		if (!this.canPlantAt(world, pos, state)) {
			this.dropAsItem(world, pos, state, 0);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		return this.canPlantOnTop(world.getBlockState(pos.down()).getBlock());
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}
}
