package net.minecraft.server.dedicated.gui;

import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.class_4325;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServerGui extends JComponent {
	private static final Font FONT_MONOSPACE = new Font("Monospaced", 0, 12);
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftDedicatedServer server;
	private Thread field_21834;

	public static void create(MinecraftDedicatedServer server) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception var3) {
		}

		DedicatedServerGui dedicatedServerGui = new DedicatedServerGui(server);
		JFrame jFrame = new JFrame("Minecraft server");
		jFrame.add(dedicatedServerGui);
		jFrame.pack();
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
		jFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				server.stopRunning();

				while (!server.isStopped()) {
					try {
						Thread.sleep(100L);
					} catch (InterruptedException var3) {
						var3.printStackTrace();
					}
				}

				System.exit(0);
			}
		});
		dedicatedServerGui.method_21233();
	}

	public DedicatedServerGui(MinecraftDedicatedServer minecraftDedicatedServer) {
		this.server = minecraftDedicatedServer;
		this.setPreferredSize(new Dimension(854, 480));
		this.setLayout(new BorderLayout());

		try {
			this.add(this.createLogPanel(), "Center");
			this.add(this.createStatsPanel(), "West");
		} catch (Exception var3) {
			LOGGER.error("Couldn't build server GUI", var3);
		}
	}

	private JComponent createStatsPanel() throws Exception {
		JPanel jPanel = new JPanel(new BorderLayout());
		jPanel.add(new PlayerStatsGui(this.server), "North");
		jPanel.add(this.createPlaysPanel(), "Center");
		jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
		return jPanel;
	}

	private JComponent createPlaysPanel() throws Exception {
		JList<?> jList = new PlayerListGui(this.server);
		JScrollPane jScrollPane = new JScrollPane(jList, 22, 30);
		jScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
		return jScrollPane;
	}

	private JComponent createLogPanel() throws Exception {
		JPanel jPanel = new JPanel(new BorderLayout());
		JTextArea jTextArea = new JTextArea();
		JScrollPane jScrollPane = new JScrollPane(jTextArea, 22, 30);
		jTextArea.setEditable(false);
		jTextArea.setFont(FONT_MONOSPACE);
		JTextField jTextField = new JTextField();
		jTextField.addActionListener(actionEvent -> {
			String string = jTextField.getText().trim();
			if (!string.isEmpty()) {
				this.server.method_2065(string, this.server.method_20330());
			}

			jTextField.setText("");
		});
		jTextArea.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent focusEvent) {
			}
		});
		jPanel.add(jScrollPane, "Center");
		jPanel.add(jTextField, "South");
		jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
		this.field_21834 = new Thread(() -> {
			String string;
			while ((string = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
				this.appendToConsole(jTextArea, jScrollPane, string);
			}
		});
		this.field_21834.setUncaughtExceptionHandler(new class_4325(LOGGER));
		this.field_21834.setDaemon(true);
		return jPanel;
	}

	public void method_21233() {
		this.field_21834.start();
	}

	public void appendToConsole(JTextArea textArea, JScrollPane scrollPane, String string) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> this.appendToConsole(textArea, scrollPane, string));
		} else {
			Document document = textArea.getDocument();
			JScrollBar jScrollBar = scrollPane.getVerticalScrollBar();
			boolean bl = false;
			if (scrollPane.getViewport().getView() == textArea) {
				bl = (double)jScrollBar.getValue() + jScrollBar.getSize().getHeight() + (double)(FONT_MONOSPACE.getSize() * 4) > (double)jScrollBar.getMaximum();
			}

			try {
				document.insertString(document.getLength(), string, null);
			} catch (BadLocationException var8) {
			}

			if (bl) {
				jScrollBar.setValue(Integer.MAX_VALUE);
			}
		}
	}
}
