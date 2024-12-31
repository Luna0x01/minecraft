package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.class_4371;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsPendingInvitesScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen lastScreen;
	private String toolTip;
	private boolean loaded;
	private RealmsPendingInvitesScreen.PendingInvitationSelectionList pendingInvitationSelectionList;
	private RealmsLabel titleLabel;
	private int selectedInvite = -1;
	private RealmsButton acceptButton;
	private RealmsButton rejectButton;

	public RealmsPendingInvitesScreen(RealmsScreen realmsScreen) {
		this.lastScreen = realmsScreen;
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.pendingInvitationSelectionList = new RealmsPendingInvitesScreen.PendingInvitationSelectionList();
		(new Thread("Realms-pending-invitations-fetcher") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					try {
						List<PendingInvite> list = realmsClient.pendingInvites().pendingInvites;
						List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> list2 = (List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry>)list.stream()
							.map(pendingInvite -> RealmsPendingInvitesScreen.this.new PendingInvitationSelectionListEntry(pendingInvite))
							.collect(Collectors.toList());
						Realms.execute((Runnable)(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list2)));
					} catch (RealmsServiceException var7) {
						RealmsPendingInvitesScreen.LOGGER.error("Couldn't list invites");
					} finally {
						RealmsPendingInvitesScreen.this.loaded = true;
					}
				}
			})
			.start();
		this.buttonsAdd(
			this.acceptButton = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.accept")) {
				@Override
				public void onPress() {
					RealmsPendingInvitesScreen.this.accept(RealmsPendingInvitesScreen.this.selectedInvite);
					RealmsPendingInvitesScreen.this.selectedInvite = -1;
					RealmsPendingInvitesScreen.this.updateButtonStates();
				}
			}
		);
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, getLocalizedString("gui.done")) {
			@Override
			public void onPress() {
				Realms.setScreen(new RealmsMainScreen(RealmsPendingInvitesScreen.this.lastScreen));
			}
		});
		this.buttonsAdd(
			this.rejectButton = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.reject")) {
				@Override
				public void onPress() {
					RealmsPendingInvitesScreen.this.reject(RealmsPendingInvitesScreen.this.selectedInvite);
					RealmsPendingInvitesScreen.this.selectedInvite = -1;
					RealmsPendingInvitesScreen.this.updateButtonStates();
				}
			}
		);
		this.titleLabel = new RealmsLabel(getLocalizedString("mco.invites.title"), this.width() / 2, 12, 16777215);
		this.addWidget(this.titleLabel);
		this.addWidget(this.pendingInvitationSelectionList);
		this.narrateLabels();
		this.updateButtonStates();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(new RealmsMainScreen(this.lastScreen));
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void updateList(int i) {
		this.pendingInvitationSelectionList.removeAtIndex(i);
	}

	private void reject(int i) {
		if (i < this.pendingInvitationSelectionList.getItemCount()) {
			(new Thread("Realms-reject-invitation") {
					public void run() {
						try {
							RealmsClient realmsClient = RealmsClient.createRealmsClient();
							realmsClient.rejectInvitation(
								((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(i))
									.mPendingInvite
									.invitationId
							);
							Realms.execute((Runnable)(() -> RealmsPendingInvitesScreen.this.updateList(i)));
						} catch (RealmsServiceException var2) {
							RealmsPendingInvitesScreen.LOGGER.error("Couldn't reject invite");
						}
					}
				})
				.start();
		}
	}

	private void accept(int i) {
		if (i < this.pendingInvitationSelectionList.getItemCount()) {
			(new Thread("Realms-accept-invitation") {
					public void run() {
						try {
							RealmsClient realmsClient = RealmsClient.createRealmsClient();
							realmsClient.acceptInvitation(
								((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(i))
									.mPendingInvite
									.invitationId
							);
							Realms.execute((Runnable)(() -> RealmsPendingInvitesScreen.this.updateList(i)));
						} catch (RealmsServiceException var2) {
							RealmsPendingInvitesScreen.LOGGER.error("Couldn't accept invite");
						}
					}
				})
				.start();
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.toolTip = null;
		this.renderBackground();
		this.pendingInvitationSelectionList.render(i, j, f);
		this.titleLabel.render(this);
		if (this.toolTip != null) {
			this.renderMousehoverTooltip(this.toolTip, i, j);
		}

		if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
			this.drawCenteredString(getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 16777215);
		}

		super.render(i, j, f);
	}

	protected void renderMousehoverTooltip(String string, int i, int j) {
		if (string != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.fontWidth(string);
			this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, k, l, 16777215);
		}
	}

	private void updateButtonStates() {
		this.acceptButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
		this.rejectButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
	}

	private boolean shouldAcceptAndRejectButtonBeVisible(int i) {
		return i != -1;
	}

	public static String method_21301(PendingInvite pendingInvite) {
		return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - pendingInvite.date.getTime());
	}

	class PendingInvitationSelectionList extends RealmsObjectSelectionList<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> {
		public PendingInvitationSelectionList() {
			super(RealmsPendingInvitesScreen.this.width(), RealmsPendingInvitesScreen.this.height(), 32, RealmsPendingInvitesScreen.this.height() - 40, 36);
		}

		public void removeAtIndex(int i) {
			this.remove(i);
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public int getRowWidth() {
			return 260;
		}

		@Override
		public boolean isFocused() {
			return RealmsPendingInvitesScreen.this.isFocused(this);
		}

		@Override
		public void renderBackground() {
			RealmsPendingInvitesScreen.this.renderBackground();
		}

		@Override
		public void selectItem(int i) {
			this.setSelected(i);
			if (i != -1) {
				List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
				PendingInvite pendingInvite = ((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)list.get(i)).mPendingInvite;
				String string = RealmsScreen.getLocalizedString("narrator.select.list.position", i + 1, list.size());
				String string2 = Realms.joinNarrations(
					Arrays.asList(pendingInvite.worldName, pendingInvite.worldOwnerName, RealmsPendingInvitesScreen.method_21301(pendingInvite), string)
				);
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string2));
			}

			this.selectInviteListItem(i);
		}

		public void selectInviteListItem(int i) {
			RealmsPendingInvitesScreen.this.selectedInvite = i;
			RealmsPendingInvitesScreen.this.updateButtonStates();
		}
	}

	class PendingInvitationSelectionListEntry extends RealmListEntry {
		final PendingInvite mPendingInvite;
		private final List<class_4371> field_19955;

		PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
			this.mPendingInvite = pendingInvite;
			this.field_19955 = Arrays.asList(
				new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.class_4404(),
				new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.class_4405()
			);
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.renderPendingInvitationItem(this.mPendingInvite, k, j, n, o);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			class_4371.method_21114(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.field_19955, i, d, e);
			return true;
		}

		private void renderPendingInvitationItem(PendingInvite pendingInvite, int i, int j, int k, int l) {
			RealmsPendingInvitesScreen.this.drawString(pendingInvite.worldName, i + 38, j + 1, 16777215);
			RealmsPendingInvitesScreen.this.drawString(pendingInvite.worldOwnerName, i + 38, j + 12, 7105644);
			RealmsPendingInvitesScreen.this.drawString(RealmsPendingInvitesScreen.method_21301(pendingInvite), i + 38, j + 24, 7105644);
			class_4371.method_21113(this.field_19955, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, i, j, k, l);
			RealmsTextureManager.withBoundFace(pendingInvite.worldOwnerUuid, () -> {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(i, j, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
				RealmsScreen.blit(i, j, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
			});
		}

		class class_4404 extends class_4371 {
			class_4404() {
				super(15, 15, 215, 5);
			}

			@Override
			protected void method_21112(int i, int j, boolean bl) {
				RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				RealmsScreen.blit(i, j, bl ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
				GlStateManager.popMatrix();
				if (bl) {
					RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.accept");
				}
			}

			@Override
			public void method_21110(int i) {
				RealmsPendingInvitesScreen.this.accept(i);
			}
		}

		class class_4405 extends class_4371 {
			class_4405() {
				super(15, 15, 235, 5);
			}

			@Override
			protected void method_21112(int i, int j, boolean bl) {
				RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				RealmsScreen.blit(i, j, bl ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
				GlStateManager.popMatrix();
				if (bl) {
					RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.reject");
				}
			}

			@Override
			public void method_21110(int i) {
				RealmsPendingInvitesScreen.this.reject(i);
			}
		}
	}
}
