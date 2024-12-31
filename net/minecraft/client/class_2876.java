package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_4231;
import net.minecraft.class_4291;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class class_2876 {
	public static final class_2876 field_13564 = new class_2876();
	private final List<class_2874> field_13565 = Lists.newArrayList();
	private final List<BakedModel> field_20806;

	private class_2876() {
		this.field_20806 = Collections.emptyList();
	}

	public class_2876(class_4231 arg, Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2, List<class_2874> list) {
		this.field_20806 = (List<BakedModel>)list.stream().map(arg2 -> {
			class_4291 lv = (class_4291)function.apply(arg2.method_12368());
			return Objects.equals(lv, arg) ? null : lv.method_19599(function, function2, ModelRotation.X0_Y0, false);
		}).collect(Collectors.toList());
		Collections.reverse(this.field_20806);

		for (int i = list.size() - 1; i >= 0; i--) {
			this.field_13565.add(list.get(i));
		}
	}

	@Nullable
	public BakedModel method_19253(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		if (!this.field_13565.isEmpty()) {
			for (int i = 0; i < this.field_13565.size(); i++) {
				class_2874 lv = (class_2874)this.field_13565.get(i);
				if (lv.method_12369(itemStack, world, livingEntity)) {
					BakedModel bakedModel2 = (BakedModel)this.field_20806.get(i);
					if (bakedModel2 == null) {
						return bakedModel;
					}

					return bakedModel2;
				}
			}
		}

		return bakedModel;
	}
}
