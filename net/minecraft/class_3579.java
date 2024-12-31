package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.recipe.ArmorDyeRecipeType;
import net.minecraft.recipe.BookCloningRecipeType;
import net.minecraft.recipe.MapCloningRecipeType;
import net.minecraft.recipe.MapUpscaleRecipeType;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RepairingRecipeType;
import net.minecraft.recipe.ShapedRecipeType;
import net.minecraft.recipe.ShapelessRecipeType;
import net.minecraft.recipe.ShieldRecipeDispatcher;
import net.minecraft.recipe.TippedArrowRecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class class_3579 {
	private static final Map<String, class_3578<?>> field_17463 = Maps.newHashMap();
	public static final class_3578<ShapedRecipeType> field_17447 = method_16218(new ShapedRecipeType.class_3581());
	public static final class_3578<ShapelessRecipeType> field_17448 = method_16218(new ShapelessRecipeType.class_3582());
	public static final class_3579.class_3580<ArmorDyeRecipeType> field_17449 = method_16218(
		new class_3579.class_3580<>("crafting_special_armordye", ArmorDyeRecipeType::new)
	);
	public static final class_3579.class_3580<BookCloningRecipeType> field_17450 = method_16218(
		new class_3579.class_3580<>("crafting_special_bookcloning", BookCloningRecipeType::new)
	);
	public static final class_3579.class_3580<MapCloningRecipeType> field_17451 = method_16218(
		new class_3579.class_3580<>("crafting_special_mapcloning", MapCloningRecipeType::new)
	);
	public static final class_3579.class_3580<MapUpscaleRecipeType> field_17452 = method_16218(
		new class_3579.class_3580<>("crafting_special_mapextending", MapUpscaleRecipeType::new)
	);
	public static final class_3579.class_3580<class_3572> field_17453 = method_16218(
		new class_3579.class_3580<>("crafting_special_firework_rocket", class_3572::new)
	);
	public static final class_3579.class_3580<class_3574> field_17454 = method_16218(
		new class_3579.class_3580<>("crafting_special_firework_star", class_3574::new)
	);
	public static final class_3579.class_3580<class_3573> field_17455 = method_16218(
		new class_3579.class_3580<>("crafting_special_firework_star_fade", class_3573::new)
	);
	public static final class_3579.class_3580<RepairingRecipeType> field_17456 = method_16218(
		new class_3579.class_3580<>("crafting_special_repairitem", RepairingRecipeType::new)
	);
	public static final class_3579.class_3580<TippedArrowRecipeType> field_17457 = method_16218(
		new class_3579.class_3580<>("crafting_special_tippedarrow", TippedArrowRecipeType::new)
	);
	public static final class_3579.class_3580<class_3570> field_17458 = method_16218(
		new class_3579.class_3580<>("crafting_special_bannerduplicate", class_3570::new)
	);
	public static final class_3579.class_3580<class_3569> field_17459 = method_16218(
		new class_3579.class_3580<>("crafting_special_banneraddpattern", class_3569::new)
	);
	public static final class_3579.class_3580<ShieldRecipeDispatcher> field_17460 = method_16218(
		new class_3579.class_3580<>("crafting_special_shielddecoration", ShieldRecipeDispatcher::new)
	);
	public static final class_3579.class_3580<class_3583> field_17461 = method_16218(
		new class_3579.class_3580<>("crafting_special_shulkerboxcoloring", class_3583::new)
	);
	public static final class_3578<class_3584> field_17462 = method_16218(new class_3584.class_3585());

	public static <S extends class_3578<T>, T extends RecipeType> S method_16218(S arg) {
		if (field_17463.containsKey(arg.method_16213())) {
			throw new IllegalArgumentException("Duplicate recipe serializer " + arg.method_16213());
		} else {
			field_17463.put(arg.method_16213(), arg);
			return arg;
		}
	}

	public static RecipeType method_16220(Identifier identifier, JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "type");
		class_3578<?> lv = (class_3578<?>)field_17463.get(string);
		if (lv == null) {
			throw new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'");
		} else {
			return lv.method_16215(identifier, jsonObject);
		}
	}

	public static RecipeType method_16219(PacketByteBuf packetByteBuf) {
		Identifier identifier = packetByteBuf.readIdentifier();
		String string = packetByteBuf.readString(32767);
		class_3578<?> lv = (class_3578<?>)field_17463.get(string);
		if (lv == null) {
			throw new IllegalArgumentException("Unknown recipe serializer " + string);
		} else {
			return lv.method_16216(identifier, packetByteBuf);
		}
	}

	public static <T extends RecipeType> void method_16217(T recipeType, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeIdentifier(recipeType.method_16202());
		packetByteBuf.writeString(recipeType.method_16200().method_16213());
		class_3578<T> lv = (class_3578<T>)recipeType.method_16200();
		lv.method_16214(packetByteBuf, recipeType);
	}

	public static final class class_3580<T extends RecipeType> implements class_3578<T> {
		private final String field_17464;
		private final Function<Identifier, T> field_17465;

		public class_3580(String string, Function<Identifier, T> function) {
			this.field_17464 = string;
			this.field_17465 = function;
		}

		@Override
		public T method_16215(Identifier identifier, JsonObject jsonObject) {
			return (T)this.field_17465.apply(identifier);
		}

		@Override
		public T method_16216(Identifier identifier, PacketByteBuf packetByteBuf) {
			return (T)this.field_17465.apply(identifier);
		}

		@Override
		public void method_16214(PacketByteBuf packetByteBuf, T recipeType) {
		}

		@Override
		public String method_16213() {
			return this.field_17464;
		}
	}
}
