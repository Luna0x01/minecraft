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

public class class_3247 implements Criterion<class_3247.class_3249> {
	private static final Identifier field_15856 = new Identifier("used_totem");
	private final Map<AdvancementFile, class_3247.class_3248> field_15857 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15856;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3247.class_3249> arg) {
		class_3247.class_3248 lv = (class_3247.class_3248)this.field_15857.get(file);
		if (lv == null) {
			lv = new class_3247.class_3248(file);
			this.field_15857.put(file, lv);
		}

		lv.method_14441(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3247.class_3249> arg) {
		class_3247.class_3248 lv = (class_3247.class_3248)this.field_15857.get(file);
		if (lv != null) {
			lv.method_14442(arg);
			if (lv.method_14439()) {
				this.field_15857.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15857.remove(file);
	}

	public class_3247.class_3249 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3200 lv = class_3200.method_14295(jsonObject.get("item"));
		return new class_3247.class_3249(lv);
	}

	public void method_14436(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		class_3247.class_3248 lv = (class_3247.class_3248)this.field_15857.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14440(itemStack);
		}
	}

	static class class_3248 {
		private final AdvancementFile field_15858;
		private final Set<Criterion.class_3353<class_3247.class_3249>> field_15859 = Sets.newHashSet();

		public class_3248(AdvancementFile advancementFile) {
			this.field_15858 = advancementFile;
		}

		public boolean method_14439() {
			return this.field_15859.isEmpty();
		}

		public void method_14441(Criterion.class_3353<class_3247.class_3249> arg) {
			this.field_15859.add(arg);
		}

		public void method_14442(Criterion.class_3353<class_3247.class_3249> arg) {
			this.field_15859.remove(arg);
		}

		public void method_14440(ItemStack itemStack) {
			List<Criterion.class_3353<class_3247.class_3249>> list = null;

			for (Criterion.class_3353<class_3247.class_3249> lv : this.field_15859) {
				if (lv.method_14975().method_14443(itemStack)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3247.class_3249> lv2 : list) {
					lv2.method_14976(this.field_15858);
				}
			}
		}
	}

	public static class class_3249 extends AbstractCriterionInstance {
		private final class_3200 field_15860;

		public class_3249(class_3200 arg) {
			super(class_3247.field_15856);
			this.field_15860 = arg;
		}

		public boolean method_14443(ItemStack itemStack) {
			return this.field_15860.method_14294(itemStack);
		}
	}
}
