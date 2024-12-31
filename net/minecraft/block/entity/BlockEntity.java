package net.minecraft.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private static Map<String, Class<? extends BlockEntity>> stringClassMap = Maps.newHashMap();
	private static Map<Class<? extends BlockEntity>, String> classStringMap = Maps.newHashMap();
	protected World world;
	protected BlockPos pos = BlockPos.ORIGIN;
	protected boolean removed;
	private int dataValue = -1;
	protected Block block;

	private static void registerBlockEntity(Class<? extends BlockEntity> clazz, String stringId) {
		if (stringClassMap.containsKey(stringId)) {
			throw new IllegalArgumentException("Duplicate id: " + stringId);
		} else {
			stringClassMap.put(stringId, clazz);
			classStringMap.put(clazz, stringId);
		}
	}

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
		String string = (String)classStringMap.get(this.getClass());
		if (string == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			tag.putString("id", string);
			tag.putInt("x", this.pos.getX());
			tag.putInt("y", this.pos.getY());
			tag.putInt("z", this.pos.getZ());
			return tag;
		}
	}

	public static BlockEntity createFromNbt(NbtCompound nbt) {
		BlockEntity blockEntity = null;
		String string = nbt.getString("id");

		try {
			Class<? extends BlockEntity> class_ = (Class<? extends BlockEntity>)stringClassMap.get(string);
			if (class_ != null) {
				blockEntity = (BlockEntity)class_.newInstance();
			}
		} catch (Throwable var5) {
			LOGGER.error("Failed to create block entity " + string, var5);
		}

		if (blockEntity != null) {
			try {
				blockEntity.fromNbt(nbt);
			} catch (Throwable var4) {
				LOGGER.error("Failed to load data for block entity " + string, var4);
				blockEntity = null;
			}
		} else {
			LOGGER.warn("Skipping BlockEntity with id " + string);
		}

		return blockEntity;
	}

	public int getDataValue() {
		if (this.dataValue == -1) {
			BlockState blockState = this.world.getBlockState(this.pos);
			this.dataValue = blockState.getBlock().getData(blockState);
		}

		return this.dataValue;
	}

	public void markDirty() {
		if (this.world != null) {
			BlockState blockState = this.world.getBlockState(this.pos);
			this.dataValue = blockState.getBlock().getData(blockState);
			this.world.markDirty(this.pos, this);
			if (this.getBlock() != Blocks.AIR) {
				this.world.updateHorizontalAdjacent(this.pos, this.getBlock());
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

	public Block getBlock() {
		if (this.block == null && this.world != null) {
			this.block = this.world.getBlockState(this.pos).getBlock();
		}

		return this.block;
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
		this.block = null;
		this.dataValue = -1;
	}

	public void populateCrashReport(CrashReportSection section) {
		section.add("Name", new CrashCallable<String>() {
			public String call() throws Exception {
				return (String)BlockEntity.classStringMap.get(BlockEntity.this.getClass()) + " // " + BlockEntity.this.getClass().getCanonicalName();
			}
		});
		if (this.world != null) {
			CrashReportSection.addBlockData(section, this.pos, this.getBlock(), this.getDataValue());
			section.add("Actual block type", new CrashCallable<String>() {
				public String call() throws Exception {
					int i = Block.getIdByBlock(BlockEntity.this.world.getBlockState(BlockEntity.this.pos).getBlock());

					try {
						return String.format("ID #%d (%s // %s)", i, Block.getById(i).getTranslationKey(), Block.getById(i).getClass().getCanonicalName());
					} catch (Throwable var3) {
						return "ID #" + i;
					}
				}
			});
			section.add("Actual block data value", new CrashCallable<String>() {
				public String call() throws Exception {
					BlockState blockState = BlockEntity.this.world.getBlockState(BlockEntity.this.pos);
					int i = blockState.getBlock().getData(blockState);
					if (i < 0) {
						return "Unknown? (Got " + i + ")";
					} else {
						String string = String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
						return String.format("%1$d / 0x%1$X / 0b%2$s", i, string);
					}
				}
			});
		}
	}

	public void setPosition(BlockPos pos) {
		if (pos instanceof BlockPos.Mutable || pos instanceof BlockPos.Pooled) {
			LOGGER.warn("Tried to assign a mutable BlockPos to a block entity...", new Error(pos.getClass().toString()));
			pos = new BlockPos(pos);
		}

		this.pos = pos;
	}

	public boolean shouldNotCopyNbtFromItem() {
		return false;
	}

	static {
		registerBlockEntity(FurnaceBlockEntity.class, "Furnace");
		registerBlockEntity(ChestBlockEntity.class, "Chest");
		registerBlockEntity(EnderChestBlockEntity.class, "EnderChest");
		registerBlockEntity(JukeboxBlock.JukeboxBlockEntity.class, "RecordPlayer");
		registerBlockEntity(DispenserBlockEntity.class, "Trap");
		registerBlockEntity(DropperBlockEntity.class, "Dropper");
		registerBlockEntity(SignBlockEntity.class, "Sign");
		registerBlockEntity(MobSpawnerBlockEntity.class, "MobSpawner");
		registerBlockEntity(NoteBlockBlockEntity.class, "Music");
		registerBlockEntity(PistonBlockEntity.class, "Piston");
		registerBlockEntity(BrewingStandBlockEntity.class, "Cauldron");
		registerBlockEntity(EnchantingTableBlockEntity.class, "EnchantTable");
		registerBlockEntity(EndPortalBlockEntity.class, "Airportal");
		registerBlockEntity(BeaconBlockEntity.class, "Beacon");
		registerBlockEntity(SkullBlockEntity.class, "Skull");
		registerBlockEntity(DaylightDetectorBlockEntity.class, "DLDetector");
		registerBlockEntity(HopperBlockEntity.class, "Hopper");
		registerBlockEntity(ComparatorBlockEntity.class, "Comparator");
		registerBlockEntity(FlowerPotBlockEntity.class, "FlowerPot");
		registerBlockEntity(BannerBlockEntity.class, "Banner");
		registerBlockEntity(StructureBlockEntity.class, "Structure");
		registerBlockEntity(EndGatewayBlockEntity.class, "EndGateway");
		registerBlockEntity(CommandBlockBlockEntity.class, "Control");
	}
}
