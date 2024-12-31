package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LilyPadBlock extends PlantBlock {
	protected static final Box SHAPE = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.09375, 0.9375);

	protected LilyPadBlock() {
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		if (!(entity instanceof BoatEntity)) {
			appendCollisionBoxes(pos, entityBox, boxes, SHAPE);
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		super.onEntityCollision(world, pos, state, entity);
		if (entity instanceof BoatEntity) {
			world.removeBlock(new BlockPos(pos), true);
		}
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return SHAPE;
	}

	@Override
	protected boolean method_11579(BlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getMaterial() == Material.ICE;
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			BlockState blockState = world.getBlockState(pos.down());
			Material material = blockState.getMaterial();
			return material == Material.WATER && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0 || material == Material.ICE;
		} else {
			return false;
		}
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}
}
