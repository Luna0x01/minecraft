package net.minecraft.achievement;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3328;
import net.minecraft.class_3361;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3348 implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(SimpleAdvancement.TaskAdvancement.class, (JsonDeserializer)(jsonElement, type, jsonDeserializationContext) -> {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
			return SimpleAdvancement.TaskAdvancement.method_14804(jsonObject, jsonDeserializationContext);
		})
		.registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.class_3338())
		.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
		.create();
	private static final class_3328 field_16380 = new class_3328();
	public static final int field_21663 = "advancements/".length();
	public static final int field_21664 = ".json".length();
	private boolean field_16382;

	private Map<Identifier, SimpleAdvancement.TaskAdvancement> method_20452(ResourceManager resourceManager) {
		Map<Identifier, SimpleAdvancement.TaskAdvancement> map = Maps.newHashMap();

		for (Identifier identifier : resourceManager.method_21372("advancements", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(field_21663, string.length() - field_21664));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var8 = null;

				try {
					SimpleAdvancement.TaskAdvancement taskAdvancement = JsonHelper.deserialize(
						GSON, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), SimpleAdvancement.TaskAdvancement.class
					);
					if (taskAdvancement == null) {
						LOGGER.error("Couldn't load custom advancement {} from {} as it's empty or null", identifier2, identifier);
					} else {
						map.put(identifier2, taskAdvancement);
					}
				} catch (Throwable var19) {
					var8 = var19;
					throw var19;
				} finally {
					if (resource != null) {
						if (var8 != null) {
							try {
								resource.close();
							} catch (Throwable var18) {
								var8.addSuppressed(var18);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IllegalArgumentException | JsonParseException var21) {
				LOGGER.error("Parsing error loading custom advancement {}: {}", identifier2, var21.getMessage());
				this.field_16382 = true;
			} catch (IOException var22) {
				LOGGER.error("Couldn't read custom advancement {} from {}", identifier2, identifier, var22);
				this.field_16382 = true;
			}
		}

		return map;
	}

	@Nullable
	public SimpleAdvancement method_14938(Identifier identifier) {
		return field_16380.method_14814(identifier);
	}

	public Collection<SimpleAdvancement> method_20451() {
		return field_16380.method_20270();
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.field_16382 = false;
		field_16380.method_14809();
		Map<Identifier, SimpleAdvancement.TaskAdvancement> map = this.method_20452(resourceManager);
		field_16380.method_14812(map);

		for (SimpleAdvancement simpleAdvancement : field_16380.method_14815()) {
			if (simpleAdvancement.getDisplay() != null) {
				class_3361.positionChildren(simpleAdvancement);
			}
		}
	}
}
