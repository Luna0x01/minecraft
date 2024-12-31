package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.minecraft.block.SkullBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.Direction;

public class SkullBlockEntity extends BlockEntity implements Tickable {
	private int skullType;
	private int rot;
	private GameProfile owner;
	private int field_12854;
	private boolean field_12855;
	private static UserCache field_12856;
	private static MinecraftSessionService field_12857;

	public static void method_11666(UserCache userCache) {
		field_12856 = userCache;
	}

	public static void method_11665(MinecraftSessionService minecraftSessionService) {
		field_12857 = minecraftSessionService;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putByte("SkullType", (byte)(this.skullType & 0xFF));
		nbt.putByte("Rot", (byte)(this.rot & 0xFF));
		if (this.owner != null) {
			NbtCompound nbtCompound = new NbtCompound();
			NbtHelper.fromGameProfile(nbtCompound, this.owner);
			nbt.put("Owner", nbtCompound);
		}

		return nbt;
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

	@Override
	public void tick() {
		if (this.skullType == 5) {
			if (this.world.isReceivingRedstonePower(this.pos)) {
				this.field_12855 = true;
				this.field_12854++;
			} else {
				this.field_12855 = false;
			}
		}
	}

	public float method_11664(float f) {
		return this.field_12855 ? (float)this.field_12854 + f : (float)this.field_12854;
	}

	@Nullable
	public GameProfile getOwner() {
		return this.owner;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 4, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public void setSkullType(int type) {
		this.skullType = type;
		this.owner = null;
	}

	public void setOwnerAndType(@Nullable GameProfile owner) {
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
			} else if (field_12856 != null && field_12857 != null) {
				GameProfile gameProfile = field_12856.findByName(profile.getName());
				if (gameProfile == null) {
					return profile;
				} else {
					Property property = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
					if (property == null) {
						gameProfile = field_12857.fillProfileProperties(gameProfile, true);
					}

					return gameProfile;
				}
			} else {
				return profile;
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

	@Override
	public void method_13321(BlockMirror mirror) {
		if (this.world != null && this.world.getBlockState(this.getPos()).get(SkullBlock.FACING) == Direction.UP) {
			this.rot = mirror.mirror(this.rot, 16);
		}
	}

	@Override
	public void method_13322(BlockRotation rotation) {
		if (this.world != null && this.world.getBlockState(this.getPos()).get(SkullBlock.FACING) == Direction.UP) {
			this.rot = rotation.rotate(this.rot, 16);
		}
	}
}
