package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.structure.MineshaftPieces;
import net.minecraft.world.World;

public class MineshaftGeneratorConfig extends GeneratorConfig {
	public MineshaftGeneratorConfig() {
	}

	public MineshaftGeneratorConfig(World world, Random random, int i, int j) {
		super(i, j);
		MineshaftPieces.MineshaftRoom mineshaftRoom = new MineshaftPieces.MineshaftRoom(0, random, (i << 4) + 2, (j << 4) + 2);
		this.field_13015.add(mineshaftRoom);
		mineshaftRoom.fillOpenings(mineshaftRoom, this.field_13015, random);
		this.setBoundingBoxFromChildren();
		this.method_80(world, random, 10);
	}
}
