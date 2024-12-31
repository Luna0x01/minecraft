package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;

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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.summon.usage");
		} else {
			String string = args[0];
			BlockPos blockPos = commandSource.getBlockPos();
			Vec3d vec3d = commandSource.getPos();
			double d = vec3d.x;
			double e = vec3d.y;
			double f = vec3d.z;
			if (args.length >= 4) {
				d = parseDouble(d, args[1], true);
				e = parseDouble(e, args[2], false);
				f = parseDouble(f, args[3], true);
				blockPos = new BlockPos(d, e, f);
			}

			World world = commandSource.getWorld();
			if (!world.blockExists(blockPos)) {
				throw new CommandException("commands.summon.outOfWorld");
			} else if (EntityType.LIGHTNING_BOLT.equals(new Identifier(string))) {
				world.addEntity(new LightningBoltEntity(world, d, e, f, false));
				run(commandSource, this, "commands.summon.success", new Object[0]);
			} else {
				NbtCompound nbtCompound = new NbtCompound();
				boolean bl = false;
				if (args.length >= 5) {
					Text text = method_4635(commandSource, args, 4);

					try {
						nbtCompound = StringNbtReader.parse(text.asUnformattedString());
						bl = true;
					} catch (NbtException var18) {
						throw new CommandException("commands.summon.tagError", var18.getMessage());
					}
				}

				nbtCompound.putString("id", string);
				Entity entity = ThreadedAnvilChunkStorage.method_11782(nbtCompound, world, d, e, f, true);
				if (entity == null) {
					throw new CommandException("commands.summon.failed");
				} else {
					entity.refreshPositionAndAngles(d, e, f, entity.yaw, entity.pitch);
					if (!bl && entity instanceof MobEntity) {
						((MobEntity)entity).initialize(world.getLocalDifficulty(new BlockPos(entity)), null);
					}

					run(commandSource, this, "commands.summon.success", new Object[0]);
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_10708(strings, EntityType.getIdentifiers());
		} else {
			return strings.length > 1 && strings.length <= 4 ? method_10707(strings, 1, pos) : Collections.emptyList();
		}
	}
}
