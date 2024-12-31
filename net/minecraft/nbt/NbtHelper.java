package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.BlockPos;

public final class NbtHelper {
	@Nullable
	public static GameProfile toGameProfile(NbtCompound nbt) {
		String string = null;
		String string2 = null;
		if (nbt.contains("Name", 8)) {
			string = nbt.getString("Name");
		}

		if (nbt.contains("Id", 8)) {
			string2 = nbt.getString("Id");
		}

		if (ChatUtil.isEmpty(string) && ChatUtil.isEmpty(string2)) {
			return null;
		} else {
			UUID uUID;
			try {
				uUID = UUID.fromString(string2);
			} catch (Throwable var12) {
				uUID = null;
			}

			GameProfile gameProfile = new GameProfile(uUID, string);
			if (nbt.contains("Properties", 10)) {
				NbtCompound nbtCompound = nbt.getCompound("Properties");

				for (String string3 : nbtCompound.getKeys()) {
					NbtList nbtList = nbtCompound.getList(string3, 10);

					for (int i = 0; i < nbtList.size(); i++) {
						NbtCompound nbtCompound2 = nbtList.getCompound(i);
						String string4 = nbtCompound2.getString("Value");
						if (nbtCompound2.contains("Signature", 8)) {
							gameProfile.getProperties().put(string3, new Property(string3, string4, nbtCompound2.getString("Signature")));
						} else {
							gameProfile.getProperties().put(string3, new Property(string3, string4));
						}
					}
				}
			}

			return gameProfile;
		}
	}

	public static NbtCompound fromGameProfile(NbtCompound nbt, GameProfile profile) {
		if (!ChatUtil.isEmpty(profile.getName())) {
			nbt.putString("Name", profile.getName());
		}

		if (profile.getId() != null) {
			nbt.putString("Id", profile.getId().toString());
		}

		if (!profile.getProperties().isEmpty()) {
			NbtCompound nbtCompound = new NbtCompound();

			for (String string : profile.getProperties().keySet()) {
				NbtList nbtList = new NbtList();

				for (Property property : profile.getProperties().get(string)) {
					NbtCompound nbtCompound2 = new NbtCompound();
					nbtCompound2.putString("Value", property.getValue());
					if (property.hasSignature()) {
						nbtCompound2.putString("Signature", property.getSignature());
					}

					nbtList.add(nbtCompound2);
				}

				nbtCompound.put(string, nbtList);
			}

			nbt.put("Properties", nbtCompound);
		}

		return nbt;
	}

	@VisibleForTesting
	public static boolean matches(NbtElement standard, NbtElement subject, boolean equalValue) {
		if (standard == subject) {
			return true;
		} else if (standard == null) {
			return true;
		} else if (subject == null) {
			return false;
		} else if (!standard.getClass().equals(subject.getClass())) {
			return false;
		} else if (standard instanceof NbtCompound) {
			NbtCompound nbtCompound = (NbtCompound)standard;
			NbtCompound nbtCompound2 = (NbtCompound)subject;

			for (String string : nbtCompound.getKeys()) {
				NbtElement nbtElement = nbtCompound.get(string);
				if (!matches(nbtElement, nbtCompound2.get(string), equalValue)) {
					return false;
				}
			}

			return true;
		} else if (standard instanceof NbtList && equalValue) {
			NbtList nbtList = (NbtList)standard;
			NbtList nbtList2 = (NbtList)subject;
			if (nbtList.size() == 0) {
				return nbtList2.size() == 0;
			} else {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtElement nbtElement2 = nbtList.get(i);
					boolean bl = false;

					for (int j = 0; j < nbtList2.size(); j++) {
						if (matches(nbtElement2, nbtList2.get(j), equalValue)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		} else {
			return standard.equals(subject);
		}
	}

	public static NbtCompound fromUuid(UUID uuid) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putLong("M", uuid.getMostSignificantBits());
		nbtCompound.putLong("L", uuid.getLeastSignificantBits());
		return nbtCompound;
	}

	public static UUID toUuid(NbtCompound compound) {
		return new UUID(compound.getLong("M"), compound.getLong("L"));
	}

	public static BlockPos toBlockPos(NbtCompound compound) {
		return new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
	}

	public static NbtCompound fromBlockPos(BlockPos pos) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putInt("X", pos.getX());
		nbtCompound.putInt("Y", pos.getY());
		nbtCompound.putInt("Z", pos.getZ());
		return nbtCompound;
	}
}
