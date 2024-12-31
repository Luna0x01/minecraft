package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.PersistentState;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4068 {
	private static final Logger field_19750 = LogManager.getLogger();
	private final DimensionType field_19751;
	private Map<String, PersistentState> field_19752 = Maps.newHashMap();
	private final Object2IntMap<String> field_19753 = new Object2IntOpenHashMap();
	@Nullable
	private final SaveHandler field_19754;

	public class_4068(DimensionType dimensionType, @Nullable SaveHandler saveHandler) {
		this.field_19751 = dimensionType;
		this.field_19754 = saveHandler;
		this.field_19753.defaultReturnValue(-1);
	}

	@Nullable
	public <T extends PersistentState> T method_17942(Function<String, T> function, String string) {
		PersistentState persistentState = (PersistentState)this.field_19752.get(string);
		if (persistentState == null && this.field_19754 != null) {
			try {
				File file = this.field_19754.method_243(this.field_19751, string);
				if (file != null && file.exists()) {
					persistentState = (PersistentState)function.apply(string);
					persistentState.fromNbt(method_17939(this.field_19754, this.field_19751, string, 1631).getCompound("data"));
					this.field_19752.put(string, persistentState);
				}
			} catch (Exception var5) {
				field_19750.error("Error loading saved data: {}", string, var5);
			}
		}

		return (T)persistentState;
	}

	public void method_17941(String string, PersistentState persistentState) {
		this.field_19752.put(string, persistentState);
	}

	public void method_17937() {
		try {
			this.field_19753.clear();
			if (this.field_19754 == null) {
				return;
			}

			File file = this.field_19754.method_243(this.field_19751, "idcounts");
			if (file != null && file.exists()) {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				NbtCompound nbtCompound = NbtIo.read(dataInputStream);
				dataInputStream.close();

				for (String string : nbtCompound.getKeys()) {
					if (nbtCompound.contains(string, 99)) {
						this.field_19753.put(string, nbtCompound.getInt(string));
					}
				}
			}
		} catch (Exception var6) {
			field_19750.error("Could not load aux values", var6);
		}
	}

	public int method_17940(String string) {
		int i = this.field_19753.getInt(string) + 1;
		this.field_19753.put(string, i);
		if (this.field_19754 == null) {
			return i;
		} else {
			try {
				File file = this.field_19754.method_243(this.field_19751, "idcounts");
				if (file != null) {
					NbtCompound nbtCompound = new NbtCompound();
					ObjectIterator dataOutputStream = this.field_19753.object2IntEntrySet().iterator();

					while (dataOutputStream.hasNext()) {
						Entry<String> entry = (Entry<String>)dataOutputStream.next();
						nbtCompound.putInt((String)entry.getKey(), entry.getIntValue());
					}

					DataOutputStream dataOutputStreamx = new DataOutputStream(new FileOutputStream(file));
					NbtIo.write(nbtCompound, dataOutputStreamx);
					dataOutputStreamx.close();
				}
			} catch (Exception var7) {
				field_19750.error("Could not get free aux value {}", string, var7);
			}

			return i;
		}
	}

	public static NbtCompound method_17939(SaveHandler saveHandler, DimensionType dimensionType, String string, int i) throws IOException {
		File file = saveHandler.method_243(dimensionType, string);
		FileInputStream fileInputStream = new FileInputStream(file);
		Throwable var6 = null;

		NbtCompound var9;
		try {
			NbtCompound nbtCompound = NbtIo.readCompressed(fileInputStream);
			int j = nbtCompound.contains("DataVersion", 99) ? nbtCompound.getInt("DataVersion") : 1343;
			var9 = NbtHelper.method_20142(saveHandler.method_17967(), DataFixTypes.SAVED_DATA, nbtCompound, j, i);
		} catch (Throwable var18) {
			var6 = var18;
			throw var18;
		} finally {
			if (fileInputStream != null) {
				if (var6 != null) {
					try {
						fileInputStream.close();
					} catch (Throwable var17) {
						var6.addSuppressed(var17);
					}
				} else {
					fileInputStream.close();
				}
			}
		}

		return var9;
	}

	public void method_17943() {
		if (this.field_19754 != null) {
			for (PersistentState persistentState : this.field_19752.values()) {
				if (persistentState.isDirty()) {
					this.method_17938(persistentState);
					persistentState.setDirty(false);
				}
			}
		}
	}

	private void method_17938(PersistentState persistentState) {
		if (this.field_19754 != null) {
			try {
				File file = this.field_19754.method_243(this.field_19751, persistentState.method_17914());
				if (file != null) {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.put("data", persistentState.toNbt(new NbtCompound()));
					nbtCompound.putInt("DataVersion", 1631);
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					NbtIo.writeCompressed(nbtCompound, fileOutputStream);
					fileOutputStream.close();
				}
			} catch (Exception var5) {
				field_19750.error("Could not save data {}", persistentState, var5);
			}
		}
	}
}
