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
import net.minecraft.class_3180;
import net.minecraft.class_3200;
import net.minecraft.class_3638;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.NbtCompoundJson;

public class class_3380 implements Criterion<class_3380.class_3382> {
	private static final Identifier CONSUME_ITEM = new Identifier("consume_item");
	private final Map<AdvancementFile, class_3380.class_3381> field_16546 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return CONSUME_ITEM;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3380.class_3382> arg) {
		class_3380.class_3381 lv = (class_3380.class_3381)this.field_16546.get(file);
		if (lv == null) {
			lv = new class_3380.class_3381(file);
			this.field_16546.put(file, lv);
		}

		lv.method_15095(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3380.class_3382> arg) {
		class_3380.class_3381 lv = (class_3380.class_3381)this.field_16546.get(file);
		if (lv != null) {
			lv.method_15096(arg);
			if (lv.method_15093()) {
				this.field_16546.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_16546.remove(file);
	}

	public class_3380.class_3382 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_3380.class_3382(class_3200.method_16171(jsonObject.get("item")));
	}

	public void method_15090(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		class_3380.class_3381 lv = (class_3380.class_3381)this.field_16546.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15094(itemStack);
		}
	}

	static class class_3381 {
		private final AdvancementFile field_16547;
		private final Set<Criterion.class_3353<class_3380.class_3382>> field_16548 = Sets.newHashSet();

		public class_3381(AdvancementFile advancementFile) {
			this.field_16547 = advancementFile;
		}

		public boolean method_15093() {
			return this.field_16548.isEmpty();
		}

		public void method_15095(Criterion.class_3353<class_3380.class_3382> arg) {
			this.field_16548.add(arg);
		}

		public void method_15096(Criterion.class_3353<class_3380.class_3382> arg) {
			this.field_16548.remove(arg);
		}

		public void method_15094(ItemStack itemStack) {
			List<Criterion.class_3353<class_3380.class_3382>> list = null;

			for (Criterion.class_3353<class_3380.class_3382> lv : this.field_16548) {
				if (lv.method_14975().method_15097(itemStack)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3380.class_3382> lv2 : list) {
					lv2.method_14976(this.field_16547);
				}
			}
		}
	}

	public static class class_3382 extends AbstractCriterionInstance {
		private final class_3200 field_16549;

		public class_3382(class_3200 arg) {
			super(class_3380.CONSUME_ITEM);
			this.field_16549 = arg;
		}

		public static class_3380.class_3382 method_15544() {
			return new class_3380.class_3382(class_3200.field_15710);
		}

		public static class_3380.class_3382 method_15543(Itemable itemable) {
			return new class_3380.class_3382(
				new class_3200(
					null, itemable.getItem(), class_3638.class_3642.field_17698, class_3638.class_3642.field_17698, new class_3180[0], null, NbtCompoundJson.EMPTY
				)
			);
		}

		public boolean method_15097(ItemStack itemStack) {
			return this.field_16549.method_14294(itemStack);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_16549.method_16170());
			return jsonObject;
		}
	}
}
