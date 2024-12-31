package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2848 extends EntryListWidget {
	private static final Logger field_13373 = LogManager.getLogger();
	private final SelectWorldScreen field_13374;
	private final List<class_2847> field_13375 = Lists.newArrayList();
	private int field_13376 = -1;

	public class_2848(SelectWorldScreen selectWorldScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.field_13374 = selectWorldScreen;
		this.method_12215();
	}

	public void method_12215() {
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();

		List<LevelSummary> list;
		try {
			list = levelStorageAccess.getLevelList();
		} catch (ClientException var5) {
			field_13373.error("Couldn't load level list", var5);
			this.client.setScreen(new FatalErrorScreen(I18n.translate("selectWorld.unable_to_load"), var5.getMessage()));
			return;
		}

		Collections.sort(list);

		for (LevelSummary levelSummary : list) {
			this.field_13375.add(new class_2847(this, levelSummary, this.client.getCurrentSave()));
		}
	}

	public class_2847 getEntry(int i) {
		return (class_2847)this.field_13375.get(i);
	}

	@Override
	protected int getEntryCount() {
		return this.field_13375.size();
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
		return this.field_13376 >= 0 && this.field_13376 < this.getEntryCount() ? this.getEntry(this.field_13376) : null;
	}

	public SelectWorldScreen method_12217() {
		return this.field_13374;
	}
}
