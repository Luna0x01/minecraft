package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType {
	public static final Identifier LIGHTNING_BOLT = new Identifier("lightning_bolt");
	private static final Identifier PLAYER = new Identifier("player");
	private static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleRegistry<Identifier, Class<? extends Entity>> REGISTRY = new SimpleRegistry<>();
	public static final Map<Identifier, EntityType.SpawnEggData> SPAWN_EGGS = Maps.newLinkedHashMap();
	public static final Set<Identifier> IDENTIFIERS = Sets.newHashSet();
	private static final List<String> NAMES = Lists.newArrayList();

	@Nullable
	public static Identifier getId(Entity entity) {
		return getId(entity.getClass());
	}

	@Nullable
	public static Identifier getId(Class<? extends Entity> clazz) {
		return REGISTRY.getIdentifier(clazz);
	}

	@Nullable
	public static String getEntityName(Entity entity) {
		int i = REGISTRY.getRawId(entity.getClass());
		return i == -1 ? null : (String)NAMES.get(i);
	}

	@Nullable
	public static String getEntityName(@Nullable Identifier id) {
		int i = REGISTRY.getRawId(REGISTRY.get(id));
		return i == -1 ? null : (String)NAMES.get(i);
	}

	@Nullable
	public static Class<? extends Entity> getEntityById(int id) {
		return REGISTRY.getByRawId(id);
	}

	@Nullable
	public static Entity createInstanceFromClass(@Nullable Class<? extends Entity> clazz, World world) {
		if (clazz == null) {
			return null;
		} else {
			try {
				return (Entity)clazz.getConstructor(World.class).newInstance(world);
			} catch (Exception var3) {
				var3.printStackTrace();
				return null;
			}
		}
	}

	@Nullable
	public static Entity createInstanceFromRawId(int id, World world) {
		return createInstanceFromClass(getEntityById(id), world);
	}

	@Nullable
	public static Entity createInstanceFromId(Identifier id, World world) {
		return createInstanceFromClass(REGISTRY.get(id), world);
	}

	@Nullable
	public static Entity createInstanceFromNbt(NbtCompound nbt, World world) {
		Identifier identifier = new Identifier(nbt.getString("id"));
		Entity entity = createInstanceFromId(identifier, world);
		if (entity == null) {
			LOGGER.warn("Skipping Entity with id {}", new Object[]{identifier});
		} else {
			entity.fromNbt(nbt);
		}

		return entity;
	}

	public static Set<Identifier> getIdentifiers() {
		return IDENTIFIERS;
	}

	public static boolean method_13943(Entity entity, Identifier id) {
		Identifier identifier = getId(entity.getClass());
		if (identifier != null) {
			return identifier.equals(id);
		} else if (entity instanceof PlayerEntity) {
			return PLAYER.equals(id);
		} else {
			return entity instanceof LightningBoltEntity ? LIGHTNING_BOLT.equals(id) : false;
		}
	}

	public static boolean isValid(Identifier id) {
		return PLAYER.equals(id) || getIdentifiers().contains(id);
	}

	public static void load() {
		register(1, "item", ItemEntity.class, "Item");
		register(2, "xp_orb", ExperienceOrbEntity.class, "XPOrb");
		register(3, "area_effect_cloud", AreaEffectCloudEntity.class, "AreaEffectCloud");
		register(4, "elder_guardian", ElderGuardianEntity.class, "ElderGuardian");
		register(5, "wither_skeleton", WhitherSkeletonEntity.class, "WitherSkeleton");
		register(6, "stray", StrayEntity.class, "Stray");
		register(7, "egg", EggEntity.class, "ThrownEgg");
		register(8, "leash_knot", LeashKnotEntity.class, "LeashKnot");
		register(9, "painting", PaintingEntity.class, "Painting");
		register(10, "arrow", ArrowEntity.class, "Arrow");
		register(11, "snowball", SnowballEntity.class, "Snowball");
		register(12, "fireball", FireballEntity.class, "Fireball");
		register(13, "small_fireball", SmallFireballEntity.class, "SmallFireball");
		register(14, "ender_pearl", EnderPearlEntity.class, "ThrownEnderpearl");
		register(15, "eye_of_ender_signal", EyeOfEnderEntity.class, "EyeOfEnderSignal");
		register(16, "potion", PotionEntity.class, "ThrownPotion");
		register(17, "xp_bottle", ExperienceBottleEntity.class, "ThrownExpBottle");
		register(18, "item_frame", ItemFrameEntity.class, "ItemFrame");
		register(19, "wither_skull", WitherSkullEntity.class, "WitherSkull");
		register(20, "tnt", TntEntity.class, "PrimedTnt");
		register(21, "falling_block", FallingBlockEntity.class, "FallingSand");
		register(22, "fireworks_rocket", FireworkRocketEntity.class, "FireworksRocketEntity");
		register(23, "husk", HuskEntity.class, "Husk");
		register(24, "spectral_arrow", SpectralArrowEntity.class, "SpectralArrow");
		register(25, "shulker_bullet", ShulkerBulletEntity.class, "ShulkerBullet");
		register(26, "dragon_fireball", DragonFireballEntity.class, "DragonFireball");
		register(27, "zombie_villager", ZombieVillagerEntity.class, "ZombieVillager");
		register(28, "skeleton_horse", SkeletonHorseEntity.class, "SkeletonHorse");
		register(29, "zombie_horse", ZombieHorseEntity.class, "ZombieHorse");
		register(30, "armor_stand", ArmorStandEntity.class, "ArmorStand");
		register(31, "donkey", DonkeyEntity.class, "Donkey");
		register(32, "mule", MuleEntity.class, "Mule");
		register(33, "evocation_fangs", EvokerFangsEntity.class, "EvocationFangs");
		register(34, "evocation_illager", EvocationIllagerEntity.class, "EvocationIllager");
		register(35, "vex", VexEntity.class, "Vex");
		register(36, "vindication_illager", VindicationIllagerEntity.class, "VindicationIllager");
		register(40, "commandblock_minecart", CommandBlockMinecartEntity.class, AbstractMinecartEntity.Type.COMMAND_BLOCK.getName());
		register(41, "boat", BoatEntity.class, "Boat");
		register(42, "minecart", MinecartEntity.class, AbstractMinecartEntity.Type.RIDEABLE.getName());
		register(43, "chest_minecart", ChestMinecartEntity.class, AbstractMinecartEntity.Type.CHEST.getName());
		register(44, "furnace_minecart", FurnaceMinecartEntity.class, AbstractMinecartEntity.Type.FURNACE.getName());
		register(45, "tnt_minecart", TntMinecartEntity.class, AbstractMinecartEntity.Type.TNT.getName());
		register(46, "hopper_minecart", HopperMinecartEntity.class, AbstractMinecartEntity.Type.HOPPER.getName());
		register(47, "spawner_minecart", SpawnerMinecartEntity.class, AbstractMinecartEntity.Type.SPAWNER.getName());
		register(50, "creeper", CreeperEntity.class, "Creeper");
		register(51, "skeleton", SkeletonEntity.class, "Skeleton");
		register(52, "spider", SpiderEntity.class, "Spider");
		register(53, "giant", GiantEntity.class, "Giant");
		register(54, "zombie", ZombieEntity.class, "Zombie");
		register(55, "slime", SlimeEntity.class, "Slime");
		register(56, "ghast", GhastEntity.class, "Ghast");
		register(57, "zombie_pigman", ZombiePigmanEntity.class, "PigZombie");
		register(58, "enderman", EndermanEntity.class, "Enderman");
		register(59, "cave_spider", CaveSpiderEntity.class, "CaveSpider");
		register(60, "silverfish", SilverfishEntity.class, "Silverfish");
		register(61, "blaze", BlazeEntity.class, "Blaze");
		register(62, "magma_cube", MagmaCubeEntity.class, "LavaSlime");
		register(63, "ender_dragon", EnderDragonEntity.class, "EnderDragon");
		register(64, "wither", WitherEntity.class, "WitherBoss");
		register(65, "bat", BatEntity.class, "Bat");
		register(66, "witch", WitchEntity.class, "Witch");
		register(67, "endermite", EndermiteEntity.class, "Endermite");
		register(68, "guardian", GuardianEntity.class, "Guardian");
		register(69, "shulker", ShulkerEntity.class, "Shulker");
		register(90, "pig", PigEntity.class, "Pig");
		register(91, "sheep", SheepEntity.class, "Sheep");
		register(92, "cow", CowEntity.class, "Cow");
		register(93, "chicken", ChickenEntity.class, "Chicken");
		register(94, "squid", SquidEntity.class, "Squid");
		register(95, "wolf", WolfEntity.class, "Wolf");
		register(96, "mooshroom", MooshroomEntity.class, "MushroomCow");
		register(97, "snowman", SnowGolemEntity.class, "SnowMan");
		register(98, "ocelot", OcelotEntity.class, "Ozelot");
		register(99, "villager_golem", IronGolemEntity.class, "VillagerGolem");
		register(100, "horse", HorseBaseEntity.class, "Horse");
		register(101, "rabbit", RabbitEntity.class, "Rabbit");
		register(102, "polar_bear", PolarBearEntity.class, "PolarBear");
		register(103, "llama", LlamaEntity.class, "Llama");
		register(104, "llama_spit", LlamaSpitEntity.class, "LlamaSpit");
		register(120, "villager", VillagerEntity.class, "Villager");
		register(200, "ender_crystal", EndCrystalEntity.class, "EnderCrystal");
		registerSpawnEgg("bat", 4996656, 986895);
		registerSpawnEgg("blaze", 16167425, 16775294);
		registerSpawnEgg("cave_spider", 803406, 11013646);
		registerSpawnEgg("chicken", 10592673, 16711680);
		registerSpawnEgg("cow", 4470310, 10592673);
		registerSpawnEgg("creeper", 894731, 0);
		registerSpawnEgg("donkey", 5457209, 8811878);
		registerSpawnEgg("elder_guardian", 13552826, 7632531);
		registerSpawnEgg("enderman", 1447446, 0);
		registerSpawnEgg("endermite", 1447446, 7237230);
		registerSpawnEgg("evocation_illager", 9804699, 1973274);
		registerSpawnEgg("ghast", 16382457, 12369084);
		registerSpawnEgg("guardian", 5931634, 15826224);
		registerSpawnEgg("horse", 12623485, 15656192);
		registerSpawnEgg("husk", 7958625, 15125652);
		registerSpawnEgg("llama", 12623485, 10051392);
		registerSpawnEgg("magma_cube", 3407872, 16579584);
		registerSpawnEgg("mooshroom", 10489616, 12040119);
		registerSpawnEgg("mule", 1769984, 5321501);
		registerSpawnEgg("ocelot", 15720061, 5653556);
		registerSpawnEgg("pig", 15771042, 14377823);
		registerSpawnEgg("polar_bear", 15921906, 9803152);
		registerSpawnEgg("rabbit", 10051392, 7555121);
		registerSpawnEgg("sheep", 15198183, 16758197);
		registerSpawnEgg("shulker", 9725844, 5060690);
		registerSpawnEgg("silverfish", 7237230, 3158064);
		registerSpawnEgg("skeleton", 12698049, 4802889);
		registerSpawnEgg("skeleton_horse", 6842447, 15066584);
		registerSpawnEgg("slime", 5349438, 8306542);
		registerSpawnEgg("spider", 3419431, 11013646);
		registerSpawnEgg("squid", 2243405, 7375001);
		registerSpawnEgg("stray", 6387319, 14543594);
		registerSpawnEgg("vex", 8032420, 15265265);
		registerSpawnEgg("villager", 5651507, 12422002);
		registerSpawnEgg("vindication_illager", 9804699, 2580065);
		registerSpawnEgg("witch", 3407872, 5349438);
		registerSpawnEgg("wither_skeleton", 1315860, 4672845);
		registerSpawnEgg("wolf", 14144467, 13545366);
		registerSpawnEgg("zombie", 44975, 7969893);
		registerSpawnEgg("zombie_horse", 3232308, 9945732);
		registerSpawnEgg("zombie_pigman", 15373203, 5009705);
		registerSpawnEgg("zombie_villager", 5651507, 7969893);
		IDENTIFIERS.add(LIGHTNING_BOLT);
	}

	private static void register(int index, String id, Class<? extends Entity> clazz, String name) {
		try {
			clazz.getConstructor(World.class);
		} catch (NoSuchMethodException var5) {
			throw new RuntimeException("Invalid class " + clazz + " no constructor taking " + World.class.getName());
		}

		if ((clazz.getModifiers() & 1024) == 1024) {
			throw new RuntimeException("Invalid abstract class " + clazz);
		} else {
			Identifier identifier = new Identifier(id);
			REGISTRY.add(index, identifier, clazz);
			IDENTIFIERS.add(identifier);

			while (NAMES.size() <= index) {
				NAMES.add(null);
			}

			NAMES.set(index, name);
		}
	}

	protected static EntityType.SpawnEggData registerSpawnEgg(String identifier, int color0, int color1) {
		Identifier identifier2 = new Identifier(identifier);
		return (EntityType.SpawnEggData)SPAWN_EGGS.put(identifier2, new EntityType.SpawnEggData(identifier2, color0, color1));
	}

	public static class SpawnEggData {
		public final Identifier identifier;
		public final int color0;
		public final int color1;
		public final Stat killEntityStat;
		public final Stat killedByEntityStat;

		public SpawnEggData(Identifier identifier, int i, int j) {
			this.identifier = identifier;
			this.color0 = i;
			this.color1 = j;
			this.killEntityStat = Stats.createKillEntityStat(this);
			this.killedByEntityStat = Stats.createKilledByEntityStat(this);
		}
	}
}
