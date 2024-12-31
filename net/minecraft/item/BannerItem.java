package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BannerItem extends BlockItem {
	public BannerItem() {
		super(Blocks.STANDING_BANNER);
		this.maxCount = 16;
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setUnbreakable(true);
		this.setMaxDamage(0);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = blockState.getBlock().method_8638(world, blockPos);
		if (direction != Direction.DOWN && (blockState.getMaterial().isSolid() || bl) && (!bl || direction == Direction.UP)) {
			blockPos = blockPos.offset(direction);
			if (!playerEntity.canModify(blockPos, direction, itemStack) || !Blocks.STANDING_BANNER.canBePlacedAtPos(world, blockPos)) {
				return ActionResult.FAIL;
			} else if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				blockPos = bl ? blockPos.down() : blockPos;
				if (direction == Direction.UP) {
					int i = MathHelper.floor((double)((playerEntity.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
					world.setBlockState(blockPos, Blocks.STANDING_BANNER.getDefaultState().with(StandingSignBlock.ROTATION, i), 3);
				} else {
					world.setBlockState(blockPos, Blocks.WALL_BANNER.getDefaultState().with(WallSignBlock.FACING, direction), 3);
				}

				itemStack.count--;
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof BannerBlockEntity) {
					((BannerBlockEntity)blockEntity).fromItemStack(itemStack);
				}

				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		String string = "item.banner.";
		DyeColor dyeColor = getDyeColor(stack);
		string = string + dyeColor.getTranslationKey() + ".name";
		return CommonI18n.translate(string);
	}

	public static void method_11359(ItemStack itemStack, List<String> list) {
		NbtCompound nbtCompound = itemStack.getSubNbt("BlockEntityTag", false);
		if (nbtCompound != null && nbtCompound.contains("Patterns")) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);

			for (int i = 0; i < nbtList.size() && i < 6; i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				DyeColor dyeColor = DyeColor.getById(nbtCompound2.getInt("Color"));
				BannerBlockEntity.BannerPattern bannerPattern = BannerBlockEntity.BannerPattern.getById(nbtCompound2.getString("Pattern"));
				if (bannerPattern != null) {
					list.add(CommonI18n.translate("item.banner." + bannerPattern.getName() + "." + dyeColor.getTranslationKey()));
				}
			}
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		method_11359(stack, lines);
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		for (DyeColor dyeColor : DyeColor.values()) {
			NbtCompound nbtCompound = new NbtCompound();
			BannerBlockEntity.toNbt(nbtCompound, dyeColor.getSwappedId(), null);
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("BlockEntityTag", nbtCompound);
			ItemStack itemStack = new ItemStack(item, 1, dyeColor.getSwappedId());
			itemStack.setNbt(nbtCompound2);
			list.add(itemStack);
		}
	}

	@Override
	public ItemGroup getItemGroup() {
		return ItemGroup.DECORATIONS;
	}

	public static DyeColor getDyeColor(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getSubNbt("BlockEntityTag", false);
		DyeColor dyeColor = null;
		if (nbtCompound != null && nbtCompound.contains("Base")) {
			dyeColor = DyeColor.getById(nbtCompound.getInt("Base"));
		} else {
			dyeColor = DyeColor.getById(itemStack.getData());
		}

		return dyeColor;
	}
}
