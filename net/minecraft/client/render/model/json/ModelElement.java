package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4230;
import net.minecraft.class_4306;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class ModelElement {
	public final class_4306 field_20777;
	public final class_4306 field_20778;
	public final Map<Direction, ModelElementFace> faces;
	public final class_4230 field_10913;
	public final boolean shade;

	public ModelElement(class_4306 arg, class_4306 arg2, Map<Direction, ModelElementFace> map, @Nullable class_4230 arg3, boolean bl) {
		this.field_20777 = arg;
		this.field_20778 = arg2;
		this.faces = map;
		this.field_10913 = arg3;
		this.shade = bl;
		this.initTextures();
	}

	private void initTextures() {
		for (Entry<Direction, ModelElementFace> entry : this.faces.entrySet()) {
			float[] fs = this.getRotatedMatrix((Direction)entry.getKey());
			((ModelElementFace)entry.getValue()).textureReference.setUvs(fs);
		}
	}

	private float[] getRotatedMatrix(Direction direction) {
		switch (direction) {
			case DOWN:
				return new float[]{
					this.field_20777.method_19662(), 16.0F - this.field_20778.method_19670(), this.field_20778.method_19662(), 16.0F - this.field_20777.method_19670()
				};
			case UP:
				return new float[]{this.field_20777.method_19662(), this.field_20777.method_19670(), this.field_20778.method_19662(), this.field_20778.method_19670()};
			case NORTH:
			default:
				return new float[]{
					16.0F - this.field_20778.method_19662(),
					16.0F - this.field_20778.method_19667(),
					16.0F - this.field_20777.method_19662(),
					16.0F - this.field_20777.method_19667()
				};
			case SOUTH:
				return new float[]{
					this.field_20777.method_19662(), 16.0F - this.field_20778.method_19667(), this.field_20778.method_19662(), 16.0F - this.field_20777.method_19667()
				};
			case WEST:
				return new float[]{
					this.field_20777.method_19670(), 16.0F - this.field_20778.method_19667(), this.field_20778.method_19670(), 16.0F - this.field_20777.method_19667()
				};
			case EAST:
				return new float[]{
					16.0F - this.field_20778.method_19670(),
					16.0F - this.field_20778.method_19667(),
					16.0F - this.field_20777.method_19670(),
					16.0F - this.field_20777.method_19667()
				};
		}
	}

	static class Deserializer implements JsonDeserializer<ModelElement> {
		public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_4306 lv = this.method_19206(jsonObject);
			class_4306 lv2 = this.method_19205(jsonObject);
			class_4230 lv3 = this.method_9987(jsonObject);
			Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
			if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
				throw new JsonParseException("Expected shade to be a Boolean");
			} else {
				boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
				return new ModelElement(lv, lv2, map, lv3, bl);
			}
		}

		@Nullable
		private class_4230 method_9987(JsonObject jsonObject) {
			class_4230 lv = null;
			if (jsonObject.has("rotation")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "rotation");
				class_4306 lv2 = this.method_9988(jsonObject2, "origin");
				lv2.method_19663(0.0625F);
				Direction.Axis axis = this.deserializeAxis(jsonObject2);
				float f = this.deserializeRotationAngle(jsonObject2);
				boolean bl = JsonHelper.getBoolean(jsonObject2, "rescale", false);
				lv = new class_4230(lv2, axis, f, bl);
			}

			return lv;
		}

		private float deserializeRotationAngle(JsonObject object) {
			float f = JsonHelper.getFloat(object, "angle");
			if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
				throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			} else {
				return f;
			}
		}

		private Direction.Axis deserializeAxis(JsonObject object) {
			String string = JsonHelper.getString(object, "axis");
			Direction.Axis axis = Direction.Axis.fromName(string.toLowerCase(Locale.ROOT));
			if (axis == null) {
				throw new JsonParseException("Invalid rotation axis: " + string);
			} else {
				return axis;
			}
		}

		private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext context, JsonObject object) {
			Map<Direction, ModelElementFace> map = this.deserializeFaces(context, object);
			if (map.isEmpty()) {
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			} else {
				return map;
			}
		}

		private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext context, JsonObject object) {
			Map<Direction, ModelElementFace> map = Maps.newEnumMap(Direction.class);
			JsonObject jsonObject = JsonHelper.getObject(object, "faces");

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				Direction direction = this.getDirection((String)entry.getKey());
				map.put(direction, context.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
			}

			return map;
		}

		private Direction getDirection(String name) {
			Direction direction = Direction.byName(name);
			if (direction == null) {
				throw new JsonParseException("Unknown facing: " + name);
			} else {
				return direction;
			}
		}

		private class_4306 method_19205(JsonObject jsonObject) {
			class_4306 lv = this.method_9988(jsonObject, "to");
			if (!(lv.method_19662() < -16.0F)
				&& !(lv.method_19667() < -16.0F)
				&& !(lv.method_19670() < -16.0F)
				&& !(lv.method_19662() > 32.0F)
				&& !(lv.method_19667() > 32.0F)
				&& !(lv.method_19670() > 32.0F)) {
				return lv;
			} else {
				throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + lv);
			}
		}

		private class_4306 method_19206(JsonObject jsonObject) {
			class_4306 lv = this.method_9988(jsonObject, "from");
			if (!(lv.method_19662() < -16.0F)
				&& !(lv.method_19667() < -16.0F)
				&& !(lv.method_19670() < -16.0F)
				&& !(lv.method_19662() > 32.0F)
				&& !(lv.method_19667() > 32.0F)
				&& !(lv.method_19670() > 32.0F)) {
				return lv;
			} else {
				throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + lv);
			}
		}

		private class_4306 method_9988(JsonObject jsonObject, String string) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, string);
			if (jsonArray.size() != 3) {
				throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
			} else {
				float[] fs = new float[3];

				for (int i = 0; i < fs.length; i++) {
					fs[i] = JsonHelper.asFloat(jsonArray.get(i), string + "[" + i + "]");
				}

				return new class_4306(fs[0], fs[1], fs[2]);
			}
		}
	}
}
