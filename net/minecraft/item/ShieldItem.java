package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.TooltipContext;
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
		DispenserBlock.SPECIAL_ITEMS.put(this, ArmorItem.ARMOR_DISPENSER_BEHAVIOR);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		if (stack.getNbtCompound("BlockEntityTag") != null) {
			DyeColor dyeColor = BannerBlockEntity.method_13721(stack);
			return CommonI18n.translate("item.shield." + dyeColor.getTranslationKey() + ".name");
		} else {
			return CommonI18n.translate("item.shield.name");
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		BannerItem.method_11359(stack, tooltip);
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
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		player.method_13050(hand);
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Item.fromBlock(Blocks.PLANKS) ? true : super.canRepair(stack, ingredient);
	}
}
