package fr.pulsedev.jarvis.ui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {



    public Window(){
        this.setTitle("Jarvis");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setUndecorated(true);
        JPanel panel = new TransparentPanel();

        this.setContentPane(panel);
        this.setVisible(true);
    }
}
