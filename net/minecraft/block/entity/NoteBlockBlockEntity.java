package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class NoteBlockBlockEntity extends BlockEntity {
	public byte note;
	public boolean powered;

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putByte("note", this.note);
		nbt.putBoolean("powered", this.powered);
		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.note = nbt.getByte("note");
		this.note = (byte)MathHelper.clamp(this.note, 0, 24);
		this.powered = nbt.getBoolean("powered");
	}

	public void increaseNote() {
		this.note = (byte)((this.note + 1) % 25);
		this.markDirty();
	}

	public void playNote(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).getMaterial() == Material.AIR) {
			BlockState blockState = world.getBlockState(blockPos.down());
			Material material = blockState.getMaterial();
			int i = 0;
			if (material == Material.STONE) {
				i = 1;
			}

			if (material == Material.SAND) {
				i = 2;
			}

			if (material == Material.GLASS) {
				i = 3;
			}

			if (material == Material.WOOD) {
				i = 4;
			}

			Block block = blockState.getBlock();
			if (block == Blocks.CLAY) {
				i = 5;
			}

			if (block == Blocks.GOLD_BLOCK) {
				i = 6;
			}

			if (block == Blocks.WOOL) {
				i = 7;
			}

			if (block == Blocks.PACKED_ICE) {
				i = 8;
			}

			if (block == Blocks.BONE_BLOCK) {
				i = 9;
			}

			world.addBlockAction(blockPos, Blocks.NOTEBLOCK, i, this.note);
		}
	}
}
