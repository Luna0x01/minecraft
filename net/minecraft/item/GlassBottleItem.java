package net.minecraft.item;

import java.util.List;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
	public GlassBottleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		List<AreaEffectCloudEntity> list = world.method_16325(
			AreaEffectCloudEntity.class,
			player.getBoundingBox().expand(2.0),
			areaEffectCloudEntity -> areaEffectCloudEntity != null
					&& areaEffectCloudEntity.isAlive()
					&& areaEffectCloudEntity.method_12965() instanceof EnderDragonEntity
		);
		ItemStack itemStack = player.getStackInHand(hand);
		if (!list.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = (AreaEffectCloudEntity)list.get(0);
			areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5F);
			world.playSound(null, player.x, player.y, player.z, Sounds.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			return new TypedActionResult<>(ActionResult.SUCCESS, this.method_11360(itemStack, player, new ItemStack(Items.DRAGON_BREATH)));
		} else {
			BlockHitResult blockHitResult = this.onHit(world, player, true);
			if (blockHitResult == null) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else {
				if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = blockHitResult.getBlockPos();
					if (!world.canPlayerModifyAt(player, blockPos)) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}

					if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
						world.playSound(player, player.x, player.y, player.z, Sounds.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
						return new TypedActionResult<>(
							ActionResult.SUCCESS, this.method_11360(itemStack, player, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER))
						);
					}
				}

				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}

	protected ItemStack method_11360(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
		itemStack.decrement(1);
		playerEntity.method_15932(Stats.USED.method_21429(this));
		if (itemStack.isEmpty()) {
			return itemStack2;
		} else {
			if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			return itemStack;
		}
	}
}
