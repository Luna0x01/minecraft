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
import net.minecraft.util.Identifier;
import net.minecraft.util.json.MinMaxJson;
import net.minecraft.util.math.BlockPos;

public class class_3244 implements Criterion<class_3244.class_3246> {
	private static final Identifier field_15833 = new Identifier("used_ender_eye");
	private final Map<AdvancementFile, class_3244.class_3245> field_15834 = Maps.newHashMap();

	@Override
	public Identifier getIdentifier() {
		return field_15833;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3244.class_3246> arg) {
		class_3244.class_3245 lv = (class_3244.class_3245)this.field_15834.get(file);
		if (lv == null) {
			lv = new class_3244.class_3245(file);
			this.field_15834.put(file, lv);
		}

		lv.method_14432(arg);
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3244.class_3246> arg) {
		class_3244.class_3245 lv = (class_3244.class_3245)this.field_15834.get(file);
		if (lv != null) {
			lv.method_14433(arg);
			if (lv.method_14430()) {
				this.field_15834.remove(file);
			}
		}
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
		this.field_15834.remove(file);
	}

	public class_3244.class_3246 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject.get("distance"));
		return new class_3244.class_3246(minMaxJson);
	}

	public void method_14427(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
		class_3244.class_3245 lv = (class_3244.class_3245)this.field_15834.get(serverPlayerEntity.getAdvancementFile());
		if (lv != null) {
			double d = serverPlayerEntity.x - (double)blockPos.getX();
			double e = serverPlayerEntity.z - (double)blockPos.getZ();
			lv.method_14431(d * d + e * e);
		}
	}

	static class class_3245 {
		private final AdvancementFile field_15835;
		private final Set<Criterion.class_3353<class_3244.class_3246>> field_15836 = Sets.newHashSet();

		public class_3245(AdvancementFile advancementFile) {
			this.field_15835 = advancementFile;
		}

		public boolean method_14430() {
			return this.field_15836.isEmpty();
		}

		public void method_14432(Criterion.class_3353<class_3244.class_3246> arg) {
			this.field_15836.add(arg);
		}

		public void method_14433(Criterion.class_3353<class_3244.class_3246> arg) {
			this.field_15836.remove(arg);
		}

		public void method_14431(double d) {
			List<Criterion.class_3353<class_3244.class_3246>> list = null;

			for (Criterion.class_3353<class_3244.class_3246> lv : this.field_15836) {
				if (lv.method_14975().method_14434(d)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (Criterion.class_3353<class_3244.class_3246> lv2 : list) {
					lv2.method_14976(this.field_15835);
				}
			}
		}
	}

	public static class class_3246 extends AbstractCriterionInstance {
		private final MinMaxJson field_15837;

		public class_3246(MinMaxJson minMaxJson) {
			super(class_3244.field_15833);
			this.field_15837 = minMaxJson;
		}

		public boolean method_14434(double d) {
			return this.field_15837.method_14334(d);
		}
	}
}
