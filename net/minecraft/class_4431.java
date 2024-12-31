package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;

public class class_4431 {
	private static final Dynamic4CommandExceptionType field_21789 = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.teams", object, object2, object3, object4)
	);
	private static final Dynamic4CommandExceptionType field_21790 = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.entities", object, object2, object3, object4)
	);

	public static void method_21017(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("spreadplayers").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("center", class_4284.method_19539())
						.then(
							CommandManager.method_17530("spreadDistance", FloatArgumentType.floatArg(0.0F))
								.then(
									CommandManager.method_17530("maxRange", FloatArgumentType.floatArg(1.0F))
										.then(
											CommandManager.method_17530("respectTeams", BoolArgumentType.bool())
												.then(
													CommandManager.method_17530("targets", class_4062.method_17899())
														.executes(
															commandContext -> method_21015(
																	(class_3915)commandContext.getSource(),
																	class_4284.method_19541(commandContext, "center"),
																	FloatArgumentType.getFloat(commandContext, "spreadDistance"),
																	FloatArgumentType.getFloat(commandContext, "maxRange"),
																	BoolArgumentType.getBool(commandContext, "respectTeams"),
																	class_4062.method_17901(commandContext, "targets")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_21015(class_3915 arg, Vec2f vec2f, float f, float g, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
		Random random = new Random();
		double d = (double)(vec2f.x - g);
		double e = (double)(vec2f.y - g);
		double h = (double)(vec2f.x + g);
		double i = (double)(vec2f.y + g);
		class_4431.class_1598[] lvs = method_21022(random, bl ? method_21020(collection) : collection.size(), d, e, h, i);
		method_21016(vec2f, (double)f, arg.method_17468(), random, d, e, h, i, lvs, bl);
		double j = method_21021(collection, arg.method_17468(), lvs, bl);
		arg.method_17459(
			new TranslatableText("commands.spreadplayers.success." + (bl ? "teams" : "entities"), lvs.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", j)),
			true
		);
		return lvs.length;
	}

	private static int method_21020(Collection<? extends Entity> collection) {
		Set<AbstractTeam> set = Sets.newHashSet();

		for (Entity entity : collection) {
			if (entity instanceof PlayerEntity) {
				set.add(entity.getScoreboardTeam());
			} else {
				set.add(null);
			}
		}

		return set.size();
	}

	private static void method_21016(
		Vec2f vec2f, double d, ServerWorld serverWorld, Random random, double e, double f, double g, double h, class_4431.class_1598[] args, boolean bl
	) throws CommandSyntaxException {
		boolean bl2 = true;
		double i = Float.MAX_VALUE;

		int j;
		for (j = 0; j < 10000 && bl2; j++) {
			bl2 = false;
			i = Float.MAX_VALUE;

			for (int k = 0; k < args.length; k++) {
				class_4431.class_1598 lv = args[k];
				int l = 0;
				class_4431.class_1598 lv2 = new class_4431.class_1598();

				for (int m = 0; m < args.length; m++) {
					if (k != m) {
						class_4431.class_1598 lv3 = args[m];
						double n = lv.method_5560(lv3);
						i = Math.min(n, i);
						if (n < d) {
							l++;
							lv2.field_6263 = lv2.field_6263 + (lv3.field_6263 - lv.field_6263);
							lv2.field_6264 = lv2.field_6264 + (lv3.field_6264 - lv.field_6264);
						}
					}
				}

				if (l > 0) {
					lv2.field_6263 = lv2.field_6263 / (double)l;
					lv2.field_6264 = lv2.field_6264 / (double)l;
					double o = (double)lv2.method_5562();
					if (o > 0.0) {
						lv2.method_5557();
						lv.method_5564(lv2);
					} else {
						lv.method_5561(random, e, f, g, h);
					}

					bl2 = true;
				}

				if (lv.method_5558(e, f, g, h)) {
					bl2 = true;
				}
			}

			if (!bl2) {
				for (class_4431.class_1598 lv4 : args) {
					if (!lv4.method_21026(serverWorld)) {
						lv4.method_5561(random, e, f, g, h);
						bl2 = true;
					}
				}
			}
		}

		if (i == Float.MAX_VALUE) {
			i = 0.0;
		}

		if (j >= 10000) {
			if (bl) {
				throw field_21789.create(args.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", i));
			} else {
				throw field_21790.create(args.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", i));
			}
		}
	}

	private static double method_21021(Collection<? extends Entity> collection, ServerWorld serverWorld, class_4431.class_1598[] args, boolean bl) {
		double d = 0.0;
		int i = 0;
		Map<AbstractTeam, class_4431.class_1598> map = Maps.newHashMap();

		for (Entity entity : collection) {
			class_4431.class_1598 lv;
			if (bl) {
				AbstractTeam abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
				if (!map.containsKey(abstractTeam)) {
					map.put(abstractTeam, args[i++]);
				}

				lv = (class_4431.class_1598)map.get(abstractTeam);
			} else {
				lv = args[i++];
			}

			entity.refreshPositionAfterTeleport(
				(double)((float)MathHelper.floor(lv.field_6263) + 0.5F), (double)lv.method_21024(serverWorld), (double)MathHelper.floor(lv.field_6264) + 0.5
			);
			double e = Double.MAX_VALUE;

			for (class_4431.class_1598 lv3 : args) {
				if (lv != lv3) {
					double f = lv.method_5560(lv3);
					e = Math.min(f, e);
				}
			}

			d += e;
		}

		return collection.size() < 2 ? 0.0 : d / (double)collection.size();
	}

	private static class_4431.class_1598[] method_21022(Random random, int i, double d, double e, double f, double g) {
		class_4431.class_1598[] lvs = new class_4431.class_1598[i];

		for (int j = 0; j < lvs.length; j++) {
			class_4431.class_1598 lv = new class_4431.class_1598();
			lv.method_5561(random, d, e, f, g);
			lvs[j] = lv;
		}

		return lvs;
	}

	static class class_1598 {
		private double field_6263;
		private double field_6264;

		double method_5560(class_4431.class_1598 arg) {
			double d = this.field_6263 - arg.field_6263;
			double e = this.field_6264 - arg.field_6264;
			return Math.sqrt(d * d + e * e);
		}

		void method_5557() {
			double d = (double)this.method_5562();
			this.field_6263 /= d;
			this.field_6264 /= d;
		}

		float method_5562() {
			return MathHelper.sqrt(this.field_6263 * this.field_6263 + this.field_6264 * this.field_6264);
		}

		public void method_5564(class_4431.class_1598 arg) {
			this.field_6263 = this.field_6263 - arg.field_6263;
			this.field_6264 = this.field_6264 - arg.field_6264;
		}

		public boolean method_5558(double d, double e, double f, double g) {
			boolean bl = false;
			if (this.field_6263 < d) {
				this.field_6263 = d;
				bl = true;
			} else if (this.field_6263 > f) {
				this.field_6263 = f;
				bl = true;
			}

			if (this.field_6264 < e) {
				this.field_6264 = e;
				bl = true;
			} else if (this.field_6264 > g) {
				this.field_6264 = g;
				bl = true;
			}

			return bl;
		}

		public int method_21024(BlockView blockView) {
			BlockPos blockPos = new BlockPos(this.field_6263, 256.0, this.field_6264);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				if (!blockView.getBlockState(blockPos).isAir()) {
					return blockPos.getY() + 1;
				}
			}

			return 257;
		}

		public boolean method_21026(BlockView blockView) {
			BlockPos blockPos = new BlockPos(this.field_6263, 256.0, this.field_6264);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				BlockState blockState = blockView.getBlockState(blockPos);
				if (!blockState.isAir()) {
					Material material = blockState.getMaterial();
					return !material.isFluid() && material != Material.FIRE;
				}
			}

			return false;
		}

		public void method_5561(Random random, double d, double e, double f, double g) {
			this.field_6263 = MathHelper.nextDouble(random, d, f);
			this.field_6264 = MathHelper.nextDouble(random, e, g);
		}
	}
}
