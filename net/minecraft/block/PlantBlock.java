package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PlantBlock extends Block {
	protected static final Box field_12594 = new Box(0.3F, 0.0, 0.3F, 0.7F, 0.6F, 0.7F);

	protected PlantBlock() {
		this(Material.PLANT);
	}

	protected PlantBlock(Material material) {
		this(material, material.getColor());
	}

	protected PlantBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && this.method_11579(world.getBlockState(pos.down()));
	}

	protected boolean method_11579(BlockState blockState) {
		return blockState.getBlock() == Blocks.GRASS || blockState.getBlock() == Blocks.DIRT || blockState.getBlock() == Blocks.FARMLAND;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		super.neighborUpdate(state, world, pos, block, neighborPos);
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
		return this.method_11579(world.getBlockState(pos.down()));
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12594;
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
