package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.lang.reflect.Type;

public class ModelTransformation {
	public static final ModelTransformation NONE = new ModelTransformation();
	public static float field_10976 = 0.0F;
	public static float field_10977 = 0.0F;
	public static float field_10978 = 0.0F;
	public static float field_10979 = 0.0F;
	public static float field_10980 = 0.0F;
	public static float field_10981 = 0.0F;
	public static float field_10982 = 0.0F;
	public static float field_10983 = 0.0F;
	public static float field_10984 = 0.0F;
	public final Transformation thirdPerson;
	public final Transformation firstPerson;
	public final Transformation head;
	public final Transformation gui;
	public final Transformation ground;
	public final Transformation fixed;

	private ModelTransformation() {
		this(Transformation.DEFAULT, Transformation.DEFAULT, Transformation.DEFAULT, Transformation.DEFAULT, Transformation.DEFAULT, Transformation.DEFAULT);
	}

	public ModelTransformation(ModelTransformation modelTransformation) {
		this.thirdPerson = modelTransformation.thirdPerson;
		this.firstPerson = modelTransformation.firstPerson;
		this.head = modelTransformation.head;
		this.gui = modelTransformation.gui;
		this.ground = modelTransformation.ground;
		this.fixed = modelTransformation.fixed;
	}

	public ModelTransformation(
		Transformation transformation,
		Transformation transformation2,
		Transformation transformation3,
		Transformation transformation4,
		Transformation transformation5,
		Transformation transformation6
	) {
		this.thirdPerson = transformation;
		this.firstPerson = transformation2;
		this.head = transformation3;
		this.gui = transformation4;
		this.ground = transformation5;
		this.fixed = transformation6;
	}

	public void apply(ModelTransformation.Mode mode) {
		Transformation transformation = this.getTransformation(mode);
		if (transformation != Transformation.DEFAULT) {
			GlStateManager.translate(transformation.translation.x + field_10976, transformation.translation.y + field_10977, transformation.translation.z + field_10978);
			GlStateManager.rotate(transformation.rotation.y + field_10980, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(transformation.rotation.x + field_10979, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(transformation.rotation.z + field_10981, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(transformation.scale.x + field_10982, transformation.scale.y + field_10983, transformation.scale.z + field_10984);
		}
	}

	public Transformation getTransformation(ModelTransformation.Mode renderMode) {
		switch (renderMode) {
			case THIRD_PERSON:
				return this.thirdPerson;
			case FIRST_PERSON:
				return this.firstPerson;
			case HEAD:
				return this.head;
			case GUI:
				return this.gui;
			case GROUND:
				return this.ground;
			case FIXED:
				return this.fixed;
			default:
				return Transformation.DEFAULT;
		}
	}

	public boolean isTransformationDefined(ModelTransformation.Mode renderMode) {
		return !this.getTransformation(renderMode).equals(Transformation.DEFAULT);
	}

	static class Deserializer implements JsonDeserializer<ModelTransformation> {
		public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Transformation transformation = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson");
			Transformation transformation2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson");
			Transformation transformation3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "head");
			Transformation transformation4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "gui");
			Transformation transformation5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "ground");
			Transformation transformation6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "fixed");
			return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6);
		}

		private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, String key) {
			return json.has(key) ? (Transformation)ctx.deserialize(json.get(key), Transformation.class) : Transformation.DEFAULT;
		}
	}

	public static enum Mode {
		NONE,
		THIRD_PERSON,
		FIRST_PERSON,
		HEAD,
		GUI,
		GROUND,
		FIXED;
	}
}
