package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalBlock extends BlockWithEntity {
	protected static final Box field_12654 = new Box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);

	protected EndPortalBlock(Material material) {
		super(material);
		this.setLightLevel(1.0F);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EndPortalBlockEntity();
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12654;
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? super.method_8654(state, view, pos, direction) : false;
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
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
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.hasMount()
			&& !entity.hasPassengers()
			&& entity.canUsePortals()
			&& !world.isClient
			&& entity.getBoundingBox().intersects(state.getCollisionBox((BlockView)world, pos).offset(pos))) {
			entity.changeDimension(1);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)((float)pos.getX() + random.nextFloat());
		double e = (double)((float)pos.getY() + 0.8F);
		double f = (double)((float)pos.getZ() + random.nextFloat());
		double g = 0.0;
		double h = 0.0;
		double i = 0.0;
		world.addParticle(ParticleType.SMOKE, d, e, f, 0.0, 0.0, 0.0);
	}

	@Nullable
	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return null;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.BLACK;
	}
}
