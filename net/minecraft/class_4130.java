package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4130 implements ResourceReloadListener {
	private static final Logger field_20098 = LogManager.getLogger();
	private final Map<Identifier, TextRenderer> field_20099 = Maps.newHashMap();
	private final TextureManager field_20100;
	private boolean field_20101;

	public class_4130(TextureManager textureManager, boolean bl) {
		this.field_20100 = textureManager;
		this.field_20101 = bl;
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		Map<Identifier, List<class_4142>> map = Maps.newHashMap();

		for (Identifier identifier : resourceManager.method_21372("font", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("font/".length(), string.length() - ".json".length()));
			List<class_4142> list = (List<class_4142>)map.computeIfAbsent(identifier2, identifierx -> Lists.newArrayList(new class_4142[]{new class_4129()}));

			try {
				for (Resource resource : resourceManager.getAllResources(identifier)) {
					try {
						InputStream inputStream = resource.getInputStream();
						Throwable var12 = null;

						try {
							JsonArray jsonArray = JsonHelper.getArray(
								JsonHelper.deserialize(gson, IOUtils.toString(inputStream, StandardCharsets.UTF_8), JsonObject.class), "providers"
							);

							for (int i = jsonArray.size() - 1; i >= 0; i--) {
								JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");

								try {
									class_4144 lv = class_4144.method_18489(JsonHelper.getString(jsonObject, "type"));
									if (!this.field_20101 || lv == class_4144.LEGACY_UNICODE || !identifier2.equals(MinecraftClient.field_19942)) {
										class_4142 lv2 = lv.method_18488(jsonObject).method_18487(resourceManager);
										if (lv2 != null) {
											list.add(lv2);
										}
									}
								} catch (RuntimeException var28) {
									field_20098.warn(
										"Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getResourcePackName(), var28.getMessage()
									);
								}
							}
						} catch (Throwable var29) {
							var12 = var29;
							throw var29;
						} finally {
							if (inputStream != null) {
								if (var12 != null) {
									try {
										inputStream.close();
									} catch (Throwable var27) {
										var12.addSuppressed(var27);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (RuntimeException var31) {
						field_20098.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getResourcePackName(), var31.getMessage());
					}
				}
			} catch (IOException var32) {
				field_20098.warn("Unable to load font '{}' in fonts.json: {}", identifier2, var32.getMessage());
			}
		}

		Stream.concat(this.field_20099.keySet().stream(), map.keySet().stream())
			.distinct()
			.forEach(
				identifierx -> {
					List<class_4142> listx = (List<class_4142>)map.getOrDefault(identifierx, Collections.emptyList());
					Collections.reverse(listx);
					((TextRenderer)this.field_20099
							.computeIfAbsent(identifierx, identifierxx -> new TextRenderer(this.field_20100, new class_4131(this.field_20100, identifierxx))))
						.method_18354(listx);
				}
			);
	}

	@Nullable
	public TextRenderer method_18453(Identifier identifier) {
		return (TextRenderer)this.field_20099.computeIfAbsent(identifier, identifierx -> {
			TextRenderer textRenderer = new TextRenderer(this.field_20100, new class_4131(this.field_20100, identifierx));
			textRenderer.method_18354(Lists.newArrayList(new class_4142[]{new class_4129()}));
			return textRenderer;
		});
	}

	public void method_18454(boolean bl) {
		if (bl != this.field_20101) {
			this.field_20101 = bl;
			this.reload(MinecraftClient.getInstance().getResourceManager());
		}
	}
}
