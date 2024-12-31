package net.minecraft.client.render;

import net.minecraft.util.math.Box;

public interface VisibleRegion {
	boolean intersects(Box box);

	void setOrigin(double d, double e, double f);
}
