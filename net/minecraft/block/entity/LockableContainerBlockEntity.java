package net.minecraft.block.entity;

import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class LockableContainerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, LockableScreenHandlerFactory {
	private ScreenHandlerLock lock = ScreenHandlerLock.NONE;

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.lock = ScreenHandlerLock.fromNbt(nbt);
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.lock != null) {
			this.lock.toNbt(nbt);
		}
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

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}
}
