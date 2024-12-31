package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.class_4069;
import net.minecraft.class_4119;
import net.minecraft.class_4287;
import net.minecraft.class_4327;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;

public class SummonCommand {
	private static final SimpleCommandExceptionType field_21791 = new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed"));

	public static void method_21042(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("summon").requires(arg -> arg.method_17575(2)))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("entity", class_4069.method_17944())
							.suggests(class_4327.field_21257)
							.executes(
								commandContext -> method_21041(
										(class_3915)commandContext.getSource(),
										class_4069.method_17946(commandContext, "entity"),
										((class_3915)commandContext.getSource()).method_17467(),
										new NbtCompound(),
										true
									)
							))
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("pos", class_4287.method_19562())
									.executes(
										commandContext -> method_21041(
												(class_3915)commandContext.getSource(),
												class_4069.method_17946(commandContext, "entity"),
												class_4287.method_19564(commandContext, "pos"),
												new NbtCompound(),
												true
											)
									))
								.then(
									CommandManager.method_17530("nbt", class_4119.method_18393())
										.executes(
											commandContext -> method_21041(
													(class_3915)commandContext.getSource(),
													class_4069.method_17946(commandContext, "entity"),
													class_4287.method_19564(commandContext, "pos"),
													class_4119.method_18395(commandContext, "nbt"),
													false
												)
										)
								)
						)
				)
		);
	}

	private static int method_21041(class_3915 arg, Identifier identifier, Vec3d vec3d, NbtCompound nbtCompound, boolean bl) throws CommandSyntaxException {
		NbtCompound nbtCompound2 = nbtCompound.copy();
		nbtCompound2.putString("id", identifier.toString());
		if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(identifier)) {
			Entity entity = new LightningBoltEntity(arg.method_17468(), vec3d.x, vec3d.y, vec3d.z, false);
			arg.method_17468().addEntity(entity);
			arg.method_17459(new TranslatableText("commands.summon.success", entity.getName()), true);
			return 1;
		} else {
			Entity entity2 = ThreadedAnvilChunkStorage.method_11782(nbtCompound2, arg.method_17468(), vec3d.x, vec3d.y, vec3d.z, true);
			if (entity2 == null) {
				throw field_21791.create();
			} else {
				entity2.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entity2.yaw, entity2.pitch);
				if (bl && entity2 instanceof MobEntity) {
					((MobEntity)entity2).initialize(arg.method_17468().method_8482(new BlockPos(entity2)), null, null);
				}

				arg.method_17459(new TranslatableText("commands.summon.success", entity2.getName()), true);
				return 1;
			}
		}
	}
}
