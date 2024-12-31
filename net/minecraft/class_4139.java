package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4139 implements class_4142 {
	private static final Logger field_20136 = LogManager.getLogger();
	private final class_4277 field_20137;
	private final Char2ObjectMap<class_4139.class_4141> field_20138;

	public class_4139(class_4277 arg, Char2ObjectMap<class_4139.class_4141> char2ObjectMap) {
		this.field_20137 = arg;
		this.field_20138 = char2ObjectMap;
	}

	@Override
	public void close() {
		this.field_20137.close();
	}

	@Nullable
	@Override
	public class_4135 method_18486(char c) {
		return (class_4135)this.field_20138.get(c);
	}

	public static class class_4140 implements class_4143 {
		private final Identifier field_20139;
		private final List<String> field_20140;
		private final int field_20141;
		private final int field_20142;

		public class_4140(Identifier identifier, int i, int j, List<String> list) {
			this.field_20139 = new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
			this.field_20140 = list;
			this.field_20141 = i;
			this.field_20142 = j;
		}

		public static class_4139.class_4140 method_18484(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "height", 8);
			int j = JsonHelper.getInt(jsonObject, "ascent");
			if (j > i) {
				throw new JsonParseException("Ascent " + j + " higher than height " + i);
			} else {
				List<String> list = Lists.newArrayList();
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "chars");

				for (int k = 0; k < jsonArray.size(); k++) {
					String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
					if (k > 0) {
						int l = string.length();
						int m = ((String)list.get(0)).length();
						if (l != m) {
							throw new JsonParseException("Elements of chars have to be the same lenght (found: " + l + ", expected: " + m + "), pad with space or \\u0000");
						}
					}

					list.add(string);
				}

				if (!list.isEmpty() && !((String)list.get(0)).isEmpty()) {
					return new class_4139.class_4140(new Identifier(JsonHelper.getString(jsonObject, "file")), i, j, list);
				} else {
					throw new JsonParseException("Expected to find data in chars, found none.");
				}
			}
		}

		@Nullable
		@Override
		public class_4142 method_18487(ResourceManager resourceManager) {
			try {
				Resource resource = resourceManager.getResource(this.field_20139);
				Throwable var3 = null;

				class_4139 var27;
				try {
					class_4277 lv = class_4277.method_19468(class_4277.class_4278.RGBA, resource.getInputStream());
					int i = lv.method_19458();
					int j = lv.method_19478();
					int k = i / ((String)this.field_20140.get(0)).length();
					int l = j / this.field_20140.size();
					float f = (float)this.field_20141 / (float)l;
					Char2ObjectMap<class_4139.class_4141> char2ObjectMap = new Char2ObjectOpenHashMap();

					for (int m = 0; m < this.field_20140.size(); m++) {
						String string = (String)this.field_20140.get(m);

						for (int n = 0; n < string.length(); n++) {
							char c = string.charAt(n);
							if (c != 0 && c != ' ') {
								int o = this.method_18485(lv, k, l, n, m);
								char2ObjectMap.put(c, new class_4139.class_4141(f, lv, n * k, m * l, k, l, (int)(0.5 + (double)((float)o * f)) + 1, this.field_20142));
							}
						}
					}

					var27 = new class_4139(lv, char2ObjectMap);
				} catch (Throwable var24) {
					var3 = var24;
					throw var24;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var23) {
								var3.addSuppressed(var23);
							}
						} else {
							resource.close();
						}
					}
				}

				return var27;
			} catch (IOException var26) {
				throw new RuntimeException(var26.getMessage());
			}
		}

		private int method_18485(class_4277 arg, int i, int j, int k, int l) {
			int m;
			for (m = i - 1; m >= 0; m--) {
				int n = k * i + m;

				for (int o = 0; o < j; o++) {
					int p = l * j + o;
					if (arg.method_19483(n, p) != 0) {
						return m + 1;
					}
				}
			}

			return m + 1;
		}
	}

	static final class class_4141 implements class_4135 {
		private final float field_20143;
		private final class_4277 field_20144;
		private final int field_20145;
		private final int field_20146;
		private final int field_20147;
		private final int field_20148;
		private final int field_20149;
		private final int field_20150;

		private class_4141(float f, class_4277 arg, int i, int j, int k, int l, int m, int n) {
			this.field_20143 = f;
			this.field_20144 = arg;
			this.field_20145 = i;
			this.field_20146 = j;
			this.field_20147 = k;
			this.field_20148 = l;
			this.field_20149 = m;
			this.field_20150 = n;
		}

		@Override
		public float method_18476() {
			return 1.0F / this.field_20143;
		}

		@Override
		public int method_18472() {
			return this.field_20147;
		}

		@Override
		public int method_18474() {
			return this.field_20148;
		}

		@Override
		public float getAdvance() {
			return (float)this.field_20149;
		}

		@Override
		public float getBearingY() {
			return class_4135.super.getBearingY() + 7.0F - (float)this.field_20150;
		}

		@Override
		public void method_18473(int i, int j) {
			this.field_20144.method_19462(0, i, j, this.field_20145, this.field_20146, this.field_20147, this.field_20148, false);
		}

		@Override
		public boolean method_18475() {
			return this.field_20144.method_19481().method_19487() > 1;
		}
	}
}
