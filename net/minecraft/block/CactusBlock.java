package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CactusBlock extends Block {
	public static final IntProperty AGE = IntProperty.of("age", 0, 15);

	protected CactusBlock() {
		super(Material.CACTUS);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		BlockPos blockPos = pos.up();
		if (world.isAir(blockPos)) {
			int i = 1;

			while (world.getBlockState(pos.down(i)).getBlock() == this) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)state.get(AGE);
				if (j == 15) {
					world.setBlockState(blockPos, this.getDefaultState());
					BlockState blockState = state.with(AGE, 0);
					world.setBlockState(pos, blockState, 4);
					this.neighborUpdate(world, blockPos, blockState, this);
				} else {
					world.setBlockState(pos, state.with(AGE, j + 1), 4);
				}
			}
		}
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		float f = 0.0625F;
		return new Box(
			(double)((float)pos.getX() + f),
			(double)pos.getY(),
			(double)((float)pos.getZ() + f),
			(double)((float)(pos.getX() + 1) - f),
			(double)((float)(pos.getY() + 1) - f),
			(double)((float)(pos.getZ() + 1) - f)
		);
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		float f = 0.0625F;
		return new Box(
			(double)((float)pos.getX() + f),
			(double)pos.getY(),
			(double)((float)pos.getZ() + f),
			(double)((float)(pos.getX() + 1) - f),
			(double)(pos.getY() + 1),
			(double)((float)(pos.getZ() + 1) - f)
		);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) ? this.canPlaceCactusAt(world, pos) : false;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!this.canPlaceCactusAt(world, pos)) {
			world.removeBlock(pos, true);
		}
	}

	public boolean canPlaceCactusAt(World world, BlockPos pos) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (world.getBlockState(pos.offset(direction)).getBlock().getMaterial().isSolid()) {
				return false;
			}
		}

		Block block = world.getBlockState(pos.down()).getBlock();
		return block == Blocks.CACTUS || block == Blocks.SAND;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		entity.damage(DamageSource.CACTUS, 1.0F);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(AGE, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(AGE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AGE);
	}
}
