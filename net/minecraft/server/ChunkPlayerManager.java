package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkUnloadS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPlayerManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final PlayerWorldManager playerWorldManager;
	private final List<ServerPlayerEntity> players = Lists.newArrayList();
	private final ChunkPos chunkPos;
	private final short[] field_8887 = new short[64];
	@Nullable
	private Chunk chunk;
	private int field_8888;
	private int field_8889;
	private long field_8890;
	private boolean field_13865;

	public ChunkPlayerManager(PlayerWorldManager playerWorldManager, int i, int j) {
		this.playerWorldManager = playerWorldManager;
		this.chunkPos = new ChunkPos(i, j);
		this.chunk = playerWorldManager.getWorld().getChunkProvider().getOrLoadChunk(i, j);
	}

	public ChunkPos getChunkPos() {
		return this.chunkPos;
	}

	public void addPlayer(ServerPlayerEntity player) {
		if (this.players.contains(player)) {
			LOGGER.debug("Failed to add player. {} already is in chunk {}, {}", player, this.chunkPos.x, this.chunkPos.z);
		} else {
			if (this.players.isEmpty()) {
				this.field_8890 = this.playerWorldManager.getWorld().getLastUpdateTime();
			}

			this.players.add(player);
			if (this.field_13865) {
				this.method_12803(player);
			}
		}
	}

	public void method_8127(ServerPlayerEntity player) {
		if (this.players.contains(player)) {
			if (this.field_13865) {
				player.networkHandler.sendPacket(new ChunkUnloadS2CPacket(this.chunkPos.x, this.chunkPos.z));
			}

			this.players.remove(player);
			if (this.players.isEmpty()) {
				this.playerWorldManager.method_12812(this);
			}
		}
	}

	public boolean method_12800(boolean bl) {
		if (this.chunk != null) {
			return true;
		} else {
			if (bl) {
				this.chunk = this.playerWorldManager.getWorld().getChunkProvider().getOrGenerateChunks(this.chunkPos.x, this.chunkPos.z);
			} else {
				this.chunk = this.playerWorldManager.getWorld().getChunkProvider().getOrLoadChunk(this.chunkPos.x, this.chunkPos.z);
			}

			return this.chunk != null;
		}
	}

	public boolean method_12801() {
		if (this.field_13865) {
			return true;
		} else if (this.chunk == null) {
			return false;
		} else if (!this.chunk.isPopulated()) {
			return false;
		} else {
			this.field_8888 = 0;
			this.field_8889 = 0;
			this.field_13865 = true;
			Packet<?> packet = new ChunkDataS2CPacket(this.chunk, 65535);

			for (ServerPlayerEntity serverPlayerEntity : this.players) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
				this.playerWorldManager.getWorld().getEntityTracker().method_4410(serverPlayerEntity, this.chunk);
			}

			return true;
		}
	}

	public void method_12803(ServerPlayerEntity player) {
		if (this.field_13865) {
			player.networkHandler.sendPacket(new ChunkDataS2CPacket(this.chunk, 65535));
			this.playerWorldManager.getWorld().getEntityTracker().method_4410(player, this.chunk);
		}
	}

	public void method_12802() {
		long l = this.playerWorldManager.getWorld().getLastUpdateTime();
		if (this.chunk != null) {
			this.chunk.setInhabitedTime(this.chunk.getInhabitedTime() + l - this.field_8890);
		}

		this.field_8890 = l;
	}

	public void method_8119(int i, int j, int k) {
		if (this.field_13865) {
			if (this.field_8888 == 0) {
				this.playerWorldManager.method_12809(this);
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
	}

	public void method_12799(Packet<?> packet) {
		if (this.field_13865) {
			for (int i = 0; i < this.players.size(); i++) {
				((ServerPlayerEntity)this.players.get(i)).networkHandler.sendPacket(packet);
			}
		}
	}

	public void method_8125() {
		if (this.field_13865 && this.chunk != null) {
			if (this.field_8888 != 0) {
				if (this.field_8888 == 1) {
					int i = (this.field_8887[0] >> 12 & 15) + this.chunkPos.x * 16;
					int j = this.field_8887[0] & 255;
					int k = (this.field_8887[0] >> 8 & 15) + this.chunkPos.z * 16;
					BlockPos blockPos = new BlockPos(i, j, k);
					this.method_12799(new BlockUpdateS2CPacket(this.playerWorldManager.getWorld(), blockPos));
					if (this.playerWorldManager.getWorld().getBlockState(blockPos).getBlock().hasBlockEntity()) {
						this.method_8120(this.playerWorldManager.getWorld().getBlockEntity(blockPos));
					}
				} else if (this.field_8888 == 64) {
					this.method_12799(new ChunkDataS2CPacket(this.chunk, this.field_8889));
				} else {
					this.method_12799(new ChunkDeltaUpdateS2CPacket(this.field_8888, this.field_8887, this.chunk));

					for (int l = 0; l < this.field_8888; l++) {
						int m = (this.field_8887[l] >> 12 & 15) + this.chunkPos.x * 16;
						int n = this.field_8887[l] & 255;
						int o = (this.field_8887[l] >> 8 & 15) + this.chunkPos.z * 16;
						BlockPos blockPos2 = new BlockPos(m, n, o);
						if (this.playerWorldManager.getWorld().getBlockState(blockPos2).getBlock().hasBlockEntity()) {
							this.method_8120(this.playerWorldManager.getWorld().getBlockEntity(blockPos2));
						}
					}
				}

				this.field_8888 = 0;
				this.field_8889 = 0;
			}
		}
	}

	private void method_8120(@Nullable BlockEntity blockEntity) {
		if (blockEntity != null) {
			BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket = blockEntity.getUpdatePacket();
			if (blockEntityUpdateS2CPacket != null) {
				this.method_12799(blockEntityUpdateS2CPacket);
			}
		}
	}

	public boolean method_12804(ServerPlayerEntity player) {
		return this.players.contains(player);
	}

	public boolean method_12798(Predicate<ServerPlayerEntity> predicate) {
		return Iterables.tryFind(this.players, predicate).isPresent();
	}

	public boolean method_12797(double distance, Predicate<ServerPlayerEntity> predicate) {
		int i = 0;

		for (int j = this.players.size(); i < j; i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (predicate.apply(serverPlayerEntity) && this.chunkPos.squaredDistanceToCenter(serverPlayerEntity) < distance * distance) {
				return true;
			}
		}

		return false;
	}

	public boolean method_12805() {
		return this.field_13865;
	}

	@Nullable
	public Chunk getChunk() {
		return this.chunk;
	}

	public double method_12807() {
		double d = Double.MAX_VALUE;

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			double e = this.chunkPos.squaredDistanceToCenter(serverPlayerEntity);
			if (e < d) {
				d = e;
			}
		}

		return d;
	}
}
