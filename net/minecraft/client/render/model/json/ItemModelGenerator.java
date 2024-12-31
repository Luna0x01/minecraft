package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.class_4231;
import net.minecraft.class_4306;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ItemModelGenerator {
	public static final List<String> LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

	public class_4231 method_19252(Function<Identifier, Sprite> function, class_4231 arg) {
		Map<String, String> map = Maps.newHashMap();
		List<ModelElement> list = Lists.newArrayList();

		for (int i = 0; i < LAYERS.size(); i++) {
			String string = (String)LAYERS.get(i);
			if (!arg.method_19223(string)) {
				break;
			}

			String string2 = arg.method_19225(string);
			map.put(string, string2);
			Sprite sprite = (Sprite)function.apply(new Identifier(string2));
			list.addAll(this.method_10063(i, string, sprite));
		}

		map.put("particle", arg.method_19223("particle") ? arg.method_19225("particle") : (String)map.get("layer0"));
		class_4231 lv = new class_4231(null, list, map, false, false, arg.method_19230(), arg.method_19228());
		lv.field_20785 = arg.field_20785;
		return lv;
	}

	private List<ModelElement> method_10063(int i, String string, Sprite sprite) {
		Map<Direction, ModelElementFace> map = Maps.newHashMap();
		map.put(Direction.SOUTH, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
		map.put(Direction.NORTH, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
		List<ModelElement> list = Lists.newArrayList();
		list.add(new ModelElement(new class_4306(0.0F, 0.0F, 7.5F), new class_4306(16.0F, 16.0F, 8.5F), map, null, true));
		list.addAll(this.method_10067(sprite, string, i));
		return list;
	}

	private List<ModelElement> method_10067(Sprite sprite, String string, int i) {
		float f = (float)sprite.getWidth();
		float g = (float)sprite.getHeight();
		List<ModelElement> list = Lists.newArrayList();

		for (ItemModelGenerator.Frame frame : this.method_10066(sprite)) {
			float h = 0.0F;
			float j = 0.0F;
			float k = 0.0F;
			float l = 0.0F;
			float m = 0.0F;
			float n = 0.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 0.0F;
			float r = 0.0F;
			float s = (float)frame.getMin();
			float t = (float)frame.getMax();
			float u = (float)frame.getLevel();
			ItemModelGenerator.Side side = frame.getSide();
			switch (side) {
				case UP:
					m = s;
					h = s;
					k = n = t + 1.0F;
					o = u;
					j = u;
					p = u;
					l = u;
					q = 16.0F / f;
					r = 16.0F / (g - 1.0F);
					break;
				case DOWN:
					p = u;
					o = u;
					m = s;
					h = s;
					k = n = t + 1.0F;
					j = u + 1.0F;
					l = u + 1.0F;
					q = 16.0F / f;
					r = 16.0F / (g - 1.0F);
					break;
				case LEFT:
					m = u;
					h = u;
					n = u;
					k = u;
					p = s;
					j = s;
					l = o = t + 1.0F;
					q = 16.0F / (f - 1.0F);
					r = 16.0F / g;
					break;
				case RIGHT:
					n = u;
					m = u;
					h = u + 1.0F;
					k = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
					q = 16.0F / (f - 1.0F);
					r = 16.0F / g;
			}

			float v = 16.0F / f;
			float w = 16.0F / g;
			h *= v;
			k *= v;
			j *= w;
			l *= w;
			j = 16.0F - j;
			l = 16.0F - l;
			m *= q;
			n *= q;
			o *= r;
			p *= r;
			Map<Direction, ModelElementFace> map = Maps.newHashMap();
			map.put(side.getDirection(), new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{m, o, n, p}, 0)));
			switch (side) {
				case UP:
					list.add(new ModelElement(new class_4306(h, j, 7.5F), new class_4306(k, j, 8.5F), map, null, true));
					break;
				case DOWN:
					list.add(new ModelElement(new class_4306(h, l, 7.5F), new class_4306(k, l, 8.5F), map, null, true));
					break;
				case LEFT:
					list.add(new ModelElement(new class_4306(h, j, 7.5F), new class_4306(h, l, 8.5F), map, null, true));
					break;
				case RIGHT:
					list.add(new ModelElement(new class_4306(k, j, 7.5F), new class_4306(k, l, 8.5F), map, null, true));
			}
		}

		return list;
	}

	private List<ItemModelGenerator.Frame> method_10066(Sprite sprite) {
		int i = sprite.getWidth();
		int j = sprite.getHeight();
		List<ItemModelGenerator.Frame> list = Lists.newArrayList();

		for (int k = 0; k < sprite.getSize(); k++) {
			for (int l = 0; l < j; l++) {
				for (int m = 0; m < i; m++) {
					boolean bl = !this.method_19251(sprite, k, m, l, i, j);
					this.method_10064(ItemModelGenerator.Side.UP, list, sprite, k, m, l, i, j, bl);
					this.method_10064(ItemModelGenerator.Side.DOWN, list, sprite, k, m, l, i, j, bl);
					this.method_10064(ItemModelGenerator.Side.LEFT, list, sprite, k, m, l, i, j, bl);
					this.method_10064(ItemModelGenerator.Side.RIGHT, list, sprite, k, m, l, i, j, bl);
				}
			}
		}

		return list;
	}

	private void method_10064(ItemModelGenerator.Side side, List<ItemModelGenerator.Frame> list, Sprite sprite, int i, int j, int k, int l, int m, boolean bl) {
		boolean bl2 = this.method_19251(sprite, i, j + side.getOffsetX(), k + side.getOffsetY(), l, m) && bl;
		if (bl2) {
			this.method_10068(list, side, j, k);
		}
	}

	private void method_10068(List<ItemModelGenerator.Frame> list, ItemModelGenerator.Side side, int i, int j) {
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

	private boolean method_19251(Sprite sprite, int i, int j, int k, int l, int m) {
		return j >= 0 && k >= 0 && j < l && k < m ? sprite.method_19517(i, j, k) : true;
	}

	static class Frame {
		private final ItemModelGenerator.Side field_10956;
		private int min;
		private int max;
		private final int level;

		public Frame(ItemModelGenerator.Side side, int i, int j) {
			this.field_10956 = side;
			this.min = i;
			this.max = i;
			this.level = j;
		}

		public void expand(int newValue) {
			if (newValue < this.min) {
				this.min = newValue;
			} else if (newValue > this.max) {
				this.max = newValue;
			}
		}

		public ItemModelGenerator.Side getSide() {
			return this.field_10956;
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
		UP(Direction.UP, 0, -1),
		DOWN(Direction.DOWN, 0, 1),
		LEFT(Direction.EAST, -1, 0),
		RIGHT(Direction.WEST, 1, 0);

		private final Direction field_10964;
		private final int offsetX;
		private final int offsetY;

		private Side(Direction direction, int j, int k) {
			this.field_10964 = direction;
			this.offsetX = j;
			this.offsetY = k;
		}

		public Direction getDirection() {
			return this.field_10964;
		}

		public int getOffsetX() {
			return this.offsetX;
		}

		public int getOffsetY() {
			return this.offsetY;
		}

		private boolean isVertical() {
			return this == DOWN || this == UP;
		}
	}
}
