package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItem extends Item {
	@Deprecated
	private final Block block;

	public BlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		return this.method_16012(new ItemPlacementContext(itemUsageContext));
	}

	public ActionResult method_16012(ItemPlacementContext itemPlacementContext) {
		if (!itemPlacementContext.method_16018()) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = this.method_16016(itemPlacementContext);
			if (blockState == null) {
				return ActionResult.FAIL;
			} else if (!this.method_16013(itemPlacementContext, blockState)) {
				return ActionResult.FAIL;
			} else {
				BlockPos blockPos = itemPlacementContext.getBlockPos();
				World world = itemPlacementContext.getWorld();
				PlayerEntity playerEntity = itemPlacementContext.getPlayer();
				ItemStack itemStack = itemPlacementContext.getItemStack();
				BlockState blockState2 = world.getBlockState(blockPos);
				Block block = blockState2.getBlock();
				if (block == blockState.getBlock()) {
					this.method_16014(blockPos, world, playerEntity, itemStack, blockState2);
					block.onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
					if (playerEntity instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)playerEntity, blockPos, itemStack);
					}
				}

				BlockSoundGroup blockSoundGroup = block.getSoundGroup();
				world.playSound(
					playerEntity,
					blockPos,
					blockSoundGroup.method_4194(),
					SoundCategory.BLOCKS,
					(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
					blockSoundGroup.getPitch() * 0.8F
				);
				itemStack.decrement(1);
				return ActionResult.SUCCESS;
			}
		}
	}

	protected boolean method_16014(BlockPos blockPos, World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState) {
		return setBlockEntityNbt(world, playerEntity, blockPos, itemStack);
	}

	@Nullable
	protected BlockState method_16016(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getBlock().getPlacementState(itemPlacementContext);
		return blockState != null && this.method_16017(itemPlacementContext, blockState) ? blockState : null;
	}

	protected boolean method_16017(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())
			&& itemPlacementContext.getWorld().method_16371(blockState, itemPlacementContext.getBlockPos());
	}

	protected boolean method_16013(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos(), blockState, 11);
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
					if (!world.isClient && blockEntity.shouldNotCopyNbtFromItem() && (player == null || !player.method_15936())) {
						return false;
					}

					NbtCompound nbtCompound2 = blockEntity.toNbt(new NbtCompound());
					NbtCompound nbtCompound3 = nbtCompound2.copy();
					nbtCompound2.putAll(nbtCompound);
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

	@Override
	public String getTranslationKey() {
		return this.getBlock().getTranslationKey();
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			this.getBlock().addStacksForDisplay(group, stacks);
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		super.appendTooltips(stack, world, tooltip, tooltipContext);
		this.getBlock().method_16564(stack, world, tooltip, tooltipContext);
	}

	public Block getBlock() {
		return this.block;
	}

	public void method_16015(Map<Block, Item> map, Item item) {
		map.put(this.getBlock(), item);
	}
}
