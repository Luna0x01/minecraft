package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends JsonDataLoader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes = ImmutableMap.of();
	private boolean errored;

	public RecipeManager() {
		super(GSON, "recipes");
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
		this.errored = false;
		Map<RecipeType<?>, Builder<Identifier, Recipe<?>>> map2 = Maps.newHashMap();

		for (Entry<Identifier, JsonElement> entry : map.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();

			try {
				Recipe<?> recipe = deserialize(identifier, JsonHelper.asObject((JsonElement)entry.getValue(), "top element"));
				((Builder)map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder())).put(identifier, recipe);
			} catch (IllegalArgumentException | JsonParseException var9) {
				LOGGER.error("Parsing error loading recipe {}", identifier, var9);
			}
		}

		this.recipes = (Map<RecipeType<?>, Map<Identifier, Recipe<?>>>)map2.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, entryx -> ((Builder)entryx.getValue()).build()));
		LOGGER.info("Loaded {} recipes", map2.size());
	}

	public boolean method_35227() {
		return this.errored;
	}

	public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> type, C inventory, World world) {
		return this.getAllOfType(type).values().stream().flatMap(recipe -> Util.stream(type.get(recipe, world, inventory))).findFirst();
	}

	public <C extends Inventory, T extends Recipe<C>> List<T> listAllOfType(RecipeType<T> recipeType) {
		return (List<T>)this.getAllOfType(recipeType).values().stream().map(recipe -> recipe).collect(Collectors.toList());
	}

	public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> type, C inventory, World world) {
		return (List<T>)this.getAllOfType(type)
			.values()
			.stream()
			.flatMap(recipe -> Util.stream(type.get(recipe, world, inventory)))
			.sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey()))
			.collect(Collectors.toList());
	}

	private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> type) {
		return (Map<Identifier, Recipe<C>>)this.recipes.getOrDefault(type, Collections.emptyMap());
	}

	public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> recipeType, C inventory, World world) {
		Optional<T> optional = this.getFirstMatch(recipeType, inventory, world);
		if (optional.isPresent()) {
			return ((Recipe)optional.get()).getRemainder(inventory);
		} else {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

			for (int i = 0; i < defaultedList.size(); i++) {
				defaultedList.set(i, inventory.getStack(i));
			}

			return defaultedList;
		}
	}

	public Optional<? extends Recipe<?>> get(Identifier id) {
		return this.recipes.values().stream().map(map -> (Recipe)map.get(id)).filter(Objects::nonNull).findFirst();
	}

	public Collection<Recipe<?>> values() {
		return (Collection<Recipe<?>>)this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
	}

	public Stream<Identifier> keys() {
		return this.recipes.values().stream().flatMap(map -> map.keySet().stream());
	}

	public static Recipe<?> deserialize(Identifier id, JsonObject json) {
		String string = JsonHelper.getString(json, "type");
		return ((RecipeSerializer)Registry.RECIPE_SERIALIZER
				.getOrEmpty(new Identifier(string))
				.orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")))
			.read(id, json);
	}

	public void setRecipes(Iterable<Recipe<?>> recipes) {
		this.errored = false;
		Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map = Maps.newHashMap();
		recipes.forEach(recipe -> {
			Map<Identifier, Recipe<?>> map2 = (Map<Identifier, Recipe<?>>)map.computeIfAbsent(recipe.getType(), recipeType -> Maps.newHashMap());
			Recipe<?> recipe2 = (Recipe<?>)map2.put(recipe.getId(), recipe);
			if (recipe2 != null) {
				throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
			}
		});
		this.recipes = ImmutableMap.copyOf(map);
	}
}
