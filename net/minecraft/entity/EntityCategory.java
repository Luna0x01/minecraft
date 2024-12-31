package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;

public enum EntityCategory {
	MONSTER(Monster.class, 70, Material.AIR, false, false),
	PASSIVE(AnimalEntity.class, 10, Material.AIR, true, true),
	AMBIENT(AmbientEntity.class, 15, Material.AIR, true, false),
	AQUATIC(WaterCreatureEntity.class, 5, Material.WATER, true, false);

	private final Class<? extends EntityCategoryProvider> clazz;
	private final int spawnCap;
	private final Material material;
	private final boolean hostile;
	private final boolean breedable;

	private EntityCategory(Class<? extends EntityCategoryProvider> class_, int j, Material material, boolean bl, boolean bl2) {
		this.clazz = class_;
		this.spawnCap = j;
		this.material = material;
		this.hostile = bl;
		this.breedable = bl2;
	}

	public Class<? extends EntityCategoryProvider> getCategoryClass() {
		return this.clazz;
	}

	public int getSpawnCap() {
		return this.spawnCap;
	}

	public boolean isHostile() {
		return this.hostile;
	}

	public boolean isBreedable() {
		return this.breedable;
	}
}
