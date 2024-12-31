package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

public class ItemModelGenerator {
	public static final List<String> LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

	public JsonUnbakedModel create(Function<SpriteIdentifier, Sprite> function, JsonUnbakedModel jsonUnbakedModel) {
		Map<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
		List<ModelElement> list = Lists.newArrayList();

		for (int i = 0; i < LAYERS.size(); i++) {
			String string = (String)LAYERS.get(i);
			if (!jsonUnbakedModel.textureExists(string)) {
				break;
			}

			SpriteIdentifier spriteIdentifier = jsonUnbakedModel.resolveSprite(string);
			map.put(string, Either.left(spriteIdentifier));
			Sprite sprite = (Sprite)function.apply(spriteIdentifier);
			list.addAll(this.addLayerElements(i, string, sprite));
		}

		map.put("particle", jsonUnbakedModel.textureExists("particle") ? Either.left(jsonUnbakedModel.resolveSprite("particle")) : (Either)map.get("layer0"));
		JsonUnbakedModel jsonUnbakedModel2 = new JsonUnbakedModel(
			null, list, map, false, jsonUnbakedModel.getGuiLight(), jsonUnbakedModel.getTransformations(), jsonUnbakedModel.getOverrides()
		);
		jsonUnbakedModel2.id = jsonUnbakedModel.id;
		return jsonUnbakedModel2;
	}

	private List<ModelElement> addLayerElements(int i, String string, Sprite sprite) {
		Map<Direction, ModelElementFace> map = Maps.newHashMap();
		map.put(Direction.field_11035, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
		map.put(Direction.field_11043, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
		List<ModelElement> list = Lists.newArrayList();
		list.add(new ModelElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, null, true));
		list.addAll(this.addSubComponents(sprite, string, i));
		return list;
	}

	private List<ModelElement> addSubComponents(Sprite sprite, String string, int i) {
		float f = (float)sprite.getWidth();
		float g = (float)sprite.getHeight();
		List<ModelElement> list = Lists.newArrayList();

		for (ItemModelGenerator.Frame frame : this.getFrames(sprite)) {
			float h = 0.0F;
			float j = 0.0F;
			float k = 0.0F;
			float l = 0.0F;
			float m = 0.0F;
			float n = 0.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 16.0F / f;
			float r = 16.0F / g;
			float s = (float)frame.getMin();
			float t = (float)frame.getMax();
			float u = (float)frame.getLevel();
			ItemModelGenerator.Side side = frame.getSide();
			switch (side) {
				case field_4281:
					m = s;
					h = s;
					k = n = t + 1.0F;
					o = u;
					j = u;
					l = u;
					p = u + 1.0F;
					break;
				case field_4277:
					o = u;
					p = u + 1.0F;
					m = s;
					h = s;
					k = n = t + 1.0F;
					j = u + 1.0F;
					l = u + 1.0F;
					break;
				case field_4278:
					m = u;
					h = u;
					k = u;
					n = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
					break;
				case field_4283:
					m = u;
					n = u + 1.0F;
					h = u + 1.0F;
					k = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
			}

			h *= q;
			k *= q;
			j *= r;
			l *= r;
			j = 16.0F - j;
			l = 16.0F - l;
			m *= q;
			n *= q;
			o *= r;
			p *= r;
			Map<Direction, ModelElementFace> map = Maps.newHashMap();
			map.put(side.getDirection(), new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{m, o, n, p}, 0)));
			switch (side) {
				case field_4281:
					list.add(new ModelElement(new Vector3f(h, j, 7.5F), new Vector3f(k, j, 8.5F), map, null, true));
					break;
				case field_4277:
					list.add(new ModelElement(new Vector3f(h, l, 7.5F), new Vector3f(k, l, 8.5F), map, null, true));
					break;
				case field_4278:
					list.add(new ModelElement(new Vector3f(h, j, 7.5F), new Vector3f(h, l, 8.5F), map, null, true));
					break;
				case field_4283:
					list.add(new ModelElement(new Vector3f(k, j, 7.5F), new Vector3f(k, l, 8.5F), map, null, true));
			}
		}

