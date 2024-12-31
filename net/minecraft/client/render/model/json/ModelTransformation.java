package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.lang.reflect.Type;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Quaternion;

public class ModelTransformation {
	public static final ModelTransformation NONE = new ModelTransformation();
	public static float field_10976;
	public static float field_10977;
	public static float field_10978;
	public static float field_10979;
	public static float field_10980;
	public static float field_10981;
	public static float field_10982;
	public static float field_10983;
	public static float field_10984;
	public final Transformation thirdPerson;
	public final Transformation firstPerson;
	public final Transformation field_13566;
	public final Transformation field_13567;
	public final Transformation head;
	public final Transformation gui;
	public final Transformation ground;
	public final Transformation fixed;

	private ModelTransformation() {
		this(
			Transformation.DEFAULT,
			Transformation.DEFAULT,
			Transformation.DEFAULT,
			Transformation.DEFAULT,
			Transformation.DEFAULT,
			Transformation.DEFAULT,
			Transformation.DEFAULT,
			Transformation.DEFAULT
		);
	}

	public ModelTransformation(ModelTransformation modelTransformation) {
		this.thirdPerson = modelTransformation.thirdPerson;
		this.firstPerson = modelTransformation.firstPerson;
		this.field_13566 = modelTransformation.field_13566;
		this.field_13567 = modelTransformation.field_13567;
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
		this.thirdPerson = transformation;
		this.firstPerson = transformation2;
		this.field_13566 = transformation3;
		this.field_13567 = transformation4;
		this.head = transformation5;
		this.gui = transformation6;
		this.ground = transformation7;
		this.fixed = transformation8;
	}

	public void apply(ModelTransformation.Mode mode) {
		method_12374(this.getTransformation(mode), false);
	}

	public static void method_12374(Transformation transformation, boolean bl) {
		if (transformation != Transformation.DEFAULT) {
			int i = bl ? -1 : 1;
			GlStateManager.translate(
				(float)i * (field_10976 + transformation.translation.x), field_10977 + transformation.translation.y, field_10978 + transformation.translation.z
			);
			float f = field_10979 + transformation.rotation.x;
			float g = field_10980 + transformation.rotation.y;
			float h = field_10981 + transformation.rotation.z;
			if (bl) {
				g = -g;
				h = -h;
			}

			GlStateManager.method_12291(method_12373(f, g, h));
			GlStateManager.scale(field_10982 + transformation.scale.x, field_10983 + transformation.scale.y, field_10984 + transformation.scale.z);
		}
	}

	private static Quaternion method_12373(float f, float g, float h) {
		float i = f * (float) (Math.PI / 180.0);
		float j = g * (float) (Math.PI / 180.0);
		float k = h * (float) (Math.PI / 180.0);
		float l = MathHelper.sin(0.5F * i);
		float m = MathHelper.cos(0.5F * i);
		float n = MathHelper.sin(0.5F * j);
		float o = MathHelper.cos(0.5F * j);
		float p = MathHelper.sin(0.5F * k);
		float q = MathHelper.cos(0.5F * k);
		return new Quaternion(l * o * q + m * n * p, m * n * q - l * o * p, l * n * q + m * o * p, m * o * q - l * n * p);
	}

	public Transformation getTransformation(ModelTransformation.Mode renderMode) {
		switch (renderMode) {
			case THIRD_PERSON_LEFT_HAND:
				return this.thirdPerson;
			case THIRD_PERSON_RIGHT_HAND:
				return this.firstPerson;
			case FIRST_PERSON_LEFT_HAND:
				return this.field_13566;
			case FIRST_PERSON_RIGHT_HAND:
				return this.field_13567;
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
		return this.getTransformation(renderMode) != Transformation.DEFAULT;
	}

	static class Deserializer implements JsonDeserializer<ModelTransformation> {
		public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Transformation transformation = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
			Transformation transformation2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
			if (transformation2 == Transformation.DEFAULT) {
				transformation2 = transformation;
			}

			Transformation transformation3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_righthand");
			Transformation transformation4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
			if (transformation4 == Transformation.DEFAULT) {
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
			return json.has(key) ? (Transformation)ctx.deserialize(json.get(key), Transformation.class) : Transformation.DEFAULT;
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
	}
}
