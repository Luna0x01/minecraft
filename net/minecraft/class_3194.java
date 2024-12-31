package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
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
import net.minecraft.item.Itemable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.NbtCompoundJson;

public class class_3194 implements Criterion<class_3194.class_3554> {
	private static final Identifier field_15690 = new Identifier("inventory_changed");
	private final Map<AdvancementFile, class_3194.class_3195> field_15691 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15690;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3194.class_3554> arg) {
		class_3194.class_3195 lv = (class_3194.class_3195)this.field_15691.get(file);
		if (lv == null) {
			lv = new class_3194.class_3195(file);
			this.field_15691.put(file, lv);
		}

		lv.method_14281(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3194.class_3554> arg) {
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

	public class_3194.class_3554 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
		class_3638.class_3642 lv = class_3638.class_3642.method_16524(jsonObject2.get("occupied"));
		class_3638.class_3642 lv2 = class_3638.class_3642.method_16524(jsonObject2.get("full"));
		class_3638.class_3642 lv3 = class_3638.class_3642.method_16524(jsonObject2.get("empty"));
		class_3200[] lvs = class_3200.method_14296(jsonObject.get("items"));
		return new class_3194.class_3554(lv, lv2, lv3, lvs);
	}

	public void method_14276(ServerPlayerEntity serverPlayerEntity, PlayerInventory playerInventory) {
		class_3194.class_3195 lv = (class_3194.class_3195)this.field_15691.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14280(playerInventory);
		}
	}

	static class class_3195 {
		private final AdvancementFile field_15692;
		private final Set<Criterion.class_3353<class_3194.class_3554>> field_15693 = Sets.newHashSet();

		public class_3195(AdvancementFile advancementFile) {
			this.field_15692 = advancementFile;
		}

		public boolean method_14279() {
			return this.field_15693.isEmpty();
		}

		public void method_14281(Criterion.class_3353<class_3194.class_3554> arg) {
			this.field_15693.add(arg);
		}

		public void method_14282(Criterion.class_3353<class_3194.class_3554> arg) {
			this.field_15693.remove(arg);
		}

		public void method_14280(PlayerInventory playerInventory) {
			List<Criterion.class_3353<class_3194.class_3554>> list = null;

			for (Criterion.class_3353<class_3194.class_3554> lv : this.field_15693) {
				if (lv.method_14975().method_16067(playerInventory)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3194.class_3554> lv2 : list) {
					lv2.method_14976(this.field_15692);
				}
			}
		}
	}

	public static class class_3554 extends AbstractCriterionInstance {
		private final class_3638.class_3642 field_17186;
		private final class_3638.class_3642 field_17187;
		private final class_3638.class_3642 field_17188;
		private final class_3200[] field_17189;

		public class_3554(class_3638.class_3642 arg, class_3638.class_3642 arg2, class_3638.class_3642 arg3, class_3200[] args) {
			super(class_3194.field_15690);
			this.field_17186 = arg;
			this.field_17187 = arg2;
			this.field_17188 = arg3;
			this.field_17189 = args;
		}

		public static class_3194.class_3554 method_16068(class_3200... args) {
			return new class_3194.class_3554(class_3638.class_3642.field_17698, class_3638.class_3642.field_17698, class_3638.class_3642.field_17698, args);
		}

		public static class_3194.class_3554 method_16069(Itemable... itemables) {
			class_3200[] lvs = new class_3200[itemables.length];

			for (int i = 0; i < itemables.length; i++) {
				lvs[i] = new class_3200(
					null, itemables[i].getItem(), class_3638.class_3642.field_17698, class_3638.class_3642.field_17698, new class_3180[0], null, NbtCompoundJson.EMPTY
				);
			}

			return method_16068(lvs);
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			if (!this.field_17186.method_16512() || !this.field_17187.method_16512() || !this.field_17188.method_16512()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("occupied", this.field_17186.method_16513());
				jsonObject2.add("full", this.field_17187.method_16513());
				jsonObject2.add("empty", this.field_17188.method_16513());
				jsonObject.add("slots", jsonObject2);
			}

			if (this.field_17189.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (class_3200 lv : this.field_17189) {
					jsonArray.add(lv.method_16170());
				}

				jsonObject.add("items", jsonArray);
			}

			return jsonObject;
		}

		public boolean method_16067(PlayerInventory playerInventory) {
			int i = 0;
			int j = 0;
			int k = 0;
			List<class_3200> list = Lists.newArrayList(this.field_17189);

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

			if (!this.field_17187.method_16531(i)) {
				return false;
			} else if (!this.field_17188.method_16531(j)) {
				return false;
			} else {
				return !this.field_17186.method_16531(k) ? false : list.isEmpty();
			}
		}
	}
}
