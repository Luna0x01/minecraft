package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtShort;

public class PersistentStateManager {
	private SaveHandler saveHandler;
	protected Map<String, PersistentState> stateMap = Maps.newHashMap();
	private List<PersistentState> states = Lists.newArrayList();
	private Map<String, Short> idCounts = Maps.newHashMap();

	public PersistentStateManager(SaveHandler saveHandler) {
		this.saveHandler = saveHandler;
		this.readIdCounts();
	}

	@Nullable
	public PersistentState getOrCreate(Class<? extends PersistentState> clazz, String name) {
		PersistentState persistentState = (PersistentState)this.stateMap.get(name);
		if (persistentState != null) {
			return persistentState;
		} else {
			if (this.saveHandler != null) {
				try {
					File file = this.saveHandler.getDataFile(name);
					if (file != null && file.exists()) {
						try {
							persistentState = (PersistentState)clazz.getConstructor(String.class).newInstance(name);
						} catch (Exception var7) {
							throw new RuntimeException("Failed to instantiate " + clazz.toString(), var7);
						}

						FileInputStream fileInputStream = new FileInputStream(file);
						NbtCompound nbtCompound = NbtIo.readCompressed(fileInputStream);
						fileInputStream.close();
						persistentState.fromNbt(nbtCompound.getCompound("data"));
					}
				} catch (Exception var8) {
					var8.printStackTrace();
				}
			}

			if (persistentState != null) {
				this.stateMap.put(name, persistentState);
				this.states.add(persistentState);
			}

			return persistentState;
		}
	}

	public void replace(String name, PersistentState state) {
		if (this.stateMap.containsKey(name)) {
			this.states.remove(this.stateMap.remove(name));
		}

		this.stateMap.put(name, state);
		this.states.add(state);
	}

	public void save() {
		for (int i = 0; i < this.states.size(); i++) {
			PersistentState persistentState = (PersistentState)this.states.get(i);
			if (persistentState.isDirty()) {
				this.save(persistentState);
				persistentState.setDirty(false);
			}
		}
	}

	private void save(PersistentState state) {
		if (this.saveHandler != null) {
			try {
				File file = this.saveHandler.getDataFile(state.id);
				if (file != null) {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.put("data", state.toNbt(new NbtCompound()));
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					NbtIo.writeCompressed(nbtCompound, fileOutputStream);
					fileOutputStream.close();
				}
			} catch (Exception var5) {
				var5.printStackTrace();
			}
		}
	}

	private void readIdCounts() {
		try {
			this.idCounts.clear();
			if (this.saveHandler == null) {
				return;
			}

			File file = this.saveHandler.getDataFile("idcounts");
			if (file != null && file.exists()) {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				NbtCompound nbtCompound = NbtIo.read(dataInputStream);
				dataInputStream.close();

				for (String string : nbtCompound.getKeys()) {
					NbtElement nbtElement = nbtCompound.get(string);
					if (nbtElement instanceof NbtShort) {
						NbtShort nbtShort = (NbtShort)nbtElement;
						short s = nbtShort.shortValue();
						this.idCounts.put(string, s);
					}
				}
			}
		} catch (Exception var9) {
			var9.printStackTrace();
		}
	}

	public int getInt(String name) {
		Short short_ = (Short)this.idCounts.get(name);
		if (short_ == null) {
			short_ = (short)0;
		} else {
			short_ = (short)(short_ + 1);
		}

		this.idCounts.put(name, short_);
		if (this.saveHandler == null) {
			return short_;
		} else {
			try {
				File file = this.saveHandler.getDataFile("idcounts");
				if (file != null) {
					NbtCompound nbtCompound = new NbtCompound();

					for (String string : this.idCounts.keySet()) {
						nbtCompound.putShort(string, (Short)this.idCounts.get(string));
					}

					DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
					NbtIo.write(nbtCompound, dataOutputStream);
					dataOutputStream.close();
				}
			} catch (Exception var7) {
				var7.printStackTrace();
			}

			return short_;
		}
	}
}
