package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.structure.MineshaftPieces;
import net.minecraft.structure.MineshaftStructure;
import net.minecraft.structure.StructurePiece;
import net.minecraft.world.World;

public class MineshaftGeneratorConfig extends GeneratorConfig {
	private MineshaftStructure.class_3014 field_14869;

	public MineshaftGeneratorConfig() {
	}

	public MineshaftGeneratorConfig(World world, Random random, int i, int j, MineshaftStructure.class_3014 arg) {
		super(i, j);
		this.field_14869 = arg;
		MineshaftPieces.MineshaftRoom mineshaftRoom = new MineshaftPieces.MineshaftRoom(0, random, (i << 4) + 2, (j << 4) + 2, this.field_14869);
		this.field_13015.add(mineshaftRoom);
		mineshaftRoom.fillOpenings(mineshaftRoom, this.field_13015, random);
		this.setBoundingBoxFromChildren();
		if (arg == MineshaftStructure.class_3014.MESA) {
			int k = -5;
			int l = world.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
			this.boundingBox.move(0, l, 0);

			for (StructurePiece structurePiece : this.field_13015) {
				structurePiece.translate(0, l, 0);
			}
		} else {
			this.method_80(world, random, 10);
		}
	}
}
