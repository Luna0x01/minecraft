package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtHelper {
	private static final Logger LOGGER = LogManager.getLogger();

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

		try {
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
							gameProfile.getProperties().put(string3, new com.mojang.authlib.properties.Property(string3, string4, nbtCompound2.getString("Signature")));
						} else {
							gameProfile.getProperties().put(string3, new com.mojang.authlib.properties.Property(string3, string4));
						}
					}
				}
			}

			return gameProfile;
		} catch (Throwable var13) {
			return null;
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

				for (com.mojang.authlib.properties.Property property : profile.getProperties().get(string)) {
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
			if (nbtList.isEmpty()) {
				return nbtList2.isEmpty();
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

	public static BlockState toBlockState(NbtCompound compound) {
		if (!compound.contains("Name", 8)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Block block = Block.REGISTRY.get(new Identifier(compound.getString("Name")));
			BlockState blockState = block.getDefaultState();
			if (compound.contains("Properties", 10)) {
				NbtCompound nbtCompound = compound.getCompound("Properties");
				StateManager stateManager = block.getStateManager();

				for (String string : nbtCompound.getKeys()) {
					Property<?> property = stateManager.getProperty(string);
					if (property != null) {
						blockState = withProperty(blockState, property, string, nbtCompound, compound);
					}
				}
			}

			return blockState;
		}
	}

	private static <T extends Comparable<T>> BlockState withProperty(BlockState state, Property<T> property, String key, NbtCompound properties, NbtCompound root) {
		Optional<T> optional = property.method_11749(properties.getString(key));
		if (optional.isPresent()) {
			return state.with(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", key, properties.getString(key), root.toString());
			return state;
		}
	}

	public static NbtCompound fromBlockState(NbtCompound compound, BlockState state) {
		compound.putString("Name", Block.REGISTRY.getIdentifier(state.getBlock()).toString());
		if (!state.getPropertyMap().isEmpty()) {
			NbtCompound nbtCompound = new NbtCompound();
			UnmodifiableIterator var3 = state.getPropertyMap().entrySet().iterator();

			while (var3.hasNext()) {
				Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var3.next();
				Property<?> property = (Property<?>)entry.getKey();
				nbtCompound.putString(property.getName(), nameValue(property, (Comparable<?>)entry.getValue()));
			}

			compound.put("Properties", nbtCompound);
		}

		return compound;
	}

	private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
		return property.name((T)value);
	}
}
