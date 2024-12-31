package net.minecraft.util.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class EffectsJson {
	public static final EffectsJson EMPTY = new EffectsJson(Collections.emptyMap());
	private final Map<StatusEffect, EffectsJson.EffectJson> field_15767;

	public EffectsJson(Map<StatusEffect, EffectsJson.EffectJson> map) {
		this.field_15767 = map;
	}

	public boolean method_14339(Entity entity) {
		if (this == EMPTY) {
			return true;
		} else {
			return entity instanceof LivingEntity ? this.method_14338(((LivingEntity)entity).getStatusEffects()) : false;
		}
	}

	public boolean method_14340(LivingEntity entity) {
		return this == EMPTY ? true : this.method_14338(entity.getStatusEffects());
	}

	public boolean method_14338(Map<StatusEffect, StatusEffectInstance> map) {
		if (this == EMPTY) {
			return true;
		} else {
			for (Entry<StatusEffect, EffectsJson.EffectJson> entry : this.field_15767.entrySet()) {
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)map.get(entry.getKey());
				if (!((EffectsJson.EffectJson)entry.getValue()).method_14342(statusEffectInstance)) {
					return false;
				}
			}

			return true;
		}
	}

	public static EffectsJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "effects");
			Map<StatusEffect, EffectsJson.EffectJson> map = Maps.newHashMap();

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				Identifier identifier = new Identifier((String)entry.getKey());
				StatusEffect statusEffect = StatusEffect.REGISTRY.get(identifier);
				if (statusEffect == null) {
					throw new JsonSyntaxException("Unknown effect '" + identifier + "'");
				}

				EffectsJson.EffectJson effectJson = EffectsJson.EffectJson.fromJson(JsonHelper.asObject((JsonElement)entry.getValue(), (String)entry.getKey()));
				map.put(statusEffect, effectJson);
			}

			return new EffectsJson(map);
		} else {
			return EMPTY;
		}
	}

	public static class EffectJson {
		private final MinMaxJson amplifier;
		private final MinMaxJson duration;
		@Nullable
		private final Boolean ambient;
		@Nullable
		private final Boolean visible;

		public EffectJson(MinMaxJson minMaxJson, MinMaxJson minMaxJson2, @Nullable Boolean boolean_, @Nullable Boolean boolean2) {
			this.amplifier = minMaxJson;
			this.duration = minMaxJson2;
			this.ambient = boolean_;
			this.visible = boolean2;
		}

		public boolean method_14342(@Nullable StatusEffectInstance instance) {
			if (instance == null) {
				return false;
			} else if (!this.amplifier.method_14335((float)instance.getAmplifier())) {
				return false;
			} else if (!this.duration.method_14335((float)instance.getDuration())) {
				return false;
			} else {
				return this.ambient != null && this.ambient != instance.isAmbient() ? false : this.visible == null || this.visible == instance.shouldShowParticles();
			}
		}

		public static EffectsJson.EffectJson fromJson(JsonObject object) {
			MinMaxJson minMaxJson = MinMaxJson.fromJson(object.get("amplifier"));
			MinMaxJson minMaxJson2 = MinMaxJson.fromJson(object.get("duration"));
			Boolean boolean_ = object.has("ambient") ? JsonHelper.getBoolean(object, "ambient") : null;
			Boolean boolean2 = object.has("visible") ? JsonHelper.getBoolean(object, "visible") : null;
			return new EffectsJson.EffectJson(minMaxJson, minMaxJson2, boolean_, boolean2);
		}
	}
}
