package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	@Override
	public CompoundTag write(String string, CompoundTag compoundTag) {
		return string.startsWith("data/minecraft/structures/") ? update(addDataVersion(compoundTag)) : compoundTag;
	}

	private static CompoundTag addDataVersion(CompoundTag compoundTag) {
		if (!compoundTag.contains("DataVersion", 99)) {
			compoundTag.putInt("DataVersion", 500);
		}

		return compoundTag;
	}

	private static CompoundTag update(CompoundTag compoundTag) {
		Structure structure = new Structure();
		structure.fromTag(NbtHelper.update(Schemas.getFixer(), DataFixTypes.field_19217, compoundTag, compoundTag.getInt("DataVersion")));
		return structure.toTag(new CompoundTag());
	}
}
