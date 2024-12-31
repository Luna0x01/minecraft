package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;

public class PlayerWorldManager {
	private static final Predicate<ServerPlayerEntity> NON_SPECTATOR_PLAYER = new Predicate<ServerPlayerEntity>() {
		public boolean apply(@Nullable ServerPlayerEntity serverPlayerEntity) {
			return serverPlayerEntity != null && !serverPlayerEntity.isSpectator();
		}
	};
	private static final Predicate<ServerPlayerEntity> PLAYER_TO_GENERATE_CHUNKS = new Predicate<ServerPlayerEntity>() {
		public boolean apply(@Nullable ServerPlayerEntity serverPlayerEntity) {
			return serverPlayerEntity != null
				&& (!serverPlayerEntity.isSpectator() || serverPlayerEntity.getServerWorld().getGameRules().getBoolean("spectatorsGenerateChunks"));
		}
	};
	private final ServerWorld world;
	private final List<ServerPlayerEntity> players = Lists.newArrayList();
	private final Long2ObjectMap<ChunkPlayerManager> field_13868 = new Long2ObjectOpenHashMap(4096);
	private final Set<ChunkPlayerManager> field_13869 = Sets.newHashSet();
	private final List<ChunkPlayerManager> field_13870 = Lists.newLinkedList();
	private final List<ChunkPlayerManager> field_13871 = Lists.newLinkedList();
	private final List<ChunkPlayerManager> playerInstances = Lists.newArrayList();
	private int viewDistance;
	private long field_6726;
	private boolean field_13872 = true;
	private boolean field_13873 = true;

