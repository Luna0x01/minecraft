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

public class CodEntity extends SchoolableFishEntity {
	public CodEntity(World world) {
		super(EntityType.COD, world);
		this.setBounds(0.5F, 0.3F);
	}

	@Override
	protected ItemStack method_15726() {
		return new ItemStack(Items.COD_BUCKET);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.COD_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_COD_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_COD_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_COD_HURT;
	}

	@Override
	protected Sound method_15724() {
		return Sounds.ENTITY_COD_FLOP;
	}
}
