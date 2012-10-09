package net.sourceforge.subsonic.booter.agent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sourceforge.subsonic.booter.Main;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.factories.Borders;

/**
 * Frame that is activated by the tray icon. Contains a tabbed pane
 * with status and settings panels.
 *
 * @author Sindre Mehus
 */
public class SubsonicFrame extends JFrame {

    private final SubsonicAgent subsonicAgent;

    private final StatusPanel statusPanel;
    private final SettingsPanel settingsPanel;
    private JTabbedPane tabbedPane;
    private JButton closeButton;

    public SubsonicFrame(SubsonicAgent subsonicAgent, StatusPanel statusPanel, SettingsPanel settingsPanel) {
        super("Subsonic Control Panel");
        this.subsonicAgent = subsonicAgent;
        this.statusPanel = statusPanel;
        this.settingsPanel = settingsPanel;
        createComponents();
        layoutComponents();
        addBehaviour();
        setupIcons();

        pack();
        centerComponent();
    }

    private void setupIcons() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Window.setIconImages() was added in Java 1.6.  Since Subsonic only requires 1.5, we
        // use reflection to invoke it.
        try {
            Method method = Window.class.getMethod("setIconImages", java.util.List.class);
            java.util.List<Image> images = Arrays.asList(
                    toolkit.createImage(Main.class.getResource("/images/subsonic-16.png")),
                    toolkit.createImage(Main.class.getResource("/images/subsonic-32.png")),
                    toolkit.createImage(Main.class.getResource("/images/subsonic-512.png")));
            method.invoke(this, images);
        } catch (Throwable x) {
            // Fallback to old method.
            setIconImage(toolkit.createImage(Main.class.getResource("/images/subsonic-32.png")));
        }
    }

    public void centerComponent() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2,
                    screenSize.height / 2 - getHeight() / 2);
    }

    private void createComponents() {
        tabbedPane = new JTabbedPane();
        closeButton = new JButton("Close");
    }

    private void layoutComponents() {
        tabbedPane.add("Status", statusPanel);
        tabbedPane.add("Settings", settingsPanel);

        JPanel pane = (JPanel) getContentPane();
        pane.setLayout(new BorderLayout(10, 10));
        pane.add(tabbedPane, BorderLayout.CENTER);
        JPanel buttons = new ButtonBarBuilder()
            .addGlue()
            .addButton(closeButton)
            .build();
        pane.add(buttons, BorderLayout.SOUTH);

        pane.setBorder(Borders.TABBED_DIALOG_BORDER);
    }

    private void addBehaviour() {
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        subsonicAgent.setServiceStatusPollingEnabled(b);
    }

    public void showStatusPanel() {
        settingsPanel.readValues();
        tabbedPane.setSelectedComponent(statusPanel);
        pack();
        setVisible(true);
        toFront();
    }

    public void showSettingsPanel() {
        settingsPanel.readValues();
        tabbedPane.setSelectedComponent(settingsPanel);
        pack();
        setVisible(true);
        toFront();
    }
}
