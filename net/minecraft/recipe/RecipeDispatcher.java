package net.minecraft.recipe;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3579;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeDispatcher implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int field_17443 = "recipes/".length();
	public static final int field_17444 = ".json".length();
	private final Map<Identifier, RecipeType> field_17445 = Maps.newHashMap();
	private boolean field_17446;

	@Override
	public void reload(ResourceManager resourceManager) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.field_17446 = false;
		this.field_17445.clear();

		for (Identifier identifier : resourceManager.method_21372("recipes", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(field_17443, string.length() - field_17444));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var8 = null;

				try {
					JsonObject jsonObject = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
					if (jsonObject == null) {
						LOGGER.error("Couldn't load recipe {} as it's null or empty", identifier2);
					} else {
						this.method_16205(class_3579.method_16220(identifier2, jsonObject));
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
				LOGGER.error("Parsing error loading recipe {}", identifier2, var21);
				this.field_17446 = true;
			} catch (IOException var22) {
				LOGGER.error("Couldn't read custom advancement {} from {}", identifier2, identifier, var22);
				this.field_17446 = true;
			}
		}

		LOGGER.info("Loaded {} recipes", this.field_17445.size());
	}

	public void method_16205(RecipeType recipeType) {
		if (this.field_17445.containsKey(recipeType.method_16202())) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + recipeType.method_16202());
		} else {
			this.field_17445.put(recipeType.method_16202(), recipeType);
		}
	}

	public ItemStack method_16204(Inventory inventory, World world) {
		for (RecipeType recipeType : this.field_17445.values()) {
			if (recipeType.method_3500(inventory, world)) {
				return recipeType.method_16201(inventory);
			}
		}

		return ItemStack.EMPTY;
	}

	@Nullable
	public RecipeType method_16209(Inventory inventory, World world) {
		for (RecipeType recipeType : this.field_17445.values()) {
			if (recipeType.method_3500(inventory, world)) {
				return recipeType;
			}
		}

		return null;
	}

	public DefaultedList<ItemStack> method_16211(Inventory inventory, World world) {
		for (RecipeType recipeType : this.field_17445.values()) {
			if (recipeType.method_3500(inventory, world)) {
				return recipeType.method_16203(inventory);
			}
		}

		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			defaultedList.set(i, inventory.getInvStack(i));
		}

		return defaultedList;
	}

	@Nullable
	public RecipeType method_16207(Identifier identifier) {
		return (RecipeType)this.field_17445.get(identifier);
	}

	public Collection<RecipeType> method_16208() {
		return this.field_17445.values();
	}

	public Collection<Identifier> method_16210() {
		return this.field_17445.keySet();
	}

	public void method_16212() {
		this.field_17445.clear();
	}
}
