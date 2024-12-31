package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.texture.ColorMaskTexture;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class class_2871 {
	public static final class_2871.class_2872 field_13540 = new class_2871.class_2872(
		"B", new Identifier("textures/entity/banner_base.png"), "textures/entity/banner/"
	);
	public static final class_2871.class_2872 field_13541 = new class_2871.class_2872(
		"S", new Identifier("textures/entity/shield_base.png"), "textures/entity/shield/"
	);
	public static final Identifier TEXTURE_SHIELD_BASE = new Identifier("textures/entity/shield_base_nopattern.png");
	public static final Identifier TEXTURE_BANNER_BASE = new Identifier("textures/entity/banner/base.png");

	static class class_2472 {
		public long field_11012;
		public Identifier field_11013;

		private class_2472() {
		}
	}

	public static class class_2872 {
		private final Map<String, class_2871.class_2472> field_13544 = Maps.newLinkedHashMap();
		private final Identifier field_13545;
		private final String field_13546;
		private String field_13547;

		public class_2872(String string, Identifier identifier, String string2) {
			this.field_13547 = string;
			this.field_13545 = identifier;
			this.field_13546 = string2;
		}

		@Nullable
		public Identifier method_12344(String string, List<BannerBlockEntity.BannerPattern> list, List<DyeColor> list2) {
			if (string.isEmpty()) {
				return null;
			} else {
				string = this.field_13547 + string;
				class_2871.class_2472 lv = (class_2871.class_2472)this.field_13544.get(string);
				if (lv == null) {
					if (this.field_13544.size() >= 256 && !this.method_12343()) {
						return class_2871.TEXTURE_BANNER_BASE;
					}

					List<String> list3 = Lists.newArrayList();

					for (BannerBlockEntity.BannerPattern bannerPattern : list) {
						list3.add(this.field_13546 + bannerPattern.getName() + ".png");
					}

					lv = new class_2871.class_2472();
					lv.field_11013 = new Identifier(string);
					MinecraftClient.getInstance().getTextureManager().loadTexture(lv.field_11013, new ColorMaskTexture(this.field_13545, list3, list2));
					this.field_13544.put(string, lv);
				}

				lv.field_11012 = System.currentTimeMillis();
				return lv.field_11013;
			}
		}

		private boolean method_12343() {
			long l = System.currentTimeMillis();
			Iterator<String> iterator = this.field_13544.keySet().iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				class_2871.class_2472 lv = (class_2871.class_2472)this.field_13544.get(string);
				if (l - lv.field_11012 > 5000L) {
					MinecraftClient.getInstance().getTextureManager().close(lv.field_11013);
					iterator.remove();
					return true;
				}
			}

			return this.field_13544.size() < 256;
		}
	}
}
