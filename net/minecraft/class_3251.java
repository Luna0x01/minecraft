package net.minecraft;

import java.io.File;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3251 {
	private static final Logger field_15863 = LogManager.getLogger();
	protected MinecraftClient field_15862;
	private final File field_15864;
	private final class_3297[] field_15865 = new class_3297[9];

	public class_3251(MinecraftClient minecraftClient, File file) {
		this.field_15862 = minecraftClient;
		this.field_15864 = new File(file, "hotbar.nbt");

		for (int i = 0; i < 9; i++) {
			this.field_15865[i] = new class_3297();
		}

		this.method_14449();
	}

	public void method_14449() {
		try {
			NbtCompound nbtCompound = NbtIo.read(this.field_15864);
			if (nbtCompound == null) {
				return;
			}

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

			for (int i = 0; i < 9; i++) {
				nbtCompound.put(String.valueOf(i), this.field_15865[i].method_14677());
			}

			NbtIo.write(nbtCompound, this.field_15864);
		} catch (Exception var3) {
			field_15863.error("Failed to save creative mode options", var3);
		}
	}

	public class_3297 method_14450(int i) {
		return this.field_15865[i];
	}
}
