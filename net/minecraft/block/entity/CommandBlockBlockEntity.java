package net.minecraft.block.entity;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockBlockEntity extends BlockEntity {
	private boolean field_12844;
	private boolean field_12845;
	private boolean field_12846;
	private boolean field_12847;
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
			BlockState blockState = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
			CommandBlockBlockEntity.this.getEntityWorld().method_11481(CommandBlockBlockEntity.this.pos, blockState, blockState, 3);
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

		@Override
		public MinecraftServer getMinecraftServer() {
			return CommandBlockBlockEntity.this.world.getServer();
		}
	};

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		this.executor.toNbt(nbt);
		nbt.putBoolean("powered", this.method_11653());
		nbt.putBoolean("conditionMet", this.method_11655());
		nbt.putBoolean("auto", this.method_11654());
		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.executor.fromNbt(nbt);
		this.method_11649(nbt.getBoolean("powered"));
		this.method_11651(nbt.getBoolean("conditionMet"));
		this.method_11650(nbt.getBoolean("auto"));
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		if (this.method_11656()) {
			this.method_11652(false);
			NbtCompound nbtCompound = this.toNbt(new NbtCompound());
			return new BlockEntityUpdateS2CPacket(this.pos, 2, nbtCompound);
		} else {
			return null;
		}
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

	public void method_11649(boolean bl) {
		this.field_12844 = bl;
	}

	public boolean method_11653() {
		return this.field_12844;
	}

	public boolean method_11654() {
		return this.field_12845;
	}

	public void method_11650(boolean bl) {
		boolean bl2 = this.field_12845;
		this.field_12845 = bl;
		if (!bl2 && bl && !this.field_12844 && this.world != null && this.method_11657() != CommandBlockBlockEntity.class_2736.SEQUENCE) {
			Block block = this.getBlock();
			if (block instanceof CommandBlock) {
				BlockPos blockPos = this.getPos();
				CommandBlock commandBlock = (CommandBlock)block;
				this.field_12846 = !this.method_11658() || commandBlock.method_11592(this.world, blockPos, this.world.getBlockState(blockPos));
				this.world.createAndScheduleBlockTick(blockPos, block, block.getTickRate(this.world));
				if (this.field_12846) {
					commandBlock.method_11591(this.world, blockPos);
				}
			}
		}
	}

	public boolean method_11655() {
		return this.field_12846;
	}

	public void method_11651(boolean bl) {
		this.field_12846 = bl;
	}

	public boolean method_11656() {
		return this.field_12847;
	}

	public void method_11652(boolean bl) {
		this.field_12847 = bl;
	}

	public CommandBlockBlockEntity.class_2736 method_11657() {
		Block block = this.getBlock();
		if (block == Blocks.COMMAND_BLOCK) {
			return CommandBlockBlockEntity.class_2736.REDSTONE;
		} else if (block == Blocks.REPEATING_COMMAND_BLOCK) {
			return CommandBlockBlockEntity.class_2736.AUTO;
		} else {
			return block == Blocks.CHAIN_COMMAND_BLOCK ? CommandBlockBlockEntity.class_2736.SEQUENCE : CommandBlockBlockEntity.class_2736.REDSTONE;
		}
	}

	public boolean method_11658() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		return blockState.getBlock() instanceof CommandBlock ? (Boolean)blockState.get(CommandBlock.field_12637) : false;
	}

	@Override
	public void cancelRemoval() {
		this.block = null;
		super.cancelRemoval();
	}

	public static enum class_2736 {
		SEQUENCE,
		AUTO,
		REDSTONE;
	}
}
