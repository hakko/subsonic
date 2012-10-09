package net.sourceforge.subsonic.booter.mac;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sourceforge.subsonic.booter.deployer.SubsonicDeployerService;

/**
 * Controller for the Mac booter.
 *
 * @author Sindre Mehus
 */
public class SubsonicController {

    private final SubsonicDeployerService deployer;
    private final SubsonicFrame frame;
    private Action openAction;
    private Action controlPanelAction;
    private Action quitAction;

    public SubsonicController(SubsonicDeployerService deployer, SubsonicFrame frame) {
        this.deployer = deployer;
        this.frame = frame;
        createActions();
        createComponents();
    }

    private void createActions() {
        openAction = new AbstractAction("Open Subsonic Web Page") {
            public void actionPerformed(ActionEvent e) {
                openBrowser();
            }
        };

        controlPanelAction = new AbstractAction("Subsonic Control Panel") {
            public void actionPerformed(ActionEvent e) {
                frame.setActive(false);
                frame.setActive(true);
            }
        };

        quitAction = new AbstractAction("Quit Subsonic") {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
    }

    private void createComponents() {
        PopupMenu menu = new PopupMenu();
        menu.add(createMenuItem(openAction));
        menu.add(createMenuItem(controlPanelAction));
        menu.addSeparator();
        menu.add(createMenuItem(quitAction));

        URL url = getClass().getResource("/images/subsonic-21.png");
        Image image = Toolkit.getDefaultToolkit().createImage(url);
        TrayIcon trayIcon = new TrayIcon(image, "Subsonic Music Streamer", menu);
        trayIcon.setImageAutoSize(false);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (Throwable x) {
            System.err.println("Failed to add tray icon.");
        }
    }

    private MenuItem createMenuItem(Action action) {
        MenuItem menuItem = new MenuItem((String) action.getValue(Action.NAME));
        menuItem.addActionListener(action);
        return menuItem;
    }

    private void openBrowser() {
        String url = deployer.getDeploymentInfo().getURL();
        if (url == null) {
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }

}