package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatUtil;

public class SkullBlockEntity extends BlockEntity {
	private int skullType;
	private int rot;
	private GameProfile owner = null;

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putByte("SkullType", (byte)(this.skullType & 0xFF));
		nbt.putByte("Rot", (byte)(this.rot & 0xFF));
		if (this.owner != null) {
			NbtCompound nbtCompound = new NbtCompound();
			NbtHelper.fromGameProfile(nbtCompound, this.owner);
			nbt.put("Owner", nbtCompound);
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.skullType = nbt.getByte("SkullType");
		this.rot = nbt.getByte("Rot");
		if (this.skullType == 3) {
			if (nbt.contains("Owner", 10)) {
				this.owner = NbtHelper.toGameProfile(nbt.getCompound("Owner"));
			} else if (nbt.contains("ExtraType", 8)) {
				String string = nbt.getString("ExtraType");
				if (!ChatUtil.isEmpty(string)) {
					this.owner = new GameProfile(null, string);
					this.loadOwnerProperties();
				}
			}
		}
	}

	public GameProfile getOwner() {
		return this.owner;
	}

	@Override
	public Packet getPacket() {
		NbtCompound nbtCompound = new NbtCompound();
		this.toNbt(nbtCompound);
		return new BlockEntityUpdateS2CPacket(this.pos, 4, nbtCompound);
	}

	public void setSkullType(int type) {
		this.skullType = type;
		this.owner = null;
	}

	public void setOwnerAndType(GameProfile owner) {
		this.skullType = 3;
		this.owner = owner;
		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		this.owner = loadProperties(this.owner);
		this.markDirty();
	}

	public static GameProfile loadProperties(GameProfile profile) {
		if (profile != null && !ChatUtil.isEmpty(profile.getName())) {
			if (profile.isComplete() && profile.getProperties().containsKey("textures")) {
				return profile;
			} else if (MinecraftServer.getServer() == null) {
				return profile;
			} else {
				GameProfile gameProfile = MinecraftServer.getServer().getUserCache().findByName(profile.getName());
				if (gameProfile == null) {
					return profile;
				} else {
					Property property = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
					if (property == null) {
						gameProfile = MinecraftServer.getServer().getSessionService().fillProfileProperties(gameProfile, true);
					}

					return gameProfile;
				}
			}
		} else {
			return profile;
		}
	}

	public int getSkullType() {
		return this.skullType;
	}

	public int getRotation() {
		return this.rot;
	}

	public void setRotation(int rot) {
		this.rot = rot;
	}
}
