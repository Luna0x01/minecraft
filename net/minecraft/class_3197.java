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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class class_3197 implements Criterion<class_3197.class_3557> {
	private static final Identifier field_15698 = new Identifier("item_durability_changed");
	private final Map<AdvancementFile, class_3197.class_3198> field_15699 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15698;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3197.class_3557> arg) {
		class_3197.class_3198 lv = (class_3197.class_3198)this.field_15699.get(file);
		if (lv == null) {
			lv = new class_3197.class_3198(file);
			this.field_15699.put(file, lv);
		}

		lv.method_14289(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3197.class_3557> arg) {
		class_3197.class_3198 lv = (class_3197.class_3198)this.field_15699.get(file);
		if (lv != null) {
			lv.method_14290(arg);
			if (lv.method_14287()) {
				this.field_15699.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15699.remove(file);
	}

	public class_3197.class_3557 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3200 lv = class_3200.method_16171(jsonObject.get("item"));
		class_3638.class_3642 lv2 = class_3638.class_3642.method_16524(jsonObject.get("durability"));
		class_3638.class_3642 lv3 = class_3638.class_3642.method_16524(jsonObject.get("delta"));
		return new class_3197.class_3557(lv, lv2, lv3);
	}

	public void method_14284(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		class_3197.class_3198 lv = (class_3197.class_3198)this.field_15699.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14288(itemStack, i);
		}
	}

	static class class_3198 {
		private final AdvancementFile field_15700;
		private final Set<Criterion.class_3353<class_3197.class_3557>> field_15701 = Sets.newHashSet();

		public class_3198(AdvancementFile advancementFile) {
			this.field_15700 = advancementFile;
		}

		public boolean method_14287() {
			return this.field_15701.isEmpty();
		}

		public void method_14289(Criterion.class_3353<class_3197.class_3557> arg) {
			this.field_15701.add(arg);
		}

		public void method_14290(Criterion.class_3353<class_3197.class_3557> arg) {
			this.field_15701.remove(arg);
		}

		public void method_14288(ItemStack itemStack, int i) {
			List<Criterion.class_3353<class_3197.class_3557>> list = null;

			for (Criterion.class_3353<class_3197.class_3557> lv : this.field_15701) {
				if (lv.method_14975().method_16123(itemStack, i)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3197.class_3557> lv2 : list) {
					lv2.method_14976(this.field_15700);
				}
			}
		}
	}

	public static class class_3557 extends AbstractCriterionInstance {
		private final class_3200 field_17377;
		private final class_3638.class_3642 field_17378;
		private final class_3638.class_3642 field_17379;

		public class_3557(class_3200 arg, class_3638.class_3642 arg2, class_3638.class_3642 arg3) {
			super(class_3197.field_15698);
			this.field_17377 = arg;
			this.field_17378 = arg2;
			this.field_17379 = arg3;
		}

		public static class_3197.class_3557 method_16124(class_3200 arg, class_3638.class_3642 arg2) {
			return new class_3197.class_3557(arg, arg2, class_3638.class_3642.field_17698);
		}

		public boolean method_16123(ItemStack itemStack, int i) {
			if (!this.field_17377.method_14294(itemStack)) {
				return false;
			} else {
				return !this.field_17378.method_16531(itemStack.getMaxDamage() - i) ? false : this.field_17379.method_16531(itemStack.getDamage() - i);
			}
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_17377.method_16170());
			jsonObject.add("durability", this.field_17378.method_16513());
			jsonObject.add("delta", this.field_17379.method_16513());
			return jsonObject;
		}
	}
}
