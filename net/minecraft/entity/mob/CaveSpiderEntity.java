package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class CaveSpiderEntity extends SpiderEntity {
	public CaveSpiderEntity(World world) {
		super(world);
		this.setBounds(0.7F, 0.5F);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(12.0);
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (super.tryAttack(target)) {
			if (target instanceof LivingEntity) {
				int i = 0;
				if (this.world.getGlobalDifficulty() == Difficulty.NORMAL) {
					i = 7;
				} else if (this.world.getGlobalDifficulty() == Difficulty.HARD) {
					i = 15;
				}

				if (i > 0) {
					((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, i * 20, 0));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		return data;
	}

	@Override
	public float getEyeHeight() {
		return 0.45F;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.CAVE_SPIDER_ENTITIE;
	}
}
