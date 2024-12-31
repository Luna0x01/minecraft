package net.minecraft;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.class_2737;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.class_2762;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

public class class_3988 {
	private static final BlockPos field_19395 = new BlockPos(4, 0, 15);
	private static final Identifier[] field_19396 = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};
	private static final Identifier[] field_19397 = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/upsidedown_full"),
		new Identifier("shipwreck/upsidedown_fronthalf"),
		new Identifier("shipwreck/upsidedown_backhalf"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/upsidedown_full_degraded"),
		new Identifier("shipwreck/upsidedown_fronthalf_degraded"),
		new Identifier("shipwreck/upsidedown_backhalf_degraded"),
		new Identifier("shipwreck/sideways_full_degraded"),
		new Identifier("shipwreck/sideways_fronthalf_degraded"),
		new Identifier("shipwreck/sideways_backhalf_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};

	public static void method_17637() {
		StructurePieceManager.registerPiece(class_3988.class_3989.class, "Shipwreck");
	}

	public static void method_17638(class_3998 arg, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random, class_3890 arg2) {
		Identifier identifier = arg2.field_19248 ? field_19396[random.nextInt(field_19396.length)] : field_19397[random.nextInt(field_19397.length)];
		list.add(new class_3988.class_3989(arg, identifier, blockPos, blockRotation, arg2.field_19248));
	}

	public static class class_3989 extends class_2762 {
		private BlockRotation field_19398;
		private Identifier field_19399;
		private boolean field_19400;

		public class_3989() {
		}

		public class_3989(class_3998 arg, Identifier identifier, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
			super(0);
			this.field_13018 = blockPos;
			this.field_19398 = blockRotation;
			this.field_19399 = identifier;
			this.field_19400 = bl;
			this.method_17640(arg);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putString("Template", this.field_19399.toString());
			structureNbt.putBoolean("isBeached", this.field_19400);
			structureNbt.putString("Rot", this.field_19398.name());
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_19399 = new Identifier(nbtCompound.getString("Template"));
			this.field_19400 = nbtCompound.getBoolean("isBeached");
			this.field_19398 = BlockRotation.valueOf(nbtCompound.getString("Rot"));
			this.method_17640(arg);
		}

		private void method_17640(class_3998 arg) {
			Structure structure = arg.method_17682(this.field_19399);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.method_11868(this.field_19398)
				.method_11866(Blocks.AIR)
				.method_11867(BlockMirror.NONE)
				.method_17691(class_3988.field_19395);
			this.method_11856(structure, this.field_13018, structurePlacementData);
		}

		@Override
		protected void method_11857(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox) {
			if ("map_chest".equals(string)) {
				class_2737.method_16833(iWorld, random, blockPos.down(), LootTables.SHIPWRECK_MAP_CHEST);
			} else if ("treasure_chest".equals(string)) {
				class_2737.method_16833(iWorld, random, blockPos.down(), LootTables.SHIPWRECK_TREASURE_CHEST);
			} else if ("supply_chest".equals(string)) {
				class_2737.method_16833(iWorld, random, blockPos.down(), LootTables.SHIPWRECK_SUPPLY_CHEST);
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			int i = 256;
			int j = 0;
			BlockPos blockPos = this.field_13018.add(this.field_13016.getSize().getX() - 1, 0, this.field_13016.getSize().getZ() - 1);

			for (BlockPos blockPos2 : BlockPos.iterate(this.field_13018, blockPos)) {
				int k = iWorld.method_16372(
					this.field_19400 ? class_3804.class_3805.WORLD_SURFACE_WG : class_3804.class_3805.OCEAN_FLOOR_WG, blockPos2.getX(), blockPos2.getZ()
				);
				j += k;
				i = Math.min(i, k);
			}

			j /= this.field_13016.getSize().getX() * this.field_13016.getSize().getZ();
			int l = this.field_19400 ? i - this.field_13016.getSize().getY() / 2 - random.nextInt(3) : j;
			this.field_13018 = new BlockPos(this.field_13018.getX(), l, this.field_13018.getZ());
			return super.method_58(iWorld, random, blockBox, chunkPos);
		}
	}
}
