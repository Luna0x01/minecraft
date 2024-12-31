package net.minecraft.entity.vehicle;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
	private final CommandBlockExecutor executor = new CommandBlockExecutor() {
		@Override
		public void markDirty() {
			CommandBlockMinecartEntity.this.getDataTracker().setProperty(23, this.getCommand());
			CommandBlockMinecartEntity.this.getDataTracker().setProperty(24, Text.Serializer.serialize(this.getLastOutput()));
		}

		@Override
		public int getType() {
			return 1;
		}

		@Override
		public void writeEntityId(ByteBuf byteBuf) {
			byteBuf.writeInt(CommandBlockMinecartEntity.this.getEntityId());
		}

		@Override
		public BlockPos getBlockPos() {
			return new BlockPos(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y + 0.5, CommandBlockMinecartEntity.this.z);
		}

		@Override
		public Vec3d getPos() {
			return new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z);
		}

		@Override
		public World getWorld() {
			return CommandBlockMinecartEntity.this.world;
		}

		@Override
		public Entity getEntity() {
			return CommandBlockMinecartEntity.this;
		}
	};
	private int lastExecuted = 0;

	public CommandBlockMinecartEntity(World world) {
		super(world);
	}

	public CommandBlockMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().track(23, "");
		this.getDataTracker().track(24, "");
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.executor.fromNbt(nbt);
		this.getDataTracker().setProperty(23, this.getCommandExecutor().getCommand());
		this.getDataTracker().setProperty(24, Text.Serializer.serialize(this.getCommandExecutor().getLastOutput()));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.executor.toNbt(nbt);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.COMMAND_BLOCK;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.COMMAND_BLOCK.getDefaultState();
	}

	public CommandBlockExecutor getCommandExecutor() {
		return this.executor;
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered && this.ticksAlive - this.lastExecuted >= 4) {
			this.getCommandExecutor().execute(this.world);
			this.lastExecuted = this.ticksAlive;
		}
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		this.executor.interact(player);
		return false;
	}

	@Override
	public void method_8364(int i) {
		super.method_8364(i);
		if (i == 24) {
			try {
				this.executor.setLastOutput(Text.Serializer.deserialize(this.getDataTracker().getString(24)));
			} catch (Throwable var3) {
			}
		} else if (i == 23) {
			this.executor.setCommand(this.getDataTracker().getString(23));
		}
	}
}
