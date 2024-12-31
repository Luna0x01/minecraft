package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TridentItem extends Item implements Vanishable {
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	public TridentItem(Item.Settings settings) {
		super(settings);
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 8.0, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED,
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9F, EntityAttributeModifier.Operation.ADDITION)
		);
		this.attributeModifiers = builder.build();
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
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
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 000: aload 3
		// 001: instanceof net/minecraft/entity/player/PlayerEntity
		// 004: ifne 008
		// 007: return
		// 008: aload 3
		// 009: checkcast net/minecraft/entity/player/PlayerEntity
		// 00c: astore 5
		// 00e: aload 0
		// 00f: aload 1
		// 010: invokevirtual net/minecraft/item/TridentItem.getMaxUseTime (Lnet/minecraft/item/ItemStack;)I
		// 013: iload 4
		// 015: isub
		// 016: istore 6
		// 018: iload 6
		// 01a: bipush 10
		// 01c: if_icmpge 020
		// 01f: return
		// 020: aload 1
		// 021: invokestatic net/minecraft/enchantment/EnchantmentHelper.getRiptide (Lnet/minecraft/item/ItemStack;)I
		// 024: istore 7
		// 026: iload 7
		// 028: ifle 034
		// 02b: aload 5
		// 02d: invokevirtual net/minecraft/entity/player/PlayerEntity.isTouchingWaterOrRain ()Z
		// 030: ifne 034
		// 033: return
		// 034: aload 2
		// 035: getfield net/minecraft/world/World.isClient Z
		// 038: ifne 0b3
		// 03b: aload 1
		// 03c: bipush 1
		// 03d: aload 5
		// 03f: aload 3
		// 040: invokedynamic accept (Lnet/minecraft/entity/LivingEntity;)Ljava/util/function/Consumer; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)V, net/minecraft/item/TridentItem.method_20285 (Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/player/PlayerEntity;)V, (Lnet/minecraft/entity/player/PlayerEntity;)V ]
		// 045: invokevirtual net/minecraft/item/ItemStack.damage (ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V
		// 048: iload 7
		// 04a: ifne 0b3
		// 04d: new net/minecraft/entity/projectile/TridentEntity
		// 050: dup
		// 051: aload 2
		// 052: aload 5
		// 054: aload 1
		// 055: invokespecial net/minecraft/entity/projectile/TridentEntity.<init> (Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V
		// 058: astore 8
		// 05a: aload 8
		// 05c: aload 5
		// 05e: aload 5
		// 060: getfield net/minecraft/entity/player/PlayerEntity.pitch F
		// 063: aload 5
		// 065: getfield net/minecraft/entity/player/PlayerEntity.yaw F
		// 068: fconst_0
		// 069: ldc 2.5
		// 06b: iload 7
		// 06d: i2f
		// 06e: ldc 0.5
		// 070: fmul
		// 071: fadd
		// 072: fconst_1
		// 073: invokevirtual net/minecraft/entity/projectile/TridentEntity.setProperties (Lnet/minecraft/entity/Entity;FFFFF)V
		// 076: aload 5
		// 078: getfield net/minecraft/entity/player/PlayerEntity.abilities Lnet/minecraft/entity/player/PlayerAbilities;
		// 07b: getfield net/minecraft/entity/player/PlayerAbilities.creativeMode Z
		// 07e: ifeq 089
		// 081: aload 8
		// 083: getstatic net/minecraft/entity/projectile/PersistentProjectileEntity$PickupPermission.CREATIVE_ONLY Lnet/minecraft/entity/projectile/PersistentProjectileEntity$PickupPermission;
		// 086: putfield net/minecraft/entity/projectile/TridentEntity.pickupType Lnet/minecraft/entity/projectile/PersistentProjectileEntity$PickupPermission;
		// 089: aload 2
		// 08a: aload 8
		// 08c: invokevirtual net/minecraft/world/World.spawnEntity (Lnet/minecraft/entity/Entity;)Z
		// 08f: pop
		// 090: aload 2
		// 091: aconst_null
		// 092: aload 8
		// 094: getstatic net/minecraft/sound/SoundEvents.ITEM_TRIDENT_THROW Lnet/minecraft/sound/SoundEvent;
		// 097: getstatic net/minecraft/sound/SoundCategory.PLAYERS Lnet/minecraft/sound/SoundCategory;
		// 09a: fconst_1
		// 09b: fconst_1
		// 09c: invokevirtual net/minecraft/world/World.playSoundFromEntity (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V
		// 09f: aload 5
		// 0a1: getfield net/minecraft/entity/player/PlayerEntity.abilities Lnet/minecraft/entity/player/PlayerAbilities;
		// 0a4: getfield net/minecraft/entity/player/PlayerAbilities.creativeMode Z
		// 0a7: ifne 0b3
		// 0aa: aload 5
		// 0ac: getfield net/minecraft/entity/player/PlayerEntity.inventory Lnet/minecraft/entity/player/PlayerInventory;
		// 0af: aload 1
		// 0b0: invokevirtual net/minecraft/entity/player/PlayerInventory.removeOne (Lnet/minecraft/item/ItemStack;)V
		// 0b3: aload 5
		// 0b5: getstatic net/minecraft/stat/Stats.USED Lnet/minecraft/stat/StatType;
		// 0b8: aload 0
		// 0b9: invokevirtual net/minecraft/stat/StatType.getOrCreateStat (Ljava/lang/Object;)Lnet/minecraft/stat/Stat;
		// 0bc: invokevirtual net/minecraft/entity/player/PlayerEntity.incrementStat (Lnet/minecraft/stat/Stat;)V
		// 0bf: iload 7
		// 0c1: ifle 1ac
		// 0c4: aload 5
		// 0c6: getfield net/minecraft/entity/player/PlayerEntity.yaw F
		// 0c9: fstore 8
		// 0cb: aload 5
		// 0cd: getfield net/minecraft/entity/player/PlayerEntity.pitch F
		// 0d0: fstore 9
		// 0d2: fload 8
		// 0d4: ldc 0.017453292
		// 0d6: fmul
		// 0d7: invokestatic net/minecraft/util/math/MathHelper.sin (F)F
		// 0da: fneg
		// 0db: fload 9
		// 0dd: ldc 0.017453292
		// 0df: fmul
		// 0e0: invokestatic net/minecraft/util/math/MathHelper.cos (F)F
		// 0e3: fmul
		// 0e4: fstore 10
		// 0e6: fload 9
		// 0e8: ldc 0.017453292
		// 0ea: fmul
		// 0eb: invokestatic net/minecraft/util/math/MathHelper.sin (F)F
		// 0ee: fneg
		// 0ef: fstore 11
		// 0f1: fload 8
		// 0f3: ldc 0.017453292
		// 0f5: fmul
		// 0f6: invokestatic net/minecraft/util/math/MathHelper.cos (F)F
		// 0f9: fload 9
		// 0fb: ldc 0.017453292
		// 0fd: fmul
		// 0fe: invokestatic net/minecraft/util/math/MathHelper.cos (F)F
		// 101: fmul
		// 102: fstore 12
		// 104: fload 10
		// 106: fload 10
		// 108: fmul
		// 109: fload 11
		// 10b: fload 11
		// 10d: fmul
		// 10e: fadd
		// 10f: fload 12
		// 111: fload 12
		// 113: fmul
		// 114: fadd
		// 115: invokestatic net/minecraft/util/math/MathHelper.sqrt (F)F
		// 118: fstore 13
		// 11a: ldc_w 3.0
		// 11d: fconst_1
		// 11e: iload 7
		// 120: i2f
		// 121: fadd
		// 122: ldc_w 4.0
		// 125: fdiv
		// 126: fmul
		// 127: fstore 14
		// 129: fload 10
		// 12b: fload 14
		// 12d: fload 13
		// 12f: fdiv
		// 130: fmul
		// 131: fstore 10
		// 133: fload 11
		// 135: fload 14
		// 137: fload 13
		// 139: fdiv
		// 13a: fmul
		// 13b: fstore 11
		// 13d: fload 12
		// 13f: fload 14
		// 141: fload 13
		// 143: fdiv
		// 144: fmul
		// 145: fstore 12
		// 147: aload 5
		// 149: fload 10
		// 14b: f2d
		// 14c: fload 11
		// 14e: f2d
		// 14f: fload 12
		// 151: f2d
		// 152: invokevirtual net/minecraft/entity/player/PlayerEntity.addVelocity (DDD)V
		// 155: aload 5
		// 157: bipush 20
		// 159: invokevirtual net/minecraft/entity/player/PlayerEntity.setRiptideTicks (I)V
		// 15c: aload 5
		// 15e: invokevirtual net/minecraft/entity/player/PlayerEntity.isOnGround ()Z
		// 161: ifeq 17d
		// 164: ldc_w 1.1999999
		// 167: fstore 15
		// 169: aload 5
		// 16b: getstatic net/minecraft/entity/MovementType.SELF Lnet/minecraft/entity/MovementType;
		// 16e: new net/minecraft/util/math/Vec3d
		// 171: dup
		// 172: dconst_0
		// 173: ldc2_w 1.1999999284744263
		// 176: dconst_0
		// 177: invokespecial net/minecraft/util/math/Vec3d.<init> (DDD)V
		// 17a: invokevirtual net/minecraft/entity/player/PlayerEntity.move (Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V
		// 17d: iload 7
		// 17f: bipush 3
		// 180: if_icmplt 18b
		// 183: getstatic net/minecraft/sound/SoundEvents.ITEM_TRIDENT_RIPTIDE_3 Lnet/minecraft/sound/SoundEvent;
		// 186: astore 15
		// 188: goto 19e
		// 18b: iload 7
		// 18d: bipush 2
		// 18e: if_icmpne 199
		// 191: getstatic net/minecraft/sound/SoundEvents.ITEM_TRIDENT_RIPTIDE_2 Lnet/minecraft/sound/SoundEvent;
		// 194: astore 15
		// 196: goto 19e
		// 199: getstatic net/minecraft/sound/SoundEvents.ITEM_TRIDENT_RIPTIDE_1 Lnet/minecraft/sound/SoundEvent;
		// 19c: astore 15
		// 19e: aload 2
		// 19f: aconst_null
		// 1a0: aload 5
		// 1a2: aload 15
		// 1a4: getstatic net/minecraft/sound/SoundCategory.PLAYERS Lnet/minecraft/sound/SoundCategory;
		// 1a7: fconst_1
		// 1a8: fconst_1
		// 1a9: invokevirtual net/minecraft/world/World.playSoundFromEntity (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V
		// 1ac: return
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
			return TypedActionResult.fail(itemStack);
		} else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isTouchingWaterOrRain()) {
			return TypedActionResult.fail(itemStack);
		} else {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		}
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if ((double)state.getHardness(world, pos) != 0.0) {
			stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return true;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
