package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4145 implements class_4142 {
	private static final Logger field_20158 = LogManager.getLogger();
	private final ResourceManager field_20159;
	private final byte[] field_20160;
	private final String field_20161;
	private final Map<Identifier, class_4277> field_20162 = Maps.newHashMap();

	public class_4145(ResourceManager resourceManager, byte[] bs, String string) {
		this.field_20159 = resourceManager;
		this.field_20160 = bs;
		this.field_20161 = string;

		for (int i = 0; i < 256; i++) {
			char c = (char)(i * 256);
			Identifier identifier = this.method_18495(c);

			try {
				Resource resource = this.field_20159.getResource(identifier);
				Throwable var8 = null;

				try (class_4277 lv = class_4277.method_19468(class_4277.class_4278.RGBA, resource.getInputStream())) {
					if (lv.method_19458() == 256 && lv.method_19478() == 256) {
						for (int j = 0; j < 256; j++) {
							byte b = bs[c + j];
							if (b != 0 && method_18492(b) > method_18494(b)) {
								bs[c + j] = 0;
							}
						}
						continue;
					}
				} catch (Throwable var41) {
					var8 = var41;
					throw var41;
				} finally {
					if (resource != null) {
						if (var8 != null) {
							try {
								resource.close();
							} catch (Throwable var37) {
								var8.addSuppressed(var37);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IOException var43) {
			}

			Arrays.fill(bs, c, c + 256, (byte)0);
		}
	}

	@Override
	public void close() {
		this.field_20162.values().forEach(class_4277::close);
	}

	private Identifier method_18495(char c) {
		Identifier identifier = new Identifier(String.format(this.field_20161, String.format("%02x", c / 256)));
		return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
	}

	@Nullable
	@Override
	public class_4135 method_18486(char c) {
		byte b = this.field_20160[c];
		if (b != 0) {
			class_4277 lv = (class_4277)this.field_20162.computeIfAbsent(this.method_18495(c), this::method_18493);
			if (lv != null) {
				int i = method_18492(b);
				return new class_4145.class_4147(c % 16 * 16 + i, (c & 255) / 16 * 16, method_18494(b) - i, 16, lv);
			}
		}

		return null;
	}

	@Nullable
	private class_4277 method_18493(Identifier identifier) {
		try {
			Resource resource = this.field_20159.getResource(identifier);
			Throwable var3 = null;

			class_4277 var4;
			try {
				var4 = class_4277.method_19468(class_4277.class_4278.RGBA, resource.getInputStream());
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (resource != null) {
					if (var3 != null) {
						try {
							resource.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						resource.close();
					}
				}
			}

			return var4;
		} catch (IOException var16) {
			field_20158.error("Couldn't load texture {}", identifier, var16);
			return null;
		}
	}

	private static int method_18492(byte b) {
		return b >> 4 & 15;
	}

	private static int method_18494(byte b) {
		return (b & 15) + 1;
	}

	public static class class_4146 implements class_4143 {
		private final Identifier field_20163;
		private final String field_20164;

		public class_4146(Identifier identifier, String string) {
			this.field_20163 = identifier;
			this.field_20164 = string;
		}

		public static class_4143 method_18496(JsonObject jsonObject) {
			return new class_4145.class_4146(new Identifier(JsonHelper.getString(jsonObject, "sizes")), JsonHelper.getString(jsonObject, "template"));
		}

		@Nullable
		@Override
		public class_4142 method_18487(ResourceManager resourceManager) {
			try {
				Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.field_20163);
				Throwable var3 = null;

				class_4145 var5;
				try {
					byte[] bs = new byte[65536];
					resource.getInputStream().read(bs);
					var5 = new class_4145(resourceManager, bs, this.field_20164);
				} catch (Throwable var15) {
					var3 = var15;
					throw var15;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var14) {
								var3.addSuppressed(var14);
							}
						} else {
							resource.close();
						}
					}
				}

				return var5;
			} catch (IOException var17) {
				class_4145.field_20158.error("Cannot load {}, unicode glyphs will not render correctly", this.field_20163);
				return null;
			}
		}
	}

	static class class_4147 implements class_4135 {
		private final int field_20165;
		private final int field_20166;
		private final int field_20167;
		private final int field_20168;
		private final class_4277 field_20169;

		private class_4147(int i, int j, int k, int l, class_4277 arg) {
			this.field_20165 = k;
			this.field_20166 = l;
			this.field_20167 = i;
			this.field_20168 = j;
			this.field_20169 = arg;
		}

		@Override
		public float method_18476() {
			return 2.0F;
		}

		@Override
		public int method_18472() {
			return this.field_20165;
		}

		@Override
		public int method_18474() {
			return this.field_20166;
		}

		@Override
		public float getAdvance() {
			return (float)(this.field_20165 / 2 + 1);
		}

		@Override
		public void method_18473(int i, int j) {
			this.field_20169.method_19462(0, i, j, this.field_20167, this.field_20168, this.field_20165, this.field_20166, false);
		}

		@Override
		public boolean method_18475() {
			return this.field_20169.method_19481().method_19487() > 1;
		}

		@Override
		public float getShadowOffset() {
			return 0.5F;
		}

		@Override
		public float getBoldOffset() {
			return 0.5F;
		}
	}
}
