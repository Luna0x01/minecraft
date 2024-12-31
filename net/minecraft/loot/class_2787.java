package net.minecraft.loot;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nullable;
import net.minecraft.class_2776;
import net.minecraft.class_2778;
import net.minecraft.class_2782;
import net.minecraft.class_2789;
import net.minecraft.class_2795;
import net.minecraft.class_2797;
import net.minecraft.class_2816;
import net.minecraft.class_2818;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2787 {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson field_13208 = new GsonBuilder()
		.registerTypeAdapter(class_2789.class, new class_2789.class_2790())
		.registerTypeAdapter(class_2776.class, new class_2776.class_2777())
		.registerTypeAdapter(class_2780.class, new class_2780.class_2781())
		.registerTypeHierarchyAdapter(class_2778.class, new class_2778.class_2779())
		.registerTypeHierarchyAdapter(class_2795.class, new class_2797.class_2798())
		.registerTypeHierarchyAdapter(class_2816.class, new class_2818.class_2819())
		.registerTypeHierarchyAdapter(class_2782.class_2784.class, new class_2782.class_2784.class_2785())
		.create();
	private final LoadingCache<Identifier, class_2780> field_13209 = CacheBuilder.newBuilder().build(new class_2787.class_2788());
	private final File field_13210;

	public class_2787(File file) {
		this.field_13210 = file;
		this.method_12004();
	}

	public class_2780 method_12006(Identifier identifier) {
		return (class_2780)this.field_13209.getUnchecked(identifier);
	}

	public void method_12004() {
		this.field_13209.invalidateAll();

		for (Identifier identifier : LootTables.method_11961()) {
			this.method_12006(identifier);
		}
	}

	class class_2788 extends CacheLoader<Identifier, class_2780> {
		private class_2788() {
		}

		public class_2780 load(Identifier identifier) throws Exception {
			if (identifier.getPath().contains(".")) {
				class_2787.LOGGER.debug("Invalid loot table name '{}' (can't contain periods)", new Object[]{identifier});
				return class_2780.field_13185;
			} else {
				class_2780 lv = this.method_12010(identifier);
				if (lv == null) {
					lv = this.method_12011(identifier);
				}

				if (lv == null) {
					lv = class_2780.field_13185;
					class_2787.LOGGER.warn("Couldn't find resource table {}", new Object[]{identifier});
				}

				return lv;
			}
		}

		@Nullable
		private class_2780 method_12010(Identifier identifier) {
			File file = new File(new File(class_2787.this.field_13210, identifier.getNamespace()), identifier.getPath() + ".json");
			if (file.exists()) {
				if (file.isFile()) {
					String string;
					try {
						string = Files.toString(file, Charsets.UTF_8);
					} catch (IOException var6) {
						class_2787.LOGGER.warn("Couldn't load loot table {} from {}", new Object[]{identifier, file, var6});
						return class_2780.field_13185;
					}

					try {
						return (class_2780)class_2787.field_13208.fromJson(string, class_2780.class);
					} catch (JsonParseException var5) {
						class_2787.LOGGER.error("Couldn't load loot table {} from {}", new Object[]{identifier, file, var5});
						return class_2780.field_13185;
					}
				} else {
					class_2787.LOGGER.warn("Expected to find loot table {} at {} but it was a folder.", new Object[]{identifier, file});
					return class_2780.field_13185;
				}
			} else {
				return null;
			}
		}

		@Nullable
		private class_2780 method_12011(Identifier identifier) {
			URL uRL = class_2787.class.getResource("/assets/" + identifier.getNamespace() + "/loot_tables/" + identifier.getPath() + ".json");
			if (uRL != null) {
				String string;
				try {
					string = Resources.toString(uRL, Charsets.UTF_8);
				} catch (IOException var6) {
					class_2787.LOGGER.warn("Couldn't load loot table {} from {}", new Object[]{identifier, uRL, var6});
					return class_2780.field_13185;
				}

				try {
					return (class_2780)class_2787.field_13208.fromJson(string, class_2780.class);
				} catch (JsonParseException var5) {
					class_2787.LOGGER.error("Couldn't load loot table {} from {}", new Object[]{identifier, uRL, var5});
					return class_2780.field_13185;
				}
			} else {
				return null;
			}
		}
	}
}