		return list;
	}

	private List<ItemModelGenerator.Frame> getFrames(Sprite sprite) {
		int i = sprite.getWidth();
		int j = sprite.getHeight();
		List<ItemModelGenerator.Frame> list = Lists.newArrayList();

		for (int k = 0; k < sprite.getFrameCount(); k++) {
			for (int l = 0; l < j; l++) {
				for (int m = 0; m < i; m++) {
					boolean bl = !this.isPixelTransparent(sprite, k, m, l, i, j);
					this.buildCube(ItemModelGenerator.Side.field_4281, list, sprite, k, m, l, i, j, bl);
					this.buildCube(ItemModelGenerator.Side.field_4277, list, sprite, k, m, l, i, j, bl);
					this.buildCube(ItemModelGenerator.Side.field_4278, list, sprite, k, m, l, i, j, bl);
					this.buildCube(ItemModelGenerator.Side.field_4283, list, sprite, k, m, l, i, j, bl);
				}
			}
		}

		return list;
	}

	private void buildCube(ItemModelGenerator.Side side, List<ItemModelGenerator.Frame> list, Sprite sprite, int i, int j, int k, int l, int m, boolean bl) {
		boolean bl2 = this.isPixelTransparent(sprite, i, j + side.getOffsetX(), k + side.getOffsetY(), l, m) && bl;
		if (bl2) {
			this.buildCube(list, side, j, k);
		}
	}

	private void buildCube(List<ItemModelGenerator.Frame> list, ItemModelGenerator.Side side, int i, int j) {
		ItemModelGenerator.Frame frame = null;

		for (ItemModelGenerator.Frame frame2 : list) {
			if (frame2.getSide() == side) {
				int k = side.isVertical() ? j : i;
				if (frame2.getLevel() == k) {
					frame = frame2;
					break;
				}
			}
		}

		int l = side.isVertical() ? j : i;
		int m = side.isVertical() ? i : j;
		if (frame == null) {
			list.add(new ItemModelGenerator.Frame(side, m, l));
		} else {
			frame.expand(m);
		}
	}

	private boolean isPixelTransparent(Sprite sprite, int i, int j, int k, int l, int m) {
		return j >= 0 && k >= 0 && j < l && k < m ? sprite.isPixelTransparent(i, j, k) : true;
	}

	static class Frame {
		private final ItemModelGenerator.Side side;
		private int min;
		private int max;
		private final int level;

		public Frame(ItemModelGenerator.Side side, int i, int j) {
			this.side = side;
			this.min = i;
			this.max = i;
			this.level = j;
		}

		public void expand(int i) {
			if (i < this.min) {
				this.min = i;
			} else if (i > this.max) {
				this.max = i;
			}
		}

		public ItemModelGenerator.Side getSide() {
			return this.side;
		}

		public int getMin() {
			return this.min;
		}

		public int getMax() {
			return this.max;
		}

		public int getLevel() {
			return this.level;
		}
	}

	static enum Side {
		field_4281(Direction.field_11036, 0, -1),
		field_4277(Direction.field_11033, 0, 1),
		field_4278(Direction.field_11034, -1, 0),
		field_4283(Direction.field_11039, 1, 0);

		private final Direction direction;
		private final int offsetX;
		private final int offsetY;

		private Side(Direction direction, int j, int k) {
			this.direction = direction;
			this.offsetX = j;
			this.offsetY = k;
		}

		public Direction getDirection() {
			return this.direction;
		}

		public int getOffsetX() {
			return this.offsetX;
		}

		public int getOffsetY() {
			return this.offsetY;
		}

		private boolean isVertical() {
			return this == field_4277 || this == field_4281;
		}
	}
}
