package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BowItem extends Item {
	public BowItem() {
		this.maxCount = 1;
		this.setMaxDamage(384);
		this.setItemGroup(ItemGroup.COMBAT);
		this.addProperty(new Identifier("pull"), new ItemPropertyGetter() {
			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				if (entity == null) {
					return 0.0F;
				} else {
					return entity.method_13064().getItem() != Items.BOW ? 0.0F : (float)(stack.getMaxUseTime() - entity.method_13065()) / 20.0F;
				}
			}
		});
		this.addProperty(new Identifier("pulling"), new ItemPropertyGetter() {
			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				return entity != null && entity.method_13061() && entity.method_13064() == stack ? 1.0F : 0.0F;
			}
		});
	}

	private ItemStack method_11362(PlayerEntity playerEntity) {
		if (this.method_11364(playerEntity.getStackInHand(Hand.OFF_HAND))) {
			return playerEntity.getStackInHand(Hand.OFF_HAND);
		} else if (this.method_11364(playerEntity.getStackInHand(Hand.MAIN_HAND))) {
			return playerEntity.getStackInHand(Hand.MAIN_HAND);
		} else {
			for (int i = 0; i < playerEntity.inventory.getInvSize(); i++) {
				ItemStack itemStack = playerEntity.inventory.getInvStack(i);
				if (this.method_11364(itemStack)) {
					return itemStack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected boolean method_11364(ItemStack itemStack) {
		return itemStack.getItem() instanceof ArrowItem;
	}

	@Override
	public void method_3359(ItemStack stack, World world, LivingEntity entity, int i) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			boolean bl = playerEntity.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
			ItemStack itemStack = this.method_11362(playerEntity);
			if (!itemStack.isEmpty() || bl) {
				if (itemStack.isEmpty()) {
					itemStack = new ItemStack(Items.ARROW);
				}

				int j = this.getMaxUseTime(stack) - i;
				float f = method_11363(j);
				if (!((double)f < 0.1)) {
					boolean bl2 = bl && itemStack.getItem() == Items.ARROW;
					if (!world.isClient) {
						ArrowItem arrowItem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
						AbstractArrowEntity abstractArrowEntity = arrowItem.method_11358(world, itemStack, playerEntity);
						abstractArrowEntity.method_13278(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, f * 3.0F, 1.0F);
						if (f == 1.0F) {
							abstractArrowEntity.setCritical(true);
						}

						int k = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
						if (k > 0) {
							abstractArrowEntity.setDamage(abstractArrowEntity.getDamage() + (double)k * 0.5 + 0.5);
						}

						int l = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
						if (l > 0) {
							abstractArrowEntity.setPunch(l);
						}

						if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
							abstractArrowEntity.setOnFireFor(100);
						}

						stack.damage(1, playerEntity);
						if (bl2 || playerEntity.abilities.creativeMode && (itemStack.getItem() == Items.SPECTRAL_ARROW || itemStack.getItem() == Items.TIPPED_ARROW)) {
							abstractArrowEntity.pickupType = AbstractArrowEntity.PickupPermission.CREATIVE_ONLY;
						}

						world.spawnEntity(abstractArrowEntity);
					}

					world.playSound(
						null,
						playerEntity.x,
						playerEntity.y,
						playerEntity.z,
						Sounds.ENTITY_ARROW_SHOOT,
						SoundCategory.PLAYERS,
						1.0F,
						1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + f * 0.5F
					);
					if (!bl2 && !playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							playerEntity.inventory.method_13257(itemStack);
						}
					}

					playerEntity.incrementStat(Stats.used(this));
				}
			}
		}
	}

	public static float method_11363(int i) {
		float f = (float)i / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		boolean bl = !this.method_11362(player).isEmpty();
		if (player.abilities.creativeMode || bl) {
			player.method_13050(hand);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return bl ? new TypedActionResult<>(ActionResult.PASS, itemStack) : new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
