package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class class_2876 {
	public static final class_2876 field_13564 = new class_2876();
	private final List<class_2874> field_13565 = Lists.newArrayList();

	private class_2876() {
	}

	public class_2876(List<class_2874> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			this.field_13565.add(list.get(i));
		}
	}

	@Nullable
	public Identifier method_12372(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		if (!this.field_13565.isEmpty()) {
			for (class_2874 lv : this.field_13565) {
				if (lv.method_12369(itemStack, world, livingEntity)) {
					return lv.method_12368();
				}
			}
		}

		return null;
	}
}
