package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4376 implements Packet<ClientPlayPacketListener> {
	private RootCommandNode<class_3965> field_21525;

	public class_4376() {
	}

	public class_4376(RootCommandNode<class_3965> rootCommandNode) {
		this.field_21525 = rootCommandNode;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		class_4376.class_4377[] lvs = new class_4376.class_4377[buf.readVarInt()];
		Deque<class_4376.class_4377> deque = new ArrayDeque(lvs.length);

		for (int i = 0; i < lvs.length; i++) {
			lvs[i] = this.method_20214(buf);
			deque.add(lvs[i]);
		}

		while (!deque.isEmpty()) {
			boolean bl = false;
			Iterator<class_4376.class_4377> iterator = deque.iterator();

			while (iterator.hasNext()) {
				class_4376.class_4377 lv = (class_4376.class_4377)iterator.next();
				if (lv.method_20217(lvs)) {
					iterator.remove();
					bl = true;
				}
			}

			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}

		this.field_21525 = (RootCommandNode<class_3965>)lvs[buf.readVarInt()].field_21530;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		Map<CommandNode<class_3965>, Integer> map = Maps.newHashMap();
		Deque<CommandNode<class_3965>> deque = new ArrayDeque();
		deque.add(this.field_21525);

		while (!deque.isEmpty()) {
			CommandNode<class_3965> commandNode = (CommandNode<class_3965>)deque.pollFirst();
			if (!map.containsKey(commandNode)) {
				int i = map.size();
				map.put(commandNode, i);
				deque.addAll(commandNode.getChildren());
				if (commandNode.getRedirect() != null) {
					deque.add(commandNode.getRedirect());
				}
			}
		}

		CommandNode<class_3965>[] commandNodes = new CommandNode[map.size()];

		for (Entry<CommandNode<class_3965>, Integer> entry : map.entrySet()) {
			commandNodes[entry.getValue()] = (CommandNode<class_3965>)entry.getKey();
		}

		buf.writeVarInt(commandNodes.length);

		for (CommandNode<class_3965> commandNode2 : commandNodes) {
			this.method_20211(buf, commandNode2, map);
		}

		buf.writeVarInt((Integer)map.get(this.field_21525));
	}

	private class_4376.class_4377 method_20214(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int[] is = packetByteBuf.readIntArray();
		int i = (b & 8) != 0 ? packetByteBuf.readVarInt() : 0;
		ArgumentBuilder<class_3965, ?> argumentBuilder = this.method_20210(packetByteBuf, b);
		return new class_4376.class_4377(argumentBuilder, b, i, is);
	}

	@Nullable
	private ArgumentBuilder<class_3965, ?> method_20210(PacketByteBuf packetByteBuf, byte b) {
		int i = b & 3;
		if (i == 2) {
			String string = packetByteBuf.readString(32767);
			ArgumentType<?> argumentType = class_4323.method_19896(packetByteBuf);
			if (argumentType == null) {
				return null;
			} else {
				RequiredArgumentBuilder<class_3965, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(string, argumentType);
				if ((b & 16) != 0) {
					requiredArgumentBuilder.suggests(class_4327.method_19903(packetByteBuf.readIdentifier()));
				}

				return requiredArgumentBuilder;
			}
		} else {
			return i == 1 ? LiteralArgumentBuilder.literal(packetByteBuf.readString(32767)) : null;
		}
	}

	private void method_20211(PacketByteBuf packetByteBuf, CommandNode<class_3965> commandNode, Map<CommandNode<class_3965>, Integer> map) {
		byte b = 0;
		if (commandNode.getRedirect() != null) {
			b = (byte)(b | 8);
		}

		if (commandNode.getCommand() != null) {
			b = (byte)(b | 4);
		}

		if (commandNode instanceof RootCommandNode) {
			b = (byte)(b | 0);
		} else if (commandNode instanceof ArgumentCommandNode) {
			b = (byte)(b | 2);
			if (((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
				b = (byte)(b | 16);
			}
		} else {
			if (!(commandNode instanceof LiteralCommandNode)) {
				throw new UnsupportedOperationException("Unknown node type " + commandNode);
			}

			b = (byte)(b | 1);
		}

		packetByteBuf.writeByte(b);
		packetByteBuf.writeVarInt(commandNode.getChildren().size());

		for (CommandNode<class_3965> commandNode2 : commandNode.getChildren()) {
			packetByteBuf.writeVarInt((Integer)map.get(commandNode2));
		}

		if (commandNode.getRedirect() != null) {
			packetByteBuf.writeVarInt((Integer)map.get(commandNode.getRedirect()));
		}

		if (commandNode instanceof ArgumentCommandNode) {
			ArgumentCommandNode<class_3965, ?> argumentCommandNode = (ArgumentCommandNode<class_3965, ?>)commandNode;
			packetByteBuf.writeString(argumentCommandNode.getName());
			class_4323.method_19897(packetByteBuf, argumentCommandNode.getType());
			if (argumentCommandNode.getCustomSuggestions() != null) {
				packetByteBuf.writeIdentifier(class_4327.method_19902(argumentCommandNode.getCustomSuggestions()));
			}
		} else if (commandNode instanceof LiteralCommandNode) {
			packetByteBuf.writeString(((LiteralCommandNode)commandNode).getLiteral());
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_20199(this);
	}

	public RootCommandNode<class_3965> method_20213() {
		return this.field_21525;
	}

	static class class_4377 {
		@Nullable
		private final ArgumentBuilder<class_3965, ?> field_21526;
		private final byte field_21527;
		private final int field_21528;
		private final int[] field_21529;
		private CommandNode<class_3965> field_21530;

		private class_4377(@Nullable ArgumentBuilder<class_3965, ?> argumentBuilder, byte b, int i, int[] is) {
			this.field_21526 = argumentBuilder;
			this.field_21527 = b;
			this.field_21528 = i;
			this.field_21529 = is;
		}

		public boolean method_20217(class_4376.class_4377[] args) {
			if (this.field_21530 == null) {
				if (this.field_21526 == null) {
					this.field_21530 = new RootCommandNode();
				} else {
					if ((this.field_21527 & 8) != 0) {
						if (args[this.field_21528].field_21530 == null) {
							return false;
						}

						this.field_21526.redirect(args[this.field_21528].field_21530);
					}

					if ((this.field_21527 & 4) != 0) {
						this.field_21526.executes(commandContext -> 0);
					}

					this.field_21530 = this.field_21526.build();
				}
			}

			for (int i : this.field_21529) {
				if (args[i].field_21530 == null) {
					return false;
				}
			}

			for (int j : this.field_21529) {
				CommandNode<class_3965> commandNode = args[j].field_21530;
				if (!(commandNode instanceof RootCommandNode)) {
					this.field_21530.addChild(commandNode);
				}
			}

			return true;
		}
	}
}
