package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class class_3685 extends BlockWithEntity {
	private final SkullBlock.class_3722 field_17712;

	public class_3685(SkullBlock.class_3722 arg, Block.Builder builder) {
		super(builder);
		this.field_17712 = arg;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean method_13704(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SkullBlockEntity();
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && player.abilities.creativeMode) {
			SkullBlockEntity.method_16840(world, pos);
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock() && !world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof SkullBlockEntity) {
				SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
				if (skullBlockEntity.method_16842()) {
					ItemStack itemStack = this.getPickBlock(world, pos, state);
					Block block = skullBlockEntity.method_16783().getBlock();
					if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && skullBlockEntity.getOwner() != null) {
						NbtCompound nbtCompound = new NbtCompound();
						NbtHelper.fromGameProfile(nbtCompound, skullBlockEntity.getOwner());
						itemStack.getOrCreateNbt().put("SkullOwner", nbtCompound);
					}

					onBlockBreak(world, pos, itemStack);
				}
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	public SkullBlock.class_3722 method_16548() {
		return this.field_17712;
	}
}
