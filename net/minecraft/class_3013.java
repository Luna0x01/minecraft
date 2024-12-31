package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

public class class_3013 extends class_3844<class_3871> {
	private static final Identifier field_14846 = new Identifier("fossil/spine_1");
	private static final Identifier field_14847 = new Identifier("fossil/spine_2");
	private static final Identifier field_14848 = new Identifier("fossil/spine_3");
	private static final Identifier field_14849 = new Identifier("fossil/spine_4");
	private static final Identifier field_14850 = new Identifier("fossil/spine_1_coal");
	private static final Identifier field_14851 = new Identifier("fossil/spine_2_coal");
	private static final Identifier field_14852 = new Identifier("fossil/spine_3_coal");
	private static final Identifier field_14853 = new Identifier("fossil/spine_4_coal");
	private static final Identifier field_14854 = new Identifier("fossil/skull_1");
	private static final Identifier field_14855 = new Identifier("fossil/skull_2");
	private static final Identifier field_14856 = new Identifier("fossil/skull_3");
	private static final Identifier field_14857 = new Identifier("fossil/skull_4");
	private static final Identifier field_14858 = new Identifier("fossil/skull_1_coal");
	private static final Identifier field_14859 = new Identifier("fossil/skull_2_coal");
	private static final Identifier field_14860 = new Identifier("fossil/skull_3_coal");
	private static final Identifier field_14861 = new Identifier("fossil/skull_4_coal");
	private static final Identifier[] field_14862 = new Identifier[]{
		field_14846, field_14847, field_14848, field_14849, field_14854, field_14855, field_14856, field_14857
	};
	private static final Identifier[] field_14863 = new Identifier[]{
		field_14850, field_14851, field_14852, field_14853, field_14858, field_14859, field_14860, field_14861
	};

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		Random random2 = iWorld.getRandom();
		BlockRotation[] blockRotations = BlockRotation.values();
		BlockRotation blockRotation = blockRotations[random2.nextInt(blockRotations.length)];
		int i = random2.nextInt(field_14862.length);
		class_3998 lv = iWorld.method_3587().method_11956();
		Structure structure = lv.method_17682(field_14862[i]);
		Structure structure2 = lv.method_17682(field_14863[i]);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		BlockBox blockBox = new BlockBox(chunkPos.getActualX(), 0, chunkPos.getActualZ(), chunkPos.getOppositeX(), 256, chunkPos.getOppositeZ());
		StructurePlacementData structurePlacementData = new StructurePlacementData().method_11868(blockRotation).method_11869(blockBox).method_13388(random2);
		BlockPos blockPos2 = structure.method_11885(blockRotation);
		int j = random2.nextInt(16 - blockPos2.getX());
		int k = random2.nextInt(16 - blockPos2.getZ());
		int l = 256;

		for (int m = 0; m < blockPos2.getX(); m++) {
			for (int n = 0; n < blockPos2.getX(); n++) {
				l = Math.min(l, iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, blockPos.getX() + m + j, blockPos.getZ() + n + k));
			}
		}

		int o = Math.max(l - 15 - random2.nextInt(10), 10);
		BlockPos blockPos3 = structure.method_13393(blockPos.add(j, o, k), BlockMirror.NONE, blockRotation);
		structurePlacementData.method_13385(0.9F);
		structure.method_17697(iWorld, blockPos3, structurePlacementData, 4);
		structurePlacementData.method_13385(0.1F);
		structure2.method_17697(iWorld, blockPos3, structurePlacementData, 4);
		return true;
	}
}
