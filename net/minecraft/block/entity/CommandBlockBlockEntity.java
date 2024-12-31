package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;

public class CommandBlockBlockEntity extends BlockEntity {
	private boolean field_12844;
	private boolean field_12845;
	private boolean field_12846;
	private boolean field_12847;
	private final CommandBlockExecutor executor = new CommandBlockExecutor() {
		@Override
		public void setCommand(String command) {
			super.setCommand(command);
			CommandBlockBlockEntity.this.markDirty();
		}

		@Override
		public ServerWorld method_16273() {
			return (ServerWorld)CommandBlockBlockEntity.this.world;
		}

		@Override
		public void markDirty() {
			BlockState blockState = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
			this.method_16273().method_11481(CommandBlockBlockEntity.this.pos, blockState, blockState, 3);
		}

		@Override
		public Vec3d method_16274() {
			return new Vec3d(
				(double)CommandBlockBlockEntity.this.pos.getX() + 0.5,
				(double)CommandBlockBlockEntity.this.pos.getY() + 0.5,
				(double)CommandBlockBlockEntity.this.pos.getZ() + 0.5
			);
		}

		@Override
		public class_3915 method_16276() {
			return new class_3915(
				this,
				new Vec3d(
					(double)CommandBlockBlockEntity.this.pos.getX() + 0.5,
					(double)CommandBlockBlockEntity.this.pos.getY() + 0.5,
					(double)CommandBlockBlockEntity.this.pos.getZ() + 0.5
				),
				Vec2f.ZERO,
				this.method_16273(),
				2,
				this.method_16277().getString(),
				this.method_16277(),
				this.method_16273().getServer(),
				null
			);
		}
	};

	public CommandBlockBlockEntity() {
		super(BlockEntityType.COMMAND_BLOCK);
	}

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
		this.field_12844 = nbt.getBoolean("powered");
		this.field_12846 = nbt.getBoolean("conditionMet");
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
			Block block = this.method_16783().getBlock();
			if (block instanceof CommandBlock) {
				this.method_14368();
				this.world.getBlockTickScheduler().schedule(this.pos, block, block.getTickDelay(this.world));
			}
		}
	}

	public boolean method_11655() {
		return this.field_12846;
	}

	public boolean method_14368() {
		this.field_12846 = true;
		if (this.method_11658()) {
			BlockPos blockPos = this.pos.offset(((Direction)this.world.getBlockState(this.pos).getProperty(CommandBlock.FACING)).getOpposite());
			if (this.world.getBlockState(blockPos).getBlock() instanceof CommandBlock) {
				BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
				this.field_12846 = blockEntity instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() > 0;
			} else {
				this.field_12846 = false;
			}
		}

		return this.field_12846;
	}

	public boolean method_11656() {
		return this.field_12847;
	}

	public void method_11652(boolean bl) {
		this.field_12847 = bl;
	}

	public CommandBlockBlockEntity.class_2736 method_11657() {
		Block block = this.method_16783().getBlock();
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
		return blockState.getBlock() instanceof CommandBlock ? (Boolean)blockState.getProperty(CommandBlock.CONDITIONAL) : false;
	}

	@Override
	public void cancelRemoval() {
		this.resetBlock();
		super.cancelRemoval();
	}

	public static enum class_2736 {
		SEQUENCE,
		AUTO,
		REDSTONE;
	}
}
