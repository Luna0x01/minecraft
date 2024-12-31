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
import net.minecraft.class_3638;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_3376 implements Criterion<class_3376.class_3378> {
	private static final Identifier field_16539 = new Identifier("construct_beacon");
	private final Map<AdvancementFile, class_3376.class_3377> field_16540 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_16539;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3376.class_3378> arg) {
		class_3376.class_3377 lv = (class_3376.class_3377)this.field_16540.get(file);
		if (lv == null) {
			lv = new class_3376.class_3377(file);
			this.field_16540.put(file, lv);
		}

		lv.method_15086(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3376.class_3378> arg) {
		class_3376.class_3377 lv = (class_3376.class_3377)this.field_16540.get(file);
		if (lv != null) {
			lv.method_15087(arg);
			if (lv.method_15084()) {
				this.field_16540.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_16540.remove(file);
	}

	public class_3376.class_3378 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_3638.class_3642 lv = class_3638.class_3642.method_16524(jsonObject.get("level"));
		return new class_3376.class_3378(lv);
	}

	public void method_15081(ServerPlayerEntity serverPlayerEntity, BeaconBlockEntity beaconBlockEntity) {
		class_3376.class_3377 lv = (class_3376.class_3377)this.field_16540.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			lv.method_15085(beaconBlockEntity);
		}
	}

	static class class_3377 {
		private final AdvancementFile field_16541;
		private final Set<Criterion.class_3353<class_3376.class_3378>> field_16542 = Sets.newHashSet();

		public class_3377(AdvancementFile advancementFile) {
			this.field_16541 = advancementFile;
		}

		public boolean method_15084() {
			return this.field_16542.isEmpty();
		}

		public void method_15086(Criterion.class_3353<class_3376.class_3378> arg) {
			this.field_16542.add(arg);
		}

		public void method_15087(Criterion.class_3353<class_3376.class_3378> arg) {
			this.field_16542.remove(arg);
		}

		public void method_15085(BeaconBlockEntity beaconBlockEntity) {
			List<Criterion.class_3353<class_3376.class_3378>> list = null;

			for (Criterion.class_3353<class_3376.class_3378> lv : this.field_16542) {
				if (lv.method_14975().method_15088(beaconBlockEntity)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3376.class_3378> lv2 : list) {
					lv2.method_14976(this.field_16541);
				}
			}
		}
	}

	public static class class_3378 extends AbstractCriterionInstance {
		private final class_3638.class_3642 field_16543;

		public class_3378(class_3638.class_3642 arg) {
			super(class_3376.field_16539);
			this.field_16543 = arg;
		}

		public static class_3376.class_3378 method_15519(class_3638.class_3642 arg) {
			return new class_3376.class_3378(arg);
		}

		public boolean method_15088(BeaconBlockEntity beaconBlockEntity) {
			return this.field_16543.method_16531(beaconBlockEntity.method_14362());
		}

		@Override
		public JsonElement method_21241() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.field_16543.method_16513());
			return jsonObject;
		}
	}
}
