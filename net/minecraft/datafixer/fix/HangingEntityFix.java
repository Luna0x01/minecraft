package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

public class HangingEntityFix implements DataFix {
	@Override
	public int getVersion() {
		return 111;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		String string = tag.getString("id");
		boolean bl = "Painting".equals(string);
		boolean bl2 = "ItemFrame".equals(string);
		if ((bl || bl2) && !tag.contains("Facing", 99)) {
			Direction direction;
			if (tag.contains("Direction", 99)) {
				direction = Direction.fromHorizontal(tag.getByte("Direction"));
				tag.putInt("TileX", tag.getInt("TileX") + direction.getOffsetX());
				tag.putInt("TileY", tag.getInt("TileY") + direction.getOffsetY());
				tag.putInt("TileZ", tag.getInt("TileZ") + direction.getOffsetZ());
				tag.remove("Direction");
				if (bl2 && tag.contains("ItemRotation", 99)) {
					tag.putByte("ItemRotation", (byte)(tag.getByte("ItemRotation") * 2));
				}
			} else {
				direction = Direction.fromHorizontal(tag.getByte("Dir"));
				tag.remove("Dir");
			}

			tag.putByte("Facing", (byte)direction.getHorizontal());
		}

		return tag;
	}
}
