package net.minecraft.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.EntityJson;

public class class_3363 implements Criterion<class_3363.class_3365> {
	private static final Identifier field_16505 = new Identifier("bred_animals");
	private final Map<AdvancementFile, class_3363.class_3364> field_16506 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_16505;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3363.class_3365> arg) {
		class_3363.class_3364 lv = (class_3363.class_3364)this.field_16506.get(file);
		if (lv == null) {
			lv = new class_3363.class_3364(file);
			this.field_16506.put(file, lv);
		}

		lv.method_15046(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3363.class_3365> arg) {
		class_3363.class_3364 lv = (class_3363.class_3364)this.field_16506.get(file);
		if (lv != null) {
			lv.method_15047(arg);
			if (lv.method_15044()) {
				this.field_16506.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_16506.remove(file);
	}

	public class_3363.class_3365 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityJson entityJson = EntityJson.fromJson(jsonObject.get("parent"));
		EntityJson entityJson2 = EntityJson.fromJson(jsonObject.get("partner"));
		EntityJson entityJson3 = EntityJson.fromJson(jsonObject.get("child"));
		return new class_3363.class_3365(entityJson, entityJson2, entityJson3);
	}

	public void method_15041(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, PassiveEntity passiveEntity) {
		class_3363.class_3364 lv = (class_3363.class_3364)this.field_16506.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15045(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity);
		}
	}

	static class class_3364 {
		private final AdvancementFile field_16507;
		private final Set<Criterion.class_3353<class_3363.class_3365>> field_16508 = Sets.newHashSet();

		public class_3364(AdvancementFile advancementFile) {
			this.field_16507 = advancementFile;
		}

		public boolean method_15044() {
			return this.field_16508.isEmpty();
		}

		public void method_15046(Criterion.class_3353<class_3363.class_3365> arg) {
			this.field_16508.add(arg);
		}

		public void method_15047(Criterion.class_3353<class_3363.class_3365> arg) {
			this.field_16508.remove(arg);
		}

		public void method_15045(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, PassiveEntity passiveEntity) {
			List<Criterion.class_3353<class_3363.class_3365>> list = null;

			for (Criterion.class_3353<class_3363.class_3365> lv : this.field_16508) {
				if (lv.method_14975().method_15048(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3363.class_3365> lv2 : list) {
					lv2.method_14976(this.field_16507);
				}
			}
		}
	}

	public static class class_3365 extends AbstractCriterionInstance {
		private final EntityJson field_16509;
		private final EntityJson field_16510;
		private final EntityJson field_16511;

		public class_3365(EntityJson entityJson, EntityJson entityJson2, EntityJson entityJson3) {
			super(class_3363.field_16505);
			this.field_16509 = entityJson;
			this.field_16510 = entityJson2;
			this.field_16511 = entityJson3;
		}

		public boolean method_15048(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, PassiveEntity passiveEntity) {
			return !this.field_16511.method_14237(serverPlayerEntity, passiveEntity)
				? false
				: this.field_16509.method_14237(serverPlayerEntity, animalEntity) && this.field_16510.method_14237(serverPlayerEntity, animalEntity2)
					|| this.field_16509.method_14237(serverPlayerEntity, animalEntity2) && this.field_16510.method_14237(serverPlayerEntity, animalEntity);
		}
	}
}
