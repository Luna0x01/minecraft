package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockItem extends Item {
	protected final Block block;

	public BlockItem(Block block) {
		this.block = block;
	}

	public BlockItem setTranslationKey(String string) {
		super.setTranslationKey(string);
		return this;
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (!block.method_8638(world, blockPos)) {
			blockPos = blockPos.offset(direction);
		}

		if (itemStack.count != 0
			&& playerEntity.canModify(blockPos, direction, itemStack)
			&& world.canBlockBePlaced(this.block, blockPos, false, direction, null, itemStack)) {
			int i = this.getMeta(itemStack.getData());
			BlockState blockState2 = this.block.getStateFromData(world, blockPos, direction, f, g, h, i, playerEntity);
			if (world.setBlockState(blockPos, blockState2, 11)) {
				blockState2 = world.getBlockState(blockPos);
				if (blockState2.getBlock() == this.block) {
					setBlockEntityNbt(world, playerEntity, blockPos, itemStack);
					this.block.onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
				}

				BlockSoundGroup blockSoundGroup = this.block.getSoundGroup();
				world.method_11486(
					playerEntity,
					blockPos,
					blockSoundGroup.method_4194(),
					SoundCategory.BLOCKS,
					(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
					blockSoundGroup.getPitch() * 0.8F
				);
				itemStack.count--;
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	public static boolean setBlockEntityNbt(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack itemStack) {
		MinecraftServer minecraftServer = world.getServer();
		if (minecraftServer == null) {
			return false;
		} else {
			if (itemStack.hasNbt() && itemStack.getNbt().contains("BlockEntityTag", 10)) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.shouldNotCopyNbtFromItem() && (player == null || !player.method_13567())) {
						return false;
					}

					NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
					NbtCompound nbtCompound2 = nbtCompound.copy();
					NbtCompound nbtCompound3 = (NbtCompound)itemStack.getNbt().get("BlockEntityTag");
					nbtCompound.copyFrom(nbtCompound3);
					nbtCompound.putInt("x", pos.getX());
					nbtCompound.putInt("y", pos.getY());
					nbtCompound.putInt("z", pos.getZ());
					if (!nbtCompound.equals(nbtCompound2)) {
						blockEntity.fromNbt(nbtCompound);
						blockEntity.markDirty();
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean canPlaceItemBlock(World world, BlockPos pos, Direction dir, PlayerEntity player, ItemStack stack) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == Blocks.SNOW_LAYER) {
			dir = Direction.UP;
		} else if (!block.method_8638(world, pos)) {
			pos = pos.offset(dir);
		}

		return world.canBlockBePlaced(this.block, pos, false, dir, null, stack);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.block.getTranslationKey();
	}

	@Override
	public String getTranslationKey() {
		return this.block.getTranslationKey();
	}

	@Override
	public ItemGroup getItemGroup() {
		return this.block.getItemGroup();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		this.block.appendItemStacks(item, group, list);
	}

	public Block getBlock() {
		return this.block;
	}
}
