package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.LongObjectStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerWorldManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerWorld world;
	private final List<ServerPlayerEntity> players = Lists.newArrayList();
	private final LongObjectStorage<PlayerWorldManager.PlayerInstance> playerInstancesById = new LongObjectStorage<>();
	private final List<PlayerWorldManager.PlayerInstance> field_2792 = Lists.newArrayList();
	private final List<PlayerWorldManager.PlayerInstance> playerInstances = Lists.newArrayList();
	private int viewDistance;
	private long field_6726;
	private final int[][] field_2794 = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

	public PlayerWorldManager(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.applyViewDistance(serverWorld.getServer().getPlayerManager().getViewDistance());
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public void method_2111() {
		long l = this.world.getLastUpdateTime();
		if (l - this.field_6726 > 8000L) {
			this.field_6726 = l;

			for (int i = 0; i < this.playerInstances.size(); i++) {
				PlayerWorldManager.PlayerInstance playerInstance = (PlayerWorldManager.PlayerInstance)this.playerInstances.get(i);
				playerInstance.method_8125();
				playerInstance.method_8118();
			}
		} else {
			for (int j = 0; j < this.field_2792.size(); j++) {
				PlayerWorldManager.PlayerInstance playerInstance2 = (PlayerWorldManager.PlayerInstance)this.field_2792.get(j);
				playerInstance2.method_8125();
			}
		}

		this.field_2792.clear();
		if (this.players.isEmpty()) {
			Dimension dimension = this.world.dimension;
			if (!dimension.containsWorldSpawn()) {
				this.world.chunkCache.unloadAll();
			}
		}
	}

	public boolean method_8116(int i, int j) {
		long l = (long)i + 2147483647L | (long)j + 2147483647L << 32;
		return this.playerInstancesById.get(l) != null;
	}

	private PlayerWorldManager.PlayerInstance method_2107(int i, int j, boolean bl) {
		long l = (long)i + 2147483647L | (long)j + 2147483647L << 32;
		PlayerWorldManager.PlayerInstance playerInstance = this.playerInstancesById.get(l);
		if (playerInstance == null && bl) {
			playerInstance = new PlayerWorldManager.PlayerInstance(i, j);
			this.playerInstancesById.set(l, playerInstance);
			this.playerInstances.add(playerInstance);
		}

		return playerInstance;
	}

	public void method_10748(BlockPos pos) {
		int i = pos.getX() >> 4;
		int j = pos.getZ() >> 4;
		PlayerWorldManager.PlayerInstance playerInstance = this.method_2107(i, j, false);
		if (playerInstance != null) {
			playerInstance.method_8119(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
		}
	}

	public void method_2109(ServerPlayerEntity player) {
		int i = (int)player.x >> 4;
		int j = (int)player.z >> 4;
		player.serverPosX = player.x;
		player.serverPosZ = player.z;

		for (int k = i - this.viewDistance; k <= i + this.viewDistance; k++) {
			for (int l = j - this.viewDistance; l <= j + this.viewDistance; l++) {
				this.method_2107(k, l, true).method_8124(player);
			}
		}

		this.players.add(player);
		this.method_2113(player);
	}

	public void method_2113(ServerPlayerEntity player) {
		List<ChunkPos> list = Lists.newArrayList(player.loadedChunks);
		int i = 0;
		int j = this.viewDistance;
		int k = (int)player.x >> 4;
		int l = (int)player.z >> 4;
		int m = 0;
		int n = 0;
		ChunkPos chunkPos = this.method_2107(k, l, true).field_8886;
		player.loadedChunks.clear();
		if (list.contains(chunkPos)) {
			player.loadedChunks.add(chunkPos);
		}

		for (int o = 1; o <= j * 2; o++) {
			for (int p = 0; p < 2; p++) {
				int[] is = this.field_2794[i++ % 4];

				for (int q = 0; q < o; q++) {
					m += is[0];
					n += is[1];
					chunkPos = this.method_2107(k + m, l + n, true).field_8886;
					if (list.contains(chunkPos)) {
						player.loadedChunks.add(chunkPos);
					}
				}
			}
		}

		i %= 4;

		for (int r = 0; r < j * 2; r++) {
			m += this.field_2794[i][0];
			n += this.field_2794[i][1];
			chunkPos = this.method_2107(k + m, l + n, true).field_8886;
			if (list.contains(chunkPos)) {
				player.loadedChunks.add(chunkPos);
			}
		}
	}

	public void method_2115(ServerPlayerEntity player) {
		int i = (int)player.serverPosX >> 4;
		int j = (int)player.serverPosZ >> 4;

		for (int k = i - this.viewDistance; k <= i + this.viewDistance; k++) {
			for (int l = j - this.viewDistance; l <= j + this.viewDistance; l++) {
				PlayerWorldManager.PlayerInstance playerInstance = this.method_2107(k, l, false);
				if (playerInstance != null) {
					playerInstance.method_8127(player);
				}
			}
		}

		this.players.remove(player);
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
							this.method_2107(p, q, true).method_8124(player);
						}

						if (!this.method_2106(p - n, q - o, i, j, m)) {
							PlayerWorldManager.PlayerInstance playerInstance = this.method_2107(p - n, q - o, false);
							if (playerInstance != null) {
								playerInstance.method_8127(player);
							}
						}
					}
				}

				this.method_2113(player);
				player.serverPosX = player.x;
				player.serverPosZ = player.z;
			}
		}
	}

	public boolean method_2110(ServerPlayerEntity player, int i, int j) {
		PlayerWorldManager.PlayerInstance playerInstance = this.method_2107(i, j, false);
		return playerInstance != null && playerInstance.field_8885.contains(player) && !player.loadedChunks.contains(playerInstance.field_8886);
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
							PlayerWorldManager.PlayerInstance playerInstance = this.method_2107(l, m, true);
							if (!playerInstance.field_8885.contains(serverPlayerEntity)) {
								playerInstance.method_8124(serverPlayerEntity);
							}
						}
					}
				} else {
					for (int n = j - this.viewDistance; n <= j + this.viewDistance; n++) {
						for (int o = k - this.viewDistance; o <= k + this.viewDistance; o++) {
							if (!this.method_2106(n, o, j, k, viewDistance)) {
								this.method_2107(n, o, true).method_8127(serverPlayerEntity);
							}
						}
					}
				}
			}

			this.viewDistance = viewDistance;
		}
	}

	public static int method_2104(int i) {
		return i * 16 - 16;
	}

	class PlayerInstance {
		private final List<ServerPlayerEntity> field_8885 = Lists.newArrayList();
		private final ChunkPos field_8886;
		private short[] field_8887 = new short[64];
		private int field_8888;
		private int field_8889;
		private long field_8890;

		public PlayerInstance(int i, int j) {
			this.field_8886 = new ChunkPos(i, j);
			PlayerWorldManager.this.getWorld().chunkCache.getOrGenerateChunk(i, j);
		}

		public void method_8124(ServerPlayerEntity serverPlayerEntity) {
			if (this.field_8885.contains(serverPlayerEntity)) {
				PlayerWorldManager.LOGGER
					.debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{serverPlayerEntity, this.field_8886.x, this.field_8886.z});
			} else {
				if (this.field_8885.isEmpty()) {
					this.field_8890 = PlayerWorldManager.this.world.getLastUpdateTime();
				}

				this.field_8885.add(serverPlayerEntity);
				serverPlayerEntity.loadedChunks.add(this.field_8886);
			}
		}

		public void method_8127(ServerPlayerEntity serverPlayerEntity) {
			if (this.field_8885.contains(serverPlayerEntity)) {
				Chunk chunk = PlayerWorldManager.this.world.getChunk(this.field_8886.x, this.field_8886.z);
				if (chunk.isPopulated()) {
					serverPlayerEntity.networkHandler.sendPacket(new ChunkDataS2CPacket(chunk, true, 0));
				}

				this.field_8885.remove(serverPlayerEntity);
				serverPlayerEntity.loadedChunks.remove(this.field_8886);
				if (this.field_8885.isEmpty()) {
					long l = (long)this.field_8886.x + 2147483647L | (long)this.field_8886.z + 2147483647L << 32;
					this.method_8121(chunk);
					PlayerWorldManager.this.playerInstancesById.remove(l);
					PlayerWorldManager.this.playerInstances.remove(this);
					if (this.field_8888 > 0) {
						PlayerWorldManager.this.field_2792.remove(this);
					}

					PlayerWorldManager.this.getWorld().chunkCache.scheduleUnload(this.field_8886.x, this.field_8886.z);
				}
			}
		}

		public void method_8118() {
			this.method_8121(PlayerWorldManager.this.world.getChunk(this.field_8886.x, this.field_8886.z));
		}

		private void method_8121(Chunk chunk) {
			chunk.setInhabitedTime(chunk.getInhabitedTime() + PlayerWorldManager.this.world.getLastUpdateTime() - this.field_8890);
			this.field_8890 = PlayerWorldManager.this.world.getLastUpdateTime();
		}

		public void method_8119(int i, int j, int k) {
			if (this.field_8888 == 0) {
				PlayerWorldManager.this.field_2792.add(this);
			}

			this.field_8889 |= 1 << (j >> 4);
			if (this.field_8888 < 64) {
				short s = (short)(i << 12 | k << 8 | j);

				for (int l = 0; l < this.field_8888; l++) {
					if (this.field_8887[l] == s) {
						return;
					}
				}

				this.field_8887[this.field_8888++] = s;
			}
		}

		public void method_8122(Packet packet) {
			for (int i = 0; i < this.field_8885.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.field_8885.get(i);
				if (!serverPlayerEntity.loadedChunks.contains(this.field_8886)) {
					serverPlayerEntity.networkHandler.sendPacket(packet);
				}
			}
		}

		public void method_8125() {
			if (this.field_8888 != 0) {
				if (this.field_8888 == 1) {
					int i = (this.field_8887[0] >> 12 & 15) + this.field_8886.x * 16;
					int j = this.field_8887[0] & 255;
					int k = (this.field_8887[0] >> 8 & 15) + this.field_8886.z * 16;
					BlockPos blockPos = new BlockPos(i, j, k);
					this.method_8122(new BlockUpdateS2CPacket(PlayerWorldManager.this.world, blockPos));
					if (PlayerWorldManager.this.world.getBlockState(blockPos).getBlock().hasBlockEntity()) {
						this.method_8120(PlayerWorldManager.this.world.getBlockEntity(blockPos));
					}
				} else if (this.field_8888 == 64) {
					int l = this.field_8886.x * 16;
					int m = this.field_8886.z * 16;
					this.method_8122(new ChunkDataS2CPacket(PlayerWorldManager.this.world.getChunk(this.field_8886.x, this.field_8886.z), false, this.field_8889));

					for (int n = 0; n < 16; n++) {
						if ((this.field_8889 & 1 << n) != 0) {
							int o = n << 4;
							List<BlockEntity> list = PlayerWorldManager.this.world.method_2134(l, o, m, l + 16, o + 16, m + 16);

							for (int p = 0; p < list.size(); p++) {
								this.method_8120((BlockEntity)list.get(p));
							}
						}
					}
				} else {
					this.method_8122(
						new ChunkDeltaUpdateS2CPacket(this.field_8888, this.field_8887, PlayerWorldManager.this.world.getChunk(this.field_8886.x, this.field_8886.z))
					);

					for (int q = 0; q < this.field_8888; q++) {
						int r = (this.field_8887[q] >> 12 & 15) + this.field_8886.x * 16;
						int s = this.field_8887[q] & 255;
						int t = (this.field_8887[q] >> 8 & 15) + this.field_8886.z * 16;
						BlockPos blockPos2 = new BlockPos(r, s, t);
						if (PlayerWorldManager.this.world.getBlockState(blockPos2).getBlock().hasBlockEntity()) {
							this.method_8120(PlayerWorldManager.this.world.getBlockEntity(blockPos2));
						}
					}
				}

				this.field_8888 = 0;
				this.field_8889 = 0;
			}
		}

		private void method_8120(BlockEntity blockEntity) {
			if (blockEntity != null) {
				Packet packet = blockEntity.getPacket();
				if (packet != null) {
					this.method_8122(packet);
				}
			}
		}
	}
}
