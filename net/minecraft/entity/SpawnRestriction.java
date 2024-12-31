package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;

public class SpawnRestriction {
	private static final Map<EntityType<?>, SpawnRestriction.Entry> RESTRICTIONS = Maps.newHashMap();

	private static <T extends MobEntity> void register(
		EntityType<T> entityType, SpawnRestriction.Location location, Heightmap.Type type, SpawnRestriction.SpawnPredicate<T> spawnPredicate
	) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.put(entityType, new SpawnRestriction.Entry(type, location, spawnPredicate));
		if (entry != null) {
			throw new IllegalStateException("Duplicate registration for type " + Registry.field_11145.getId(entityType));
		}
	}

	public static SpawnRestriction.Location getLocation(EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.get(entityType);
		return entry == null ? SpawnRestriction.Location.field_19350 : entry.location;
	}

	public static Heightmap.Type getHeightmapType(@Nullable EntityType<?> entityType) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.get(entityType);
		return entry == null ? Heightmap.Type.field_13203 : entry.heightmapType;
	}

	public static <T extends Entity> boolean canSpawn(EntityType<T> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		SpawnRestriction.Entry entry = (SpawnRestriction.Entry)RESTRICTIONS.get(entityType);
		return entry == null || entry.predicate.test(entityType, iWorld, spawnType, blockPos, random);
	}

	static {
		register(EntityType.field_6070, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, FishEntity::canSpawn);
		register(EntityType.field_6087, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, DolphinEntity::canSpawn);
		register(EntityType.field_6123, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, DrownedEntity::canSpawn);
		register(EntityType.field_6118, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, GuardianEntity::canSpawn);
		register(EntityType.field_6062, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, FishEntity::canSpawn);
		register(EntityType.field_6073, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, FishEntity::canSpawn);
		register(EntityType.field_6114, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, SquidEntity::canSpawn);
		register(EntityType.field_6111, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, FishEntity::canSpawn);
		register(EntityType.field_6108, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, BatEntity::canSpawn);
		register(EntityType.field_6099, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnIgnoreLightLevel);
		register(EntityType.field_6084, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6132, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6085, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6046, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6067, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6091, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6128, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, EndermiteEntity::canSpawn);
		register(EntityType.field_6116, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
		register(EntityType.field_6107, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, GhastEntity::canSpawn);
		register(EntityType.field_6095, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6139, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6071, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HuskEntity::canSpawn);
		register(EntityType.field_6147, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
		register(EntityType.field_6074, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6102, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, MagmaCubeEntity::canMagmaCubeSpawn);
		register(EntityType.field_6143, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, MooshroomEntity::canSpawn);
		register(EntityType.field_6057, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6081, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13197, OcelotEntity::canSpawn);
		register(EntityType.field_6104, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13197, ParrotEntity::canSpawn);
		register(EntityType.field_6093, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6105, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, PatrolEntity::canSpawn);
		register(EntityType.field_6042, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, PolarBearEntity::canSpawn);
		register(EntityType.field_6140, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, RabbitEntity::canSpawn);
		register(EntityType.field_6115, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6125, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, SilverfishEntity::canSpawn);
		register(EntityType.field_6137, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6075, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6069, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, SlimeEntity::canSpawn);
		register(EntityType.field_6047, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
		register(EntityType.field_6079, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6098, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, StrayEntity::canSpawn);
		register(EntityType.field_6113, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, TurtleEntity::canSpawn);
		register(EntityType.field_6077, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
		register(EntityType.field_6145, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6119, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6076, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6055, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6051, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6048, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6050, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, ZombiePigmanEntity::canSpawn);
		register(EntityType.field_6054, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_16281, SpawnRestriction.Location.field_6317, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6086, SpawnRestriction.Location.field_6318, Heightmap.Type.field_13203, GuardianEntity::canSpawn);
		register(EntityType.field_6090, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_17943, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6065, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6146, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6078, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
		register(EntityType.field_6134, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6109, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
		register(EntityType.field_17714, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, AnimalEntity::isValidNaturalSpawn);
		register(EntityType.field_6059, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_6117, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, HostileEntity::canSpawnInDark);
		register(EntityType.field_17713, SpawnRestriction.Location.field_19350, Heightmap.Type.field_13203, MobEntity::canMobSpawn);
	}

	static class Entry {
		private final Heightmap.Type heightmapType;
		private final SpawnRestriction.Location location;
		private final SpawnRestriction.SpawnPredicate<?> predicate;

		public Entry(Heightmap.Type type, SpawnRestriction.Location location, SpawnRestriction.SpawnPredicate<?> spawnPredicate) {
			this.heightmapType = type;
			this.location = location;
			this.predicate = spawnPredicate;
		}
	}

	public static enum Location {
		field_6317,
		field_6318,
		field_19350;
	}

	@FunctionalInterface
	public interface SpawnPredicate<T extends Entity> {
		boolean test(EntityType<T> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random);
	}
}
