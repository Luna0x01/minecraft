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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3226 implements Criterion<class_3226.class_3707> {
	private static final Identifier field_15802 = new Identifier("player_hurt_entity");
	private final Map<AdvancementFile, class_3226.class_3227> field_15803 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15802;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3226.class_3707> arg) {
		class_3226.class_3227 lv = (class_3226.class_3227)this.field_15803.get(file);
		if (lv == null) {
			lv = new class_3226.class_3227(file);
			this.field_15803.put(file, lv);
		}

		lv.method_14384(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3226.class_3707> arg) {
		class_3226.class_3227 lv = (class_3226.class_3227)this.field_15803.get(file);
		if (lv != null) {
			lv.method_14385(arg);
			if (lv.method_14382()) {
				this.field_15803.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15803.remove(file);
	}

	public class_3226.class_3707 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3160 lv = class_3160.method_14116(jsonObject.get("damage"));
		class_3528 lv2 = class_3528.method_15905(jsonObject.get("entity"));
		return new class_3226.class_3707(lv, lv2);
	}

	public void method_14379(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource, float f, float g, boolean bl) {
		class_3226.class_3227 lv = (class_3226.class_3227)this.field_15803.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14383(serverPlayerEntity, entity, damageSource, f, g, bl);
		}
	}

	static class class_3227 {
		private final AdvancementFile field_15804;
		private final Set<Criterion.class_3353<class_3226.class_3707>> field_15805 = Sets.newHashSet();

		public class_3227(AdvancementFile advancementFile) {
			this.field_15804 = advancementFile;
		}

		public boolean method_14382() {
			return this.field_15805.isEmpty();
		}

		public void method_14384(Criterion.class_3353<class_3226.class_3707> arg) {
			this.field_15805.add(arg);
		}

		public void method_14385(Criterion.class_3353<class_3226.class_3707> arg) {
			this.field_15805.remove(arg);
		}

		public void method_14383(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource, float f, float g, boolean bl) {
			List<Criterion.class_3353<class_3226.class_3707>> list = null;

			for (Criterion.class_3353<class_3226.class_3707> lv : this.field_15805) {
				if (lv.method_14975().method_16684(serverPlayerEntity, entity, damageSource, f, g, bl)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3226.class_3707> lv2 : list) {
					lv2.method_14976(this.field_15804);
				}
			}
		}
	}

	public static class class_3707 extends AbstractCriterionInstance {
		private final class_3160 field_18349;
		private final class_3528 field_18350;

		public class_3707(class_3160 arg, class_3528 arg2) {
			super(class_3226.field_15802);
			this.field_18349 = arg;
			this.field_18350 = arg2;
		}

		public static class_3226.class_3707 method_16683(class_3160.class_3466 arg) {
			return new class_3226.class_3707(arg.method_15672(), class_3528.field_17075);
		}

		public boolean method_16684(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource, float f, float g, boolean bl) {
			return !this.field_18349.method_14117(serverPlayerEntity, damageSource, f, g, bl) ? false : this.field_18350.method_15906(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("damage", this.field_18349.method_15668());
			jsonObject.add("entity", this.field_18350.method_15904());
			return jsonObject;
		}
	}
}
