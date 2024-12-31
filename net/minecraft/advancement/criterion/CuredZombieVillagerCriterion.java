package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.EntityJson;

public class CuredZombieVillagerCriterion implements Criterion<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> {
	private static final Identifier CURED_ZOMBIE_VILLAGER = new Identifier("cured_zombie_villager");
	private final Map<AdvancementFile, CuredZombieVillagerCriterion.class_3156> field_15552 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return CURED_ZOMBIE_VILLAGER;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> arg) {
		CuredZombieVillagerCriterion.class_3156 lv = (CuredZombieVillagerCriterion.class_3156)this.field_15552.get(file);
		if (lv == null) {
			lv = new CuredZombieVillagerCriterion.class_3156(file);
			this.field_15552.put(file, lv);
		}

		lv.method_14098(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> arg) {
		CuredZombieVillagerCriterion.class_3156 lv = (CuredZombieVillagerCriterion.class_3156)this.field_15552.get(file);
		if (lv != null) {
			lv.method_14099(arg);
			if (lv.method_14096()) {
				this.field_15552.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15552.remove(file);
	}

	public CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityJson entityJson = EntityJson.fromJson(jsonObject.get("zombie"));
		EntityJson entityJson2 = EntityJson.fromJson(jsonObject.get("villager"));
		return new CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance(entityJson, entityJson2);
	}

	public void grant(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
		CuredZombieVillagerCriterion.class_3156 lv = (CuredZombieVillagerCriterion.class_3156)this.field_15552.get(player.getAdvancementFile());
		if (lv != null) {
			lv.method_14097(player, zombie, villager);
		}
	}

	public static class CuredZombieVillagerCriterionInstance extends AbstractCriterionInstance {
		private final EntityJson zombie;
		private final EntityJson villager;

		public CuredZombieVillagerCriterionInstance(EntityJson entityJson, EntityJson entityJson2) {
			super(CuredZombieVillagerCriterion.CURED_ZOMBIE_VILLAGER);
			this.zombie = entityJson;
			this.villager = entityJson2;
		}

		public boolean method_14100(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
			return !this.zombie.method_14237(player, zombie) ? false : this.villager.method_14237(player, villager);
		}
	}

	static class class_3156 {
		private final AdvancementFile file;
		private final Set<Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance>> field_15554 = Sets.newHashSet();

		public class_3156(AdvancementFile advancementFile) {
			this.file = advancementFile;
		}

		public boolean method_14096() {
			return this.field_15554.isEmpty();
		}

		public void method_14098(Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> arg) {
			this.field_15554.add(arg);
		}

		public void method_14099(Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> arg) {
			this.field_15554.remove(arg);
		}

		public void method_14097(ServerPlayerEntity player, ZombieEntity entity, VillagerEntity villager) {
			List<Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance>> list = null;

			for (Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> lv : this.field_15554) {
				if (lv.method_14975().method_14100(player, entity, villager)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance> lv2 : list) {
					lv2.method_14976(this.file);
				}
			}
		}
	}
}
