package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.Command;
import net.minecraft.command.CommandProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.CommandBlockExecutor;

public class CommandManager extends CommandRegistry implements CommandProvider {
	public CommandManager() {
		this.registerCommand(new TimeCommand());
		this.registerCommand(new GameModeCommand());
		this.registerCommand(new DifficultyCommand());
		this.registerCommand(new DefaultGameModeCommand());
		this.registerCommand(new KillCommand());
		this.registerCommand(new ToggleDownfallCommand());
		this.registerCommand(new WeatherCommand());
		this.registerCommand(new ExperienceCommand());
		this.registerCommand(new TeleportCommand());
		this.registerCommand(new GiveCommand());
		this.registerCommand(new ReplaceItemCommand());
		this.registerCommand(new StatsCommand());
		this.registerCommand(new EffectCommand());
		this.registerCommand(new EnchantCommand());
		this.registerCommand(new ParticleCommand());
		this.registerCommand(new MeCommand());
		this.registerCommand(new SeedCommand());
		this.registerCommand(new HelpCommand());
		this.registerCommand(new DebugCommand());
		this.registerCommand(new MessageCommand());
		this.registerCommand(new SayCommand());
		this.registerCommand(new SpawnPointCommand());
		this.registerCommand(new SetWorldSpawnCommand());
		this.registerCommand(new GameRuleCommand());
		this.registerCommand(new ClearCommand());
		this.registerCommand(new TestForCommand());
		this.registerCommand(new SpreadPlayersCommand());
		this.registerCommand(new PlaySoundCommand());
		this.registerCommand(new ScoreboardCommand());
		this.registerCommand(new ExecuteCommand());
		this.registerCommand(new TriggerCommand());
		this.registerCommand(new AchievementCommand());
		this.registerCommand(new SummonCommand());
		this.registerCommand(new SetBlockCommand());
		this.registerCommand(new FillCommand());
		this.registerCommand(new CloneCommand());
		this.registerCommand(new TestForBlocksCommand());
		this.registerCommand(new BlockDataCommand());
		this.registerCommand(new TestForBlockCommand());
		this.registerCommand(new TellRawCommand());
		this.registerCommand(new WorldBorderCommand());
		this.registerCommand(new TitleCommand());
		this.registerCommand(new EntityDataCommand());
		if (MinecraftServer.getServer().isDedicated()) {
			this.registerCommand(new OpCommand());
			this.registerCommand(new DeOpCommand());
			this.registerCommand(new StopCommand());
			this.registerCommand(new SaveAllCommand());
			this.registerCommand(new SaveOffCommand());
			this.registerCommand(new SaveOnCommand());
			this.registerCommand(new BanIpCommand());
			this.registerCommand(new PardonIpCommand());
			this.registerCommand(new BanCommand());
			this.registerCommand(new BanListCommand());
			this.registerCommand(new PardonCommand());
			this.registerCommand(new KickCommand());
			this.registerCommand(new ListCommand());
			this.registerCommand(new WhitelistCommand());
			this.registerCommand(new SetIdleTimeoutCommand());
		} else {
			this.registerCommand(new PublishCommand());
		}

		AbstractCommand.setCommandProvider(this);
	}

	@Override
	public void run(CommandSource sender, Command command, int permissionLevel, String label, Object... args) {
		boolean bl = true;
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		if (!sender.sendCommandFeedback()) {
			bl = false;
		}

		Text text = new TranslatableText("chat.type.admin", sender.getTranslationKey(), new TranslatableText(label, args));
		text.getStyle().setFormatting(Formatting.GRAY);
		text.getStyle().setItalic(true);
		if (bl) {
			for (PlayerEntity playerEntity : minecraftServer.getPlayerManager().getPlayers()) {
				if (playerEntity != sender && minecraftServer.getPlayerManager().isOperator(playerEntity.getGameProfile()) && command.isAccessible(sender)) {
					boolean bl2 = sender instanceof MinecraftServer && MinecraftServer.getServer().shouldBroadcastConsoleToIps();
					boolean bl3 = sender instanceof Console && MinecraftServer.getServer().shouldBroadcastRconToOps();
					if (bl2 || bl3 || !(sender instanceof Console) && !(sender instanceof MinecraftServer)) {
						playerEntity.sendMessage(text);
					}
				}
			}
		}

		if (sender != minecraftServer && minecraftServer.worlds[0].getGameRules().getBoolean("logAdminCommands")) {
			minecraftServer.sendMessage(text);
		}

		boolean bl4 = minecraftServer.worlds[0].getGameRules().getBoolean("sendCommandFeedback");
		if (sender instanceof CommandBlockExecutor) {
			bl4 = ((CommandBlockExecutor)sender).isTrackingOutput();
		}

		if ((permissionLevel & 1) != 1 && bl4 || sender instanceof MinecraftServer) {
			sender.sendMessage(new TranslatableText(label, args));
		}
	}
}
