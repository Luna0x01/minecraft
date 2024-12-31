package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MultipartEntityProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;

public class EnderDragonPart extends Entity {
	public final MultipartEntityProvider owner;
	public final String name;

	public EnderDragonPart(MultipartEntityProvider multipartEntityProvider, String string, float f, float g) {
		super(multipartEntityProvider.getServerWorld());
		this.setBounds(f, g);
		this.owner = multipartEntityProvider;
		this.name = string;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : this.owner.setAngry(this, source, amount);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}
}
