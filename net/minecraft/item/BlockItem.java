package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockItem extends Item {
	protected final Block block;

	public BlockItem(Block block) {
		this.block = block;
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!block.method_8638(world, pos)) {
			pos = pos.offset(direction);
		}

		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isEmpty() && player.canModify(pos, direction, itemStack) && world.method_8493(this.block, pos, false, direction, null)) {
			int i = this.getMeta(itemStack.getData());
			BlockState blockState2 = this.block.getStateFromData(world, pos, direction, x, y, z, i, player);
			if (world.setBlockState(pos, blockState2, 11)) {
				blockState2 = world.getBlockState(pos);
				if (blockState2.getBlock() == this.block) {
					setBlockEntityNbt(world, player, pos, itemStack);
					this.block.onPlaced(world, pos, blockState2, player, itemStack);
					if (player instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)player, pos, itemStack);
					}
				}

				BlockSoundGroup blockSoundGroup = this.block.getSoundGroup();
				world.method_11486(
					player, pos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
				);
				itemStack.decrement(1);
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
			NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
			if (nbtCompound != null) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.shouldNotCopyNbtFromItem() && (player == null || !player.method_13567())) {
						return false;
					}

					NbtCompound nbtCompound2 = blockEntity.toNbt(new NbtCompound());
					NbtCompound nbtCompound3 = nbtCompound2.copy();
					nbtCompound2.copyFrom(nbtCompound);
					nbtCompound2.putInt("x", pos.getX());
					nbtCompound2.putInt("y", pos.getY());
					nbtCompound2.putInt("z", pos.getZ());
					if (!nbtCompound2.equals(nbtCompound3)) {
						blockEntity.fromNbt(nbtCompound2);
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

		return world.method_8493(this.block, pos, false, dir, null);
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
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			this.block.addStacksForDisplay(group, stacks);
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		super.appendTooltips(stack, world, tooltip, tooltipContext);
		this.block.method_14306(stack, world, tooltip, tooltipContext);
	}

	public Block getBlock() {
		return this.block;
	}
}
