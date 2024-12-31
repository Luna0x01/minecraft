package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "summon";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.summon.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.summon.usage");
		} else {
			String string = args[0];
			BlockPos blockPos = source.getBlockPos();
			Vec3d vec3d = source.getPos();
			double d = vec3d.x;
			double e = vec3d.y;
			double f = vec3d.z;
			if (args.length >= 4) {
				d = parseDouble(d, args[1], true);
				e = parseDouble(e, args[2], false);
				f = parseDouble(f, args[3], true);
				blockPos = new BlockPos(d, e, f);
			}

			World world = source.getWorld();
			if (!world.blockExists(blockPos)) {
				throw new CommandException("commands.summon.outOfWorld");
			} else if ("LightningBolt".equals(string)) {
				world.addEntity(new LightningBoltEntity(world, d, e, f));
				run(source, this, "commands.summon.success", new Object[0]);
			} else {
				NbtCompound nbtCompound = new NbtCompound();
				boolean bl = false;
				if (args.length >= 5) {
					Text text = method_4635(source, args, 4);

					try {
						nbtCompound = StringNbtReader.parse(text.asUnformattedString());
						bl = true;
					} catch (NbtException var20) {
						throw new CommandException("commands.summon.tagError", var20.getMessage());
					}
				}

				nbtCompound.putString("id", string);

				Entity entity;
				try {
					entity = EntityType.createInstanceFromNbt(nbtCompound, world);
				} catch (RuntimeException var19) {
					throw new CommandException("commands.summon.failed");
				}

				if (entity == null) {
					throw new CommandException("commands.summon.failed");
				} else {
					entity.refreshPositionAndAngles(d, e, f, entity.yaw, entity.pitch);
					if (!bl && entity instanceof MobEntity) {
						((MobEntity)entity).initialize(world.getLocalDifficulty(new BlockPos(entity)), null);
					}

					world.spawnEntity(entity);
					Entity entity3 = entity;

					for (NbtCompound nbtCompound2 = nbtCompound; entity3 != null && nbtCompound2.contains("Riding", 10); nbtCompound2 = nbtCompound2.getCompound("Riding")) {
						Entity entity4 = EntityType.createInstanceFromNbt(nbtCompound2.getCompound("Riding"), world);
						if (entity4 != null) {
							entity4.refreshPositionAndAngles(d, e, f, entity4.yaw, entity4.pitch);
							world.spawnEntity(entity4);
							entity3.startRiding(entity4);
						}

						entity3 = entity4;
					}

					run(source, this, "commands.summon.success", new Object[0]);
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_10708(args, EntityType.getEntityNames());
		} else {
			return args.length > 1 && args.length <= 4 ? method_10707(args, 1, pos) : null;
		}
	}
}
