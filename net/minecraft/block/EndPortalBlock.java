package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalBlock extends BlockWithEntity {
	protected EndPortalBlock(Material material) {
		super(material);
		this.setLightLevel(1.0F);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EndPortalBlockEntity();
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		float f = 0.0625F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return facing == Direction.DOWN ? super.isSideInvisible(view, pos, facing) : false;
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
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
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity.vehicle == null && entity.rider == null && !world.isClient) {
			entity.teleportToDimension(1);
		}
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		double d = (double)((float)pos.getX() + rand.nextFloat());
		double e = (double)((float)pos.getY() + 0.8F);
		double f = (double)((float)pos.getZ() + rand.nextFloat());
		double g = 0.0;
		double h = 0.0;
		double i = 0.0;
		world.addParticle(ParticleType.SMOKE, d, e, f, g, h, i);
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return null;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.BLACK;
	}
}
