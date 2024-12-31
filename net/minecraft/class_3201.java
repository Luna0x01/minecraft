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
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3201 implements Criterion<class_3201.class_3586> {
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
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3201.class_3586> arg) {
		class_3201.class_3202 lv = (class_3201.class_3202)this.field_15718.get(file);
		if (lv == null) {
			lv = new class_3201.class_3202(file);
			this.field_15718.put(file, lv);
		}

		lv.method_14301(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3201.class_3586> arg) {
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

	public class_3201.class_3586 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_3201.class_3586(this.field_15719, class_3528.method_15905(jsonObject.get("entity")), class_3161.method_14118(jsonObject.get("killing_blow")));
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
		class_3201.class_3202 lv = (class_3201.class_3202)this.field_15718.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14300(serverPlayerEntity, entity, damageSource);
		}
	}

	static class class_3202 {
		private final AdvancementFile field_15720;
		private final Set<Criterion.class_3353<class_3201.class_3586>> field_15721 = Sets.newHashSet();

		public class_3202(AdvancementFile advancementFile) {
			this.field_15720 = advancementFile;
		}

		public boolean method_14299() {
			return this.field_15721.isEmpty();
		}

		public void method_14301(Criterion.class_3353<class_3201.class_3586> arg) {
			this.field_15721.add(arg);
		}

		public void method_14302(Criterion.class_3353<class_3201.class_3586> arg) {
			this.field_15721.remove(arg);
		}

		public void method_14300(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			List<Criterion.class_3353<class_3201.class_3586>> list = null;

			for (Criterion.class_3353<class_3201.class_3586> lv : this.field_15721) {
				if (lv.method_14975().method_16253(serverPlayerEntity, entity, damageSource)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3201.class_3586> lv2 : list) {
					lv2.method_14976(this.field_15720);
				}
			}
		}
	}

	public static class class_3586 extends AbstractCriterionInstance {
		private final class_3528 field_17474;
		private final class_3161 field_17475;

		public class_3586(Identifier identifier, class_3528 arg, class_3161 arg2) {
			super(identifier);
			this.field_17474 = arg;
			this.field_17475 = arg2;
		}

		public static class_3201.class_3586 method_16251(class_3528.class_3529 arg) {
			return new class_3201.class_3586(AchievementsAndCriterions.PLAYER_KILLED_ENTITY.field_15719, arg.method_15916(), class_3161.field_16861);
		}

		public static class_3201.class_3586 method_16254() {
			return new class_3201.class_3586(AchievementsAndCriterions.PLAYER_KILLED_ENTITY.field_15719, class_3528.field_17075, class_3161.field_16861);
		}

		public static class_3201.class_3586 method_16252(class_3528.class_3529 arg, class_3161.class_3472 arg2) {
			return new class_3201.class_3586(AchievementsAndCriterions.PLAYER_KILLED_ENTITY.field_15719, arg.method_15916(), arg2.method_15693());
		}

		public static class_3201.class_3586 method_16255() {
			return new class_3201.class_3586(AchievementsAndCriterions.ENTITY_KILLED_PLAYER.field_15719, class_3528.field_17075, class_3161.field_16861);
		}

		public boolean method_16253(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			return !this.field_17475.method_14120(serverPlayerEntity, damageSource) ? false : this.field_17474.method_15906(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.field_17474.method_15904());
			jsonObject.add("killing_blow", this.field_17475.method_15688());
			return jsonObject;
		}
	}
}
