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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.EntityJson;

public class class_3241 implements Criterion<class_3241.class_3243> {
	private static final Identifier field_15827 = new Identifier("villager_trade");
	private final Map<AdvancementFile, class_3241.class_3242> field_15828 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15827;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3241.class_3243> arg) {
		class_3241.class_3242 lv = (class_3241.class_3242)this.field_15828.get(file);
		if (lv == null) {
			lv = new class_3241.class_3242(file);
			this.field_15828.put(file, lv);
		}

		lv.method_14424(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3241.class_3243> arg) {
		class_3241.class_3242 lv = (class_3241.class_3242)this.field_15828.get(file);
		if (lv != null) {
			lv.method_14425(arg);
			if (lv.method_14422()) {
				this.field_15828.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15828.remove(file);
	}

	public class_3241.class_3243 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityJson entityJson = EntityJson.fromJson(jsonObject.get("villager"));
		class_3200 lv = class_3200.method_14295(jsonObject.get("item"));
		return new class_3241.class_3243(entityJson, lv);
	}

	public void method_14419(ServerPlayerEntity serverPlayerEntity, VillagerEntity villagerEntity, ItemStack itemStack) {
		class_3241.class_3242 lv = (class_3241.class_3242)this.field_15828.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_14423(serverPlayerEntity, villagerEntity, itemStack);
		}
	}

	static class class_3242 {
		private final AdvancementFile field_15829;
		private final Set<Criterion.class_3353<class_3241.class_3243>> field_15830 = Sets.newHashSet();

		public class_3242(AdvancementFile advancementFile) {
			this.field_15829 = advancementFile;
		}

		public boolean method_14422() {
			return this.field_15830.isEmpty();
		}

		public void method_14424(Criterion.class_3353<class_3241.class_3243> arg) {
			this.field_15830.add(arg);
		}

		public void method_14425(Criterion.class_3353<class_3241.class_3243> arg) {
			this.field_15830.remove(arg);
		}

		public void method_14423(ServerPlayerEntity serverPlayerEntity, VillagerEntity villagerEntity, ItemStack itemStack) {
			List<Criterion.class_3353<class_3241.class_3243>> list = null;

			for (Criterion.class_3353<class_3241.class_3243> lv : this.field_15830) {
				if (lv.method_14975().method_14426(serverPlayerEntity, villagerEntity, itemStack)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3241.class_3243> lv2 : list) {
					lv2.method_14976(this.field_15829);
				}
			}
		}
	}

	public static class class_3243 extends AbstractCriterionInstance {
		private final EntityJson field_15831;
		private final class_3200 field_15832;

		public class_3243(EntityJson entityJson, class_3200 arg) {
			super(class_3241.field_15827);
			this.field_15831 = entityJson;
			this.field_15832 = arg;
		}

		public boolean method_14426(ServerPlayerEntity serverPlayerEntity, VillagerEntity villagerEntity, ItemStack itemStack) {
			return !this.field_15831.method_14237(serverPlayerEntity, villagerEntity) ? false : this.field_15832.method_14294(itemStack);
		}
	}
}
