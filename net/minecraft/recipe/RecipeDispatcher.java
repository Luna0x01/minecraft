package net.minecraft.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeDispatcher {
	private static final Logger LOGGER = LogManager.getLogger();
	private static int field_15685;
	public static final SimpleRegistry<Identifier, RecipeType> REGISTRY = new SimpleRegistry<>();

	public static boolean setup() {
		try {
			register("armordye", new ArmorDyeRecipeType());
			register("bookcloning", new BookCloningRecipeType());
			register("mapcloning", new MapCloningRecipeType());
			register("mapextending", new MapUpscaleRecipeType());
			register("fireworks", new FireworkRecipeType());
			register("repairitem", new RepairingRecipeType());
			register("tippedarrow", new TippedArrowRecipeType());
			register("bannerduplicate", new BannerRecipeDispatcher.PatternRecipeType());
			register("banneraddpattern", new BannerRecipeDispatcher.CopyingRecipeType());
			register("shielddecoration", new ShieldRecipeDispatcher.DecorationRecipeType());
			register("shulkerboxcoloring", new ShulkerBoxRecipeDispatcher.ColoringRecipeType());
			return method_14261();
		} catch (Throwable var1) {
			return false;
		}
	}

	private static boolean method_14261() {
		FileSystem fileSystem = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		try {
			URL uRL = RecipeDispatcher.class.getResource("/assets/.mcassetsroot");
			if (uRL == null) {
				LOGGER.error("Couldn't find .mcassetsroot");
				return false;
			} else {
				URI uRI = uRL.toURI();
				Path path;
				if ("file".equals(uRI.getScheme())) {
					path = Paths.get(RecipeDispatcher.class.getResource("/assets/minecraft/recipes").toURI());
				} else {
					if (!"jar".equals(uRI.getScheme())) {
						LOGGER.error("Unsupported scheme " + uRI + " trying to list all recipes");
						return false;
					}

					fileSystem = FileSystems.newFileSystem(uRI, Collections.emptyMap());
					path = fileSystem.getPath("/assets/minecraft/recipes");
				}

				Iterator<Path> iterator = Files.walk(path).iterator();

				while (iterator.hasNext()) {
					Path path4 = (Path)iterator.next();
					if ("json".equals(FilenameUtils.getExtension(path4.toString()))) {
						Path path5 = path.relativize(path4);
						String string = FilenameUtils.removeExtension(path5.toString()).replaceAll("\\\\", "/");
						Identifier identifier = new Identifier(string);
						BufferedReader bufferedReader = null;

						try {
							bufferedReader = Files.newBufferedReader(path4);
							register(string, load(JsonHelper.deserialize(gson, bufferedReader, JsonObject.class)));
						} catch (JsonParseException var25) {
							LOGGER.error("Parsing error loading recipe " + identifier, var25);
							return false;
						} catch (IOException var26) {
							LOGGER.error("Couldn't read recipe " + identifier + " from " + path4, var26);
							return false;
						} finally {
							IOUtils.closeQuietly(bufferedReader);
						}
					}
				}

				return true;
			}
		} catch (IOException | URISyntaxException var28) {
			LOGGER.error("Couldn't get a list of all recipe files", var28);
			return false;
		} finally {
			IOUtils.closeQuietly(fileSystem);
		}
	}

	private static RecipeType load(JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "type");
		if ("crafting_shaped".equals(string)) {
			return ShapedRecipeType.load(jsonObject);
		} else if ("crafting_shapeless".equals(string)) {
			return ShapelessRecipeType.load(jsonObject);
		} else {
			throw new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'");
		}
	}

	public static void register(String string, RecipeType recipeType) {
		method_14260(new Identifier(string), recipeType);
	}

	public static void method_14260(Identifier identifier, RecipeType recipeType) {
		if (REGISTRY.containsKey(identifier)) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + identifier);
		} else {
			REGISTRY.add(field_15685++, identifier, recipeType);
		}
	}

	public static ItemStack matches(CraftingInventory craftingInventory, World inventory) {
		for (RecipeType recipeType : REGISTRY) {
			if (recipeType.matches(craftingInventory, inventory)) {
				return recipeType.getResult(craftingInventory);
			}
		}

		return ItemStack.EMPTY;
	}

	@Nullable
	public static RecipeType method_14262(CraftingInventory craftingInventory, World world) {
		for (RecipeType recipeType : REGISTRY) {
			if (recipeType.matches(craftingInventory, world)) {
				return recipeType;
			}
		}

		return null;
	}

	public static DefaultedList<ItemStack> method_13671(CraftingInventory craftingInventory, World world) {
		for (RecipeType recipeType : REGISTRY) {
			if (recipeType.matches(craftingInventory, world)) {
				return recipeType.method_13670(craftingInventory);
			}
		}

		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			defaultedList.set(i, craftingInventory.getInvStack(i));
		}

		return defaultedList;
	}

	@Nullable
	public static RecipeType get(Identifier identifier) {
		return REGISTRY.get(identifier);
	}

	public static int getRawId(RecipeType type) {
		return REGISTRY.getRawId(type);
	}

	@Nullable
	public static RecipeType getByRawId(int id) {
		return REGISTRY.getByRawId(id);
	}
}
