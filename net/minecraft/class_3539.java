package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class class_3539 implements Criterion<class_3539.class_3541> {
	private static final Identifier field_17132 = new Identifier("fishing_rod_hooked");
	private final Map<AdvancementFile, class_3539.class_3540> field_17133 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_17132;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3539.class_3541> arg) {
		class_3539.class_3540 lv = (class_3539.class_3540)this.field_17133.get(file);
		if (lv == null) {
			lv = new class_3539.class_3540(file);
			this.field_17133.put(file, lv);
		}

		lv.method_15992(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3539.class_3541> arg) {
		class_3539.class_3540 lv = (class_3539.class_3540)this.field_17133.get(file);
		if (lv != null) {
			lv.method_15994(arg);
			if (lv.method_15991()) {
				this.field_17133.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_17133.remove(file);
	}

	public class_3539.class_3541 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3200 lv = class_3200.method_16171(jsonObject.get("rod"));
		class_3528 lv2 = class_3528.method_15905(jsonObject.get("entity"));
		class_3200 lv3 = class_3200.method_16171(jsonObject.get("item"));
		return new class_3539.class_3541(lv, lv2, lv3);
	}

	public void method_15988(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
		class_3539.class_3540 lv = (class_3539.class_3540)this.field_17133.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15993(serverPlayerEntity, itemStack, fishingBobberEntity, collection);
		}
	}

	static class class_3540 {
		private final AdvancementFile field_17134;
		private final Set<Criterion.class_3353<class_3539.class_3541>> field_17135 = Sets.newHashSet();

		public class_3540(AdvancementFile advancementFile) {
			this.field_17134 = advancementFile;
		}

		public boolean method_15991() {
			return this.field_17135.isEmpty();
		}

		public void method_15992(Criterion.class_3353<class_3539.class_3541> arg) {
			this.field_17135.add(arg);
		}

		public void method_15994(Criterion.class_3353<class_3539.class_3541> arg) {
			this.field_17135.remove(arg);
		}

		public void method_15993(
			ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection
		) {
			List<Criterion.class_3353<class_3539.class_3541>> list = null;

			for (Criterion.class_3353<class_3539.class_3541> lv : this.field_17135) {
				if (lv.method_14975().method_15996(serverPlayerEntity, itemStack, fishingBobberEntity, collection)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3539.class_3541> lv2 : list) {
					lv2.method_14976(this.field_17134);
				}
			}
		}
	}

	public static class class_3541 extends AbstractCriterionInstance {
		private final class_3200 field_17136;
		private final class_3528 field_17137;
		private final class_3200 field_17138;

		public class_3541(class_3200 arg, class_3528 arg2, class_3200 arg3) {
			super(class_3539.field_17132);
			this.field_17136 = arg;
			this.field_17137 = arg2;
			this.field_17138 = arg3;
		}

		public static class_3539.class_3541 method_15995(class_3200 arg, class_3528 arg2, class_3200 arg3) {
			return new class_3539.class_3541(arg, arg2, arg3);
		}

		public boolean method_15996(
			ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection
		) {
			if (!this.field_17136.method_14294(itemStack)) {
				return false;
			} else if (!this.field_17137.method_15906(serverPlayerEntity, fishingBobberEntity.caughtEntity)) {
				return false;
			} else {
				if (this.field_17138 != class_3200.field_15710) {
					boolean bl = false;
					if (fishingBobberEntity.caughtEntity instanceof ItemEntity) {
						ItemEntity itemEntity = (ItemEntity)fishingBobberEntity.caughtEntity;
						if (this.field_17138.method_14294(itemEntity.getItemStack())) {
							bl = true;
						}
					}

					for (ItemStack itemStack2 : collection) {
						if (this.field_17138.method_14294(itemStack2)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rod", this.field_17136.method_16170());
			jsonObject.add("entity", this.field_17137.method_15904());
			jsonObject.add("item", this.field_17138.method_16170());
			return jsonObject;
		}
	}
}
