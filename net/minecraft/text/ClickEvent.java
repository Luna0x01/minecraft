package net.minecraft.text;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
	private final ClickEvent.Action action;
	private final String value;

	public ClickEvent(ClickEvent.Action action, String string) {
		this.action = action;
		this.value = string;
	}

	public ClickEvent.Action getAction() {
		return this.action;
	}

	public String getValue() {
		return this.value;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			ClickEvent clickEvent = (ClickEvent)object;
			if (this.action != clickEvent.action) {
				return false;
			} else {
				return this.value != null ? this.value.equals(clickEvent.value) : clickEvent.value == null;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
	}

	public int hashCode() {
		int i = this.action.hashCode();
		return 31 * i + (this.value != null ? this.value.hashCode() : 0);
	}

	public static enum Action {
		OPEN_URL("open_url", true),
		OPEN_FILE("open_file", false),
		RUN_COMMAND("run_command", true),
		TWITCH_USER_INFO("twitch_user_info", false),
		SUGGEST_COMMAND("suggest_command", true),
		CHANGE_PAGE("change_page", true);

		private static final Map<String, ClickEvent.Action> ACTIONS = Maps.newHashMap();
		private final boolean userDefinable;
		private final String name;

		private Action(String string2, boolean bl) {
			this.name = string2;
			this.userDefinable = bl;
		}

		public boolean isUserDefinable() {
			return this.userDefinable;
		}

		public String getName() {
			return this.name;
		}

		public static ClickEvent.Action byName(String name) {
			return (ClickEvent.Action)ACTIONS.get(name);
		}

		static {
			for (ClickEvent.Action action : values()) {
				ACTIONS.put(action.getName(), action);
			}
		}
	}
}
