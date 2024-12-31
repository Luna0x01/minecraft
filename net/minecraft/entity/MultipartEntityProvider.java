package net.minecraft.entity;

import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public interface MultipartEntityProvider {
	World getServerWorld();

	boolean setAngry(EnderDragonPart multipart, DamageSource source, float angry);
}
