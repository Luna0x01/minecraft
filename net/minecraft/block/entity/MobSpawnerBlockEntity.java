package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.Schema;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class MobSpawnerBlockEntity extends BlockEntity implements Tickable {
	private final SpawnerBlockEntityBehavior behaviorHandler = new SpawnerBlockEntityBehavior() {
		@Override
		public void sendStatus(int status) {
			MobSpawnerBlockEntity.this.world.addBlockAction(MobSpawnerBlockEntity.this.pos, Blocks.SPAWNER, status, 0);
		}

		@Override
		public World getWorld() {
			return MobSpawnerBlockEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return MobSpawnerBlockEntity.this.pos;
		}

		@Override
		public void setSpawnData(SpawnerBlockEntityBehaviorEntry data) {
			super.setSpawnData(data);
			if (this.getWorld() != null) {
				BlockState blockState = this.getWorld().getBlockState(this.getPos());
				this.getWorld().method_11481(MobSpawnerBlockEntity.this.pos, blockState, blockState, 4);
			}
		}
	};

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if (BlockEntity.getIdentifier(MobSpawnerBlockEntity.class).equals(new Identifier(tag.getString("id")))) {
					if (tag.contains("SpawnPotentials", 9)) {
						NbtList nbtList = tag.getList("SpawnPotentials", 10);

						for (int i = 0; i < nbtList.size(); i++) {
							NbtCompound nbtCompound = nbtList.getCompound(i);
							nbtCompound.put("Entity", dataFixer.update(LevelDataType.ENTITY, nbtCompound.getCompound("Entity"), dataVersion));
						}
					}

					tag.put("SpawnData", dataFixer.update(LevelDataType.ENTITY, tag.getCompound("SpawnData"), dataVersion));
				}

				return tag;
			}
		});
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.behaviorHandler.deserialize(nbt);
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		this.behaviorHandler.toTag(nbt);
		return nbt;
	}

	@Override
	public void tick() {
		this.behaviorHandler.tick();
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 1, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		NbtCompound nbtCompound = this.toNbt(new NbtCompound());
		nbtCompound.remove("SpawnPotentials");
		return nbtCompound;
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		return this.behaviorHandler.handleStatus(code) ? true : super.onBlockAction(code, data);
	}

	@Override
	public boolean shouldNotCopyNbtFromItem() {
		return true;
	}

	public SpawnerBlockEntityBehavior getLogic() {
		return this.behaviorHandler;
	}
}
