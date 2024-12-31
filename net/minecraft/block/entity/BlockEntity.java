package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockEntityType<?> field_18593;
	protected World world;
	protected BlockPos pos = BlockPos.ORIGIN;
	protected boolean removed;
	@Nullable
	private BlockState field_18594;

	public BlockEntity(BlockEntityType<?> blockEntityType) {
		this.field_18593 = blockEntityType;
	}

	@Nullable
	public World getEntityWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public boolean hasWorld() {
		return this.world != null;
	}

	public void fromNbt(NbtCompound nbt) {
		this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		return this.method_11648(nbt);
	}

	private NbtCompound method_11648(NbtCompound tag) {
		Identifier identifier = BlockEntityType.method_16785(this.method_16780());
		if (identifier == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			tag.putString("id", identifier.toString());
			tag.putInt("x", this.pos.getX());
			tag.putInt("y", this.pos.getY());
			tag.putInt("z", this.pos.getZ());
			return tag;
		}
	}

	@Nullable
	public static BlockEntity method_16781(NbtCompound nbtCompound) {
		BlockEntity blockEntity = null;
		String string = nbtCompound.getString("id");

		try {
			blockEntity = BlockEntityType.method_16786(string);
		} catch (Throwable var5) {
			LOGGER.error("Failed to create block entity {}", string, var5);
		}

		if (blockEntity != null) {
			try {
				blockEntity.fromNbt(nbtCompound);
			} catch (Throwable var4) {
				LOGGER.error("Failed to load data for block entity {}", string, var4);
				blockEntity = null;
			}
		} else {
			LOGGER.warn("Skipping BlockEntity with id {}", string);
		}

		return blockEntity;
	}

	public void markDirty() {
		if (this.world != null) {
			this.field_18594 = this.world.getBlockState(this.pos);
			this.world.markDirty(this.pos, this);
			if (!this.field_18594.isAir()) {
				this.world.updateHorizontalAdjacent(this.pos, this.field_18594.getBlock());
			}
		}
	}

	public double getSquaredDistance(double x, double y, double z) {
		double d = (double)this.pos.getX() + 0.5 - x;
		double e = (double)this.pos.getY() + 0.5 - y;
		double f = (double)this.pos.getZ() + 0.5 - z;
		return d * d + e * e + f * f;
	}

	public double getSquaredRenderDistance() {
		return 4096.0;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public BlockState method_16783() {
		if (this.field_18594 == null) {
			this.field_18594 = this.world.getBlockState(this.pos);
		}

		return this.field_18594;
	}

	@Nullable
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return null;
	}

	public NbtCompound getUpdatePacketContent() {
		return this.method_11648(new NbtCompound());
	}

	public boolean isRemoved() {
		return this.removed;
	}

	public void markRemoved() {
		this.removed = true;
	}

	public void cancelRemoval() {
		this.removed = false;
	}

	public boolean onBlockAction(int code, int data) {
		return false;
	}

	public void resetBlock() {
		this.field_18594 = null;
	}

	public void populateCrashReport(CrashReportSection section) {
		section.add("Name", (CrashCallable<String>)(() -> Registry.BLOCK_ENTITY_TYPE.getId(this.method_16780()) + " // " + this.getClass().getCanonicalName()));
		if (this.world != null) {
			CrashReportSection.addBlockInfo(section, this.pos, this.method_16783());
			CrashReportSection.addBlockInfo(section, this.pos, this.world.getBlockState(this.pos));
		}
	}

	public void setPosition(BlockPos pos) {
		this.pos = pos.toImmutable();
	}

	public boolean shouldNotCopyNbtFromItem() {
		return false;
	}

	public void method_13322(BlockRotation rotation) {
	}

	public void method_13321(BlockMirror mirror) {
	}

	public BlockEntityType<?> method_16780() {
		return this.field_18593;
	}
}
