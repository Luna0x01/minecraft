package net.minecraft.client;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2848 extends EntryListWidget<class_2847> {
	private static final Logger field_13373 = LogManager.getLogger();
	private final SelectWorldScreen field_13374;
	private int field_13376 = -1;
	@Nullable
	private List<LevelSummary> field_20505 = null;

	public class_2848(
		SelectWorldScreen selectWorldScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m, Supplier<String> supplier, @Nullable class_2848 arg
	) {
		super(minecraftClient, i, j, k, l, m);
		this.field_13374 = selectWorldScreen;
		if (arg != null) {
			this.field_20505 = arg.field_20505;
		}

		this.method_18898(supplier, false);
	}

	public void method_18898(Supplier<String> supplier, boolean bl) {
		this.method_18399();
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		if (this.field_20505 == null || bl) {
			try {
				this.field_20505 = levelStorageAccess.getLevelList();
			} catch (ClientException var7) {
				field_13373.error("Couldn't load level list", var7);
				this.client.setScreen(new FatalErrorScreen(I18n.translate("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.field_20505);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (LevelSummary levelSummary : this.field_20505) {
			if (levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) || levelSummary.getFileName().toLowerCase(Locale.ROOT).contains(string)) {
				this.method_18398(new class_2847(this, levelSummary, this.client.getCurrentSave()));
			}
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	public void method_12214(int i) {
		this.field_13376 = i;
		this.field_13374.method_12200(this.method_12216());
	}

	@Override
	protected boolean isEntrySelected(int index) {
		return index == this.field_13376;
	}

	@Nullable
	public class_2847 method_12216() {
		return this.field_13376 >= 0 && this.field_13376 < this.getEntryCount() ? (class_2847)this.method_18423().get(this.field_13376) : null;
	}

	public SelectWorldScreen method_12217() {
		return this.field_13374;
	}
}
