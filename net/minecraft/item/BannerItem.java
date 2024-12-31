package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction == Direction.DOWN) {
			return false;
		} else if (!world.getBlockState(pos).getBlock().getMaterial().isSolid()) {
			return false;
		} else {
			pos = pos.offset(direction);
			if (!player.canModify(pos, direction, itemStack)) {
				return false;
			} else if (!Blocks.STANDING_BANNER.canBePlacedAtPos(world, pos)) {
				return false;
			} else if (world.isClient) {
				return true;
			} else {
				if (direction == Direction.UP) {
					int i = MathHelper.floor((double)((player.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
					world.setBlockState(pos, Blocks.STANDING_BANNER.getDefaultState().with(StandingSignBlock.ROTATION, i), 3);
				} else {
					world.setBlockState(pos, Blocks.WALL_BANNER.getDefaultState().with(WallSignBlock.FACING, direction), 3);
				}

				itemStack.count--;
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof BannerBlockEntity) {
					((BannerBlockEntity)blockEntity).fromItemStack(itemStack);
				}

				return true;
			}
		}
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		String string = "item.banner.";
		DyeColor dyeColor = this.getDyeColor(stack);
		string = string + dyeColor.getTranslationKey() + ".name";
		return CommonI18n.translate(string);
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag", false);
		if (nbtCompound != null && nbtCompound.contains("Patterns")) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);

			for (int i = 0; i < nbtList.size() && i < 6; i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				DyeColor dyeColor = DyeColor.getById(nbtCompound2.getInt("Color"));
				BannerBlockEntity.BannerPattern bannerPattern = BannerBlockEntity.BannerPattern.getById(nbtCompound2.getString("Pattern"));
				if (bannerPattern != null) {
					lines.add(CommonI18n.translate("item.banner." + bannerPattern.getName() + "." + dyeColor.getTranslationKey()));
				}
			}
		}
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		if (color == 0) {
			return 16777215;
		} else {
			DyeColor dyeColor = this.getDyeColor(stack);
			return dyeColor.getMaterialColor().color;
		}
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

	private DyeColor getDyeColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag", false);
		DyeColor dyeColor = null;
		if (nbtCompound != null && nbtCompound.contains("Base")) {
			dyeColor = DyeColor.getById(nbtCompound.getInt("Base"));
		} else {
			dyeColor = DyeColor.getById(stack.getData());
		}

		return dyeColor;
	}
}
