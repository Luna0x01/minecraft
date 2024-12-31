package net.minecraft.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_3528;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3363 implements Criterion<class_3363.class_4515> {
	private static final Identifier field_16505 = new Identifier("bred_animals");
	private final Map<AdvancementFile, class_3363.class_3364> field_16506 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_16505;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3363.class_4515> arg) {
		class_3363.class_3364 lv = (class_3363.class_3364)this.field_16506.get(file);
		if (lv == null) {
			lv = new class_3363.class_3364(file);
			this.field_16506.put(file, lv);
		}

		lv.method_15046(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3363.class_4515> arg) {
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

	public class_3363.class_4515 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3528 lv = class_3528.method_15905(jsonObject.get("parent"));
		class_3528 lv2 = class_3528.method_15905(jsonObject.get("partner"));
		class_3528 lv3 = class_3528.method_15905(jsonObject.get("child"));
		return new class_3363.class_4515(lv, lv2, lv3);
	}

	public void method_15041(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity) {
		class_3363.class_3364 lv = (class_3363.class_3364)this.field_16506.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15045(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity);
		}
	}

	static class class_3364 {
		private final AdvancementFile field_16507;
		private final Set<Criterion.class_3353<class_3363.class_4515>> field_16508 = Sets.newHashSet();

		public class_3364(AdvancementFile advancementFile) {
			this.field_16507 = advancementFile;
		}

		public boolean method_15044() {
			return this.field_16508.isEmpty();
		}

		public void method_15046(Criterion.class_3353<class_3363.class_4515> arg) {
			this.field_16508.add(arg);
		}

		public void method_15047(Criterion.class_3353<class_3363.class_4515> arg) {
			this.field_16508.remove(arg);
		}

		public void method_15045(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity) {
			List<Criterion.class_3353<class_3363.class_4515>> list = null;

			for (Criterion.class_3353<class_3363.class_4515> lv : this.field_16508) {
				if (lv.method_14975().method_21705(serverPlayerEntity, animalEntity, animalEntity2, passiveEntity)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3363.class_4515> lv2 : list) {
					lv2.method_14976(this.field_16507);
				}
			}
		}
	}

	public static class class_4515 extends AbstractCriterionInstance {
		private final class_3528 field_22331;
		private final class_3528 field_22332;
		private final class_3528 field_22333;

		public class_4515(class_3528 arg, class_3528 arg2, class_3528 arg3) {
			super(class_3363.field_16505);
			this.field_22331 = arg;
			this.field_22332 = arg2;
			this.field_22333 = arg3;
		}

		public static class_3363.class_4515 method_21706() {
			return new class_3363.class_4515(class_3528.field_17075, class_3528.field_17075, class_3528.field_17075);
		}

		public static class_3363.class_4515 method_21704(class_3528.class_3529 arg) {
			return new class_3363.class_4515(arg.method_15916(), class_3528.field_17075, class_3528.field_17075);
		}

		public boolean method_21705(
			ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity
		) {
			return !this.field_22333.method_15906(serverPlayerEntity, passiveEntity)
				? false
				: this.field_22331.method_15906(serverPlayerEntity, animalEntity) && this.field_22332.method_15906(serverPlayerEntity, animalEntity2)
					|| this.field_22331.method_15906(serverPlayerEntity, animalEntity2) && this.field_22332.method_15906(serverPlayerEntity, animalEntity);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("parent", this.field_22331.method_15904());
			jsonObject.add("partner", this.field_22332.method_15904());
			jsonObject.add("child", this.field_22333.method_15904());
			return jsonObject;
		}
	}
}
