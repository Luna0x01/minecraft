package net.minecraft.util.math;

public class Boxes {
	public static Box stretch(Box box, Direction direction, double d) {
		double e = d * (double)direction.getDirection().offset();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (direction) {
			case field_11039:
				return new Box(box.x1 + f, box.y1, box.z1, box.x1 + g, box.y2, box.z2);
			case field_11034:
				return new Box(box.x2 + f, box.y1, box.z1, box.x2 + g, box.y2, box.z2);
			case field_11033:
				return new Box(box.x1, box.y1 + f, box.z1, box.x2, box.y1 + g, box.z2);
			case field_11036:
			default:
				return new Box(box.x1, box.y2 + f, box.z1, box.x2, box.y2 + g, box.z2);
			case field_11043:
				return new Box(box.x1, box.y1, box.z1 + f, box.x2, box.y2, box.z1 + g);
			case field_11035:
				return new Box(box.x1, box.y1, box.z2 + f, box.x2, box.y2, box.z2 + g);
		}
	}
}
