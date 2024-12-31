package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
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
import net.minecraft.entity.mob.ZombieVillagerEntity;
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
	private static final Map<Class<?>, MobEntity.Location> field_14564 = Maps.newHashMap();

	public static MobEntity.Location getLocation(Class<?> entityClass) {
		return (MobEntity.Location)field_14564.get(entityClass);
	}

	static {
		field_14564.put(BatEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(ChickenEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(CowEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(HorseBaseEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SkeletonHorseEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(ZombieHorseEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(DonkeyEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(MuleEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(MooshroomEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(OcelotEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(PigEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(RabbitEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(ParrotEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SheepEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SnowGolemEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SquidEntity.class, MobEntity.Location.IN_WATER);
		field_14564.put(IronGolemEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(WolfEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(VillagerEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(EnderDragonEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(WitherEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(BlazeEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(CaveSpiderEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(CreeperEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(EndermanEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(EndermiteEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(GhastEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(GiantEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(GuardianEntity.class, MobEntity.Location.IN_WATER);
		field_14564.put(MagmaCubeEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(ZombiePigmanEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SilverfishEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SkeletonEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(WhitherSkeletonEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(StrayEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SlimeEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(SpiderEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(WitchEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(ZombieEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(ZombieVillagerEntity.class, MobEntity.Location.ON_GROUND);
		field_14564.put(HuskEntity.class, MobEntity.Location.ON_GROUND);
	}
}
