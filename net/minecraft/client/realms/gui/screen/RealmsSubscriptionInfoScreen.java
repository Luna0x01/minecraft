package net.minecraft.client.realms.gui.screen;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSubscriptionInfoScreen extends RealmsScreen {
	static final Logger LOGGER = LogManager.getLogger();
	private static final Text subscriptionTitle = new TranslatableText("mco.configure.world.subscription.title");
	private static final Text subscriptionStartLabelText = new TranslatableText("mco.configure.world.subscription.start");
	private static final Text timeLeftLabelText = new TranslatableText("mco.configure.world.subscription.timeleft");
	private static final Text daysLeftLabelText = new TranslatableText("mco.configure.world.subscription.recurring.daysleft");
	private static final Text EXPIRED_TEXT = new TranslatableText("mco.configure.world.subscription.expired");
	private static final Text EXPIRES_IN_LESS_THAN_A_DAY_TEXT = new TranslatableText("mco.configure.world.subscription.less_than_a_day");
	private static final Text MONTH_TEXT = new TranslatableText("mco.configure.world.subscription.month");
	private static final Text MONTHS_TEXT = new TranslatableText("mco.configure.world.subscription.months");
	private static final Text DAY_TEXT = new TranslatableText("mco.configure.world.subscription.day");
	private static final Text DAYS_TEXT = new TranslatableText("mco.configure.world.subscription.days");
	private static final Text UNKNOWN_TEXT = new TranslatableText("mco.configure.world.subscription.unknown");
	private final Screen parent;
	final RealmsServer serverData;
	final Screen mainScreen;
	private Text daysLeft = UNKNOWN_TEXT;
	private Text startDate = UNKNOWN_TEXT;
	@Nullable
	private Subscription.SubscriptionType type;
	private static final String EXTEND_JAVA_REALMS_URL = "https://aka.ms/ExtendJavaRealms";

	public RealmsSubscriptionInfoScreen(Screen parent, RealmsServer serverData, Screen mainScreen) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.serverData = serverData;
		this.mainScreen = mainScreen;
	}

	@Override
	public void init() {
		this.getSubscription(this.serverData.id);
		this.client.keyboard.setRepeatEvents(true);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100,
				row(6),
				200,
				20,
				new TranslatableText("mco.configure.world.subscription.extend"),
				button -> {
					String string = "https://aka.ms/ExtendJavaRealms?subscriptionId="
						+ this.serverData.remoteSubscriptionId
						+ "&profileId="
						+ this.client.getSession().getUuid();
					this.client.keyboard.setClipboard(string);
					Util.getOperatingSystem().open(string);
				}
			)
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, ScreenTexts.BACK, button -> this.client.openScreen(this.parent)));
		if (this.serverData.expired) {
			this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, row(10), 200, 20, new TranslatableText("mco.configure.world.delete.button"), button -> {
				Text text = new TranslatableText("mco.configure.world.delete.question.line1");
				Text text2 = new TranslatableText("mco.configure.world.delete.question.line2");
				this.client.openScreen(new RealmsLongConfirmationScreen(this::onDeletionConfirmed, RealmsLongConfirmationScreen.Type.Warning, text, text2, true));
			}));
		}
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinLines(subscriptionTitle, subscriptionStartLabelText, this.startDate, timeLeftLabelText, this.daysLeft);
	}

	private void onDeletionConfirmed(boolean delete) {
		if (delete) {
			(new Thread("Realms-delete-realm") {
				public void run() {
					try {
						RealmsClient realmsClient = RealmsClient.createRealmsClient();
						realmsClient.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
					} catch (RealmsServiceException var2) {
						RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
						RealmsSubscriptionInfoScreen.LOGGER.error(var2);
					}

					RealmsSubscriptionInfoScreen.this.client.execute(() -> RealmsSubscriptionInfoScreen.this.client.openScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
				}
			}).start();
		}

		this.client.openScreen(this);
	}

	private void getSubscription(long worldId) {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			Subscription subscription = realmsClient.subscriptionFor(worldId);
			this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
			this.startDate = localPresentation(subscription.startDate);
			this.type = subscription.type;
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't get subscription");
			this.client.openScreen(new RealmsGenericErrorScreen(var5, this.parent));
		}
	}

	private static Text localPresentation(long time) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(time);
		return new LiteralText(DateFormat.getDateTimeInstance().format(calendar.getTime()));
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		int i = this.width / 2 - 100;
		drawCenteredText(matrices, this.textRenderer, subscriptionTitle, this.width / 2, 17, 16777215);
		this.textRenderer.draw(matrices, subscriptionStartLabelText, (float)i, (float)row(0), 10526880);
		this.textRenderer.draw(matrices, this.startDate, (float)i, (float)row(1), 16777215);
		if (this.type == Subscription.SubscriptionType.NORMAL) {
			this.textRenderer.draw(matrices, timeLeftLabelText, (float)i, (float)row(3), 10526880);
		} else if (this.type == Subscription.SubscriptionType.RECURRING) {
			this.textRenderer.draw(matrices, daysLeftLabelText, (float)i, (float)row(3), 10526880);
		}

		this.textRenderer.draw(matrices, this.daysLeft, (float)i, (float)row(4), 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private Text daysLeftPresentation(int daysLeft) {
		if (daysLeft < 0 && this.serverData.expired) {
			return EXPIRED_TEXT;
		} else if (daysLeft <= 1) {
			return EXPIRES_IN_LESS_THAN_A_DAY_TEXT;
		} else {
			int i = daysLeft / 30;
			int j = daysLeft % 30;
			MutableText mutableText = new LiteralText("");
			if (i > 0) {
				mutableText.append(Integer.toString(i)).append(" ");
				if (i == 1) {
					mutableText.append(MONTH_TEXT);
				} else {
					mutableText.append(MONTHS_TEXT);
				}
			}

			if (j > 0) {
				if (i > 0) {
					mutableText.append(", ");
				}

				mutableText.append(Integer.toString(j)).append(" ");
				if (j == 1) {
					mutableText.append(DAY_TEXT);
				} else {
					mutableText.append(DAYS_TEXT);
				}
			}

			return mutableText;
		}
	}
}
