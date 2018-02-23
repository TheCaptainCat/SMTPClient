package pop3.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class GUI extends javax.swing.JFrame implements Observer, ActionListener {

    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JButton buttonOk;
    private JButton buttonGetEmails;

    private Client client;

    public GUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panel = new JPanel();
        panel.add(splitPane);

        JPanel panelSplitTop = new JPanel(new GridLayout(0,2));
        JLabel labelAddress = new JLabel("Adresse :");
        this.textFieldAddress = new JTextField("127.0.0.1");
        JLabel labelPort = new JLabel("Port :");
        this.textFieldPort = new JTextField("1337");
        panelSplitTop.add(labelAddress);
        panelSplitTop.add(this.textFieldAddress);
        panelSplitTop.add(labelPort);
        panelSplitTop.add(this.textFieldPort);
        splitPane.setTopComponent(panelSplitTop);

        this.buttonOk = new JButton("Connexion");
        panelSplitTop.add(this.buttonOk);

        JPanel panelSplitBottom = new JPanel(new BorderLayout());
        JTextArea textAreaResult = new JTextArea();
        this.buttonGetEmails = new JButton("Relever les messages");
        this.buttonGetEmails.setEnabled(false);
        panelSplitBottom.add(textAreaResult, BorderLayout.CENTER);
        panelSplitBottom.add(this.buttonGetEmails, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelSplitBottom);

        setContentPane(splitPane);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(500, 500);
        setVisible(true);

        this.buttonOk.addActionListener(this);
        this.buttonGetEmails.addActionListener(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println((String) arg);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonOk) {
            try {
                this.client = new Client(this.textFieldAddress.getText(), Integer.parseInt(this.textFieldPort.getText()));
                this.client.addObserver(this);
                this.buttonOk.setEnabled(false);
                this.buttonGetEmails.setEnabled(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == this.buttonGetEmails) {
            this.client.fetchMessages();
        }
    }
}
