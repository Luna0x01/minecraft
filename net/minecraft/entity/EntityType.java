package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_3402;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
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
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register(
		"area_effect_cloud", EntityType.EntityBuilder.create(AreaEffectCloudEntity.class, AreaEffectCloudEntity::new)
	);
	public static final EntityType<ArmorStandEntity> ARMOR_STAND = register(
		"armor_stand", EntityType.EntityBuilder.create(ArmorStandEntity.class, ArmorStandEntity::new)
	);
	public static final EntityType<ArrowEntity> ARROW = register("arrow", EntityType.EntityBuilder.create(ArrowEntity.class, ArrowEntity::new));
	public static final EntityType<BatEntity> BAT = register("bat", EntityType.EntityBuilder.create(BatEntity.class, BatEntity::new));
	public static final EntityType<BlazeEntity> BLAZE = register("blaze", EntityType.EntityBuilder.create(BlazeEntity.class, BlazeEntity::new));
	public static final EntityType<BoatEntity> BOAT = register("boat", EntityType.EntityBuilder.create(BoatEntity.class, BoatEntity::new));
	public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register(
		"cave_spider", EntityType.EntityBuilder.create(CaveSpiderEntity.class, CaveSpiderEntity::new)
	);
	public static final EntityType<ChickenEntity> CHICKEN = register("chicken", EntityType.EntityBuilder.create(ChickenEntity.class, ChickenEntity::new));
	public static final EntityType<CodEntity> COD = register("cod", EntityType.EntityBuilder.create(CodEntity.class, CodEntity::new));
	public static final EntityType<CowEntity> COW = register("cow", EntityType.EntityBuilder.create(CowEntity.class, CowEntity::new));
	public static final EntityType<CreeperEntity> CREEPER = register("creeper", EntityType.EntityBuilder.create(CreeperEntity.class, CreeperEntity::new));
	public static final EntityType<DonkeyEntity> DONKEY = register("donkey", EntityType.EntityBuilder.create(DonkeyEntity.class, DonkeyEntity::new));
	public static final EntityType<DolphinEntity> DOLPHIN = register("dolphin", EntityType.EntityBuilder.create(DolphinEntity.class, DolphinEntity::new));
	public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register(
		"dragon_fireball", EntityType.EntityBuilder.create(DragonFireballEntity.class, DragonFireballEntity::new)
	);
	public static final EntityType<DrownedEntity> DROWNED = register("drowned", EntityType.EntityBuilder.create(DrownedEntity.class, DrownedEntity::new));
	public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register(
		"elder_guardian", EntityType.EntityBuilder.create(ElderGuardianEntity.class, ElderGuardianEntity::new)
	);
	public static final EntityType<EndCrystalEntity> END_CRYSTAL = register(
		"end_crystal", EntityType.EntityBuilder.create(EndCrystalEntity.class, EndCrystalEntity::new)
	);
	public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register(
		"ender_dragon", EntityType.EntityBuilder.create(EnderDragonEntity.class, EnderDragonEntity::new)
	);
	public static final EntityType<EndermanEntity> ENDERMAN = register("enderman", EntityType.EntityBuilder.create(EndermanEntity.class, EndermanEntity::new));
	public static final EntityType<EndermiteEntity> ENDERMITE = register("endermite", EntityType.EntityBuilder.create(EndermiteEntity.class, EndermiteEntity::new));
	public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register(
		"evoker_fangs", EntityType.EntityBuilder.create(EvokerFangsEntity.class, EvokerFangsEntity::new)
	);
	public static final EntityType<EvocationIllagerEntity> EVOKER = register(
		"evoker", EntityType.EntityBuilder.create(EvocationIllagerEntity.class, EvocationIllagerEntity::new)
	);
	public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register(
		"experience_orb", EntityType.EntityBuilder.create(ExperienceOrbEntity.class, ExperienceOrbEntity::new)
	);
	public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = register(
		"eye_of_ender", EntityType.EntityBuilder.create(EyeOfEnderEntity.class, EyeOfEnderEntity::new)
	);
	public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
		"falling_block", EntityType.EntityBuilder.create(FallingBlockEntity.class, FallingBlockEntity::new)
	);
	public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register(
		"firework_rocket", EntityType.EntityBuilder.create(FireworkRocketEntity.class, FireworkRocketEntity::new)
	);
	public static final EntityType<GhastEntity> GHAST = register("ghast", EntityType.EntityBuilder.create(GhastEntity.class, GhastEntity::new));
	public static final EntityType<GiantEntity> GIANT = register("giant", EntityType.EntityBuilder.create(GiantEntity.class, GiantEntity::new));
	public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.EntityBuilder.create(GuardianEntity.class, GuardianEntity::new));
	public static final EntityType<HorseBaseEntity> HORSE = register("horse", EntityType.EntityBuilder.create(HorseBaseEntity.class, HorseBaseEntity::new));
	public static final EntityType<HuskEntity> HUSK = register("husk", EntityType.EntityBuilder.create(HuskEntity.class, HuskEntity::new));
	public static final EntityType<IllusionIllagerEntity> ILLUSIONER = register(
		"illusioner", EntityType.EntityBuilder.create(IllusionIllagerEntity.class, IllusionIllagerEntity::new)
	);
	public static final EntityType<ItemEntity> ITEM = register("item", EntityType.EntityBuilder.create(ItemEntity.class, ItemEntity::new));
	public static final EntityType<ItemFrameEntity> ITEM_FRAME = register(
		"item_frame", EntityType.EntityBuilder.create(ItemFrameEntity.class, ItemFrameEntity::new)
	);
	public static final EntityType<FireballEntity> FIREBALL = register("fireball", EntityType.EntityBuilder.create(FireballEntity.class, FireballEntity::new));
	public static final EntityType<LeashKnotEntity> LEASH_KNOT = register(
		"leash_knot", EntityType.EntityBuilder.<LeashKnotEntity>create(LeashKnotEntity.class, LeashKnotEntity::new).dontSave()
	);
	public static final EntityType<LlamaEntity> LLAMA = register("llama", EntityType.EntityBuilder.create(LlamaEntity.class, LlamaEntity::new));
	public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register(
		"llama_spit", EntityType.EntityBuilder.create(LlamaSpitEntity.class, LlamaSpitEntity::new)
	);
	public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register(
		"magma_cube", EntityType.EntityBuilder.create(MagmaCubeEntity.class, MagmaCubeEntity::new)
	);
	public static final EntityType<MinecartEntity> MINECART = register("minecart", EntityType.EntityBuilder.create(MinecartEntity.class, MinecartEntity::new));
	public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register(
		"chest_minecart", EntityType.EntityBuilder.create(ChestMinecartEntity.class, ChestMinecartEntity::new)
	);
	public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register(
		"command_block_minecart", EntityType.EntityBuilder.create(CommandBlockMinecartEntity.class, CommandBlockMinecartEntity::new)
	);
	public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register(
		"furnace_minecart", EntityType.EntityBuilder.create(FurnaceMinecartEntity.class, FurnaceMinecartEntity::new)
	);
	public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register(
		"hopper_minecart", EntityType.EntityBuilder.create(HopperMinecartEntity.class, HopperMinecartEntity::new)
	);
	public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register(
		"spawner_minecart", EntityType.EntityBuilder.create(SpawnerMinecartEntity.class, SpawnerMinecartEntity::new)
	);
	public static final EntityType<TntMinecartEntity> TNT_MINECART = register(
		"tnt_minecart", EntityType.EntityBuilder.create(TntMinecartEntity.class, TntMinecartEntity::new)
	);
	public static final EntityType<MuleEntity> MULE = register("mule", EntityType.EntityBuilder.create(MuleEntity.class, MuleEntity::new));
	public static final EntityType<MooshroomEntity> MOOSHROOM = register("mooshroom", EntityType.EntityBuilder.create(MooshroomEntity.class, MooshroomEntity::new));
	public static final EntityType<OcelotEntity> OCELOT = register("ocelot", EntityType.EntityBuilder.create(OcelotEntity.class, OcelotEntity::new));
	public static final EntityType<PaintingEntity> PAINTING = register("painting", EntityType.EntityBuilder.create(PaintingEntity.class, PaintingEntity::new));
	public static final EntityType<ParrotEntity> PARROT = register("parrot", EntityType.EntityBuilder.create(ParrotEntity.class, ParrotEntity::new));
	public static final EntityType<PigEntity> PIG = register("pig", EntityType.EntityBuilder.create(PigEntity.class, PigEntity::new));
	public static final EntityType<PufferfishEntity> PUFFERFISH = register(
		"pufferfish", EntityType.EntityBuilder.create(PufferfishEntity.class, PufferfishEntity::new)
	);
	public static final EntityType<ZombiePigmanEntity> ZOMBIE_PIGMAN = register(
		"zombie_pigman", EntityType.EntityBuilder.create(ZombiePigmanEntity.class, ZombiePigmanEntity::new)
	);
	public static final EntityType<PolarBearEntity> POLAR_BEAR = register(
		"polar_bear", EntityType.EntityBuilder.create(PolarBearEntity.class, PolarBearEntity::new)
	);
	public static final EntityType<TntEntity> TNT = register("tnt", EntityType.EntityBuilder.create(TntEntity.class, TntEntity::new));
	public static final EntityType<RabbitEntity> RABBIT = register("rabbit", EntityType.EntityBuilder.create(RabbitEntity.class, RabbitEntity::new));
	public static final EntityType<SalmonEntity> SALMON = register("salmon", EntityType.EntityBuilder.create(SalmonEntity.class, SalmonEntity::new));
	public static final EntityType<SheepEntity> SHEEP = register("sheep", EntityType.EntityBuilder.create(SheepEntity.class, SheepEntity::new));
	public static final EntityType<ShulkerEntity> SHULKER = register("shulker", EntityType.EntityBuilder.create(ShulkerEntity.class, ShulkerEntity::new));
	public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register(
		"shulker_bullet", EntityType.EntityBuilder.create(ShulkerBulletEntity.class, ShulkerBulletEntity::new)
	);
	public static final EntityType<SilverfishEntity> SILVERFISH = register(
		"silverfish", EntityType.EntityBuilder.create(SilverfishEntity.class, SilverfishEntity::new)
	);
	public static final EntityType<SkeletonEntity> SKELETON = register("skeleton", EntityType.EntityBuilder.create(SkeletonEntity.class, SkeletonEntity::new));
	public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register(
		"skeleton_horse", EntityType.EntityBuilder.create(SkeletonHorseEntity.class, SkeletonHorseEntity::new)
	);
	public static final EntityType<SlimeEntity> SLIME = register("slime", EntityType.EntityBuilder.create(SlimeEntity.class, SlimeEntity::new));
	public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register(
		"small_fireball", EntityType.EntityBuilder.create(SmallFireballEntity.class, SmallFireballEntity::new)
	);
	public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register(
		"snow_golem", EntityType.EntityBuilder.create(SnowGolemEntity.class, SnowGolemEntity::new)
	);
	public static final EntityType<SnowballEntity> SNOWBALL = register("snowball", EntityType.EntityBuilder.create(SnowballEntity.class, SnowballEntity::new));
	public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register(
		"spectral_arrow", EntityType.EntityBuilder.create(SpectralArrowEntity.class, SpectralArrowEntity::new)
	);
	public static final EntityType<SpiderEntity> SPIDER = register("spider", EntityType.EntityBuilder.create(SpiderEntity.class, SpiderEntity::new));
	public static final EntityType<SquidEntity> SQUID = register("squid", EntityType.EntityBuilder.create(SquidEntity.class, SquidEntity::new));
	public static final EntityType<StrayEntity> STRAY = register("stray", EntityType.EntityBuilder.create(StrayEntity.class, StrayEntity::new));
	public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register(
		"tropical_fish", EntityType.EntityBuilder.create(TropicalFishEntity.class, TropicalFishEntity::new)
	);
	public static final EntityType<TurtleEntity> TURTLE = register("turtle", EntityType.EntityBuilder.create(TurtleEntity.class, TurtleEntity::new));
	public static final EntityType<EggEntity> EGG = register("egg", EntityType.EntityBuilder.create(EggEntity.class, EggEntity::new));
	public static final EntityType<EnderPearlEntity> ENDER_PEARL = register(
		"ender_pearl", EntityType.EntityBuilder.create(EnderPearlEntity.class, EnderPearlEntity::new)
	);
	public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = register(
		"experience_bottle", EntityType.EntityBuilder.create(ExperienceBottleEntity.class, ExperienceBottleEntity::new)
	);
	public static final EntityType<PotionEntity> POTION = register("potion", EntityType.EntityBuilder.create(PotionEntity.class, PotionEntity::new));
	public static final EntityType<VexEntity> VEX = register("vex", EntityType.EntityBuilder.create(VexEntity.class, VexEntity::new));
	public static final EntityType<VillagerEntity> VILLAGER = register("villager", EntityType.EntityBuilder.create(VillagerEntity.class, VillagerEntity::new));
	public static final EntityType<IronGolemEntity> IRON_GOLEM = register(
		"iron_golem", EntityType.EntityBuilder.create(IronGolemEntity.class, IronGolemEntity::new)
	);
	public static final EntityType<VindicationIllagerEntity> VINDICATOR = register(
		"vindicator", EntityType.EntityBuilder.create(VindicationIllagerEntity.class, VindicationIllagerEntity::new)
	);
	public static final EntityType<WitchEntity> WITCH = register("witch", EntityType.EntityBuilder.create(WitchEntity.class, WitchEntity::new));
	public static final EntityType<WitherEntity> WITHER = register("wither", EntityType.EntityBuilder.create(WitherEntity.class, WitherEntity::new));
	public static final EntityType<WhitherSkeletonEntity> WITHER_SKELETON = register(
		"wither_skeleton", EntityType.EntityBuilder.create(WhitherSkeletonEntity.class, WhitherSkeletonEntity::new)
	);
	public static final EntityType<WitherSkullEntity> WITHER_SKULL = register(
		"wither_skull", EntityType.EntityBuilder.create(WitherSkullEntity.class, WitherSkullEntity::new)
	);
	public static final EntityType<WolfEntity> WOLF = register("wolf", EntityType.EntityBuilder.create(WolfEntity.class, WolfEntity::new));
	public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", EntityType.EntityBuilder.create(ZombieEntity.class, ZombieEntity::new));
	public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register(
		"zombie_horse", EntityType.EntityBuilder.create(ZombieHorseEntity.class, ZombieHorseEntity::new)
	);
	public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register(
		"zombie_villager", EntityType.EntityBuilder.create(ZombieVillagerEntity.class, ZombieVillagerEntity::new)
	);
	public static final EntityType<PhantomEntity> PHANTOM = register("phantom", EntityType.EntityBuilder.create(PhantomEntity.class, PhantomEntity::new));
	public static final EntityType<LightningBoltEntity> LIGHTNING_BOLT = register(
		"lightning_bolt", EntityType.EntityBuilder.<LightningBoltEntity>create(LightningBoltEntity.class).dontSave()
	);
	public static final EntityType<PlayerEntity> PLAYER = register(
		"player", EntityType.EntityBuilder.<PlayerEntity>create(PlayerEntity.class).dontSave().dontSummon()
	);
	public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register(
		"fishing_bobber", EntityType.EntityBuilder.<FishingBobberEntity>create(FishingBobberEntity.class).dontSave().dontSummon()
	);
	public static final EntityType<TridentEntity> TRIDENT = register("trident", EntityType.EntityBuilder.create(TridentEntity.class, TridentEntity::new));
	private final Class<? extends T> entityClass;
	private final Function<? super World, ? extends T> entityFactory;
	private final boolean shouldSave;
	private final boolean shouldSummon;
	@Nullable
	private String translationKey;
	@Nullable
	private Text field_16756;
	@Nullable
	private final Type<?> field_16757;

	public static <T extends Entity> EntityType<T> register(String identifier, EntityType.EntityBuilder<T> builder) {
		EntityType<T> entityType = builder.create(identifier);
		Registry.ENTITY_TYPE.add(new Identifier(identifier), entityType);
		return entityType;
	}

	@Nullable
	public static Identifier getId(EntityType<?> entityType) {
		return Registry.ENTITY_TYPE.getId(entityType);
	}

	@Nullable
	public static EntityType<?> getById(String identifier) {
		return Registry.ENTITY_TYPE.getByIdentifier(Identifier.fromString(identifier));
	}

	public EntityType(Class<? extends T> class_, Function<? super World, ? extends T> function, boolean bl, boolean bl2, @Nullable Type<?> type) {
		this.entityClass = class_;
		this.entityFactory = function;
		this.shouldSave = bl;
		this.shouldSummon = bl2;
		this.field_16757 = type;
	}

	@Nullable
	public Entity method_15619(World world, @Nullable ItemStack itemStack, @Nullable PlayerEntity playerEntity, BlockPos blockPos, boolean bl, boolean bl2) {
		return this.method_15620(
			world,
			itemStack == null ? null : itemStack.getNbt(),
			itemStack != null && itemStack.hasCustomName() ? itemStack.getName() : null,
			playerEntity,
			blockPos,
			bl,
			bl2
		);
	}

	@Nullable
	public T method_15620(
		World world, @Nullable NbtCompound nbtCompound, @Nullable Text text, @Nullable PlayerEntity playerEntity, BlockPos blockPos, boolean bl, boolean bl2
	) {
		T entity = this.method_15627(world, nbtCompound, text, playerEntity, blockPos, bl, bl2);
		world.method_3686(entity);
		return entity;
	}

	@Nullable
	public T method_15627(
		World world, @Nullable NbtCompound nbtCompound, @Nullable Text text, @Nullable PlayerEntity playerEntity, BlockPos blockPos, boolean bl, boolean bl2
	) {
		T entity = this.spawn(world);
		if (entity == null) {
			return null;
		} else {
			double d;
			if (bl) {
				entity.updatePosition((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5);
				d = method_15622(world, blockPos, bl2, entity.getBoundingBox());
			} else {
				d = 0.0;
			}

			entity.refreshPositionAndAngles(
				(double)blockPos.getX() + 0.5, (double)blockPos.getY() + d, (double)blockPos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F
			);
			if (entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity)entity;
				mobEntity.headYaw = mobEntity.yaw;
				mobEntity.bodyYaw = mobEntity.yaw;
				mobEntity.initialize(world.method_8482(new BlockPos(mobEntity)), null, nbtCompound);
				mobEntity.playAmbientSound();
			}

			if (text != null && entity instanceof LivingEntity) {
				entity.method_15578(text);
			}

			method_15618(world, playerEntity, entity, nbtCompound);
			return entity;
		}
	}

	protected static double method_15622(RenderBlockView renderBlockView, BlockPos blockPos, boolean bl, Box box) {
		Box box2 = new Box(blockPos);
		if (bl) {
			box2 = box2.stretch(0.0, -1.0, 0.0);
		}

		Stream<VoxelShape> stream = renderBlockView.method_16384(null, box2);
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, stream, bl ? -2.0 : -1.0);
	}

	public static void method_15618(World world, @Nullable PlayerEntity playerEntity, @Nullable Entity entity, @Nullable NbtCompound nbtCompound) {
		if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isClient
					|| !entity.entityDataRequiresOperator()
					|| playerEntity != null && minecraftServer.getPlayerManager().isOperator(playerEntity.getGameProfile())) {
					NbtCompound nbtCompound2 = entity.toNbt(new NbtCompound());
					UUID uUID = entity.getUuid();
					nbtCompound2.putAll(nbtCompound.getCompound("EntityTag"));
					entity.setUuid(uUID);
					entity.fromNbt(nbtCompound2);
				}
			}
		}
	}

	public boolean method_15613() {
		return this.shouldSave;
	}

	public boolean method_15626() {
		return this.shouldSummon;
	}

	public Class<? extends T> entityClass() {
		return this.entityClass;
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("entity", Registry.ENTITY_TYPE.getId(this));
		}

		return this.translationKey;
	}

	public Text method_15630() {
		if (this.field_16756 == null) {
			this.field_16756 = new TranslatableText(this.getTranslationKey());
		}

		return this.field_16756;
	}

	@Nullable
	public T spawn(World world) {
		return (T)this.entityFactory.apply(world);
	}

	@Nullable
	public static Entity spawnById(World world, Identifier identifier) {
		return spawn(world, Registry.ENTITY_TYPE.getByIdentifier(identifier));
	}

	@Nullable
	public static Entity spawnByRawId(int rawId, World world) {
		return spawn(world, Registry.ENTITY_TYPE.getByRawId(rawId));
	}

	@Nullable
	public static Entity method_15623(NbtCompound nbtCompound, World world) {
		Identifier identifier = new Identifier(nbtCompound.getString("id"));
		Entity entity = spawnById(world, identifier);
		if (entity == null) {
			LOGGER.warn("Skipping Entity with id {}", identifier);
		} else {
			entity.fromNbt(nbtCompound);
		}

		return entity;
	}

	@Nullable
	private static Entity spawn(World world, @Nullable EntityType<?> entityType) {
		return entityType == null ? null : entityType.spawn(world);
	}

	public static class EntityBuilder<T extends Entity> {
		private final Class<? extends T> entityClass;
		private final Function<? super World, ? extends T> entityFactory;
		private boolean shouldSave = true;
		private boolean shouldSummon = true;

		private EntityBuilder(Class<? extends T> class_, Function<? super World, ? extends T> function) {
			this.entityClass = class_;
			this.entityFactory = function;
		}

		public static <T extends Entity> EntityType.EntityBuilder<T> create(Class<? extends T> entityClass, Function<? super World, ? extends T> entityFactory) {
			return new EntityType.EntityBuilder<>(entityClass, entityFactory);
		}

		public static <T extends Entity> EntityType.EntityBuilder<T> create(Class<? extends T> entityClass) {
			return new EntityType.EntityBuilder<>(entityClass, world -> null);
		}

		public EntityType.EntityBuilder<T> dontSummon() {
			this.shouldSummon = false;
			return this;
		}

		public EntityType.EntityBuilder<T> dontSave() {
			this.shouldSave = false;
			return this;
		}

		public EntityType<T> create(String identifier) {
			Type<?> type = null;
			if (this.shouldSave) {
				try {
					type = DataFixerFactory.method_21531().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(class_3402.field_16595, identifier);
				} catch (IllegalStateException var4) {
					if (SharedConstants.isDevelopment) {
						throw var4;
					}

					EntityType.LOGGER.warn("No data fixer registered for entity {}", identifier);
				}
			}

			return new EntityType<>(this.entityClass, this.entityFactory, this.shouldSave, this.shouldSummon, type);
		}
	}
}
