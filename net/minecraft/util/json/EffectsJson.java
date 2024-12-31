package net.minecraft.util.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_3638;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class EffectsJson {
	public static final EffectsJson EMPTY = new EffectsJson(Collections.emptyMap());
	private final Map<StatusEffect, EffectsJson.EffectJson> field_15767;

	public EffectsJson(Map<StatusEffect, EffectsJson.EffectJson> map) {
		this.field_15767 = map;
	}

	public static EffectsJson method_16537() {
		return new EffectsJson(Maps.newHashMap());
	}

	public EffectsJson method_16538(StatusEffect statusEffect) {
		this.field_15767.put(statusEffect, new EffectsJson.EffectJson());
		return this;
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
				StatusEffect statusEffect = Registry.MOB_EFFECT.getByIdentifier(identifier);
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

	public JsonElement method_16539() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();

			for (Entry<StatusEffect, EffectsJson.EffectJson> entry : this.field_15767.entrySet()) {
				jsonObject.add(Registry.MOB_EFFECT.getId((StatusEffect)entry.getKey()).toString(), ((EffectsJson.EffectJson)entry.getValue()).method_16540());
			}

			return jsonObject;
		}
	}

	public static class EffectJson {
		private final class_3638.class_3642 field_15768;
		private final class_3638.class_3642 field_15769;
		@Nullable
		private final Boolean ambient;
		@Nullable
		private final Boolean visible;

		public EffectJson(class_3638.class_3642 arg, class_3638.class_3642 arg2, @Nullable Boolean boolean_, @Nullable Boolean boolean2) {
			this.field_15768 = arg;
			this.field_15769 = arg2;
			this.ambient = boolean_;
			this.visible = boolean2;
		}

		public EffectJson() {
			this(class_3638.class_3642.field_17698, class_3638.class_3642.field_17698, null, null);
		}

		public boolean method_14342(@Nullable StatusEffectInstance instance) {
			if (instance == null) {
				return false;
			} else if (!this.field_15768.method_16531(instance.getAmplifier())) {
				return false;
			} else if (!this.field_15769.method_16531(instance.getDuration())) {
				return false;
			} else {
				return this.ambient != null && this.ambient != instance.isAmbient() ? false : this.visible == null || this.visible == instance.shouldShowParticles();
			}
		}

		public JsonElement method_16540() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("amplifier", this.field_15768.method_16513());
			jsonObject.add("duration", this.field_15769.method_16513());
			jsonObject.addProperty("ambient", this.ambient);
			jsonObject.addProperty("visible", this.visible);
			return jsonObject;
		}

		public static EffectsJson.EffectJson fromJson(JsonObject object) {
			class_3638.class_3642 lv = class_3638.class_3642.method_16524(object.get("amplifier"));
			class_3638.class_3642 lv2 = class_3638.class_3642.method_16524(object.get("duration"));
			Boolean boolean_ = object.has("ambient") ? JsonHelper.getBoolean(object, "ambient") : null;
			Boolean boolean2 = object.has("visible") ? JsonHelper.getBoolean(object, "visible") : null;
			return new EffectsJson.EffectJson(lv, lv2, boolean_, boolean2);
		}
	}
}
