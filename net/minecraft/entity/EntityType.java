package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, Class<? extends Entity>> NAME_CLASS_MAP = Maps.newHashMap();
	private static final Map<Class<? extends Entity>, String> CLASS_NAME_MAP = Maps.newHashMap();
	private static final Map<Integer, Class<? extends Entity>> ID_CLASS_MAP = Maps.newHashMap();
	private static final Map<Class<? extends Entity>, Integer> CLASS_ID_MAP = Maps.newHashMap();
	private static final Map<String, Integer> NAME_ID_MAP = Maps.newHashMap();
	public static final Map<Integer, EntityType.SpawnEggData> SPAWN_EGGS = Maps.newLinkedHashMap();

	private static void registerEntity(Class<? extends Entity> clazz, String name, int id) {
		if (NAME_CLASS_MAP.containsKey(name)) {
			throw new IllegalArgumentException("ID is already registered: " + name);
		} else if (ID_CLASS_MAP.containsKey(id)) {
			throw new IllegalArgumentException("ID is already registered: " + id);
		} else if (id == 0) {
			throw new IllegalArgumentException("Cannot register to reserved id: " + id);
		} else if (clazz == null) {
			throw new IllegalArgumentException("Cannot register null clazz for id: " + id);
		} else {
			NAME_CLASS_MAP.put(name, clazz);
			CLASS_NAME_MAP.put(clazz, name);
			ID_CLASS_MAP.put(id, clazz);
			CLASS_ID_MAP.put(clazz, id);
			NAME_ID_MAP.put(name, id);
		}
	}

	private static void registerEntity(Class<? extends Entity> clazz, String name, int id, int foregroundColor, int backgroundColor) {
		registerEntity(clazz, name, id);
		SPAWN_EGGS.put(id, new EntityType.SpawnEggData(id, foregroundColor, backgroundColor));
	}

	public static Entity createInstanceFromName(String name, World world) {
		Entity entity = null;

		try {
			Class<? extends Entity> class_ = (Class<? extends Entity>)NAME_CLASS_MAP.get(name);
			if (class_ != null) {
				entity = (Entity)class_.getConstructor(World.class).newInstance(world);
			}
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		return entity;
	}

	public static Entity createInstanceFromNbt(NbtCompound nbt, World world) {
		Entity entity = null;
		if ("Minecart".equals(nbt.getString("id"))) {
			nbt.putString("id", AbstractMinecartEntity.Type.getById(nbt.getInt("Type")).getName());
			nbt.remove("Type");
		}

		try {
			Class<? extends Entity> class_ = (Class<? extends Entity>)NAME_CLASS_MAP.get(nbt.getString("id"));
			if (class_ != null) {
				entity = (Entity)class_.getConstructor(World.class).newInstance(world);
			}
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		if (entity != null) {
			entity.fromNbt(nbt);
		} else {
			LOGGER.warn("Skipping Entity with id " + nbt.getString("id"));
		}

		return entity;
	}

	public static Entity createInstanceFromRawId(int id, World world) {
		Entity entity = null;

		try {
			Class<? extends Entity> class_ = getEntityById(id);
			if (class_ != null) {
				entity = (Entity)class_.getConstructor(World.class).newInstance(world);
			}
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		if (entity == null) {
			LOGGER.warn("Skipping Entity with id " + id);
		}

		return entity;
	}

	public static int getIdByEntity(Entity entity) {
		Integer integer = (Integer)CLASS_ID_MAP.get(entity.getClass());
		return integer == null ? 0 : integer;
	}

	public static Class<? extends Entity> getEntityById(int id) {
		return (Class<? extends Entity>)ID_CLASS_MAP.get(id);
	}

	public static String getEntityName(Entity entity) {
		return (String)CLASS_NAME_MAP.get(entity.getClass());
	}

	public static int getIdByName(String name) {
		Integer integer = (Integer)NAME_ID_MAP.get(name);
		return integer == null ? 90 : integer;
	}

	public static String getEntityName(int id) {
		return (String)CLASS_NAME_MAP.get(getEntityById(id));
	}

	public static void load() {
	}

	public static List<String> getEntityNames() {
		Set<String> set = NAME_CLASS_MAP.keySet();
		List<String> list = Lists.newArrayList();

		for (String string : set) {
			Class<? extends Entity> class_ = (Class<? extends Entity>)NAME_CLASS_MAP.get(string);
			if ((class_.getModifiers() & 1024) != 1024) {
				list.add(string);
			}
		}

		list.add("LightningBolt");
		return list;
	}

	public static boolean equals(Entity entity, String string) {
		String string2 = getEntityName(entity);
		if (string2 == null && entity instanceof PlayerEntity) {
			string2 = "Player";
		} else if (string2 == null && entity instanceof LightningBoltEntity) {
			string2 = "LightningBolt";
		}

		return string.equals(string2);
	}

	public static boolean isEntityRegistered(String name) {
		return "Player".equals(name) || getEntityNames().contains(name);
	}

	static {
		registerEntity(ItemEntity.class, "Item", 1);
		registerEntity(ExperienceOrbEntity.class, "XPOrb", 2);
		registerEntity(EggEntity.class, "ThrownEgg", 7);
		registerEntity(LeashKnotEntity.class, "LeashKnot", 8);
		registerEntity(PaintingEntity.class, "Painting", 9);
		registerEntity(AbstractArrowEntity.class, "Arrow", 10);
		registerEntity(SnowballEntity.class, "Snowball", 11);
		registerEntity(FireballEntity.class, "Fireball", 12);
		registerEntity(SmallFireballEntity.class, "SmallFireball", 13);
		registerEntity(EnderPearlEntity.class, "ThrownEnderpearl", 14);
		registerEntity(EyeOfEnderEntity.class, "EyeOfEnderSignal", 15);
		registerEntity(PotionEntity.class, "ThrownPotion", 16);
		registerEntity(ExperienceBottleEntity.class, "ThrownExpBottle", 17);
		registerEntity(ItemFrameEntity.class, "ItemFrame", 18);
		registerEntity(WitherSkullEntity.class, "WitherSkull", 19);
		registerEntity(TntEntity.class, "PrimedTnt", 20);
		registerEntity(FallingBlockEntity.class, "FallingSand", 21);
		registerEntity(FireworkRocketEntity.class, "FireworksRocketEntity", 22);
		registerEntity(ArmorStandEntity.class, "ArmorStand", 30);
		registerEntity(BoatEntity.class, "Boat", 41);
		registerEntity(MinecartEntity.class, AbstractMinecartEntity.Type.RIDEABLE.getName(), 42);
		registerEntity(ChestMinecartEntity.class, AbstractMinecartEntity.Type.CHEST.getName(), 43);
		registerEntity(FurnaceMinecartEntity.class, AbstractMinecartEntity.Type.FURNACE.getName(), 44);
		registerEntity(TntMinecartEntity.class, AbstractMinecartEntity.Type.TNT.getName(), 45);
		registerEntity(HopperMinecartEntity.class, AbstractMinecartEntity.Type.HOPPER.getName(), 46);
		registerEntity(SpawnerMinecartEntity.class, AbstractMinecartEntity.Type.SPAWNER.getName(), 47);
		registerEntity(CommandBlockMinecartEntity.class, AbstractMinecartEntity.Type.COMMAND_BLOCK.getName(), 40);
		registerEntity(MobEntity.class, "Mob", 48);
		registerEntity(HostileEntity.class, "Monster", 49);
		registerEntity(CreeperEntity.class, "Creeper", 50, 894731, 0);
		registerEntity(SkeletonEntity.class, "Skeleton", 51, 12698049, 4802889);
		registerEntity(SpiderEntity.class, "Spider", 52, 3419431, 11013646);
		registerEntity(GiantEntity.class, "Giant", 53);
		registerEntity(ZombieEntity.class, "Zombie", 54, 44975, 7969893);
		registerEntity(SlimeEntity.class, "Slime", 55, 5349438, 8306542);
		registerEntity(GhastEntity.class, "Ghast", 56, 16382457, 12369084);
		registerEntity(ZombiePigmanEntity.class, "PigZombie", 57, 15373203, 5009705);
		registerEntity(EndermanEntity.class, "Enderman", 58, 1447446, 0);
		registerEntity(CaveSpiderEntity.class, "CaveSpider", 59, 803406, 11013646);
		registerEntity(SilverfishEntity.class, "Silverfish", 60, 7237230, 3158064);
		registerEntity(BlazeEntity.class, "Blaze", 61, 16167425, 16775294);
		registerEntity(MagmaCubeEntity.class, "LavaSlime", 62, 3407872, 16579584);
		registerEntity(EnderDragonEntity.class, "EnderDragon", 63);
		registerEntity(WitherEntity.class, "WitherBoss", 64);
		registerEntity(BatEntity.class, "Bat", 65, 4996656, 986895);
		registerEntity(WitchEntity.class, "Witch", 66, 3407872, 5349438);
		registerEntity(EndermiteEntity.class, "Endermite", 67, 1447446, 7237230);
		registerEntity(GuardianEntity.class, "Guardian", 68, 5931634, 15826224);
		registerEntity(PigEntity.class, "Pig", 90, 15771042, 14377823);
		registerEntity(SheepEntity.class, "Sheep", 91, 15198183, 16758197);
		registerEntity(CowEntity.class, "Cow", 92, 4470310, 10592673);
		registerEntity(ChickenEntity.class, "Chicken", 93, 10592673, 16711680);
		registerEntity(SquidEntity.class, "Squid", 94, 2243405, 7375001);
		registerEntity(WolfEntity.class, "Wolf", 95, 14144467, 13545366);
		registerEntity(MooshroomEntity.class, "MushroomCow", 96, 10489616, 12040119);
		registerEntity(SnowGolemEntity.class, "SnowMan", 97);
		registerEntity(OcelotEntity.class, "Ozelot", 98, 15720061, 5653556);
		registerEntity(IronGolemEntity.class, "VillagerGolem", 99);
		registerEntity(HorseBaseEntity.class, "EntityHorse", 100, 12623485, 15656192);
		registerEntity(RabbitEntity.class, "Rabbit", 101, 10051392, 7555121);
		registerEntity(VillagerEntity.class, "Villager", 120, 5651507, 12422002);
		registerEntity(EndCrystalEntity.class, "EnderCrystal", 200);
	}

	public static class SpawnEggData {
		public final int id;
		public final int foregroundColor;
		public final int backgroundColor;
		public final Stat killEntityStat;
		public final Stat killedByEntityStat;

		public SpawnEggData(int i, int j, int k) {
			this.id = i;
			this.foregroundColor = j;
			this.backgroundColor = k;
			this.killEntityStat = Stats.createKillEntityStat(this);
			this.killedByEntityStat = Stats.createKilledByEntityStat(this);
		}
	}
}
