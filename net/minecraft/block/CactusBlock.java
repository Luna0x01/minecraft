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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CactusBlock extends Block {
	public static final IntProperty AGE = IntProperty.of("age", 0, 15);
	protected static final Box field_12607 = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.9375, 0.9375);
	protected static final Box field_12608 = new Box(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);

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
					blockState.neighbourUpdate(world, blockPos, this, pos);
				} else {
					world.setBlockState(pos, state.with(AGE, j + 1), 4);
				}
			}
		}
	}

	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return field_12607;
	}

	@Override
	public Box method_11563(BlockState blockState, World world, BlockPos blockPos) {
		return field_12608.offset(blockPos);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) ? this.canPlaceCactusAt(world, pos) : false;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!this.canPlaceCactusAt(world, pos)) {
			world.removeBlock(pos, true);
		}
	}

	public boolean canPlaceCactusAt(World world, BlockPos pos) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			Material material = world.getBlockState(pos.offset(direction)).getMaterial();
			if (material.isSolid() || material == Material.LAVA) {
				return false;
			}
		}

		Block block = world.getBlockState(pos.down()).getBlock();
		return block == Blocks.CACTUS || block == Blocks.SAND && !world.getBlockState(pos.up()).getMaterial().isFluid();
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
