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

	public ModelTransformation(ModelTransformation other) {
		this.thirdPersonLeftHand = other.thirdPersonLeftHand;
		this.thirdPersonRightHand = other.thirdPersonRightHand;
		this.firstPersonLeftHand = other.firstPersonLeftHand;
		this.firstPersonRightHand = other.firstPersonRightHand;
		this.head = other.head;
		this.gui = other.gui;
		this.ground = other.ground;
		this.fixed = other.fixed;
	}

	public ModelTransformation(
		Transformation thirdPersonLeftHand,
		Transformation thirdPersonRightHand,
		Transformation firstPersonLeftHand,
		Transformation firstPersonRightHand,
		Transformation head,
		Transformation gui,
		Transformation ground,
		Transformation fixed
	) {
		this.thirdPersonLeftHand = thirdPersonLeftHand;
		this.thirdPersonRightHand = thirdPersonRightHand;
		this.firstPersonLeftHand = firstPersonLeftHand;
		this.firstPersonRightHand = firstPersonRightHand;
		this.head = head;
		this.gui = gui;
		this.ground = ground;
		this.fixed = fixed;
	}

	public Transformation getTransformation(ModelTransformation.Mode renderMode) {
		switch (renderMode) {
			case THIRD_PERSON_LEFT_HAND:
				return this.thirdPersonLeftHand;
			case THIRD_PERSON_RIGHT_HAND:
				return this.thirdPersonRightHand;
			case FIRST_PERSON_LEFT_HAND:
				return this.firstPersonLeftHand;
			case FIRST_PERSON_RIGHT_HAND:
				return this.firstPersonRightHand;
			case HEAD:
				return this.head;
			case GUI:
				return this.gui;
			case GROUND:
				return this.ground;
			case FIXED:
				return this.fixed;
			default:
				return Transformation.IDENTITY;
		}
	}

	public boolean isTransformationDefined(ModelTransformation.Mode renderMode) {
		return this.getTransformation(renderMode) != Transformation.IDENTITY;
	}

	protected static class Deserializer implements JsonDeserializer<ModelTransformation> {
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

		private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, String key) {
			return json.has(key) ? (Transformation)ctx.deserialize(json.get(key), Transformation.class) : Transformation.IDENTITY;
		}
	}

	public static enum Mode {
		NONE,
		THIRD_PERSON_LEFT_HAND,
		THIRD_PERSON_RIGHT_HAND,
		FIRST_PERSON_LEFT_HAND,
		FIRST_PERSON_RIGHT_HAND,
		HEAD,
		GUI,
		GROUND,
		FIXED;

		public boolean isFirstPerson() {
			return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
		}
	}
}
