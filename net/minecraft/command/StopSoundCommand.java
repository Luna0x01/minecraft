package net.minecraft.command;

import io.netty.buffer.Unpooled;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.Sound;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class StopSoundCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "stopsound";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.stopsound.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length >= 1 && args.length <= 3) {
			int i = 0;
			ServerPlayerEntity serverPlayerEntity = method_4639(minecraftServer, commandSource, args[i++]);
			String string = "";
			String string2 = "";
			if (args.length >= 2) {
				String string3 = args[i++];
				SoundCategory soundCategory = SoundCategory.byName(string3);
				if (soundCategory == null) {
					throw new CommandException("commands.stopsound.unknownSoundSource", string3);
				}

				string = soundCategory.getName();
			}

			if (args.length == 3) {
				string2 = args[i++];
			}

			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeString(string);
			packetByteBuf.writeString(string2);
			serverPlayerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|StopSound", packetByteBuf));
			if (string.isEmpty() && string2.isEmpty()) {
				run(commandSource, this, "commands.stopsound.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
			} else if (string2.isEmpty()) {
				run(commandSource, this, "commands.stopsound.success.soundSource", new Object[]{string, serverPlayerEntity.getTranslationKey()});
			} else {
				run(commandSource, this, "commands.stopsound.success.individualSound", new Object[]{string2, string, serverPlayerEntity.getTranslationKey()});
			}
		} else {
			throw new IncorrectUsageException(this.getUsageTranslationKey(commandSource));
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else if (strings.length == 2) {
			return method_10708(strings, SoundCategory.method_12844());
		} else {
			return strings.length == 3 ? method_10708(strings, Sound.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
