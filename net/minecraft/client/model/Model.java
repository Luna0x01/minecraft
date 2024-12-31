package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class Model {
	public final List<Cuboid> cuboidList = Lists.newArrayList();
	public int textureWidth = 64;
	public int textureHeight = 32;

	public Cuboid getRandomCuboid(Random random) {
		return (Cuboid)this.cuboidList.get(random.nextInt(this.cuboidList.size()));
	}
}
