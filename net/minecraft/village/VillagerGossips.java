package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class VillagerGossips {
	public static final int field_30236 = 2;
	private final Map<UUID, VillagerGossips.Reputation> entityReputation = Maps.newHashMap();

	@Debug
	public Map<UUID, Object2IntMap<VillageGossipType>> getEntityReputationAssociatedGossips() {
		Map<UUID, Object2IntMap<VillageGossipType>> map = Maps.newHashMap();
		this.entityReputation.keySet().forEach(uuid -> {
			VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(uuid);
			map.put(uuid, reputation.associatedGossip);
		});
		return map;
	}

	public void decay() {
		Iterator<VillagerGossips.Reputation> iterator = this.entityReputation.values().iterator();

		while (iterator.hasNext()) {
			VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)iterator.next();
			reputation.decay();
			if (reputation.isObsolete()) {
				iterator.remove();
			}
		}
	}

	private Stream<VillagerGossips.GossipEntry> entries() {
		return this.entityReputation.entrySet().stream().flatMap(entry -> ((VillagerGossips.Reputation)entry.getValue()).entriesFor((UUID)entry.getKey()));
	}

	private Collection<VillagerGossips.GossipEntry> pickGossips(Random random, int count) {
		List<VillagerGossips.GossipEntry> list = (List<VillagerGossips.GossipEntry>)this.entries().collect(Collectors.toList());
		if (list.isEmpty()) {
			return Collections.emptyList();
		} else {
			int[] is = new int[list.size()];
			int i = 0;

			for (int j = 0; j < list.size(); j++) {
				VillagerGossips.GossipEntry gossipEntry = (VillagerGossips.GossipEntry)list.get(j);
				i += Math.abs(gossipEntry.getValue());
				is[j] = i - 1;
			}

			Set<VillagerGossips.GossipEntry> set = Sets.newIdentityHashSet();

			for (int k = 0; k < count; k++) {
				int l = random.nextInt(i);
				int m = Arrays.binarySearch(is, l);
				set.add((VillagerGossips.GossipEntry)list.get(m < 0 ? -m - 1 : m));
			}

			return set;
		}
	}

	private VillagerGossips.Reputation getReputationFor(UUID target) {
		return (VillagerGossips.Reputation)this.entityReputation.computeIfAbsent(target, uUID -> new VillagerGossips.Reputation());
	}

	public void shareGossipFrom(VillagerGossips from, Random random, int count) {
		Collection<VillagerGossips.GossipEntry> collection = from.pickGossips(random, count);
		collection.forEach(gossipEntry -> {
			int i = gossipEntry.value - gossipEntry.type.shareDecrement;
			if (i >= 2) {
				this.getReputationFor(gossipEntry.target).associatedGossip.mergeInt(gossipEntry.type, i, VillagerGossips::max);
			}
		});
	}

	public int getReputationFor(UUID target, Predicate<VillageGossipType> gossipTypeFilter) {
		VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(target);
		return reputation != null ? reputation.getValueFor(gossipTypeFilter) : 0;
	}

	public long method_35122(VillageGossipType villageGossipType, DoublePredicate doublePredicate) {
		return this.entityReputation
			.values()
			.stream()
			.filter(reputation -> doublePredicate.test((double)(reputation.associatedGossip.getOrDefault(villageGossipType, 0) * villageGossipType.multiplier)))
			.count();
	}

	public void startGossip(UUID target, VillageGossipType type, int value) {
		VillagerGossips.Reputation reputation = this.getReputationFor(target);
		reputation.associatedGossip.mergeInt(type, value, (integer, integer2) -> this.mergeReputation(type, integer, integer2));
		reputation.clamp(type);
		if (reputation.isObsolete()) {
			this.entityReputation.remove(target);
		}
	}

	public void method_35126(UUID uUID, VillageGossipType villageGossipType, int i) {
		this.startGossip(uUID, villageGossipType, -i);
	}

	public void method_35124(UUID uUID, VillageGossipType villageGossipType) {
		VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(uUID);
		if (reputation != null) {
			reputation.remove(villageGossipType);
			if (reputation.isObsolete()) {
				this.entityReputation.remove(uUID);
			}
		}
	}

	public void method_35121(VillageGossipType villageGossipType) {
		Iterator<VillagerGossips.Reputation> iterator = this.entityReputation.values().iterator();

		while (iterator.hasNext()) {
			VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)iterator.next();
			reputation.remove(villageGossipType);
			if (reputation.isObsolete()) {
				iterator.remove();
			}
		}
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic(dynamicOps, dynamicOps.createList(this.entries().map(gossipEntry -> gossipEntry.serialize(dynamicOps)).map(Dynamic::getValue)));
	}

	public void deserialize(Dynamic<?> dynamic) {
		dynamic.asStream()
			.map(VillagerGossips.GossipEntry::deserialize)
			.flatMap(dataResult -> Util.stream(dataResult.result()))
			.forEach(gossipEntry -> this.getReputationFor(gossipEntry.target).associatedGossip.put(gossipEntry.type, gossipEntry.value));
	}

	private static int max(int left, int right) {
		return Math.max(left, right);
	}

	private int mergeReputation(VillageGossipType type, int left, int right) {
		int i = left + right;
		return i > type.maxValue ? Math.max(type.maxValue, left) : i;
	}

	static class GossipEntry {
		public static final String TARGET_KEY = "Target";
		public static final String TYPE_KEY = "Type";
		public static final String VALUE_KEY = "Value";
		public final UUID target;
		public final VillageGossipType type;
		public final int value;

		public GossipEntry(UUID target, VillageGossipType type, int value) {
			this.target = target;
			this.type = type;
			this.value = value;
		}

		public int getValue() {
			return this.value * this.type.multiplier;
		}

		public String toString() {
			return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + "}";
		}

		public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
			return new Dynamic(
				dynamicOps,
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("Target"),
						DynamicSerializableUuid.CODEC.encodeStart(dynamicOps, this.target).result().orElseThrow(RuntimeException::new),
						dynamicOps.createString("Type"),
						dynamicOps.createString(this.type.key),
						dynamicOps.createString("Value"),
						dynamicOps.createInt(this.value)
					)
				)
			);
		}

		public static DataResult<VillagerGossips.GossipEntry> deserialize(Dynamic<?> dynamic) {
			return DataResult.unbox(
				DataResult.instance()
					.group(
						dynamic.get("Target").read(DynamicSerializableUuid.CODEC),
						dynamic.get("Type").asString().map(VillageGossipType::byKey),
						dynamic.get("Value").asNumber().map(Number::intValue)
					)
					.apply(DataResult.instance(), VillagerGossips.GossipEntry::new)
			);
		}
	}

	static class Reputation {
		final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap();

		public int getValueFor(Predicate<VillageGossipType> gossipTypeFilter) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.filter(entry -> gossipTypeFilter.test((VillageGossipType)entry.getKey()))
				.mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier)
				.sum();
		}

		public Stream<VillagerGossips.GossipEntry> entriesFor(UUID target) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.map(entry -> new VillagerGossips.GossipEntry(target, (VillageGossipType)entry.getKey(), entry.getIntValue()));
		}

		public void decay() {
			ObjectIterator<Entry<VillageGossipType>> objectIterator = this.associatedGossip.object2IntEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<VillageGossipType> entry = (Entry<VillageGossipType>)objectIterator.next();
				int i = entry.getIntValue() - ((VillageGossipType)entry.getKey()).decay;
				if (i < 2) {
					objectIterator.remove();
				} else {
					entry.setValue(i);
				}
			}
		}

		public boolean isObsolete() {
			return this.associatedGossip.isEmpty();
		}

		public void clamp(VillageGossipType gossipType) {
			int i = this.associatedGossip.getInt(gossipType);
			if (i > gossipType.maxValue) {
				this.associatedGossip.put(gossipType, gossipType.maxValue);
			}

			if (i < 2) {
				this.remove(gossipType);
			}
		}

		public void remove(VillageGossipType gossipType) {
			this.associatedGossip.removeInt(gossipType);
		}
	}
}
