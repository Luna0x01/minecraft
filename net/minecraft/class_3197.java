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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.MinMaxJson;

public class class_3197 implements Criterion<class_3197.class_3199> {
	private static final Identifier field_15698 = new Identifier("item_durability_changed");
	private final Map<AdvancementFile, class_3197.class_3198> field_15699 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15698;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3197.class_3199> arg) {
		class_3197.class_3198 lv = (class_3197.class_3198)this.field_15699.get(file);
		if (lv == null) {
			lv = new class_3197.class_3198(file);
			this.field_15699.put(file, lv);
		}

		lv.method_14289(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3197.class_3199> arg) {
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

	public class_3197.class_3199 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3200 lv = class_3200.method_14295(jsonObject.get("item"));
		MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject.get("durability"));
		MinMaxJson minMaxJson2 = MinMaxJson.fromJson(jsonObject.get("delta"));
		return new class_3197.class_3199(lv, minMaxJson, minMaxJson2);
	}

	public void method_14284(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		class_3197.class_3198 lv = (class_3197.class_3198)this.field_15699.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14288(itemStack, i);
		}
	}

	static class class_3198 {
		private final AdvancementFile field_15700;
		private final Set<Criterion.class_3353<class_3197.class_3199>> field_15701 = Sets.newHashSet();

		public class_3198(AdvancementFile advancementFile) {
			this.field_15700 = advancementFile;
		}

		public boolean method_14287() {
			return this.field_15701.isEmpty();
		}

		public void method_14289(Criterion.class_3353<class_3197.class_3199> arg) {
			this.field_15701.add(arg);
		}

		public void method_14290(Criterion.class_3353<class_3197.class_3199> arg) {
			this.field_15701.remove(arg);
		}

		public void method_14288(ItemStack itemStack, int i) {
			List<Criterion.class_3353<class_3197.class_3199>> list = null;

			for (Criterion.class_3353<class_3197.class_3199> lv : this.field_15701) {
				if (lv.method_14975().method_14291(itemStack, i)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3197.class_3199> lv2 : list) {
					lv2.method_14976(this.field_15700);
				}
			}
		}
	}

	public static class class_3199 extends AbstractCriterionInstance {
		private final class_3200 field_15702;
		private final MinMaxJson field_15703;
		private final MinMaxJson field_15704;

		public class_3199(class_3200 arg, MinMaxJson minMaxJson, MinMaxJson minMaxJson2) {
			super(class_3197.field_15698);
			this.field_15702 = arg;
			this.field_15703 = minMaxJson;
			this.field_15704 = minMaxJson2;
		}

		public boolean method_14291(ItemStack itemStack, int i) {
			if (!this.field_15702.method_14294(itemStack)) {
				return false;
			} else {
				return !this.field_15703.method_14335((float)(itemStack.getMaxDamage() - i)) ? false : this.field_15704.method_14335((float)(itemStack.getDamage() - i));
			}
		}
	}
}
