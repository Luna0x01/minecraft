package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.MinMaxJson;

public class class_3194 implements Criterion<class_3194.class_3196> {
	private static final Identifier field_15690 = new Identifier("inventory_changed");
	private final Map<AdvancementFile, class_3194.class_3195> field_15691 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15690;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3194.class_3196> arg) {
		class_3194.class_3195 lv = (class_3194.class_3195)this.field_15691.get(file);
		if (lv == null) {
			lv = new class_3194.class_3195(file);
			this.field_15691.put(file, lv);
		}

		lv.method_14281(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3194.class_3196> arg) {
		class_3194.class_3195 lv = (class_3194.class_3195)this.field_15691.get(file);
		if (lv != null) {
			lv.method_14282(arg);
			if (lv.method_14279()) {
				this.field_15691.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15691.remove(file);
	}

	public class_3194.class_3196 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
		MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject2.get("occupied"));
		MinMaxJson minMaxJson2 = MinMaxJson.fromJson(jsonObject2.get("full"));
		MinMaxJson minMaxJson3 = MinMaxJson.fromJson(jsonObject2.get("empty"));
		class_3200[] lvs = class_3200.method_14296(jsonObject.get("items"));
		return new class_3194.class_3196(minMaxJson, minMaxJson2, minMaxJson3, lvs);
	}

	public void method_14276(ServerPlayerEntity serverPlayerEntity, PlayerInventory playerInventory) {
		class_3194.class_3195 lv = (class_3194.class_3195)this.field_15691.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14280(playerInventory);
		}
	}

	static class class_3195 {
		private final AdvancementFile field_15692;
		private final Set<Criterion.class_3353<class_3194.class_3196>> field_15693 = Sets.newHashSet();

		public class_3195(AdvancementFile advancementFile) {
			this.field_15692 = advancementFile;
		}

		public boolean method_14279() {
			return this.field_15693.isEmpty();
		}

		public void method_14281(Criterion.class_3353<class_3194.class_3196> arg) {
			this.field_15693.add(arg);
		}

		public void method_14282(Criterion.class_3353<class_3194.class_3196> arg) {
			this.field_15693.remove(arg);
		}

		public void method_14280(PlayerInventory playerInventory) {
			List<Criterion.class_3353<class_3194.class_3196>> list = null;

			for (Criterion.class_3353<class_3194.class_3196> lv : this.field_15693) {
				if (lv.method_14975().method_14283(playerInventory)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3194.class_3196> lv2 : list) {
					lv2.method_14976(this.field_15692);
				}
			}
		}
	}

	public static class class_3196 extends AbstractCriterionInstance {
		private final MinMaxJson field_15694;
		private final MinMaxJson field_15695;
		private final MinMaxJson field_15696;
		private final class_3200[] field_15697;

		public class_3196(MinMaxJson minMaxJson, MinMaxJson minMaxJson2, MinMaxJson minMaxJson3, class_3200[] args) {
			super(class_3194.field_15690);
			this.field_15694 = minMaxJson;
			this.field_15695 = minMaxJson2;
			this.field_15696 = minMaxJson3;
			this.field_15697 = args;
		}

		public boolean method_14283(PlayerInventory playerInventory) {
			int i = 0;
			int j = 0;
			int k = 0;
			List<class_3200> list = Lists.newArrayList(this.field_15697);

			for (int l = 0; l < playerInventory.getInvSize(); l++) {
				ItemStack itemStack = playerInventory.getInvStack(l);
				if (itemStack.isEmpty()) {
					j++;
				} else {
					k++;
					if (itemStack.getCount() >= itemStack.getMaxCount()) {
						i++;
					}

					Iterator<class_3200> iterator = list.iterator();

					while (iterator.hasNext()) {
						class_3200 lv = (class_3200)iterator.next();
						if (lv.method_14294(itemStack)) {
							iterator.remove();
						}
					}
				}
			}

			if (!this.field_15695.method_14335((float)i)) {
				return false;
			} else if (!this.field_15696.method_14335((float)j)) {
				return false;
			} else {
				return !this.field_15694.method_14335((float)k) ? false : list.isEmpty();
			}
		}
	}
}
