package net.minecraft.entity.vehicle;

import net.minecraft.class_3915;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<String> COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Text> LAST_OUTPUT = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
	private final CommandBlockExecutor executor = new CommandBlockMinecartEntity.class_3532();
	private int lastExecuted;

	public CommandBlockMinecartEntity(World world) {
		super(EntityType.COMMAND_BLOCK_MINECART, world);
	}

	public CommandBlockMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.COMMAND_BLOCK_MINECART, world, d, e, f);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(COMMAND, "");
		this.getDataTracker().startTracking(LAST_OUTPUT, new LiteralText(""));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.executor.fromNbt(nbt);
		this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
		this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
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
	public boolean interact(PlayerEntity player, Hand hand) {
		this.executor.interact(player);
		return true;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (LAST_OUTPUT.equals(data)) {
			try {
				this.executor.setLastOutput(this.getDataTracker().get(LAST_OUTPUT));
			} catch (Throwable var3) {
			}
		} else if (COMMAND.equals(data)) {
			this.executor.setCommand(this.getDataTracker().get(COMMAND));
		}
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}

	public class class_3532 extends CommandBlockExecutor {
		@Override
		public ServerWorld method_16273() {
			return (ServerWorld)CommandBlockMinecartEntity.this.world;
		}

		@Override
		public void markDirty() {
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
		}

		@Override
		public Vec3d method_16274() {
			return new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z);
		}

		public CommandBlockMinecartEntity method_15964() {
			return CommandBlockMinecartEntity.this;
		}

		@Override
		public class_3915 method_16276() {
			return new class_3915(
				this,
				new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z),
				CommandBlockMinecartEntity.this.getRotationClient(),
				this.method_16273(),
				2,
				this.method_16277().getString(),
				CommandBlockMinecartEntity.this.getName(),
				this.method_16273().getServer(),
				CommandBlockMinecartEntity.this
			);
		}
	}
}
