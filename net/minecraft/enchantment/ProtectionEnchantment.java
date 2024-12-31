package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
	public final ProtectionEnchantment.ProtectionType protectionType;

	public ProtectionEnchantment(Enchantment.Rarity rarity, ProtectionEnchantment.ProtectionType protectionType, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.ALL_ARMOR, equipmentSlots);
		this.protectionType = protectionType;
		if (protectionType == ProtectionEnchantment.ProtectionType.FALL) {
			this.target = EnchantmentTarget.FEET;
		}
	}

	@Override
	public int getMinimumPower(int level) {
		return this.protectionType.method_11468() + (level - 1) * this.protectionType.method_11469();
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + this.protectionType.method_11469();
	}

	@Override
	public int getMaximumLevel() {
		return 4;
	}

	@Override
	public int getProtectionAmount(int level, DamageSource source) {
		if (source.isOutOfWorld()) {
			return 0;
		} else if (this.protectionType == ProtectionEnchantment.ProtectionType.ALL) {
			return level;
		} else if (this.protectionType == ProtectionEnchantment.ProtectionType.FIRE && source.isFire()) {
			return level * 2;
		} else if (this.protectionType == ProtectionEnchantment.ProtectionType.FALL && source == DamageSource.FALL) {
			return level * 3;
		} else if (this.protectionType == ProtectionEnchantment.ProtectionType.EXPLOSION && source.isExplosive()) {
			return level * 2;
		} else {
			return this.protectionType == ProtectionEnchantment.ProtectionType.PROJECTILE && source.isProjectile() ? level * 2 : 0;
		}
	}

	@Override
	public String getTranslationKey() {
		return "enchantment.protect." + this.protectionType.method_11467();
	}

	@Override
	public boolean differs(Enchantment other) {
		if (other instanceof ProtectionEnchantment) {
			ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)other;
			return this.protectionType == protectionEnchantment.protectionType
				? false
				: this.protectionType == ProtectionEnchantment.ProtectionType.FALL || protectionEnchantment.protectionType == ProtectionEnchantment.ProtectionType.FALL;
		} else {
			return super.differs(other);
		}
	}

	public static int method_11466(LivingEntity livingEntity, int i) {
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, livingEntity);
		if (j > 0) {
			i -= MathHelper.floor((float)i * (float)j * 0.15F);
		}

		return i;
	}

	public static double method_11465(LivingEntity livingEntity, double d) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
		if (i > 0) {
			d -= (double)MathHelper.floor(d * (double)((float)i * 0.15F));
		}

		return d;
	}

	public static enum ProtectionType {
		ALL("all", 1, 11, 20),
		FIRE("fire", 10, 8, 12),
		FALL("fall", 5, 6, 10),
		EXPLOSION("explosion", 5, 8, 12),
		PROJECTILE("projectile", 3, 6, 15);

		private final String field_12429;
		private final int field_12430;
		private final int field_12431;
		private final int field_12432;

		private ProtectionType(String string2, int j, int k, int l) {
			this.field_12429 = string2;
			this.field_12430 = j;
			this.field_12431 = k;
			this.field_12432 = l;
		}

		public String method_11467() {
			return this.field_12429;
		}

		public int method_11468() {
			return this.field_12430;
		}

		public int method_11469() {
			return this.field_12431;
		}
	}
}
