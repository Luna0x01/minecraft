package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PlayerSelector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpreadPlayersCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "spreadplayers";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.spreadplayers.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 6) {
			throw new IncorrectUsageException("commands.spreadplayers.usage");
		} else {
			int i = 0;
			BlockPos blockPos = source.getBlockPos();
			double d = parseDouble((double)blockPos.getX(), args[i++], true);
			double e = parseDouble((double)blockPos.getZ(), args[i++], true);
			double f = parseClampedDouble(args[i++], 0.0);
			double g = parseClampedDouble(args[i++], f + 1.0);
			boolean bl = parseBoolean(args[i++]);
			List<Entity> list = Lists.newArrayList();

			while (i < args.length) {
				String string = args[i++];
				if (PlayerSelector.method_4091(string)) {
					List<Entity> list2 = PlayerSelector.method_10866(source, string, Entity.class);
					if (list2.size() == 0) {
						throw new EntityNotFoundException();
					}

					list.addAll(list2);
				} else {
					PlayerEntity playerEntity = MinecraftServer.getServer().getPlayerManager().getPlayer(string);
					if (playerEntity == null) {
						throw new PlayerNotFoundException();
					}

					list.add(playerEntity);
				}
			}

			source.setStat(CommandStats.Type.AFFECTED_ENTITIES, list.size());
			if (list.isEmpty()) {
				throw new EntityNotFoundException();
			} else {
				source.sendMessage(new TranslatableText("commands.spreadplayers.spreading." + (bl ? "teams" : "players"), list.size(), g, d, e, f));
				this.method_5549(source, list, new SpreadPlayersCommand.Pile(d, e), f, g, ((Entity)list.get(0)).world, bl);
			}
		}
	}

	private void method_5549(CommandSource commandSource, List<Entity> list, SpreadPlayersCommand.Pile pile, double d, double e, World world, boolean bl) throws CommandException {
		Random random = new Random();
		double f = pile.x - e;
		double g = pile.z - e;
		double h = pile.x + e;
		double i = pile.z + e;
		SpreadPlayersCommand.Pile[] piles = this.makePiles(random, bl ? this.method_5551(list) : list.size(), f, g, h, i);
		int j = this.method_5550(pile, d, world, random, f, g, h, i, piles, bl);
		double k = this.method_5552(list, world, piles, bl);
		run(commandSource, this, "commands.spreadplayers.success." + (bl ? "teams" : "players"), new Object[]{piles.length, pile.x, pile.z});
		if (piles.length > 1) {
			commandSource.sendMessage(new TranslatableText("commands.spreadplayers.info." + (bl ? "teams" : "players"), String.format("%.2f", k), j));
		}
	}

	private int method_5551(List<Entity> list) {
		Set<AbstractTeam> set = Sets.newHashSet();

		for (Entity entity : list) {
			if (entity instanceof PlayerEntity) {
				set.add(((PlayerEntity)entity).getScoreboardTeam());
			} else {
				set.add(null);
			}
		}

		return set.size();
	}

	private int method_5550(
		SpreadPlayersCommand.Pile pile, double d, World world, Random random, double e, double f, double g, double h, SpreadPlayersCommand.Pile[] piles, boolean bl
	) throws CommandException {
		boolean bl2 = true;
		double i = Float.MAX_VALUE;

		int j;
		for (j = 0; j < 10000 && bl2; j++) {
			bl2 = false;
			i = Float.MAX_VALUE;

			for (int k = 0; k < piles.length; k++) {
				SpreadPlayersCommand.Pile pile2 = piles[k];
				int l = 0;
				SpreadPlayersCommand.Pile pile3 = new SpreadPlayersCommand.Pile();

				for (int m = 0; m < piles.length; m++) {
					if (k != m) {
						SpreadPlayersCommand.Pile pile4 = piles[m];
						double n = pile2.getDistance(pile4);
						i = Math.min(n, i);
						if (n < d) {
							l++;
							pile3.x = pile3.x + (pile4.x - pile2.x);
							pile3.z = pile3.z + (pile4.z - pile2.z);
						}
					}
				}

				if (l > 0) {
					pile3.x /= (double)l;
					pile3.z /= (double)l;
					double o = (double)pile3.absolute();
					if (o > 0.0) {
						pile3.normalize();
						pile2.subtract(pile3);
					} else {
						pile2.setPileLocation(random, e, f, g, h);
					}

					bl2 = true;
				}

				if (pile2.clamp(e, f, g, h)) {
					bl2 = true;
				}
			}

			if (!bl2) {
				for (SpreadPlayersCommand.Pile pile5 : piles) {
					if (!pile5.method_5563(world)) {
						pile5.setPileLocation(random, e, f, g, h);
						bl2 = true;
					}
				}
			}
		}

		if (j >= 10000) {
			throw new CommandException("commands.spreadplayers.failure." + (bl ? "teams" : "players"), piles.length, pile.x, pile.z, String.format("%.2f", i));
		} else {
			return j;
		}
	}

	private double method_5552(List<Entity> list, World world, SpreadPlayersCommand.Pile[] piles, boolean bl) {
		double d = 0.0;
		int i = 0;
		Map<AbstractTeam, SpreadPlayersCommand.Pile> map = Maps.newHashMap();

		for (int j = 0; j < list.size(); j++) {
			Entity entity = (Entity)list.get(j);
			SpreadPlayersCommand.Pile pile;
			if (bl) {
				AbstractTeam abstractTeam = entity instanceof PlayerEntity ? ((PlayerEntity)entity).getScoreboardTeam() : null;
				if (!map.containsKey(abstractTeam)) {
					map.put(abstractTeam, piles[i++]);
				}

				pile = (SpreadPlayersCommand.Pile)map.get(abstractTeam);
			} else {
				pile = piles[i++];
			}

			entity.refreshPositionAfterTeleport(
				(double)((float)MathHelper.floor(pile.x) + 0.5F), (double)pile.method_5559(world), (double)MathHelper.floor(pile.z) + 0.5
			);
			double e = Double.MAX_VALUE;

			for (int k = 0; k < piles.length; k++) {
				if (pile != piles[k]) {
					double f = pile.getDistance(piles[k]);
					e = Math.min(f, e);
				}
			}

			d += e;
		}

		return d / (double)list.size();
	}

	private SpreadPlayersCommand.Pile[] makePiles(Random count, int minX, double d, double e, double f, double g) {
		SpreadPlayersCommand.Pile[] piles = new SpreadPlayersCommand.Pile[minX];

		for (int i = 0; i < piles.length; i++) {
			SpreadPlayersCommand.Pile pile = new SpreadPlayersCommand.Pile();
			pile.setPileLocation(count, d, e, f, g);
			piles[i] = pile;
		}

		return piles;
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length >= 1 && args.length <= 2 ? method_10712(args, 0, pos) : null;
	}

	static class Pile {
		double x;
		double z;

		Pile() {
		}

		Pile(double d, double e) {
			this.x = d;
			this.z = e;
		}

		double getDistance(SpreadPlayersCommand.Pile other) {
			double d = this.x - other.x;
			double e = this.z - other.z;
			return Math.sqrt(d * d + e * e);
		}

		void normalize() {
			double d = (double)this.absolute();
			this.x /= d;
			this.z /= d;
		}

		float absolute() {
			return MathHelper.sqrt(this.x * this.x + this.z * this.z);
		}

		public void subtract(SpreadPlayersCommand.Pile other) {
			this.x = this.x - other.x;
			this.z = this.z - other.z;
		}

		public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
			boolean bl = false;
			if (this.x < minX) {
				this.x = minX;
				bl = true;
			} else if (this.x > maxX) {
				this.x = maxX;
				bl = true;
			}

			if (this.z < minZ) {
				this.z = minZ;
				bl = true;
			} else if (this.z > maxZ) {
				this.z = maxZ;
				bl = true;
			}

			return bl;
		}

		public int method_5559(World world) {
			BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				if (world.getBlockState(blockPos).getBlock().getMaterial() != Material.AIR) {
					return blockPos.getY() + 1;
				}
			}

			return 257;
		}

		public boolean method_5563(World world) {
			BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				Material material = world.getBlockState(blockPos).getBlock().getMaterial();
				if (material != Material.AIR) {
					return !material.isFluid() && material != Material.FIRE;
				}
			}

			return false;
		}

		public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
			this.x = MathHelper.nextDouble(random, minX, maxX);
			this.z = MathHelper.nextDouble(random, minZ, maxZ);
		}
	}
}
