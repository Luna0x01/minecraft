package net.minecraft.item;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
	public GlassBottleItem() {
		this.setItemGroup(ItemGroup.BREWING);
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		List<AreaEffectCloudEntity> list = world.getEntitiesInBox(
			AreaEffectCloudEntity.class, playerEntity.getBoundingBox().expand(2.0), new Predicate<AreaEffectCloudEntity>() {
				public boolean apply(@Nullable AreaEffectCloudEntity areaEffectCloudEntity) {
					return areaEffectCloudEntity != null && areaEffectCloudEntity.isAlive() && areaEffectCloudEntity.method_12965() instanceof EnderDragonEntity;
				}
			}
		);
		if (!list.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = (AreaEffectCloudEntity)list.get(0);
			areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5F);
			world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			return new TypedActionResult<>(ActionResult.SUCCESS, this.method_11360(itemStack, playerEntity, new ItemStack(Items.DRAGON_BREATH)));
		} else {
			BlockHitResult blockHitResult = this.onHit(world, playerEntity, true);
			if (blockHitResult == null) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else {
				if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = blockHitResult.getBlockPos();
					if (!world.canPlayerModifyAt(playerEntity, blockPos)
						|| !playerEntity.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, itemStack)) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}

					if (world.getBlockState(blockPos).getMaterial() == Material.WATER) {
						world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
						return new TypedActionResult<>(ActionResult.SUCCESS, this.method_11360(itemStack, playerEntity, new ItemStack(Items.POTION)));
					}
				}

				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}

	protected ItemStack method_11360(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
		itemStack.count--;
		playerEntity.incrementStat(Stats.used(this));
		if (itemStack.count <= 0) {
			return itemStack2;
		} else {
			if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			return itemStack;
		}
	}
}
