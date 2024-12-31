package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.HashMap;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;

public class EntityLocations {
	private static final HashMap<Class, MobEntity.Location> LOCATION_MAP = Maps.newHashMap();

	public static MobEntity.Location getLocation(Class entityClass) {
		return (MobEntity.Location)LOCATION_MAP.get(entityClass);
	}

	static {
		LOCATION_MAP.put(BatEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(ChickenEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(CowEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(HorseBaseEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(MooshroomEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(OcelotEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(PigEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(RabbitEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SheepEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SnowGolemEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SquidEntity.class, MobEntity.Location.IN_WATER);
		LOCATION_MAP.put(IronGolemEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(WolfEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(VillagerEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(EnderDragonEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(WitherEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(BlazeEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(CaveSpiderEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(CreeperEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(EndermanEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(EndermiteEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(GhastEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(GiantEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(GuardianEntity.class, MobEntity.Location.IN_WATER);
		LOCATION_MAP.put(MagmaCubeEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(ZombiePigmanEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SilverfishEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SkeletonEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SlimeEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(SpiderEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(WitchEntity.class, MobEntity.Location.ON_GROUND);
		LOCATION_MAP.put(ZombieEntity.class, MobEntity.Location.ON_GROUND);
	}
}
