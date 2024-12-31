package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.EffectsJson;

public class class_3171 implements Criterion<class_3171.class_3476> {
	private static final Identifier field_15619 = new Identifier("effects_changed");
	private final Map<AdvancementFile, class_3171.class_3172> field_15620 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15619;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3171.class_3476> arg) {
		class_3171.class_3172 lv = (class_3171.class_3172)this.field_15620.get(file);
		if (lv == null) {
			lv = new class_3171.class_3172(file);
			this.field_15620.put(file, lv);
		}

		lv.method_14145(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3171.class_3476> arg) {
		class_3171.class_3172 lv = (class_3171.class_3172)this.field_15620.get(file);
		if (lv != null) {
			lv.method_14146(arg);
			if (lv.method_14143()) {
				this.field_15620.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15620.remove(file);
	}

	public class_3171.class_3476 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EffectsJson effectsJson = EffectsJson.fromJson(jsonObject.get("effects"));
		return new class_3171.class_3476(effectsJson);
	}

	public void method_14140(ServerPlayerEntity serverPlayerEntity) {
		class_3171.class_3172 lv = (class_3171.class_3172)this.field_15620.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14144(serverPlayerEntity);
		}
	}

	static class class_3172 {
		private final AdvancementFile field_15621;
		private final Set<Criterion.class_3353<class_3171.class_3476>> field_15622 = Sets.newHashSet();

		public class_3172(AdvancementFile advancementFile) {
			this.field_15621 = advancementFile;
		}

		public boolean method_14143() {
			return this.field_15622.isEmpty();
		}

		public void method_14145(Criterion.class_3353<class_3171.class_3476> arg) {
			this.field_15622.add(arg);
		}

		public void method_14146(Criterion.class_3353<class_3171.class_3476> arg) {
			this.field_15622.remove(arg);
		}

		public void method_14144(ServerPlayerEntity serverPlayerEntity) {
			List<Criterion.class_3353<class_3171.class_3476>> list = null;

			for (Criterion.class_3353<class_3171.class_3476> lv : this.field_15622) {
				if (lv.method_14975().method_15714(serverPlayerEntity)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3171.class_3476> lv2 : list) {
					lv2.method_14976(this.field_15621);
				}
			}
		}
	}

	public static class class_3476 extends AbstractCriterionInstance {
		private final EffectsJson field_16892;

		public class_3476(EffectsJson effectsJson) {
			super(class_3171.field_15619);
			this.field_16892 = effectsJson;
		}

		public static class_3171.class_3476 method_15713(EffectsJson effectsJson) {
			return new class_3171.class_3476(effectsJson);
		}

		public boolean method_15714(ServerPlayerEntity serverPlayerEntity) {
			return this.field_16892.method_14340(serverPlayerEntity);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("effects", this.field_16892.method_16539());
			return jsonObject;
		}
	}
}
