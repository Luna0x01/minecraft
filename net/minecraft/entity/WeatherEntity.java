package net.minecraft.entity;

import net.minecraft.world.World;

public abstract class WeatherEntity extends Entity {
	public WeatherEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}
}
