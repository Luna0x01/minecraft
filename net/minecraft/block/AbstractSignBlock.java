package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AbstractSignBlock extends BlockWithEntity {
	protected static final Box field_12751 = new Box(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);

	protected AbstractSignBlock() {
		super(Material.WOOD);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12751;
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
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

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SIGN;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.SIGN);
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			return blockEntity instanceof SignBlockEntity ? ((SignBlockEntity)blockEntity).onActivate(playerEntity) : false;
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return !this.isAdjacentToCactus(world, pos) && super.canBePlacedAtPos(world, pos);
	}
}
