package net.minecraft.server.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DemoServerPlayerInteractionManager extends ServerPlayerInteractionManager {
	private boolean sentHelp;
	private boolean demoEnded;
	private int reminderTicks;
	private int tick;

	public DemoServerPlayerInteractionManager(World world) {
		super(world);
	}

	@Override
	public void tick() {
		super.tick();
		this.tick++;
		long l = this.world.getLastUpdateTime();
		long m = l / 24000L + 1L;
		if (!this.sentHelp && this.tick > 20) {
			this.sentHelp = true;
			this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 0.0F));
		}

		this.demoEnded = l > 120500L;
		if (this.demoEnded) {
			this.reminderTicks++;
		}

		if (l % 24000L == 500L) {
			if (m <= 6L) {
				this.player.sendMessage(new TranslatableText("demo.day." + m));
			}
		} else if (m == 1L) {
			if (l == 100L) {
				this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 101.0F));
			} else if (l == 175L) {
				this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 102.0F));
			} else if (l == 250L) {
				this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 103.0F));
			}
		} else if (m == 5L && l % 24000L == 22000L) {
			this.player.sendMessage(new TranslatableText("demo.day.warning"));
		}
	}

	private void sendDemoReminder() {
		if (this.reminderTicks > 100) {
			this.player.sendMessage(new TranslatableText("demo.reminder"));
			this.reminderTicks = 0;
		}
	}

	@Override
	public void processBlockBreakingAction(BlockPos pos, Direction direction) {
		if (this.demoEnded) {
			this.sendDemoReminder();
		} else {
			super.processBlockBreakingAction(pos, direction);
		}
	}

	@Override
	public void method_10764(BlockPos blockPos) {
		if (!this.demoEnded) {
			super.method_10764(blockPos);
		}
	}

	@Override
	public boolean method_10766(BlockPos pos) {
		return this.demoEnded ? false : super.method_10766(pos);
	}

	@Override
	public boolean interactItem(PlayerEntity player, World world, ItemStack stack) {
		if (this.demoEnded) {
			this.sendDemoReminder();
			return false;
		} else {
			return super.interactItem(player, world, stack);
		}
	}

	@Override
	public boolean interactBlock(PlayerEntity player, World world, ItemStack stack, BlockPos pos, Direction direction, float x, float y, float z) {
		if (this.demoEnded) {
			this.sendDemoReminder();
			return false;
		} else {
			return super.interactBlock(player, world, stack, pos, direction, x, y, z);
		}
	}
}
