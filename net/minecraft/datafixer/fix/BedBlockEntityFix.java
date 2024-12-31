package net.minecraft.datafixer.fix;

import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BedBlockEntityFix implements DataFix {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public int getVersion() {
		return 1125;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		int i = 416;

		try {
			NbtCompound nbtCompound = tag.getCompound("Level");
			int j = nbtCompound.getInt("xPos");
			int k = nbtCompound.getInt("zPos");
			NbtList nbtList = nbtCompound.getList("TileEntities", 10);
			NbtList nbtList2 = nbtCompound.getList("Sections", 10);

			for (int l = 0; l < nbtList2.size(); l++) {
				NbtCompound nbtCompound2 = nbtList2.getCompound(l);
				int m = nbtCompound2.getByte("Y");
				byte[] bs = nbtCompound2.getByteArray("Blocks");

				for (int n = 0; n < bs.length; n++) {
					if (416 == (bs[n] & 255) << 4) {
						int o = n & 15;
						int p = n >> 8 & 15;
						int q = n >> 4 & 15;
						NbtCompound nbtCompound3 = new NbtCompound();
						nbtCompound3.putString("id", "bed");
						nbtCompound3.putInt("x", o + (j << 4));
						nbtCompound3.putInt("y", p + (m << 4));
						nbtCompound3.putInt("z", q + (k << 4));
						nbtList.add(nbtCompound3);
					}
				}
			}
		} catch (Exception var17) {
			LOGGER.warn("Unable to datafix Bed blocks, level format may be missing tags.");
		}

		return tag;
	}
}
