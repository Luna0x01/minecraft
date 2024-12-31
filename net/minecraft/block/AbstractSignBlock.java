package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AbstractSignBlock extends BlockWithEntity {
	protected AbstractSignBlock() {
		super(Material.WOOD);
		float f = 0.25F;
		float g = 1.0F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, g, 0.5F + f);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		this.setBoundingBox(world, pos);
		return super.getSelectionBox(world, pos);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return true;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new SignBlockEntity();
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SIGN;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.SIGN;
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			return blockEntity instanceof SignBlockEntity ? ((SignBlockEntity)blockEntity).onActivate(player) : false;
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return !this.isAdjacentToCactus(world, pos) && super.canBePlacedAtPos(world, pos);
	}
}
