package net.minecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> {
	public static final MemoryModuleType<Void> DUMMY = register("dummy");
	public static final MemoryModuleType<GlobalPos> HOME = register("home", GlobalPos.CODEC);
	public static final MemoryModuleType<GlobalPos> JOB_SITE = register("job_site", GlobalPos.CODEC);
	public static final MemoryModuleType<GlobalPos> POTENTIAL_JOB_SITE = register("potential_job_site", GlobalPos.CODEC);
	public static final MemoryModuleType<GlobalPos> MEETING_POINT = register("meeting_point", GlobalPos.CODEC);
	public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = register("secondary_job_site");
	public static final MemoryModuleType<List<LivingEntity>> MOBS = register("mobs");
	public static final MemoryModuleType<List<LivingEntity>> VISIBLE_MOBS = register("visible_mobs");
	public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = register("visible_villager_babies");
	public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = register("nearest_players");
	public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = register("nearest_visible_player");
	public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_TARGETABLE_PLAYER = register("nearest_visible_targetable_player");
	public static final MemoryModuleType<WalkTarget> WALK_TARGET = register("walk_target");
	public static final MemoryModuleType<LookTarget> LOOK_TARGET = register("look_target");
	public static final MemoryModuleType<LivingEntity> ATTACK_TARGET = register("attack_target");
	public static final MemoryModuleType<Boolean> ATTACK_COOLING_DOWN = register("attack_cooling_down");
	public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = register("interaction_target");
	public static final MemoryModuleType<PassiveEntity> BREED_TARGET = register("breed_target");
	public static final MemoryModuleType<Entity> RIDE_TARGET = register("ride_target");
	public static final MemoryModuleType<Path> PATH = register("path");
	public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = register("interactable_doors");
	public static final MemoryModuleType<Set<GlobalPos>> DOORS_TO_CLOSE = register("doors_to_close");
	public static final MemoryModuleType<BlockPos> NEAREST_BED = register("nearest_bed");
	public static final MemoryModuleType<DamageSource> HURT_BY = register("hurt_by");
	public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = register("hurt_by_entity");
	public static final MemoryModuleType<LivingEntity> AVOID_TARGET = register("avoid_target");
	public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = register("nearest_hostile");
	public static final MemoryModuleType<LivingEntity> NEAREST_ATTACKABLE = register("nearest_attackable");
	public static final MemoryModuleType<GlobalPos> HIDING_PLACE = register("hiding_place");
	public static final MemoryModuleType<Long> HEARD_BELL_TIME = register("heard_bell_time");
	public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = register("cant_reach_walk_target_since");
	public static final MemoryModuleType<Boolean> GOLEM_DETECTED_RECENTLY = register("golem_detected_recently", Codec.BOOL);
	public static final MemoryModuleType<Long> LAST_SLEPT = register("last_slept", Codec.LONG);
	public static final MemoryModuleType<Long> LAST_WOKEN = register("last_woken", Codec.LONG);
	public static final MemoryModuleType<Long> LAST_WORKED_AT_POI = register("last_worked_at_poi", Codec.LONG);
	public static final MemoryModuleType<PassiveEntity> NEAREST_VISIBLE_ADULT = register("nearest_visible_adult");
	public static final MemoryModuleType<ItemEntity> NEAREST_VISIBLE_WANTED_ITEM = register("nearest_visible_wanted_item");
	public static final MemoryModuleType<MobEntity> NEAREST_VISIBLE_NEMESIS = register("nearest_visible_nemesis");
	public static final MemoryModuleType<Integer> PLAY_DEAD_TICKS = register("play_dead_ticks", Codec.INT);
	public static final MemoryModuleType<PlayerEntity> TEMPTING_PLAYER = register("tempting_player");
	public static final MemoryModuleType<Integer> TEMPTATION_COOLDOWN_TICKS = register("temptation_cooldown_ticks", Codec.INT);
	public static final MemoryModuleType<Boolean> IS_TEMPTED = register("is_tempted", Codec.BOOL);
	public static final MemoryModuleType<Integer> LONG_JUMP_COOLING_DOWN = register("long_jump_cooling_down", Codec.INT);
	public static final MemoryModuleType<Boolean> LONG_JUMP_MID_JUMP = register("long_jump_mid_jump");
	public static final MemoryModuleType<Boolean> HAS_HUNTING_COOLDOWN = register("has_hunting_cooldown", Codec.BOOL);
	public static final MemoryModuleType<Integer> RAM_COOLDOWN_TICKS = register("ram_cooldown_ticks", Codec.INT);
	public static final MemoryModuleType<Vec3d> RAM_TARGET = register("ram_target");
	public static final MemoryModuleType<UUID> ANGRY_AT = register("angry_at", DynamicSerializableUuid.CODEC);
	public static final MemoryModuleType<Boolean> UNIVERSAL_ANGER = register("universal_anger", Codec.BOOL);
	public static final MemoryModuleType<Boolean> ADMIRING_ITEM = register("admiring_item", Codec.BOOL);
	public static final MemoryModuleType<Integer> TIME_TRYING_TO_REACH_ADMIRE_ITEM = register("time_trying_to_reach_admire_item");
	public static final MemoryModuleType<Boolean> DISABLE_WALK_TO_ADMIRE_ITEM = register("disable_walk_to_admire_item");
	public static final MemoryModuleType<Boolean> ADMIRING_DISABLED = register("admiring_disabled", Codec.BOOL);
	public static final MemoryModuleType<Boolean> HUNTED_RECENTLY = register("hunted_recently", Codec.BOOL);
	public static final MemoryModuleType<BlockPos> CELEBRATE_LOCATION = register("celebrate_location");
	public static final MemoryModuleType<Boolean> DANCING = register("dancing");
	public static final MemoryModuleType<HoglinEntity> NEAREST_VISIBLE_HUNTABLE_HOGLIN = register("nearest_visible_huntable_hoglin");
	public static final MemoryModuleType<HoglinEntity> NEAREST_VISIBLE_BABY_HOGLIN = register("nearest_visible_baby_hoglin");
	public static final MemoryModuleType<PlayerEntity> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD = register("nearest_targetable_player_not_wearing_gold");
	public static final MemoryModuleType<List<AbstractPiglinEntity>> NEARBY_ADULT_PIGLINS = register("nearby_adult_piglins");
	public static final MemoryModuleType<List<AbstractPiglinEntity>> NEAREST_VISIBLE_ADULT_PIGLINS = register("nearest_visible_adult_piglins");
	public static final MemoryModuleType<List<HoglinEntity>> NEAREST_VISIBLE_ADULT_HOGLINS = register("nearest_visible_adult_hoglins");
	public static final MemoryModuleType<AbstractPiglinEntity> NEAREST_VISIBLE_ADULT_PIGLIN = register("nearest_visible_adult_piglin");
	public static final MemoryModuleType<LivingEntity> NEAREST_VISIBLE_ZOMBIFIED = register("nearest_visible_zombified");
	public static final MemoryModuleType<Integer> VISIBLE_ADULT_PIGLIN_COUNT = register("visible_adult_piglin_count");
	public static final MemoryModuleType<Integer> VISIBLE_ADULT_HOGLIN_COUNT = register("visible_adult_hoglin_count");
	public static final MemoryModuleType<PlayerEntity> NEAREST_PLAYER_HOLDING_WANTED_ITEM = register("nearest_player_holding_wanted_item");
	public static final MemoryModuleType<Boolean> ATE_RECENTLY = register("ate_recently");
	public static final MemoryModuleType<BlockPos> NEAREST_REPELLENT = register("nearest_repellent");
	public static final MemoryModuleType<Boolean> PACIFIED = register("pacified");
	private final Optional<Codec<Memory<U>>> codec;

	private MemoryModuleType(Optional<Codec<U>> codec) {
		this.codec = codec.map(Memory::createCodec);
	}

	public String toString() {
		return Registry.MEMORY_MODULE_TYPE.getId(this).toString();
	}

	public Optional<Codec<Memory<U>>> getCodec() {
		return this.codec;
	}

	private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<>(Optional.of(codec)));
	}

	private static <U> MemoryModuleType<U> register(String id) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(id), new MemoryModuleType<>(Optional.empty()));
	}
}
