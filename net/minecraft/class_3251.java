package net.minecraft;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3251 {
	private static final Logger field_15863 = LogManager.getLogger();
	private final File field_15864;
	private final DataFixer field_19908;
	private final class_3297[] field_15865 = new class_3297[9];
	private boolean field_19909;

	public class_3251(File file, DataFixer dataFixer) {
		this.field_15864 = new File(file, "hotbar.nbt");
		this.field_19908 = dataFixer;

		for (int i = 0; i < 9; i++) {
			this.field_15865[i] = new class_3297();
		}
	}

	private void method_18153() {
		try {
			NbtCompound nbtCompound = NbtIo.read(this.field_15864);
			if (nbtCompound == null) {
				return;
			}

			if (!nbtCompound.contains("DataVersion", 99)) {
				nbtCompound.putInt("DataVersion", 1343);
			}

			nbtCompound = NbtHelper.method_20141(this.field_19908, DataFixTypes.HOTBAR, nbtCompound, nbtCompound.getInt("DataVersion"));

			for (int i = 0; i < 9; i++) {
				this.field_15865[i].method_14678(nbtCompound.getList(String.valueOf(i), 10));
			}
		} catch (Exception var3) {
			field_15863.error("Failed to load creative mode options", var3);
		}
	}

	public void method_14451() {
		try {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putInt("DataVersion", 1631);

			for (int i = 0; i < 9; i++) {
				nbtCompound.put(String.valueOf(i), this.method_14450(i).method_14677());
			}

			NbtIo.write(nbtCompound, this.field_15864);
		} catch (Exception var3) {
			field_15863.error("Failed to save creative mode options", var3);
		}
	}

	public class_3297 method_14450(int i) {
		if (!this.field_19909) {
			this.method_18153();
			this.field_19909 = true;
		}

		return this.field_15865[i];
	}
}
