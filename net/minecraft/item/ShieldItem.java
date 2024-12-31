package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShieldItem extends Item {
	public ShieldItem() {
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.COMBAT);
		this.setMaxDamage(336);
		this.addProperty(new Identifier("blocking"), new ItemPropertyGetter() {
			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				return entity != null && entity.method_13061() && entity.method_13064() == stack ? 1.0F : 0.0F;
			}
		});
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		return super.method_3355(itemStack, playerEntity, world, blockPos, hand, direction, f, g, h);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		if (stack.getSubNbt("BlockEntityTag", false) != null) {
			String string = "item.shield.";
			DyeColor dyeColor = BannerItem.getDyeColor(stack);
			string = string + dyeColor.getTranslationKey() + ".name";
			return CommonI18n.translate(string);
		} else {
			return CommonI18n.translate("item.shield.name");
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		BannerItem.method_11359(stack, lines);
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		ItemStack itemStack = new ItemStack(item, 1, 0);
		list.add(itemStack);
	}

	@Override
	public ItemGroup getItemGroup() {
		return ItemGroup.COMBAT;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.method_13050(hand);
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Item.fromBlock(Blocks.PLANKS) ? true : super.canRepair(stack, ingredient);
	}
}
