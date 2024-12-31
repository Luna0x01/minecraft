package net.minecraft;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class class_3564 extends Item {
	public class_3564(Item.Settings settings) {
		super(settings);
		this.addProperty(
			new Identifier("throwing"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.method_13061() && livingEntity.method_13064() == itemStack ? 1.0F : 0.0F
		);
	}

	@Override
	public boolean beforeBlockBreak(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		return !player.isCreative();
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return false;
	}

	@Override
	public void method_3359(ItemStack stack, World world, LivingEntity entity, int i) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			int j = this.getMaxUseTime(stack) - i;
			if (j >= 10) {
				int k = EnchantmentHelper.method_16267(stack);
				if (k <= 0 || playerEntity.tickFire()) {
					if (!world.isClient) {
						stack.damage(1, playerEntity);
						if (k == 0) {
							TridentEntity tridentEntity = new TridentEntity(world, playerEntity, stack);
							tridentEntity.method_13278(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 2.5F + (float)k * 0.5F, 1.0F);
							if (playerEntity.abilities.creativeMode) {
								tridentEntity.pickupType = AbstractArrowEntity.PickupPermission.CREATIVE_ONLY;
							}

							world.method_3686(tridentEntity);
							if (!playerEntity.abilities.creativeMode) {
								playerEntity.inventory.method_13257(stack);
							}
						}
					}

					playerEntity.method_15932(Stats.USED.method_21429(this));
					Sound sound = Sounds.ITEM_TRIDENT_THROW;
					if (k > 0) {
						float f = playerEntity.yaw;
						float g = playerEntity.pitch;
						float h = -MathHelper.sin(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
						float l = -MathHelper.sin(g * (float) (Math.PI / 180.0));
						float m = MathHelper.cos(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
						float n = MathHelper.sqrt(h * h + l * l + m * m);
						float o = 3.0F * ((1.0F + (float)k) / 4.0F);
						h *= o / n;
						l *= o / n;
						m *= o / n;
						playerEntity.addVelocity((double)h, (double)l, (double)m);
						if (k >= 3) {
							sound = Sounds.ITEM_TRIDENT_RIPTIDE_3;
						} else if (k == 2) {
							sound = Sounds.ITEM_TRIDENT_RIPTIDE_2;
						} else {
							sound = Sounds.ITEM_TRIDENT_RIPTIDE_1;
						}

						playerEntity.method_15650(20);
						if (playerEntity.onGround) {
							float p = 1.1999999F;
							playerEntity.move(MovementType.SELF, 0.0, 1.1999999F, 0.0);
						}
					}

					world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		} else if (EnchantmentHelper.method_16267(itemStack) > 0 && !player.tickFire()) {
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		} else {
			player.method_13050(hand);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		}
	}

	@Override
	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		stack.damage(1, entity2);
		return true;
	}

	@Override
	public boolean method_3356(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if ((double)blockState.getHardness(world, blockPos) != 0.0) {
			itemStack.damage(2, livingEntity);
		}

		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.method_6326(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			multimap.put(EntityAttributes.GENERIC_ATTACK_DAMAGE.getId(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", 8.0, 0));
			multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED.getId(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.9F, 0));
		}

		return multimap;
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
