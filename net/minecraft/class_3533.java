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

public class class_3533 implements Criterion<class_3533.class_3535> {
	private static final Identifier field_17125 = new Identifier("filled_bucket");
	private final Map<AdvancementFile, class_3533.class_3534> field_17126 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_17125;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3533.class_3535> arg) {
		class_3533.class_3534 lv = (class_3533.class_3534)this.field_17126.get(file);
		if (lv == null) {
			lv = new class_3533.class_3534(file);
			this.field_17126.put(file, lv);
		}

		lv.method_15972(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3533.class_3535> arg) {
		class_3533.class_3534 lv = (class_3533.class_3534)this.field_17126.get(file);
		if (lv != null) {
			lv.method_15973(arg);
			if (lv.method_15970()) {
				this.field_17126.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_17126.remove(file);
	}

	public class_3533.class_3535 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3200 lv = class_3200.method_16171(jsonObject.get("item"));
		return new class_3533.class_3535(lv);
	}

	public void method_15967(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		class_3533.class_3534 lv = (class_3533.class_3534)this.field_17126.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15971(itemStack);
		}
	}

	static class class_3534 {
		private final AdvancementFile field_17127;
		private final Set<Criterion.class_3353<class_3533.class_3535>> field_17128 = Sets.newHashSet();

		public class_3534(AdvancementFile advancementFile) {
			this.field_17127 = advancementFile;
		}

		public boolean method_15970() {
			return this.field_17128.isEmpty();
		}

		public void method_15972(Criterion.class_3353<class_3533.class_3535> arg) {
			this.field_17128.add(arg);
		}

		public void method_15973(Criterion.class_3353<class_3533.class_3535> arg) {
			this.field_17128.remove(arg);
		}

		public void method_15971(ItemStack itemStack) {
			List<Criterion.class_3353<class_3533.class_3535>> list = null;

			for (Criterion.class_3353<class_3533.class_3535> lv : this.field_17128) {
				if (lv.method_14975().method_15974(itemStack)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3533.class_3535> lv2 : list) {
					lv2.method_14976(this.field_17127);
				}
			}
		}
	}

	public static class class_3535 extends AbstractCriterionInstance {
		private final class_3200 field_17129;

		public class_3535(class_3200 arg) {
			super(class_3533.field_17125);
			this.field_17129 = arg;
		}

		public static class_3533.class_3535 method_15975(class_3200 arg) {
			return new class_3533.class_3535(arg);
		}

		public boolean method_15974(ItemStack itemStack) {
			return this.field_17129.method_14294(itemStack);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_17129.method_16170());
			return jsonObject;
		}
	}
}