	public PlayerWorldManager(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.applyViewDistance(serverWorld.getServer().getPlayerManager().getViewDistance());
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public Iterator<Chunk> method_12810() {
		final Iterator<ChunkPlayerManager> iterator = this.playerInstances.iterator();
		return new AbstractIterator<Chunk>() {
			protected Chunk computeNext() {
				while (iterator.hasNext()) {
					ChunkPlayerManager chunkPlayerManager = (ChunkPlayerManager)iterator.next();
					Chunk chunk = chunkPlayerManager.getChunk();
					if (chunk != null) {
						if (!chunk.isLightPopulated() && chunk.isTerrainPopulated()) {
							return chunk;
						}

						if (!chunk.hasPopulatedBlockEntities()) {
							return chunk;
						}

						if (chunkPlayerManager.method_12797(128.0, PlayerWorldManager.NON_SPECTATOR_PLAYER)) {
							return chunk;
						}
					}
				}

				return (Chunk)this.endOfData();
			}
		};
	}

	public void method_2111() {
		long l = this.world.getLastUpdateTime();
		if (l - this.field_6726 > 8000L) {
			this.field_6726 = l;

			for (int i = 0; i < this.playerInstances.size(); i++) {
				ChunkPlayerManager chunkPlayerManager = (ChunkPlayerManager)this.playerInstances.get(i);
				chunkPlayerManager.method_8125();
				chunkPlayerManager.method_12802();
			}
		}

		if (!this.field_13869.isEmpty()) {
			for (ChunkPlayerManager chunkPlayerManager2 : this.field_13869) {
				chunkPlayerManager2.method_8125();
			}

			this.field_13869.clear();
		}

		if (this.field_13872 && l % 4L == 0L) {
			this.field_13872 = false;
			Collections.sort(this.field_13871, new Comparator<ChunkPlayerManager>() {
				public int compare(ChunkPlayerManager chunkPlayerManager, ChunkPlayerManager chunkPlayerManager2) {
					return ComparisonChain.start().compare(chunkPlayerManager.method_12807(), chunkPlayerManager2.method_12807()).result();
				}
			});
		}

		if (this.field_13873 && l % 4L == 2L) {
			this.field_13873 = false;
			Collections.sort(this.field_13870, new Comparator<ChunkPlayerManager>() {
				public int compare(ChunkPlayerManager chunkPlayerManager, ChunkPlayerManager chunkPlayerManager2) {
					return ComparisonChain.start().compare(chunkPlayerManager.method_12807(), chunkPlayerManager2.method_12807()).result();
				}
			});
		}

		if (!this.field_13871.isEmpty()) {
			long m = System.nanoTime() + 50000000L;
			int j = 49;
			Iterator<ChunkPlayerManager> iterator2 = this.field_13871.iterator();

			while (iterator2.hasNext()) {
				ChunkPlayerManager chunkPlayerManager3 = (ChunkPlayerManager)iterator2.next();
				if (chunkPlayerManager3.getChunk() == null) {
					boolean bl = chunkPlayerManager3.method_12798(PLAYER_TO_GENERATE_CHUNKS);
					if (chunkPlayerManager3.method_12800(bl)) {
						iterator2.remove();
						if (chunkPlayerManager3.method_12801()) {
							this.field_13870.remove(chunkPlayerManager3);
						}

						if (--j < 0 || System.nanoTime() > m) {
							break;
						}
					}
				}
			}
		}

		if (!this.field_13870.isEmpty()) {
			int k = 81;
			Iterator<ChunkPlayerManager> iterator3 = this.field_13870.iterator();

			while (iterator3.hasNext()) {
				ChunkPlayerManager chunkPlayerManager4 = (ChunkPlayerManager)iterator3.next();
				if (chunkPlayerManager4.method_12801()) {
					iterator3.remove();
					if (--k < 0) {
						break;
					}
				}
			}
		}

		if (this.players.isEmpty()) {
			Dimension dimension = this.world.dimension;
			if (!dimension.containsWorldSpawn()) {
				this.world.getChunkProvider().unloadAll();
			}
		}
	}

	public boolean method_12808(int i, int j) {
		long l = method_12815(i, j);
		return this.field_13868.get(l) != null;
	}

	@Nullable
	public ChunkPlayerManager method_12811(int x, int z) {
		return (ChunkPlayerManager)this.field_13868.get(method_12815(x, z));
	}

	private ChunkPlayerManager method_12813(int x, int z) {
		long l = method_12815(x, z);
		ChunkPlayerManager chunkPlayerManager = (ChunkPlayerManager)this.field_13868.get(l);
		if (chunkPlayerManager == null) {
			chunkPlayerManager = new ChunkPlayerManager(this, x, z);
			this.field_13868.put(l, chunkPlayerManager);
			this.playerInstances.add(chunkPlayerManager);
			if (chunkPlayerManager.getChunk() == null) {
				this.field_13871.add(chunkPlayerManager);
			}

			if (!chunkPlayerManager.method_12801()) {
				this.field_13870.add(chunkPlayerManager);
			}
		}

		return chunkPlayerManager;
	}

	public void method_10748(BlockPos pos) {
		int i = pos.getX() >> 4;
		int j = pos.getZ() >> 4;
		ChunkPlayerManager chunkPlayerManager = this.method_12811(i, j);
		if (chunkPlayerManager != null) {
			chunkPlayerManager.method_8119(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
		}
	}

	public void method_2109(ServerPlayerEntity player) {
		int i = (int)player.x >> 4;
		int j = (int)player.z >> 4;
		player.serverPosX = player.x;
		player.serverPosZ = player.z;

		for (int k = i - this.viewDistance; k <= i + this.viewDistance; k++) {
			for (int l = j - this.viewDistance; l <= j + this.viewDistance; l++) {
				this.method_12813(k, l).addPlayer(player);
			}
		}

		this.players.add(player);
		this.method_12816();
	}

	public void method_2115(ServerPlayerEntity player) {
		int i = (int)player.serverPosX >> 4;
		int j = (int)player.serverPosZ >> 4;

		for (int k = i - this.viewDistance; k <= i + this.viewDistance; k++) {
			for (int l = j - this.viewDistance; l <= j + this.viewDistance; l++) {
				ChunkPlayerManager chunkPlayerManager = this.method_12811(k, l);
				if (chunkPlayerManager != null) {
					chunkPlayerManager.method_8127(player);
				}
			}
		}

		this.players.remove(player);
		this.method_12816();
	}

	private boolean method_2106(int i, int j, int k, int l, int m) {
		int n = i - k;
		int o = j - l;
		return n < -m || n > m ? false : o >= -m && o <= m;
	}

	public void method_2116(ServerPlayerEntity player) {
		int i = (int)player.x >> 4;
		int j = (int)player.z >> 4;
		double d = player.serverPosX - player.x;
		double e = player.serverPosZ - player.z;
		double f = d * d + e * e;
		if (!(f < 64.0)) {
			int k = (int)player.serverPosX >> 4;
			int l = (int)player.serverPosZ >> 4;
			int m = this.viewDistance;
			int n = i - k;
			int o = j - l;
			if (n != 0 || o != 0) {
				for (int p = i - m; p <= i + m; p++) {
					for (int q = j - m; q <= j + m; q++) {
						if (!this.method_2106(p, q, k, l, m)) {
							this.method_12813(p, q).addPlayer(player);
						}

						if (!this.method_2106(p - n, q - o, i, j, m)) {
							ChunkPlayerManager chunkPlayerManager = this.method_12811(p - n, q - o);
							if (chunkPlayerManager != null) {
								chunkPlayerManager.method_8127(player);
							}
						}
					}
				}

				player.serverPosX = player.x;
				player.serverPosZ = player.z;
				this.method_12816();
			}
		}
	}

	public boolean method_2110(ServerPlayerEntity player, int i, int j) {
		ChunkPlayerManager chunkPlayerManager = this.method_12811(i, j);
		return chunkPlayerManager != null && chunkPlayerManager.method_12804(player) && chunkPlayerManager.method_12805();
	}

	public void applyViewDistance(int viewDistance) {
		viewDistance = MathHelper.clamp(viewDistance, 3, 32);
		if (viewDistance != this.viewDistance) {
			int i = viewDistance - this.viewDistance;

			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(this.players)) {
				int j = (int)serverPlayerEntity.x >> 4;
				int k = (int)serverPlayerEntity.z >> 4;
				if (i > 0) {
					for (int l = j - viewDistance; l <= j + viewDistance; l++) {
						for (int m = k - viewDistance; m <= k + viewDistance; m++) {
							ChunkPlayerManager chunkPlayerManager = this.method_12813(l, m);
							if (!chunkPlayerManager.method_12804(serverPlayerEntity)) {
								chunkPlayerManager.addPlayer(serverPlayerEntity);
							}
						}
					}
				} else {
					for (int n = j - this.viewDistance; n <= j + this.viewDistance; n++) {
						for (int o = k - this.viewDistance; o <= k + this.viewDistance; o++) {
							if (!this.method_2106(n, o, j, k, viewDistance)) {
								this.method_12813(n, o).method_8127(serverPlayerEntity);
							}
						}
					}
				}
			}

			this.viewDistance = viewDistance;
			this.method_12816();
		}
	}

	private void method_12816() {
		this.field_13872 = true;
		this.field_13873 = true;
	}

	public static int method_2104(int i) {
		return i * 16 - 16;
	}

	private static long method_12815(int x, int z) {
		return (long)x + 2147483647L | (long)z + 2147483647L << 32;
	}

	public void method_12809(ChunkPlayerManager chunkPlayerManager) {
		this.field_13869.add(chunkPlayerManager);
	}

	public void method_12812(ChunkPlayerManager chunkPlayerManager) {
		ChunkPos chunkPos = chunkPlayerManager.getChunkPos();
		long l = method_12815(chunkPos.x, chunkPos.z);
		chunkPlayerManager.method_12802();
		this.field_13868.remove(l);
		this.playerInstances.remove(chunkPlayerManager);
		this.field_13869.remove(chunkPlayerManager);
		this.field_13870.remove(chunkPlayerManager);
		this.field_13871.remove(chunkPlayerManager);
		Chunk chunk = chunkPlayerManager.getChunk();
		if (chunk != null) {
			this.getWorld().getChunkProvider().unload(chunk);
		}
	}
}
