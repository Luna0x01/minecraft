package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagContainer<T> {
	private static final Logger field_22233 = LogManager.getLogger();
	private static final Gson field_22234 = new Gson();
	private static final int field_22235 = ".json".length();
	private final Map<Identifier, Tag<T>> field_22236 = Maps.newHashMap();
	private final Function<Identifier, T> field_22237;
	private final Predicate<Identifier> field_22238;
	private final String field_22239;
	private final boolean field_22240;
	private final String field_22241;

	public TagContainer(Predicate<Identifier> predicate, Function<Identifier, T> function, String string, boolean bl, String string2) {
		this.field_22238 = predicate;
		this.field_22237 = function;
		this.field_22239 = string;
		this.field_22240 = bl;
		this.field_22241 = string2;
	}

	public void method_21488(Tag<T> tag) {
		if (this.field_22236.containsKey(tag.getId())) {
			throw new IllegalArgumentException("Duplicate " + this.field_22241 + " tag '" + tag.getId() + "'");
		} else {
			this.field_22236.put(tag.getId(), tag);
		}
	}

	@Nullable
	public Tag<T> method_21486(Identifier identifier) {
		return (Tag<T>)this.field_22236.get(identifier);
	}

	public Tag<T> getOrCreate(Identifier identifier) {
		Tag<T> tag = (Tag<T>)this.field_22236.get(identifier);
		return tag == null ? new Tag<>(identifier) : tag;
	}

	public Collection<Identifier> method_21483() {
		return this.field_22236.keySet();
	}

	public Collection<Identifier> method_21484(T object) {
		List<Identifier> list = Lists.newArrayList();

		for (Entry<Identifier, Tag<T>> entry : this.field_22236.entrySet()) {
			if (((Tag)entry.getValue()).contains(object)) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public void method_21489() {
		this.field_22236.clear();
	}

	public void method_21487(ResourceManager resourceManager) {
		Map<Identifier, Tag.Builder<T>> map = Maps.newHashMap();

		for (Identifier identifier : resourceManager.method_21372(this.field_22239, string -> string.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(this.field_22239.length() + 1, string.length() - field_22235));

			try {
				for (Resource resource : resourceManager.getAllResources(identifier)) {
					try {
						JsonObject jsonObject = JsonHelper.deserialize(field_22234, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
						if (jsonObject == null) {
							field_22233.error(
								"Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.field_22241, identifier2, identifier, resource.getResourcePackName()
							);
						} else {
							Tag.Builder<T> builder = (Tag.Builder<T>)map.getOrDefault(identifier2, Tag.Builder.create());
							builder.fromJson(this.field_22238, this.field_22237, jsonObject);
							map.put(identifier2, builder);
						}
					} catch (RuntimeException | IOException var15) {
						field_22233.error(
							"Couldn't read {} tag list {} from {} in data pack {}", this.field_22241, identifier2, identifier, resource.getResourcePackName(), var15
						);
					} finally {
						IOUtils.closeQuietly(resource);
					}
				}
			} catch (IOException var17) {
				field_22233.error("Couldn't read {} tag list {} from {}", this.field_22241, identifier2, identifier, var17);
			}
		}

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, Tag.Builder<T>>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, Tag.Builder<T>> entry = (Entry<Identifier, Tag.Builder<T>>)iterator.next();
				if (((Tag.Builder)entry.getValue()).applyTagGetter(this::method_21486)) {
					bl = true;
					this.method_21488(((Tag.Builder)entry.getValue()).build((Identifier)entry.getKey()));
					iterator.remove();
				}
			}

			if (!bl) {
				for (Entry<Identifier, Tag.Builder<T>> entry2 : map.entrySet()) {
					field_22233.error(
						"Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.field_22241, entry2.getKey()
					);
				}
				break;
			}
		}

		for (Entry<Identifier, Tag.Builder<T>> entry3 : map.entrySet()) {
			this.method_21488(((Tag.Builder)entry3.getValue()).setOrdered(this.field_22240).build((Identifier)entry3.getKey()));
		}
	}

	public Map<Identifier, Tag<T>> method_21491() {
		return this.field_22236;
	}
}
