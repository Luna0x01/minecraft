package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!block.isReplaceable(world, pos)) {
			pos = pos.offset(direction);
		}

		if (itemStack.count == 0) {
			return false;
		} else if (!player.canModify(pos, direction, itemStack)) {
			return false;
		} else if (world.canBlockBePlaced(this.block, pos, false, direction, null, itemStack)) {
			int i = this.getMeta(itemStack.getData());
			BlockState blockState2 = this.block.getStateFromData(world, pos, direction, facingX, facingY, facingZ, i, player);
			if (world.setBlockState(pos, blockState2, 3)) {
				blockState2 = world.getBlockState(pos);
				if (blockState2.getBlock() == this.block) {
					setBlockEntityNbt(world, player, pos, itemStack);
					this.block.onPlaced(world, pos, blockState2, player, itemStack);
				}

				world.playSound(
					(double)((float)pos.getX() + 0.5F),
					(double)((float)pos.getY() + 0.5F),
					(double)((float)pos.getZ() + 0.5F),
					this.block.sound.getSound(),
					(this.block.sound.getVolume() + 1.0F) / 2.0F,
					this.block.sound.getPitch() * 0.8F
				);
				itemStack.count--;
			}

			return true;
		} else {
			return false;
		}
	}

	public static boolean setBlockEntityNbt(World world, PlayerEntity player, BlockPos pos, ItemStack itemStack) {
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		if (minecraftServer == null) {
			return false;
		} else {
			if (itemStack.hasNbt() && itemStack.getNbt().contains("BlockEntityTag", 10)) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.shouldNotCopyNbtFromItem() && !minecraftServer.getPlayerManager().isOperator(player.getGameProfile())) {
						return false;
					}

					NbtCompound nbtCompound = new NbtCompound();
					NbtCompound nbtCompound2 = (NbtCompound)nbtCompound.copy();
					blockEntity.toNbt(nbtCompound);
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
		} else if (!block.isReplaceable(world, pos)) {
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
