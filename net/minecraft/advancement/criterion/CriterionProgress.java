package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.util.PacketByteBuf;

public class CriterionProgress {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private Date obtained;

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

	public static CriterionProgress method_14955(PacketByteBuf packetByteBuf) {
		CriterionProgress criterionProgress = new CriterionProgress();
		if (packetByteBuf.readBoolean()) {
			criterionProgress.obtained = packetByteBuf.readDate();
		}

		return criterionProgress;
	}

	public static CriterionProgress method_14956(String string) {
		CriterionProgress criterionProgress = new CriterionProgress();

		try {
			criterionProgress.obtained = DATE_FORMAT.parse(string);
			return criterionProgress;
		} catch (ParseException var3) {
			throw new JsonSyntaxException("Invalid datetime: " + string, var3);
		}
	}
}
