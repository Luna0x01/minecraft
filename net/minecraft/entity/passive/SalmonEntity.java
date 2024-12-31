package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SalmonEntity extends SchoolableFishEntity {
	public SalmonEntity(World world) {
		super(EntityType.SALMON, world);
		this.setBounds(0.7F, 0.4F);
	}

	@Override
	public int getMaxGroupSize() {
		return 5;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SALMON_ENTITIE;
	}

	@Override
	protected ItemStack method_15726() {
		return new ItemStack(Items.SALMON_BUCKET);
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SALMON_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SALMON_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_SALMON_HURT;
	}

	@Override
	protected Sound method_15724() {
		return Sounds.ENTITY_SALMON_FLOP;
	}
}
