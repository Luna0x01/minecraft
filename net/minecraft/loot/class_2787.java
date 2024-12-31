package net.minecraft.loot;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.class_2776;
import net.minecraft.class_2778;
import net.minecraft.class_2782;
import net.minecraft.class_2789;
import net.minecraft.class_2795;
import net.minecraft.class_2797;
import net.minecraft.class_2816;
import net.minecraft.class_2818;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2787 implements ResourceReloadListener {
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
	private final Map<Identifier, class_2780> field_19798 = Maps.newHashMap();
	public static final int field_19796 = "loot_tables/".length();
	public static final int field_19797 = ".json".length();

	public class_2780 method_12006(Identifier identifier) {
		return (class_2780)this.field_19798.getOrDefault(identifier, class_2780.field_13185);
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.field_19798.clear();

		for (Identifier identifier : resourceManager.method_21372("loot_tables", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(field_19796, string.length() - field_19797));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var7 = null;

				try {
					class_2780 lv = JsonHelper.deserialize(field_13208, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), class_2780.class);
					if (lv != null) {
						this.field_19798.put(identifier2, lv);
					}
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (resource != null) {
						if (var7 != null) {
							try {
								resource.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (Throwable var19) {
				LOGGER.error("Couldn't read loot table {} from {}", identifier2, identifier, var19);
			}
		}
	}
}
