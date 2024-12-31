package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.PacketByteBuf;

public class CriterionProgress {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private final AdvancementProgress progress;
	private Date obtained;

	public CriterionProgress(AdvancementProgress advancementProgress) {
		this.progress = advancementProgress;
	}

	public boolean hasBeenObtained() {
		return this.obtained != null;
	}

	public void setObtained() {
		this.obtained = new Date();
	}

	public void reset() {
		this.obtained = null;
	}

	public Date getObtainDate() {
		return this.obtained;
	}

	public String toString() {
		return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + '}';
	}

	public void writeToByteBuf(PacketByteBuf buf) {
		buf.writeBoolean(this.obtained != null);
		if (this.obtained != null) {
			buf.writeDate(this.obtained);
		}
	}

	public JsonElement toJson() {
		return (JsonElement)(this.obtained != null ? new JsonPrimitive(DATE_FORMAT.format(this.obtained)) : JsonNull.INSTANCE);
	}

	public static CriterionProgress fromPacketByteBuf(PacketByteBuf buf, AdvancementProgress progress) {
		CriterionProgress criterionProgress = new CriterionProgress(progress);
		if (buf.readBoolean()) {
			criterionProgress.obtained = buf.readDate();
		}

		return criterionProgress;
	}

	public static CriterionProgress read(AdvancementProgress progress, String date) {
		CriterionProgress criterionProgress = new CriterionProgress(progress);

		try {
			criterionProgress.obtained = DATE_FORMAT.parse(date);
			return criterionProgress;
		} catch (ParseException var4) {
			throw new JsonSyntaxException("Invalid datetime: " + date, var4);
		}
	}
}
