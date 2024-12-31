package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final SimpleRegistry<Identifier, Class<? extends BlockEntity>> BLOCK_ENTITY = new SimpleRegistry<>();
	protected World world;
	protected BlockPos pos = BlockPos.ORIGIN;
	protected boolean removed;
	private int dataValue = -1;
	protected Block block;

	private static void addBlockEntity(String identifier, Class<? extends BlockEntity> entityClass) {
		BLOCK_ENTITY.put(new Identifier(identifier), entityClass);
	}

	@Nullable
	public static Identifier getIdentifier(Class<? extends BlockEntity> entityClass) {
		return BLOCK_ENTITY.getIdentifier(entityClass);
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
		Identifier identifier = BLOCK_ENTITY.getIdentifier(this.getClass());
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
	public static BlockEntity create(World world, NbtCompound tag) {
		BlockEntity blockEntity = null;
		String string = tag.getString("id");

		try {
			Class<? extends BlockEntity> class_ = BLOCK_ENTITY.get(new Identifier(string));
			if (class_ != null) {
				blockEntity = (BlockEntity)class_.newInstance();
			}
		} catch (Throwable var6) {
			LOGGER.error("Failed to create block entity {}", string, var6);
		}

		if (blockEntity != null) {
			try {
				blockEntity.method_13323(world);
				blockEntity.fromNbt(tag);
			} catch (Throwable var5) {
				LOGGER.error("Failed to load data for block entity {}", string, var5);
				blockEntity = null;
			}
		} else {
			LOGGER.warn("Skipping BlockEntity with id {}", string);
		}

		return blockEntity;
	}

	protected void method_13323(World world) {
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
				return BlockEntity.BLOCK_ENTITY.getIdentifier(BlockEntity.this.getClass()) + " // " + BlockEntity.this.getClass().getCanonicalName();
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
		this.pos = pos.toImmutable();
	}

	public boolean shouldNotCopyNbtFromItem() {
		return false;
	}

	@Nullable
	public Text getName() {
		return null;
	}

	public void method_13322(BlockRotation rotation) {
	}

	public void method_13321(BlockMirror mirror) {
	}

	static {
		addBlockEntity("furnace", FurnaceBlockEntity.class);
		addBlockEntity("chest", ChestBlockEntity.class);
		addBlockEntity("ender_chest", EnderChestBlockEntity.class);
		addBlockEntity("jukebox", JukeboxBlock.JukeboxBlockEntity.class);
		addBlockEntity("dispenser", DispenserBlockEntity.class);
		addBlockEntity("dropper", DropperBlockEntity.class);
		addBlockEntity("sign", SignBlockEntity.class);
		addBlockEntity("mob_spawner", MobSpawnerBlockEntity.class);
		addBlockEntity("noteblock", NoteBlockBlockEntity.class);
		addBlockEntity("piston", PistonBlockEntity.class);
		addBlockEntity("brewing_stand", BrewingStandBlockEntity.class);
		addBlockEntity("enchanting_table", EnchantingTableBlockEntity.class);
		addBlockEntity("end_portal", EndPortalBlockEntity.class);
		addBlockEntity("beacon", BeaconBlockEntity.class);
		addBlockEntity("skull", SkullBlockEntity.class);
		addBlockEntity("daylight_detector", DaylightDetectorBlockEntity.class);
		addBlockEntity("hopper", HopperBlockEntity.class);
		addBlockEntity("comparator", ComparatorBlockEntity.class);
		addBlockEntity("flower_pot", FlowerPotBlockEntity.class);
		addBlockEntity("banner", BannerBlockEntity.class);
		addBlockEntity("structure_block", StructureBlockEntity.class);
		addBlockEntity("end_gateway", EndGatewayBlockEntity.class);
		addBlockEntity("command_block", CommandBlockBlockEntity.class);
		addBlockEntity("shulker_box", ShulkerBoxBlockEntity.class);
		addBlockEntity("bed", BedBlockEntity.class);
	}
}
