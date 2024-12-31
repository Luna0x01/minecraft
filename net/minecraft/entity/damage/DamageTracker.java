package net.minecraft.entity.damage;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class DamageTracker {
	private final List<DamageRecord> recentDamage = Lists.newArrayList();
	private final LivingEntity entity;
	private int ageOnLastDamage;
	private int ageOnLastAttacked;
	private int ageOnLastUpdate;
	private boolean recentlyAttacked;
	private boolean hasDamage;
	private String fallDeathSuffix;

	public DamageTracker(LivingEntity livingEntity) {
		this.entity = livingEntity;
	}

	public void setFallDeathSuffix() {
		this.clearFallDeathSuffix();
		if (this.entity.isClimbing()) {
			Block block = this.entity.world.getBlockState(new BlockPos(this.entity.x, this.entity.getBoundingBox().minY, this.entity.z)).getBlock();
			if (block == Blocks.LADDER) {
				this.fallDeathSuffix = "ladder";
			} else if (block == Blocks.VINE) {
				this.fallDeathSuffix = "vines";
			}
		} else if (this.entity.isTouchingWater()) {
			this.fallDeathSuffix = "water";
		}
	}

	public void onDamage(DamageSource damageSource, float originalHealth, float f) {
		this.update();
		this.setFallDeathSuffix();
		DamageRecord damageRecord = new DamageRecord(damageSource, this.entity.ticksAlive, originalHealth, f, this.fallDeathSuffix, this.entity.fallDistance);
		this.recentDamage.add(damageRecord);
		this.ageOnLastDamage = this.entity.ticksAlive;
		this.hasDamage = true;
		if (damageRecord.isAttackerLiving() && !this.recentlyAttacked && this.entity.isAlive()) {
			this.recentlyAttacked = true;
			this.ageOnLastAttacked = this.entity.ticksAlive;
			this.ageOnLastUpdate = this.ageOnLastAttacked;
			this.entity.enterCombat();
		}
	}

	public Text getDeathMessage() {
		if (this.recentDamage.size() == 0) {
			return new TranslatableText("death.attack.generic", this.entity.getName());
		} else {
			DamageRecord damageRecord = this.getBiggestFall();
			DamageRecord damageRecord2 = (DamageRecord)this.recentDamage.get(this.recentDamage.size() - 1);
			Text text = damageRecord2.getAttackerName();
			Entity entity = damageRecord2.getDamageSource().getAttacker();
			Text text3;
			if (damageRecord != null && damageRecord2.getDamageSource() == DamageSource.FALL) {
				Text text2 = damageRecord.getAttackerName();
				if (damageRecord.getDamageSource() == DamageSource.FALL || damageRecord.getDamageSource() == DamageSource.OUT_OF_WORLD) {
					text3 = new TranslatableText("death.fell.accident." + this.getFallDeathSuffix(damageRecord), this.entity.getName());
				} else if (text2 != null && (text == null || !text2.equals(text))) {
					Entity entity2 = damageRecord.getDamageSource().getAttacker();
					ItemStack itemStack = entity2 instanceof LivingEntity ? ((LivingEntity)entity2).getStackInHand() : null;
					if (itemStack != null && itemStack.hasCustomName()) {
						text3 = new TranslatableText("death.fell.assist.item", this.entity.getName(), text2, itemStack.toHoverableText());
					} else {
						text3 = new TranslatableText("death.fell.assist", this.entity.getName(), text2);
					}
				} else if (text != null) {
					ItemStack itemStack2 = entity instanceof LivingEntity ? ((LivingEntity)entity).getStackInHand() : null;
					if (itemStack2 != null && itemStack2.hasCustomName()) {
						text3 = new TranslatableText("death.fell.finish.item", this.entity.getName(), text, itemStack2.toHoverableText());
					} else {
						text3 = new TranslatableText("death.fell.finish", this.entity.getName(), text);
					}
				} else {
					text3 = new TranslatableText("death.fell.killer", this.entity.getName());
				}
			} else {
				text3 = damageRecord2.getDamageSource().getDeathMessage(this.entity);
			}

			return text3;
		}
	}

	public LivingEntity getLastAttacker() {
		LivingEntity livingEntity = null;
		PlayerEntity playerEntity = null;
		float f = 0.0F;
		float g = 0.0F;

		for (DamageRecord damageRecord : this.recentDamage) {
			if (damageRecord.getDamageSource().getAttacker() instanceof PlayerEntity && (playerEntity == null || damageRecord.getDamage() > g)) {
				g = damageRecord.getDamage();
				playerEntity = (PlayerEntity)damageRecord.getDamageSource().getAttacker();
			}

			if (damageRecord.getDamageSource().getAttacker() instanceof LivingEntity && (livingEntity == null || damageRecord.getDamage() > f)) {
				f = damageRecord.getDamage();
				livingEntity = (LivingEntity)damageRecord.getDamageSource().getAttacker();
			}
		}

		return (LivingEntity)(playerEntity != null && g >= f / 3.0F ? playerEntity : livingEntity);
	}

	private DamageRecord getBiggestFall() {
		DamageRecord damageRecord = null;
		DamageRecord damageRecord2 = null;
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < this.recentDamage.size(); j++) {
			DamageRecord damageRecord3 = (DamageRecord)this.recentDamage.get(j);
			DamageRecord damageRecord4 = j > 0 ? (DamageRecord)this.recentDamage.get(j - 1) : null;
			if ((damageRecord3.getDamageSource() == DamageSource.FALL || damageRecord3.getDamageSource() == DamageSource.OUT_OF_WORLD)
				&& damageRecord3.getFallDistance() > 0.0F
				&& (damageRecord == null || damageRecord3.getFallDistance() > f)) {
				if (j > 0) {
					damageRecord = damageRecord4;
				} else {
					damageRecord = damageRecord3;
				}

				f = damageRecord3.getFallDistance();
			}

			if (damageRecord3.getFallDeathSuffix() != null && (damageRecord2 == null || damageRecord3.getDamage() > (float)i)) {
				damageRecord2 = damageRecord3;
			}
		}

		if (f > 5.0F && damageRecord != null) {
			return damageRecord;
		} else {
			return i > 5 && damageRecord2 != null ? damageRecord2 : null;
		}
	}

	private String getFallDeathSuffix(DamageRecord damageRecord) {
		return damageRecord.getFallDeathSuffix() == null ? "generic" : damageRecord.getFallDeathSuffix();
	}

	public int getTimeSinceLastAttack() {
		return this.recentlyAttacked ? this.entity.ticksAlive - this.ageOnLastAttacked : this.ageOnLastUpdate - this.ageOnLastAttacked;
	}

	private void clearFallDeathSuffix() {
		this.fallDeathSuffix = null;
	}

	public void update() {
		int i = this.recentlyAttacked ? 300 : 100;
		if (this.hasDamage && (!this.entity.isAlive() || this.entity.ticksAlive - this.ageOnLastDamage > i)) {
			boolean bl = this.recentlyAttacked;
			this.hasDamage = false;
			this.recentlyAttacked = false;
			this.ageOnLastUpdate = this.entity.ticksAlive;
			if (bl) {
				this.entity.endCombat();
			}

			this.recentDamage.clear();
		}
	}

	public LivingEntity getEntity() {
		return this.entity;
	}
}
