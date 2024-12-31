package net.minecraft.achievement;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3328;
import net.minecraft.class_3361;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3348 {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(
			SimpleAdvancement.TaskAdvancement.class,
			new JsonDeserializer<SimpleAdvancement.TaskAdvancement>() {
				public SimpleAdvancement.TaskAdvancement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
					JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
					return SimpleAdvancement.TaskAdvancement.method_14804(jsonObject, jsonDeserializationContext);
				}
			}
		)
		.registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.class_3338())
		.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
		.create();
	private static final class_3328 field_16380 = new class_3328();
	private final File file;
	private boolean field_16382;

	public class_3348(@Nullable File file) {
		this.file = file;
		this.method_14936();
	}

	public void method_14936() {
		this.field_16382 = false;
		field_16380.method_14809();
		Map<Identifier, SimpleAdvancement.TaskAdvancement> map = this.method_14941();
		this.method_14937(map);
		field_16380.method_14812(map);

		for (SimpleAdvancement simpleAdvancement : field_16380.method_14815()) {
			if (simpleAdvancement.getDisplay() != null) {
				class_3361.positionChildren(simpleAdvancement);
			}
		}
	}

	public boolean method_14939() {
		return this.field_16382;
	}

	private Map<Identifier, SimpleAdvancement.TaskAdvancement> method_14941() {
		if (this.file == null) {
			return Maps.newHashMap();
		} else {
			Map<Identifier, SimpleAdvancement.TaskAdvancement> map = Maps.newHashMap();
			this.file.mkdirs();

			for (File file : FileUtils.listFiles(this.file, new String[]{"json"}, true)) {
				String string = FilenameUtils.removeExtension(this.file.toURI().relativize(file.toURI()).toString());
				String[] strings = string.split("/", 2);
				if (strings.length == 2) {
					Identifier identifier = new Identifier(strings[0], strings[1]);

					try {
						SimpleAdvancement.TaskAdvancement taskAdvancement = JsonHelper.deserialize(
							GSON, FileUtils.readFileToString(file, StandardCharsets.UTF_8), SimpleAdvancement.TaskAdvancement.class
						);
						if (taskAdvancement == null) {
							LOGGER.error("Couldn't load custom advancement " + identifier + " from " + file + " as it's empty or null");
						} else {
							map.put(identifier, taskAdvancement);
						}
					} catch (IllegalArgumentException | JsonParseException var8) {
						LOGGER.error("Parsing error loading custom advancement " + identifier, var8);
						this.field_16382 = true;
					} catch (IOException var9) {
						LOGGER.error("Couldn't read custom advancement " + identifier + " from " + file, var9);
						this.field_16382 = true;
					}
				}
			}

			return map;
		}
	}

	private void method_14937(Map<Identifier, SimpleAdvancement.TaskAdvancement> map) {
		FileSystem fileSystem = null;

		try {
			URL uRL = class_3348.class.getResource("/assets/.mcassetsroot");
			if (uRL == null) {
				LOGGER.error("Couldn't find .mcassetsroot");
				this.field_16382 = true;
			} else {
				URI uRI = uRL.toURI();
				Path path;
				if ("file".equals(uRI.getScheme())) {
					path = Paths.get(RecipeDispatcher.class.getResource("/assets/minecraft/advancements").toURI());
				} else {
					if (!"jar".equals(uRI.getScheme())) {
						LOGGER.error("Unsupported scheme " + uRI + " trying to list all built-in advancements (NYI?)");
						this.field_16382 = true;
						return;
					}

					fileSystem = FileSystems.newFileSystem(uRI, Collections.emptyMap());
					path = fileSystem.getPath("/assets/minecraft/advancements");
				}

				Iterator<Path> iterator = Files.walk(path).iterator();

				while (iterator.hasNext()) {
					Path path4 = (Path)iterator.next();
					if ("json".equals(FilenameUtils.getExtension(path4.toString()))) {
						Path path5 = path.relativize(path4);
						String string = FilenameUtils.removeExtension(path5.toString()).replaceAll("\\\\", "/");
						Identifier identifier = new Identifier("minecraft", string);
						if (!map.containsKey(identifier)) {
							BufferedReader bufferedReader = null;

							try {
								bufferedReader = Files.newBufferedReader(path4);
								SimpleAdvancement.TaskAdvancement taskAdvancement = JsonHelper.deserialize(GSON, bufferedReader, SimpleAdvancement.TaskAdvancement.class);
								map.put(identifier, taskAdvancement);
							} catch (JsonParseException var25) {
								LOGGER.error("Parsing error loading built-in advancement " + identifier, var25);
								this.field_16382 = true;
							} catch (IOException var26) {
								LOGGER.error("Couldn't read advancement " + identifier + " from " + path4, var26);
								this.field_16382 = true;
							} finally {
								IOUtils.closeQuietly(bufferedReader);
							}
						}
					}
				}
			}
		} catch (IOException | URISyntaxException var28) {
			LOGGER.error("Couldn't get a list of all built-in advancement files", var28);
			this.field_16382 = true;
		} finally {
			IOUtils.closeQuietly(fileSystem);
		}
	}

	@Nullable
	public SimpleAdvancement method_14938(Identifier identifier) {
		return field_16380.method_14814(identifier);
	}

	public Iterable<SimpleAdvancement> method_14940() {
		return field_16380.method_14816();
	}
}
