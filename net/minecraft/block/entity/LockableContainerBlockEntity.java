package net.minecraft.block.entity;

import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.nbt.NbtCompound;

public abstract class LockableContainerBlockEntity extends BlockEntity implements LockableScreenHandlerFactory {
	private ScreenHandlerLock lock = ScreenHandlerLock.NONE;

	protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.lock = ScreenHandlerLock.fromNbt(nbt);
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.lock != null) {
			this.lock.toNbt(nbt);
		}

		return nbt;
	}

	@Override
	public boolean hasLock() {
		return this.lock != null && !this.lock.hasLock();
	}

	@Override
	public ScreenHandlerLock getLock() {
		return this.lock;
	}

	@Override
	public void setLock(ScreenHandlerLock lock) {
		this.lock = lock;
	}
}
