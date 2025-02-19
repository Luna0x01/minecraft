package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsDataFetcher;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class RealmsNotificationsScreen extends RealmsScreen {
	private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
	private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
	private static final Identifier NEWS_NOTIFICATION = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
	private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher(MinecraftClient.getInstance(), RealmsClient.createRealmsClient());
	private volatile int numberOfPendingInvites;
	static boolean checkedMcoAvailability;
	private static boolean trialAvailable;
	static boolean validClient;
	private static boolean hasUnreadNews;

	public RealmsNotificationsScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public void init() {
		this.checkIfMcoEnabled();
		this.client.keyboard.setRepeatEvents(true);
	}

	@Override
	public void tick() {
		if ((!this.shouldShowNotifications() || !this.isTitleScreen() || !validClient) && !REALMS_DATA_FETCHER.isStopped()) {
			REALMS_DATA_FETCHER.stop();
		} else if (validClient && this.shouldShowNotifications()) {
			REALMS_DATA_FETCHER.initWithSpecificTaskList();
			if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
				this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
			}

			if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
				trialAvailable = REALMS_DATA_FETCHER.isTrialAvailable();
			}

			if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
				hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
			}

			REALMS_DATA_FETCHER.markClean();
		}
	}

	private boolean shouldShowNotifications() {
		return this.client.options.realmsNotifications;
	}

	private boolean isTitleScreen() {
		return this.client.currentScreen instanceof TitleScreen;
	}

	private void checkIfMcoEnabled() {
		if (!checkedMcoAvailability) {
			checkedMcoAvailability = true;
			(new Thread("Realms Notification Availability checker #1") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					try {
						RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
						if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
							return;
						}
					} catch (RealmsServiceException var3) {
						if (var3.httpResultCode != 401) {
							RealmsNotificationsScreen.checkedMcoAvailability = false;
						}

						return;
					}

					RealmsNotificationsScreen.validClient = true;
				}
			}).start();
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (validClient) {
			this.drawIcons(matrices, mouseX, mouseY);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void drawIcons(MatrixStack matrices, int mouseX, int mouseY) {
		int i = this.numberOfPendingInvites;
		int j = 24;
		int k = this.height / 4 + 48;
		int l = this.width / 2 + 80;
		int m = k + 48 + 2;
		int n = 0;
		if (hasUnreadNews) {
			RenderSystem.setShaderTexture(0, NEWS_NOTIFICATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			matrices.push();
			matrices.scale(0.4F, 0.4F, 0.4F);
			DrawableHelper.drawTexture(matrices, (int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
			matrices.pop();
			n += 14;
		}

		if (i != 0) {
			RenderSystem.setShaderTexture(0, INVITE_ICON);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, l - n, m - 6, 0.0F, 0.0F, 15, 25, 31, 25);
			n += 16;
		}

		if (trialAvailable) {
			RenderSystem.setShaderTexture(0, TRIAL_ICON);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int o = 0;
			if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
				o = 8;
			}

			DrawableHelper.drawTexture(matrices, l + 4 - n, m + 4, 0.0F, (float)o, 8, 8, 8, 16);
		}
	}

	@Override
	public void removed() {
		REALMS_DATA_FETCHER.stop();
	}
}
