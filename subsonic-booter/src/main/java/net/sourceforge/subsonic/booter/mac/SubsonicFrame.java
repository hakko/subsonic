package net.sourceforge.subsonic.booter.mac;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.subsonic.booter.Main;
import net.sourceforge.subsonic.booter.deployer.SubsonicDeployerService;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.factories.Borders;

/**
 * Frame with Subsonic status.  Used on Mac installs.
 *
 * @author Sindre Mehus
 */
public class SubsonicFrame extends JFrame {

    private final SubsonicDeployerService deployer;
    private StatusPanel statusPanel;
    private JButton hideButton;
    private JButton exitButton;

    public SubsonicFrame(SubsonicDeployerService deployer) {
        super("Subsonic");
        this.deployer = deployer;
        createComponents();
        layoutComponents();
        addBehaviour();

        URL url = Main.class.getResource("/images/subsonic-512.png");
        setIconImage(Toolkit.getDefaultToolkit().createImage(url));
    }

    public void setActive(boolean active) {
        if (active) {
            pack();
            centerComponent();
            setVisible(true);
            toFront();
        } else {
            dispose();
        }
    }

    private void centerComponent() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2,
                    screenSize.height / 2 - getHeight() / 2);
    }

    private void createComponents() {
        statusPanel = new StatusPanel(deployer);
        hideButton = new JButton("Hide");
        exitButton = new JButton("Exit");
    }

    private void layoutComponents() {
        JPanel pane = (JPanel) getContentPane();
        pane.setLayout(new BorderLayout(10, 10));
        pane.add(statusPanel, BorderLayout.CENTER);
        JPanel buttons = new ButtonBarBuilder()
            .addGlue()
            .addButton(hideButton)
            .addRelatedGap()
            .addButton(exitButton)
            .build();
        pane.add(buttons, BorderLayout.SOUTH);

        pane.setBorder(Borders.DIALOG_BORDER);
    }

    private void addBehaviour() {
        hideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActive(false);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

}