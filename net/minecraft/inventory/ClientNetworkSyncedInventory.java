package net.minecraft.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class ClientNetworkSyncedInventory extends SimpleInventory implements LockableScreenHandlerFactory {
	private String id;
	private Map<Integer, Integer> properties = Maps.newHashMap();

	public ClientNetworkSyncedInventory(String string, Text text, int i) {
		super(text, i);
		this.id = string;
	}

	@Override
	public int getProperty(int key) {
		return this.properties.containsKey(key) ? (Integer)this.properties.get(key) : 0;
	}

	@Override
	public void setProperty(int id, int value) {
		this.properties.put(id, value);
	}

	@Override
	public int getProperties() {
		return this.properties.size();
	}

	@Override
	public boolean hasLock() {
		return false;
	}

	@Override
	public void setLock(ScreenHandlerLock lock) {
	}

	@Override
	public ScreenHandlerLock getLock() {
		return ScreenHandlerLock.NONE;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		throw new UnsupportedOperationException();
	}
}
