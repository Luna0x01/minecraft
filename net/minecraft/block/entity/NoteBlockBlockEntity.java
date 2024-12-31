package net.minecraft.block.entity;

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
			Material material = world.getBlockState(blockPos.down()).getMaterial();
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

			world.addBlockAction(blockPos, Blocks.NOTEBLOCK, i, this.note);
		}
	}
}
