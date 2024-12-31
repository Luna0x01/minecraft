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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3232 implements Criterion<class_3232.class_3718> {
	private static final Identifier field_15813 = new Identifier("summoned_entity");
	private final Map<AdvancementFile, class_3232.class_3233> field_15814 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15813;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3232.class_3718> arg) {
		class_3232.class_3233 lv = (class_3232.class_3233)this.field_15814.get(file);
		if (lv == null) {
			lv = new class_3232.class_3233(file);
			this.field_15814.put(file, lv);
		}

		lv.method_14402(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3232.class_3718> arg) {
		class_3232.class_3233 lv = (class_3232.class_3233)this.field_15814.get(file);
		if (lv != null) {
			lv.method_14403(arg);
			if (lv.method_14400()) {
				this.field_15814.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15814.remove(file);
	}

	public class_3232.class_3718 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3528 lv = class_3528.method_15905(jsonObject.get("entity"));
		return new class_3232.class_3718(lv);
	}

	public void method_14397(ServerPlayerEntity serverPlayerEntity, Entity entity) {
		class_3232.class_3233 lv = (class_3232.class_3233)this.field_15814.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14401(serverPlayerEntity, entity);
		}
	}

	static class class_3233 {
		private final AdvancementFile field_15815;
		private final Set<Criterion.class_3353<class_3232.class_3718>> field_15816 = Sets.newHashSet();

		public class_3233(AdvancementFile advancementFile) {
			this.field_15815 = advancementFile;
		}

		public boolean method_14400() {
			return this.field_15816.isEmpty();
		}

		public void method_14402(Criterion.class_3353<class_3232.class_3718> arg) {
			this.field_15816.add(arg);
		}

		public void method_14403(Criterion.class_3353<class_3232.class_3718> arg) {
			this.field_15816.remove(arg);
		}

		public void method_14401(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			List<Criterion.class_3353<class_3232.class_3718>> list = null;

			for (Criterion.class_3353<class_3232.class_3718> lv : this.field_15816) {
				if (lv.method_14975().method_16733(serverPlayerEntity, entity)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3232.class_3718> lv2 : list) {
					lv2.method_14976(this.field_15815);
				}
			}
		}
	}

	public static class class_3718 extends AbstractCriterionInstance {
		private final class_3528 field_18459;

		public class_3718(class_3528 arg) {
			super(class_3232.field_15813);
			this.field_18459 = arg;
		}

		public static class_3232.class_3718 method_16732(class_3528.class_3529 arg) {
			return new class_3232.class_3718(arg.method_15916());
		}

		public boolean method_16733(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			return this.field_18459.method_15906(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.field_18459.method_15904());
			return jsonObject;
		}
	}
}
