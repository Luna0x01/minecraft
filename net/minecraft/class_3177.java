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

public class class_3177 implements Criterion<class_3177.class_3179> {
	private static final Identifier field_15637 = new Identifier("enchanted_item");
	private final Map<AdvancementFile, class_3177.class_3178> field_15638 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15637;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3177.class_3179> arg) {
		class_3177.class_3178 lv = (class_3177.class_3178)this.field_15638.get(file);
		if (lv == null) {
			lv = new class_3177.class_3178(file);
			this.field_15638.put(file, lv);
		}

		lv.method_14200(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3177.class_3179> arg) {
		class_3177.class_3178 lv = (class_3177.class_3178)this.field_15638.get(file);
		if (lv != null) {
			lv.method_14201(arg);
			if (lv.method_14198()) {
				this.field_15638.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15638.remove(file);
	}

	public class_3177.class_3179 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3200 lv = class_3200.method_14295(jsonObject.get("item"));
		MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject.get("levels"));
		return new class_3177.class_3179(lv, minMaxJson);
	}

	public void method_14195(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		class_3177.class_3178 lv = (class_3177.class_3178)this.field_15638.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14199(itemStack, i);
		}
	}

	static class class_3178 {
		private final AdvancementFile field_15639;
		private final Set<Criterion.class_3353<class_3177.class_3179>> field_15640 = Sets.newHashSet();

		public class_3178(AdvancementFile advancementFile) {
			this.field_15639 = advancementFile;
		}

		public boolean method_14198() {
			return this.field_15640.isEmpty();
		}

		public void method_14200(Criterion.class_3353<class_3177.class_3179> arg) {
			this.field_15640.add(arg);
		}

		public void method_14201(Criterion.class_3353<class_3177.class_3179> arg) {
			this.field_15640.remove(arg);
		}

		public void method_14199(ItemStack itemStack, int i) {
			List<Criterion.class_3353<class_3177.class_3179>> list = null;

			for (Criterion.class_3353<class_3177.class_3179> lv : this.field_15640) {
				if (lv.method_14975().method_14202(itemStack, i)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3177.class_3179> lv2 : list) {
					lv2.method_14976(this.field_15639);
				}
			}
		}
	}

	public static class class_3179 extends AbstractCriterionInstance {
		private final class_3200 field_15641;
		private final MinMaxJson field_15642;

		public class_3179(class_3200 arg, MinMaxJson minMaxJson) {
			super(class_3177.field_15637);
			this.field_15641 = arg;
			this.field_15642 = minMaxJson;
		}

		public boolean method_14202(ItemStack itemStack, int i) {
			return !this.field_15641.method_14294(itemStack) ? false : this.field_15642.method_14335((float)i);
		}
	}
}
