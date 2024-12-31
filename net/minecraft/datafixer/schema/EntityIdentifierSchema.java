package net.minecraft.datafixer.schema;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.Schema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public abstract class EntityIdentifierSchema implements Schema {
	private final Identifier IDENTIFIER;

	public EntityIdentifierSchema(Class<?> class_) {
		if (Entity.class.isAssignableFrom(class_)) {
			this.IDENTIFIER = EntityType.getId((Class<? extends Entity>)class_);
		} else if (BlockEntity.class.isAssignableFrom(class_)) {
			this.IDENTIFIER = BlockEntity.getIdentifier((Class<? extends BlockEntity>)class_);
		} else {
			this.IDENTIFIER = null;
		}
	}

	@Override
	public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
		if (new Identifier(tag.getString("id")).equals(this.IDENTIFIER)) {
			tag = this.updateDataForProperties(dataFixer, tag, dataVersion);
		}

		return tag;
	}

	abstract NbtCompound updateDataForProperties(DataFixer dataFixer, NbtCompound tag, int version);
}
