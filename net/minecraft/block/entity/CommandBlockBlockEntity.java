package net.minecraft.block.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockBlockEntity extends BlockEntity {
	private final CommandBlockExecutor executor = new CommandBlockExecutor() {
		@Override
		public BlockPos getBlockPos() {
			return CommandBlockBlockEntity.this.pos;
		}

		@Override
		public Vec3d getPos() {
			return new Vec3d(
				(double)CommandBlockBlockEntity.this.pos.getX() + 0.5,
				(double)CommandBlockBlockEntity.this.pos.getY() + 0.5,
				(double)CommandBlockBlockEntity.this.pos.getZ() + 0.5
			);
		}

		@Override
		public World getWorld() {
			return CommandBlockBlockEntity.this.getEntityWorld();
		}

		@Override
		public void setCommand(String command) {
			super.setCommand(command);
			CommandBlockBlockEntity.this.markDirty();
		}

		@Override
		public void markDirty() {
			CommandBlockBlockEntity.this.getEntityWorld().onBlockUpdate(CommandBlockBlockEntity.this.pos);
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		public void writeEntityId(ByteBuf byteBuf) {
			byteBuf.writeInt(CommandBlockBlockEntity.this.pos.getX());
			byteBuf.writeInt(CommandBlockBlockEntity.this.pos.getY());
			byteBuf.writeInt(CommandBlockBlockEntity.this.pos.getZ());
		}

		@Override
		public Entity getEntity() {
			return null;
		}
	};

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		this.executor.toNbt(nbt);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.executor.fromNbt(nbt);
	}

	@Override
	public Packet getPacket() {
		NbtCompound nbtCompound = new NbtCompound();
		this.toNbt(nbtCompound);
		return new BlockEntityUpdateS2CPacket(this.pos, 2, nbtCompound);
	}

	@Override
	public boolean shouldNotCopyNbtFromItem() {
		return true;
	}

	public CommandBlockExecutor getCommandExecutor() {
		return this.executor;
	}

	public CommandStats getCommandStats() {
		return this.executor.getCommandStats();
	}
}
