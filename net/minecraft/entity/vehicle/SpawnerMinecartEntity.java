package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SpawnerBlockEntityBehavior;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.Schema;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final SpawnerBlockEntityBehavior spawner = new SpawnerBlockEntityBehavior() {
		@Override
		public void sendStatus(int status) {
			SpawnerMinecartEntity.this.world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)status);
		}

		@Override
		public World getWorld() {
			return SpawnerMinecartEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(SpawnerMinecartEntity.this);
		}
	};

	public SpawnerMinecartEntity(World world) {
		super(world);
	}

	public SpawnerMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		registerDataFixes(dataFixer, SpawnerMinecartEntity.class);
		dataFixer.addSchema(LevelDataType.ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				String string = tag.getString("id");
				if (EntityType.getId(SpawnerMinecartEntity.class).equals(new Identifier(string))) {
					tag.putString("id", BlockEntity.getIdentifier(MobSpawnerBlockEntity.class).toString());
					dataFixer.update(LevelDataType.BLOCK_ENTITY, tag, dataVersion);
					tag.putString("id", string);
				}

				return tag;
			}
		});
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.SPAWNER;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.SPAWNER.getDefaultState();
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.spawner.deserialize(nbt);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.spawner.toTag(nbt);
	}

	@Override
	public void handleStatus(byte status) {
		this.spawner.handleStatus(status);
	}

	@Override
	public void tick() {
		super.tick();
		this.spawner.tick();
	}
}
