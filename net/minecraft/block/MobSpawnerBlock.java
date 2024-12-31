package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSpawnerBlock extends BlockWithEntity {
	protected MobSpawnerBlock() {
		super(Material.STONE);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new MobSpawnerBlockEntity();
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, id);
		int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);
		this.dropExperience(world, pos, i);
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Nullable
	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return null;
	}
}
