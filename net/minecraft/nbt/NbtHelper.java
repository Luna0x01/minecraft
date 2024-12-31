package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4372;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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

					nbtList.add((NbtElement)nbtCompound2);
				}

				nbtCompound.put(string, nbtList);
			}

			nbt.put("Properties", nbtCompound);
		}

		return nbt;
	}

	@VisibleForTesting
	public static boolean areEqual(@Nullable NbtElement nbt1, @Nullable NbtElement nbt2, boolean compareLists) {
		if (nbt1 == nbt2) {
			return true;
		} else if (nbt1 == null) {
			return true;
		} else if (nbt2 == null) {
			return false;
		} else if (!nbt1.getClass().equals(nbt2.getClass())) {
			return false;
		} else if (nbt1 instanceof NbtCompound) {
			NbtCompound nbtCompound = (NbtCompound)nbt1;
			NbtCompound nbtCompound2 = (NbtCompound)nbt2;

			for (String string : nbtCompound.getKeys()) {
				NbtElement nbtElement = nbtCompound.get(string);
				if (!areEqual(nbtElement, nbtCompound2.get(string), compareLists)) {
					return false;
				}
			}

			return true;
		} else if (nbt1 instanceof NbtList && compareLists) {
			NbtList nbtList = (NbtList)nbt1;
			NbtList nbtList2 = (NbtList)nbt2;
			if (nbtList.isEmpty()) {
				return nbtList2.isEmpty();
			} else {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtElement nbtElement2 = nbtList.get(i);
					boolean bl = false;

					for (int j = 0; j < nbtList2.size(); j++) {
						if (areEqual(nbtElement2, nbtList2.get(j), compareLists)) {
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
			return nbt1.equals(nbt2);
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
			Block block = Registry.BLOCK.get(new Identifier(compound.getString("Name")));
			BlockState blockState = block.getDefaultState();
			if (compound.contains("Properties", 10)) {
				NbtCompound nbtCompound = compound.getCompound("Properties");
				StateManager<Block, BlockState> stateManager = block.getStateManager();

				for (String string : nbtCompound.getKeys()) {
					Property<?> property = stateManager.getProperty(string);
					if (property != null) {
						blockState = method_20140(blockState, property, string, nbtCompound, compound);
					}
				}
			}

			return blockState;
		}
	}

	private static <S extends PropertyContainer<S>, T extends Comparable<T>> S method_20140(
		S propertyContainer, Property<T> property, String string, NbtCompound nbtCompound, NbtCompound nbtCompound2
	) {
		Optional<T> optional = property.getValueAsString(nbtCompound.getString(string));
		if (optional.isPresent()) {
			return propertyContainer.withProperty(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", string, nbtCompound.getString(string), nbtCompound2.toString());
			return propertyContainer;
		}
	}

	public static NbtCompound method_20139(BlockState blockState) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", Registry.BLOCK.getId(blockState.getBlock()).toString());
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = blockState.getEntries();
		if (!immutableMap.isEmpty()) {
			NbtCompound nbtCompound2 = new NbtCompound();
			UnmodifiableIterator var4 = immutableMap.entrySet().iterator();

			while (var4.hasNext()) {
				Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var4.next();
				Property<?> property = (Property<?>)entry.getKey();
				nbtCompound2.putString(property.getName(), nameValue(property, (Comparable<?>)entry.getValue()));
			}

			nbtCompound.put("Properties", nbtCompound2);
		}

		return nbtCompound;
	}

	private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
		return property.name((T)value);
	}

	public static NbtCompound method_20141(DataFixer dataFixer, TypeReference typeReference, NbtCompound nbtCompound, int i) {
		return method_20142(dataFixer, typeReference, nbtCompound, i, 1631);
	}

	public static NbtCompound method_20142(DataFixer dataFixer, TypeReference typeReference, NbtCompound nbtCompound, int i, int j) {
		return (NbtCompound)dataFixer.update(typeReference, new Dynamic(class_4372.field_21487, nbtCompound), i, j).getValue();
	}
}
