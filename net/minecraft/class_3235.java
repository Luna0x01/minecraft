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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3235 implements Criterion<class_3235.class_3727> {
	private static final Identifier field_15818 = new Identifier("tame_animal");
	private final Map<AdvancementFile, class_3235.class_3236> field_15819 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15818;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3235.class_3727> arg) {
		class_3235.class_3236 lv = (class_3235.class_3236)this.field_15819.get(file);
		if (lv == null) {
			lv = new class_3235.class_3236(file);
			this.field_15819.put(file, lv);
		}

		lv.method_14410(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3235.class_3727> arg) {
		class_3235.class_3236 lv = (class_3235.class_3236)this.field_15819.get(file);
		if (lv != null) {
			lv.method_14411(arg);
			if (lv.method_14408()) {
				this.field_15819.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15819.remove(file);
	}

	public class_3235.class_3727 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3528 lv = class_3528.method_15905(jsonObject.get("entity"));
		return new class_3235.class_3727(lv);
	}

	public void method_14405(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
		class_3235.class_3236 lv = (class_3235.class_3236)this.field_15819.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14409(serverPlayerEntity, animalEntity);
		}
	}

	static class class_3236 {
		private final AdvancementFile field_15820;
		private final Set<Criterion.class_3353<class_3235.class_3727>> field_15821 = Sets.newHashSet();

		public class_3236(AdvancementFile advancementFile) {
			this.field_15820 = advancementFile;
		}

		public boolean method_14408() {
			return this.field_15821.isEmpty();
		}

		public void method_14410(Criterion.class_3353<class_3235.class_3727> arg) {
			this.field_15821.add(arg);
		}

		public void method_14411(Criterion.class_3353<class_3235.class_3727> arg) {
			this.field_15821.remove(arg);
		}

		public void method_14409(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
			List<Criterion.class_3353<class_3235.class_3727>> list = null;

			for (Criterion.class_3353<class_3235.class_3727> lv : this.field_15821) {
				if (lv.method_14975().method_16747(serverPlayerEntity, animalEntity)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3235.class_3727> lv2 : list) {
					lv2.method_14976(this.field_15820);
				}
			}
		}
	}

	public static class class_3727 extends AbstractCriterionInstance {
		private final class_3528 field_18521;

		public class_3727(class_3528 arg) {
			super(class_3235.field_15818);
			this.field_18521 = arg;
		}

		public static class_3235.class_3727 method_16748() {
			return new class_3235.class_3727(class_3528.field_17075);
		}

		public boolean method_16747(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
			return this.field_18521.method_15906(serverPlayerEntity, animalEntity);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.field_18521.method_15904());
			return jsonObject;
		}
	}
}
