package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;

public class ChunkTicketType<T> {
	private final String name;
	private final Comparator<T> argumentComparator;
	private final long expiryTicks;
	public static final ChunkTicketType<Unit> field_14030 = create("start", (unit, unit2) -> 0);
	public static final ChunkTicketType<Unit> field_17264 = create("dragon", (unit, unit2) -> 0);
	public static final ChunkTicketType<ChunkPos> field_14033 = create("player", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_14031 = create("forced", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_19270 = create("light", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<BlockPos> field_19280 = create("portal", Vec3i::compareTo, 300);
	public static final ChunkTicketType<Integer> field_19347 = create("post_teleport", Integer::compareTo, 5);
	public static final ChunkTicketType<ChunkPos> field_14032 = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

	public static <T> ChunkTicketType<T> create(String string, Comparator<T> comparator) {
		return new ChunkTicketType<>(string, comparator, 0L);
	}

	public static <T> ChunkTicketType<T> create(String string, Comparator<T> comparator, int i) {
		return new ChunkTicketType<>(string, comparator, (long)i);
	}

	protected ChunkTicketType(String string, Comparator<T> comparator, long l) {
		this.name = string;
		this.argumentComparator = comparator;
		this.expiryTicks = l;
	}

	public String toString() {
		return this.name;
	}

	public Comparator<T> getArgumentComparator() {
		return this.argumentComparator;
	}

	public long getExpiryTicks() {
		return this.expiryTicks;
	}
}
