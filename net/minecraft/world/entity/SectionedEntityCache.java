package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.Objects;
import java.util.Spliterators;
import java.util.PrimitiveIterator.OfLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public class SectionedEntityCache<T extends EntityLike> {
	private final Class<T> entityClass;
	private final Long2ObjectFunction<EntityTrackingStatus> posToStatus;
	private final Long2ObjectMap<EntityTrackingSection<T>> trackingSections = new Long2ObjectOpenHashMap();
	private final LongSortedSet trackedPositions = new LongAVLTreeSet();

	public SectionedEntityCache(Class<T> entityClass, Long2ObjectFunction<EntityTrackingStatus> chunkStatusDiscriminator) {
		this.entityClass = entityClass;
		this.posToStatus = chunkStatusDiscriminator;
	}

	public void forEachInBox(Box box, Consumer<EntityTrackingSection<T>> action) {
		int i = ChunkSectionPos.getSectionCoord(box.minX - 2.0);
		int j = ChunkSectionPos.getSectionCoord(box.minY - 2.0);
		int k = ChunkSectionPos.getSectionCoord(box.minZ - 2.0);
		int l = ChunkSectionPos.getSectionCoord(box.maxX + 2.0);
		int m = ChunkSectionPos.getSectionCoord(box.maxY + 2.0);
		int n = ChunkSectionPos.getSectionCoord(box.maxZ + 2.0);

		for (int o = i; o <= l; o++) {
			long p = ChunkSectionPos.asLong(o, 0, 0);
			long q = ChunkSectionPos.asLong(o, -1, -1);
			LongIterator longIterator = this.trackedPositions.subSet(p, q + 1L).iterator();

			while (longIterator.hasNext()) {
				long r = longIterator.nextLong();
				int s = ChunkSectionPos.unpackY(r);
				int t = ChunkSectionPos.unpackZ(r);
				if (s >= j && s <= m && t >= k && t <= n) {
					EntityTrackingSection<T> entityTrackingSection = (EntityTrackingSection<T>)this.trackingSections.get(r);
					if (entityTrackingSection != null && entityTrackingSection.getStatus().shouldTrack()) {
						action.accept(entityTrackingSection);
					}
				}
			}
		}
	}

	public LongStream getSections(long chunkPos) {
		int i = ChunkPos.getPackedX(chunkPos);
		int j = ChunkPos.getPackedZ(chunkPos);
		LongSortedSet longSortedSet = this.getSections(i, j);
		if (longSortedSet.isEmpty()) {
			return LongStream.empty();
		} else {
			OfLong ofLong = longSortedSet.iterator();
			return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(ofLong, 1301), false);
		}
	}

	private LongSortedSet getSections(int chunkX, int chunkZ) {
		long l = ChunkSectionPos.asLong(chunkX, 0, chunkZ);
		long m = ChunkSectionPos.asLong(chunkX, -1, chunkZ);
		return this.trackedPositions.subSet(l, m + 1L);
	}

	public Stream<EntityTrackingSection<T>> getTrackingSections(long chunkPos) {
		return this.getSections(chunkPos).mapToObj(this.trackingSections::get).filter(Objects::nonNull);
	}

	private static long chunkPosFromSectionPos(long sectionPos) {
		return ChunkPos.toLong(ChunkSectionPos.unpackX(sectionPos), ChunkSectionPos.unpackZ(sectionPos));
	}

	public EntityTrackingSection<T> getTrackingSection(long sectionPos) {
		return (EntityTrackingSection<T>)this.trackingSections.computeIfAbsent(sectionPos, this::addSection);
	}

	@Nullable
	public EntityTrackingSection<T> findTrackingSection(long sectionPos) {
		return (EntityTrackingSection<T>)this.trackingSections.get(sectionPos);
	}

	private EntityTrackingSection<T> addSection(long sectionPos) {
		long l = chunkPosFromSectionPos(sectionPos);
		EntityTrackingStatus entityTrackingStatus = (EntityTrackingStatus)this.posToStatus.get(l);
		this.trackedPositions.add(sectionPos);
		return new EntityTrackingSection<>(this.entityClass, entityTrackingStatus);
	}

	public LongSet getChunkPositions() {
		LongSet longSet = new LongOpenHashSet();
		this.trackingSections.keySet().forEach(sectionPos -> longSet.add(chunkPosFromSectionPos(sectionPos)));
		return longSet;
	}

	private static <T extends EntityLike> Predicate<T> intersecting(Box box) {
		return entityLike -> entityLike.getBoundingBox().intersects(box);
	}

	public void forEachIntersects(Box box, Consumer<T> action) {
		this.forEachInBox(box, entityTrackingSection -> entityTrackingSection.forEach(intersecting(box), action));
	}

	public <U extends T> void forEachIntersects(TypeFilter<T, U> filter, Box box, Consumer<U> action) {
		this.forEachInBox(box, entityTrackingSection -> entityTrackingSection.forEach(filter, intersecting(box), action));
	}

	public void removeSection(long sectionPos) {
		this.trackingSections.remove(sectionPos);
		this.trackedPositions.remove(sectionPos);
	}

	@Debug
	public int sectionCount() {
		return this.trackedPositions.size();
	}
}
