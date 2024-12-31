package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class ModelTransformation {
	public static final ModelTransformation NONE = new ModelTransformation();
	public final Transformation thirdPersonLeftHand;
	public final Transformation thirdPersonRightHand;
	public final Transformation firstPersonLeftHand;
	public final Transformation firstPersonRightHand;
	public final Transformation head;
	public final Transformation gui;
	public final Transformation ground;
	public final Transformation fixed;

	private ModelTransformation() {
		this(
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY
		);
	}

	public ModelTransformation(ModelTransformation modelTransformation) {
		this.thirdPersonLeftHand = modelTransformation.thirdPersonLeftHand;
		this.thirdPersonRightHand = modelTransformation.thirdPersonRightHand;
		this.firstPersonLeftHand = modelTransformation.firstPersonLeftHand;
		this.firstPersonRightHand = modelTransformation.firstPersonRightHand;
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
		Transformation transformation6,
		Transformation transformation7,
		Transformation transformation8
	) {
		this.thirdPersonLeftHand = transformation;
		this.thirdPersonRightHand = transformation2;
		this.firstPersonLeftHand = transformation3;
		this.firstPersonRightHand = transformation4;
		this.head = transformation5;
		this.gui = transformation6;
		this.ground = transformation7;
		this.fixed = transformation8;
	}

	public Transformation getTransformation(ModelTransformation.Mode mode) {
		switch (mode) {
			case field_4323:
				return this.thirdPersonLeftHand;
			case field_4320:
				return this.thirdPersonRightHand;
			case field_4321:
				return this.firstPersonLeftHand;
			case field_4322:
				return this.firstPersonRightHand;
			case field_4316:
				return this.head;
			case field_4317:
				return this.gui;
			case field_4318:
				return this.ground;
			case field_4319:
				return this.fixed;
			default:
				return Transformation.IDENTITY;
		}
	}

	public boolean isTransformationDefined(ModelTransformation.Mode mode) {
		return this.getTransformation(mode) != Transformation.IDENTITY;
	}

	public static class Deserializer implements JsonDeserializer<ModelTransformation> {
		protected Deserializer() {
		}

		public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Transformation transformation = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
			Transformation transformation2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
			if (transformation2 == Transformation.IDENTITY) {
				transformation2 = transformation;
			}

			Transformation transformation3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_righthand");
			Transformation transformation4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
			if (transformation4 == Transformation.IDENTITY) {
				transformation4 = transformation3;
			}

			Transformation transformation5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "head");
			Transformation transformation6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "gui");
			Transformation transformation7 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "ground");
			Transformation transformation8 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "fixed");
			return new ModelTransformation(
				transformation2, transformation, transformation4, transformation3, transformation5, transformation6, transformation7, transformation8
			);
		}

		private Transformation parseModelTransformation(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
			return jsonObject.has(string)
				? (Transformation)jsonDeserializationContext.deserialize(jsonObject.get(string), Transformation.class)
				: Transformation.IDENTITY;
		}
	}

	public static enum Mode {
		field_4315,
		field_4323,
		field_4320,
		field_4321,
		field_4322,
		field_4316,
		field_4317,
		field_4318,
		field_4319;
	}
}
