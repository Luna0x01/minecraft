package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BannerPattern;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		boolean bl = blockState.getBlock().method_8638(world, pos);
		if (direction != Direction.DOWN && (blockState.getMaterial().isSolid() || bl) && (!bl || direction == Direction.UP)) {
			pos = pos.offset(direction);
			ItemStack itemStack = player.getStackInHand(hand);
			if (!player.canModify(pos, direction, itemStack) || !Blocks.STANDING_BANNER.canBePlacedAtPos(world, pos)) {
				return ActionResult.FAIL;
			} else if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				pos = bl ? pos.down() : pos;
				if (direction == Direction.UP) {
					int i = MathHelper.floor((double)((player.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
					world.setBlockState(pos, Blocks.STANDING_BANNER.getDefaultState().with(StandingSignBlock.ROTATION, i), 3);
				} else {
					world.setBlockState(pos, Blocks.WALL_BANNER.getDefaultState().with(WallSignBlock.FACING, direction), 3);
				}

				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof BannerBlockEntity) {
					((BannerBlockEntity)blockEntity).method_13720(itemStack, false);
				}

				if (player instanceof ServerPlayerEntity) {
					AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)player, pos, itemStack);
				}

				itemStack.decrement(1);
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
		NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns")) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);

			for (int i = 0; i < nbtList.size() && i < 6; i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				DyeColor dyeColor = DyeColor.getById(nbtCompound2.getInt("Color"));
				BannerPattern bannerPattern = BannerPattern.getById(nbtCompound2.getString("Pattern"));
				if (bannerPattern != null) {
					list.add(CommonI18n.translate("item.banner." + bannerPattern.getName() + "." + dyeColor.getTranslationKey()));
				}
			}
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		method_11359(stack, tooltip);
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			for (DyeColor dyeColor : DyeColor.values()) {
				stacks.add(method_13645(dyeColor, null));
			}
		}
	}

	public static ItemStack method_13645(DyeColor dyeColor, @Nullable NbtList nbtList) {
		ItemStack itemStack = new ItemStack(Items.BANNER, 1, dyeColor.getSwappedId());
		if (nbtList != null && !nbtList.isEmpty()) {
			itemStack.getOrCreateNbtCompound("BlockEntityTag").put("Patterns", nbtList.copy());
		}

		return itemStack;
	}

	@Override
	public ItemGroup getItemGroup() {
		return ItemGroup.DECORATIONS;
	}

	public static DyeColor getDyeColor(ItemStack itemStack) {
		return DyeColor.getById(itemStack.getData() & 15);
	}
}
