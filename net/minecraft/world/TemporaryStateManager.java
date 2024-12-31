package net.minecraft.world;

public class TemporaryStateManager extends PersistentStateManager {
	public TemporaryStateManager() {
		super(null);
	}

	@Override
	public PersistentState getOrCreate(Class<? extends PersistentState> clazz, String name) {
		return (PersistentState)this.stateMap.get(name);
	}

	@Override
	public void replace(String name, PersistentState state) {
		this.stateMap.put(name, state);
	}

	@Override
	public void save() {
	}

	@Override
	public int getInt(String name) {
		return 0;
	}
}
