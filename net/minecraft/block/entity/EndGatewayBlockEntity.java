package net.minecraft.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.feature.EndGatewayFeature;
import net.minecraft.world.gen.feature.class_2754;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayBlockEntity extends BlockEntity implements Tickable {
	private static final Logger GATEWAY_LOGGER = LogManager.getLogger();
	private long age;
	private int cooldown;
	private BlockPos exitPortal;
	private boolean exactTeleport;

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putLong("Age", this.age);
		if (this.exitPortal != null) {
			nbt.put("ExitPortal", NbtHelper.fromBlockPos(this.exitPortal));
		}

		if (this.exactTeleport) {
			nbt.putBoolean("ExactTeleport", this.exactTeleport);
		}

		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.age = nbt.getLong("Age");
		if (nbt.contains("ExitPortal", 10)) {
			this.exitPortal = NbtHelper.toBlockPos(nbt.getCompound("ExitPortal"));
		}

		this.exactTeleport = nbt.getBoolean("ExactTeleport");
	}

	@Override
	public double getSquaredRenderDistance() {
		return 65536.0;
	}

	@Override
	public void tick() {
		boolean bl = this.method_11692();
		boolean bl2 = this.hasCooldown();
		this.age++;
		if (bl2) {
			this.cooldown--;
		} else if (!this.world.isClient) {
			List<Entity> list = this.world.getEntitiesInBox(Entity.class, new Box(this.getPos()));
			if (!list.isEmpty()) {
				this.teleport((Entity)list.get(0));
			}
		}

		if (bl != this.method_11692() || bl2 != this.hasCooldown()) {
			this.markDirty();
		}
	}

	public boolean method_11692() {
		return this.age < 200L;
	}

	public boolean hasCooldown() {
		return this.cooldown > 0;
	}

	public float method_11694() {
		return MathHelper.clamp((float)this.age / 200.0F, 0.0F, 1.0F);
	}

	public float method_11695() {
		return 1.0F - MathHelper.clamp((float)this.cooldown / 20.0F, 0.0F, 1.0F);
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 8, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public void method_11696() {
		if (!this.world.isClient) {
			this.cooldown = 20;
			this.world.addBlockAction(this.getPos(), this.getBlock(), 1, 0);
			this.markDirty();
		}
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		if (code == 1) {
			this.cooldown = 20;
			return true;
		} else {
			return super.onBlockAction(code, data);
		}
	}

	public void teleport(Entity entity) {
		if (!this.world.isClient && !this.hasCooldown()) {
			this.cooldown = 100;
			if (this.exitPortal == null && this.world.dimension instanceof TheEndDimension) {
				this.method_11699();
			}

			if (this.exitPortal != null) {
				BlockPos blockPos = this.exactTeleport ? this.exitPortal : this.method_11698();
				entity.refreshPositionAfterTeleport((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
			}

			this.method_11696();
		}
	}

	private BlockPos method_11698() {
		BlockPos blockPos = method_11687(this.world, this.exitPortal, 5, false);
		GATEWAY_LOGGER.debug("Best exit position for portal at {} is {}", new Object[]{this.exitPortal, blockPos});
		return blockPos.up();
	}

	private void method_11699() {
		Vec3d vec3d = new Vec3d((double)this.getPos().getX(), 0.0, (double)this.getPos().getZ()).normalize();
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int i = 16; getChunk(this.world, vec3d2).getHighestNonEmptySectionYOffset() > 0 && i-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			GATEWAY_LOGGER.debug("Skipping backwards past nonempty chunk at {}", new Object[]{vec3d2});
		}

		for (int var5 = 16; getChunk(this.world, vec3d2).getHighestNonEmptySectionYOffset() == 0 && var5-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			GATEWAY_LOGGER.debug("Skipping forward past empty chunk at {}", new Object[]{vec3d2});
		}

		GATEWAY_LOGGER.debug("Found chunk at {}", new Object[]{vec3d2});
		Chunk chunk = getChunk(this.world, vec3d2);
		this.exitPortal = method_11688(chunk);
		if (this.exitPortal == null) {
			this.exitPortal = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
			GATEWAY_LOGGER.debug("Failed to find suitable block, settling on {}", new Object[]{this.exitPortal});
			new class_2754().generate(this.world, new Random(this.exitPortal.asLong()), this.exitPortal);
		} else {
			GATEWAY_LOGGER.debug("Found block at {}", new Object[]{this.exitPortal});
		}

		this.exitPortal = method_11687(this.world, this.exitPortal, 16, true);
		GATEWAY_LOGGER.debug("Creating portal at {}", new Object[]{this.exitPortal});
		this.exitPortal = this.exitPortal.up(10);
		this.generateEndGateway(this.exitPortal);
		this.markDirty();
	}

	private static BlockPos method_11687(World world, BlockPos blockPos, int i, boolean bl) {
		BlockPos blockPos2 = null;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (j != 0 || k != 0 || bl) {
					for (int l = 255; l > (blockPos2 == null ? 0 : blockPos2.getY()); l--) {
						BlockPos blockPos3 = new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
						BlockState blockState = world.getBlockState(blockPos3);
						if (blockState.method_11733() && (bl || blockState.getBlock() != Blocks.BEDROCK)) {
							blockPos2 = blockPos3;
							break;
						}
					}
				}
			}
		}

		return blockPos2 == null ? blockPos : blockPos2;
	}

	private static Chunk getChunk(World world, Vec3d position) {
		return world.getChunk(MathHelper.floor(position.x / 16.0), MathHelper.floor(position.z / 16.0));
	}

	@Nullable
	private static BlockPos method_11688(Chunk chunk) {
		BlockPos blockPos = new BlockPos(chunk.chunkX * 16, 30, chunk.chunkZ * 16);
		int i = chunk.getHighestNonEmptySectionYOffset() + 16 - 1;
		BlockPos blockPos2 = new BlockPos(chunk.chunkX * 16 + 16 - 1, i, chunk.chunkZ * 16 + 16 - 1);
		BlockPos blockPos3 = null;
		double d = 0.0;

		for (BlockPos blockPos4 : BlockPos.iterate(blockPos, blockPos2)) {
			BlockState blockState = chunk.getBlockState(blockPos4);
			if (blockState.getBlock() == Blocks.END_STONE
				&& !chunk.getBlockState(blockPos4.up(1)).method_11733()
				&& !chunk.getBlockState(blockPos4.up(2)).method_11733()) {
				double e = blockPos4.squaredDistanceToCenter(0.0, 0.0, 0.0);
				if (blockPos3 == null || e < d) {
					blockPos3 = blockPos4;
					d = e;
				}
			}
		}

		return blockPos3;
	}

	private void generateEndGateway(BlockPos position) {
		new EndGatewayFeature().generate(this.world, new Random(), position);
		BlockEntity blockEntity = this.world.getBlockEntity(position);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
			endGatewayBlockEntity.exitPortal = new BlockPos(this.getPos());
			endGatewayBlockEntity.markDirty();
		} else {
			GATEWAY_LOGGER.warn("Couldn't save exit portal at {}", new Object[]{position});
		}
	}

	public boolean method_11689(Direction direction) {
		return this.getBlock().getDefaultState().method_11724(this.world, this.getPos(), direction);
	}

	public int method_11697() {
		int i = 0;

		for (Direction direction : Direction.values()) {
			i += this.method_11689(direction) ? 1 : 0;
		}

		return i;
	}
}
