package net.minecraft;

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
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.EntityJson;

public class class_3201 implements Criterion<class_3201.class_3203> {
	private final Map<AdvancementFile, class_3201.class_3202> field_15718 = Maps.newHashMap();
	private final Identifier field_15719;

	public class_3201(Identifier identifier) {
		this.field_15719 = identifier;
	}

	@Override
	public Identifier getIdentifier() {
		return this.field_15719;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3201.class_3203> arg) {
		class_3201.class_3202 lv = (class_3201.class_3202)this.field_15718.get(file);
		if (lv == null) {
			lv = new class_3201.class_3202(file);
			this.field_15718.put(file, lv);
		}

		lv.method_14301(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3201.class_3203> arg) {
		class_3201.class_3202 lv = (class_3201.class_3202)this.field_15718.get(file);
		if (lv != null) {
			lv.method_14302(arg);
			if (lv.method_14299()) {
				this.field_15718.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15718.remove(file);
	}

	public class_3201.class_3203 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_3201.class_3203(this.field_15719, EntityJson.fromJson(jsonObject.get("entity")), class_3161.method_14118(jsonObject.get("killing_blow")));
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
		class_3201.class_3202 lv = (class_3201.class_3202)this.field_15718.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14300(serverPlayerEntity, entity, damageSource);
		}
	}

	static class class_3202 {
		private final AdvancementFile field_15720;
		private final Set<Criterion.class_3353<class_3201.class_3203>> field_15721 = Sets.newHashSet();

		public class_3202(AdvancementFile advancementFile) {
			this.field_15720 = advancementFile;
		}

		public boolean method_14299() {
			return this.field_15721.isEmpty();
		}

		public void method_14301(Criterion.class_3353<class_3201.class_3203> arg) {
			this.field_15721.add(arg);
		}

		public void method_14302(Criterion.class_3353<class_3201.class_3203> arg) {
			this.field_15721.remove(arg);
		}

		public void method_14300(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			List<Criterion.class_3353<class_3201.class_3203>> list = null;

			for (Criterion.class_3353<class_3201.class_3203> lv : this.field_15721) {
				if (lv.method_14975().method_14303(serverPlayerEntity, entity, damageSource)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3201.class_3203> lv2 : list) {
					lv2.method_14976(this.field_15720);
				}
			}
		}
	}

	public static class class_3203 extends AbstractCriterionInstance {
		private final EntityJson field_15722;
		private final class_3161 field_15723;

		public class_3203(Identifier identifier, EntityJson entityJson, class_3161 arg) {
			super(identifier);
			this.field_15722 = entityJson;
			this.field_15723 = arg;
		}

		public boolean method_14303(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			return !this.field_15723.method_14120(serverPlayerEntity, damageSource) ? false : this.field_15722.method_14237(serverPlayerEntity, entity);
		}
	}
}
