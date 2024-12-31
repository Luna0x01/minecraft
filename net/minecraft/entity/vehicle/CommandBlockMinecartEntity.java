package net.minecraft.entity.vehicle;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.Schema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<String> COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Text> LAST_OUTPUT = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
	private final CommandBlockExecutor executor = new CommandBlockExecutor() {
		@Override
		public void markDirty() {
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
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

		@Override
		public MinecraftServer getMinecraftServer() {
			return CommandBlockMinecartEntity.this.world.getServer();
		}
	};
	private int lastExecuted;

	public CommandBlockMinecartEntity(World world) {
		super(world);
	}

	public CommandBlockMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	public static void registerDataFixes(DataFixerUpper arg) {
		AbstractMinecartEntity.method_13302(arg, "MinecartCommandBlock");
		arg.addSchema(LevelDataType.ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if ("MinecartCommandBlock".equals(tag.getString("id"))) {
					tag.putString("id", "Control");
					dataFixer.update(LevelDataType.BLOCK_ENTITY, tag, dataVersion);
					tag.putString("id", "MinecartCommandBlock");
				}

				return tag;
			}
		});
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
	public boolean method_6100(PlayerEntity playerEntity, @Nullable ItemStack itemStack, Hand hand) {
		this.executor.interact(playerEntity);
		return false;
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
}
