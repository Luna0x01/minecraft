package net.minecraft.advancement;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3171;
import net.minecraft.class_3177;
import net.minecraft.class_3181;
import net.minecraft.class_3184;
import net.minecraft.class_3189;
import net.minecraft.class_3194;
import net.minecraft.class_3197;
import net.minecraft.class_3201;
import net.minecraft.class_3204;
import net.minecraft.class_3210;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3226;
import net.minecraft.class_3229;
import net.minecraft.class_3232;
import net.minecraft.class_3235;
import net.minecraft.class_3238;
import net.minecraft.class_3241;
import net.minecraft.class_3244;
import net.minecraft.class_3247;
import net.minecraft.class_3430;
import net.minecraft.class_3533;
import net.minecraft.class_3539;
import net.minecraft.achievement.class_3363;
import net.minecraft.achievement.class_3366;
import net.minecraft.achievement.class_3370;
import net.minecraft.achievement.class_3376;
import net.minecraft.achievement.class_3380;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionInstance;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.util.Identifier;

public class AchievementsAndCriterions {
	private static final Map<Identifier, Criterion<?>> CRITERIONS = Maps.newHashMap();
	public static final class_3189 field_16329 = register(new class_3189());
	public static final class_3201 PLAYER_KILLED_ENTITY = register(new class_3201(new Identifier("player_killed_entity")));
	public static final class_3201 ENTITY_KILLED_PLAYER = register(new class_3201(new Identifier("entity_killed_player")));
	public static final class_3181 field_16332 = register(new class_3181());
	public static final class_3194 field_16333 = register(new class_3194());
	public static final class_3229 field_16334 = register(new class_3229());
	public static final class_3226 field_16335 = register(new class_3226());
	public static final class_3184 field_16336 = register(new class_3184());
	public static final class_3177 field_16337 = register(new class_3177());
	public static final class_3533 field_21658 = register(new class_3533());
	public static final class_3366 field_16338 = register(new class_3366());
	public static final class_3376 field_16339 = register(new class_3376());
	public static final class_3244 field_16340 = register(new class_3244());
	public static final class_3232 field_16341 = register(new class_3232());
	public static final class_3363 field_16342 = register(new class_3363());
	public static final class_3210 LOCATION = register(new class_3210(new Identifier("location")));
	public static final class_3210 SLEPT_IN_BED = register(new class_3210(new Identifier("slept_in_bed")));
	public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = register(new CuredZombieVillagerCriterion());
	public static final class_3241 field_16346 = register(new class_3241());
	public static final class_3197 field_16347 = register(new class_3197());
	public static final class_3204 field_16348 = register(new class_3204());
	public static final class_3370 field_16349 = register(new class_3370());
	public static final class_3238 field_16350 = register(new class_3238());
	public static final class_3235 field_16351 = register(new class_3235());
	public static final class_3222 field_16352 = register(new class_3222());
	public static final class_3380 field_16353 = register(new class_3380());
	public static final class_3171 field_16354 = register(new class_3171());
	public static final class_3247 field_16326 = register(new class_3247());
	public static final class_3218 field_16327 = register(new class_3218());
	public static final class_3539 field_21656 = register(new class_3539());
	public static final class_3430 field_21657 = register(new class_3430());

	private static <T extends Criterion<?>> T register(T criterion) {
		if (CRITERIONS.containsKey(criterion.getIdentifier())) {
			throw new IllegalArgumentException("Duplicate criterion id " + criterion.getIdentifier());
		} else {
			CRITERIONS.put(criterion.getIdentifier(), criterion);
			return criterion;
		}
	}

	@Nullable
	public static <T extends CriterionInstance> Criterion<T> getInstance(Identifier id) {
		return (Criterion<T>)CRITERIONS.get(id);
	}

	public static Iterable<? extends Criterion<?>> getCriterions() {
		return CRITERIONS.values();
	}
}
