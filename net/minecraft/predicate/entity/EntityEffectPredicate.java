package net.minecraft.predicate.entity;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class EntityEffectPredicate {
	public static final EntityEffectPredicate EMPTY = new EntityEffectPredicate(Collections.emptyMap());
	private final Map<StatusEffect, EntityEffectPredicate.EffectData> effects;

	public EntityEffectPredicate(Map<StatusEffect, EntityEffectPredicate.EffectData> effects) {
		this.effects = effects;
	}

	public static EntityEffectPredicate create() {
		return new EntityEffectPredicate(Maps.newLinkedHashMap());
	}

	public EntityEffectPredicate withEffect(StatusEffect statusEffect) {
		this.effects.put(statusEffect, new EntityEffectPredicate.EffectData());
		return this;
	}

	public EntityEffectPredicate withEffect(StatusEffect statusEffect, EntityEffectPredicate.EffectData data) {
		this.effects.put(statusEffect, data);
		return this;
	}

	public boolean test(Entity entity) {
		if (this == EMPTY) {
			return true;
		} else {
			return entity instanceof LivingEntity ? this.test(((LivingEntity)entity).getActiveStatusEffects()) : false;
		}
	}

	public boolean test(LivingEntity livingEntity) {
		return this == EMPTY ? true : this.test(livingEntity.getActiveStatusEffects());
	}

	public boolean test(Map<StatusEffect, StatusEffectInstance> effects) {
		if (this == EMPTY) {
			return true;
		} else {
			for (Entry<StatusEffect, EntityEffectPredicate.EffectData> entry : this.effects.entrySet()) {
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)effects.get(entry.getKey());
				if (!((EntityEffectPredicate.EffectData)entry.getValue()).test(statusEffectInstance)) {
					return false;
				}
			}

			return true;
		}
	}

	public static EntityEffectPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "effects");
			Map<StatusEffect, EntityEffectPredicate.EffectData> map = Maps.newLinkedHashMap();

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				Identifier identifier = new Identifier((String)entry.getKey());
				StatusEffect statusEffect = (StatusEffect)Registry.STATUS_EFFECT
					.getOrEmpty(identifier)
					.orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + identifier + "'"));
				EntityEffectPredicate.EffectData effectData = EntityEffectPredicate.EffectData.fromJson(
					JsonHelper.asObject((JsonElement)entry.getValue(), (String)entry.getKey())
				);
				map.put(statusEffect, effectData);
			}

			return new EntityEffectPredicate(map);
		} else {
			return EMPTY;
		}
	}

	public JsonElement toJson() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();

			for (Entry<StatusEffect, EntityEffectPredicate.EffectData> entry : this.effects.entrySet()) {
				jsonObject.add(Registry.STATUS_EFFECT.getId((StatusEffect)entry.getKey()).toString(), ((EntityEffectPredicate.EffectData)entry.getValue()).toJson());
			}

			return jsonObject;
		}
	}

	public static class EffectData {
		private final NumberRange.IntRange amplifier;
		private final NumberRange.IntRange duration;
		@Nullable
		private final Boolean ambient;
		@Nullable
		private final Boolean visible;

		public EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, @Nullable Boolean ambient, @Nullable Boolean visible) {
			this.amplifier = amplifier;
			this.duration = duration;
			this.ambient = ambient;
			this.visible = visible;
		}

		public EffectData() {
			this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, null, null);
		}

		public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
			if (statusEffectInstance == null) {
				return false;
			} else if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
				return false;
			} else if (!this.duration.test(statusEffectInstance.getDuration())) {
				return false;
			} else {
				return this.ambient != null && this.ambient != statusEffectInstance.isAmbient()
					? false
					: this.visible == null || this.visible == statusEffectInstance.shouldShowParticles();
			}
		}

		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("amplifier", this.amplifier.toJson());
			jsonObject.add("duration", this.duration.toJson());
			jsonObject.addProperty("ambient", this.ambient);
			jsonObject.addProperty("visible", this.visible);
			return jsonObject;
		}

		public static EntityEffectPredicate.EffectData fromJson(JsonObject json) {
			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(json.get("amplifier"));
			NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(json.get("duration"));
			Boolean boolean_ = json.has("ambient") ? JsonHelper.getBoolean(json, "ambient") : null;
			Boolean boolean2 = json.has("visible") ? JsonHelper.getBoolean(json, "visible") : null;
			return new EntityEffectPredicate.EffectData(intRange, intRange2, boolean_, boolean2);
		}
	}
}
