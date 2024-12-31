package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
	private static final String[] TYPES = new String[]{"all", "fire", "fall", "explosion", "projectile"};
	private static final int[] MINIMUM_ENCHANTABILITY = new int[]{1, 10, 5, 5, 3};
	private static final int[] field_4500 = new int[]{11, 8, 6, 8, 6};
	private static final int[] MAXIMUM_ENCHANTABILITY = new int[]{20, 12, 10, 12, 15};
	public final int protectionTypeId;

	public ProtectionEnchantment(int i, Identifier identifier, int j, int k) {
		super(i, identifier, j, EnchantmentTarget.ALL_ARMOR);
		this.protectionTypeId = k;
		if (k == 2) {
			this.target = EnchantmentTarget.FEET;
		}
	}

	@Override
	public int getMinimumPower(int level) {
		return MINIMUM_ENCHANTABILITY[this.protectionTypeId] + (level - 1) * field_4500[this.protectionTypeId];
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + MAXIMUM_ENCHANTABILITY[this.protectionTypeId];
	}

	@Override
	public int getMaximumLevel() {
		return 4;
	}

	@Override
	public int getProtectionAmount(int level, DamageSource source) {
		if (source.isOutOfWorld()) {
			return 0;
		} else {
			float f = (float)(6 + level * level) / 3.0F;
			if (this.protectionTypeId == 0) {
				return MathHelper.floor(f * 0.75F);
			} else if (this.protectionTypeId == 1 && source.isFire()) {
				return MathHelper.floor(f * 1.25F);
			} else if (this.protectionTypeId == 2 && source == DamageSource.FALL) {
				return MathHelper.floor(f * 2.5F);
			} else if (this.protectionTypeId == 3 && source.isExplosive()) {
				return MathHelper.floor(f * 1.5F);
			} else {
				return this.protectionTypeId == 4 && source.isProjectile() ? MathHelper.floor(f * 1.5F) : 0;
			}
		}
	}

	@Override
	public String getTranslationKey() {
		return "enchantment.protect." + TYPES[this.protectionTypeId];
	}

	@Override
	public boolean differs(Enchantment other) {
		if (other instanceof ProtectionEnchantment) {
			ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)other;
			return protectionEnchantment.protectionTypeId == this.protectionTypeId ? false : this.protectionTypeId == 2 || protectionEnchantment.protectionTypeId == 2;
		} else {
			return super.differs(other);
		}
	}

	public static int method_4659(Entity entity, int i) {
		int j = EnchantmentHelper.getLevel(Enchantment.FIRE_PROTECTION.id, entity.getArmorStacks());
		if (j > 0) {
			i -= MathHelper.floor((float)i * (float)j * 0.15F);
		}

		return i;
	}

	public static double method_4658(Entity entity, double d) {
		int i = EnchantmentHelper.getLevel(Enchantment.BLAST_PROTECTION.id, entity.getArmorStacks());
		if (i > 0) {
			d -= (double)MathHelper.floor(d * (double)((float)i * 0.15F));
		}

		return d;
	}
}
