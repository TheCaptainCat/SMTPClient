package pop3.client;

import pop3.client.Commands.ApopCommand;
import pop3.client.Commands.Command;
import pop3.client.Commands.ConnectionCommand;

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
    private JTextField textFieldUsername;
    private JPasswordField textFieldPassword;
    private JButton buttonOk;
    private JButton buttonGetEmails;
    private JTextArea textAreaResult;

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
        JLabel labelUsername = new JLabel("Nom d'utilisateur :");
        this.textFieldUsername = new JTextField("Bob");
        JLabel labelPassword = new JLabel("Mot de passe :");
        this.textFieldPassword = new JPasswordField("test");

        panelSplitTop.add(labelAddress);
        panelSplitTop.add(this.textFieldAddress);
        panelSplitTop.add(labelPort);
        panelSplitTop.add(this.textFieldPort);
        panelSplitTop.add(labelUsername);
        panelSplitTop.add(this.textFieldUsername);
        panelSplitTop.add(labelPassword);
        panelSplitTop.add(this.textFieldPassword);
        splitPane.setTopComponent(panelSplitTop);

        this.buttonOk = new JButton("Connexion");
        panelSplitTop.add(this.buttonOk);

        JPanel panelSplitBottom = new JPanel(new BorderLayout());
        this.textAreaResult = new JTextArea();
        this.buttonGetEmails = new JButton("Relever les messages");
        this.buttonGetEmails.setEnabled(false);
        panelSplitBottom.add(this.textAreaResult, BorderLayout.CENTER);
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
        Command command = (Command)arg;
        if (command instanceof ConnectionCommand) {
            if (!command.isError()) {
                this.textAreaResult.setText("Connexion au serveur réussie.");
                this.client.performApop(this.textFieldUsername.getText(), new String(this.textFieldPassword.getPassword()));
            } else {
                this.textAreaResult.setText("Impossible de se connecter au serveur.");
            }
        } else if (command instanceof ApopCommand) {
            if (!command.isError()) {
                this.textAreaResult.setText(this.textAreaResult.getText() + "\nIdentification réussie.");
            } else {
                this.textAreaResult.setText(this.textAreaResult.getText() + "\nIdentifiant ou mot de passe incorrect.");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonOk) {
            if (this.client == null || !this.client.isConnected()) {
                this.client = new Client(this.textFieldAddress.getText(), Integer.parseInt(this.textFieldPort.getText()));
                this.client.addObserver(this);
                this.client.connect();
            } else {
                // The client exists and is connected to the server
                // If he is not logged in yet, let's try...

                if (!this.client.isLoggedIn()) {
                    this.client.performApop(this.textFieldUsername.getText(), new String(this.textFieldPassword.getPassword()));
                } else {
                    System.out.println("Déjà connecté");
                }

            }

        } else if (e.getSource() == this.buttonGetEmails) {
            this.client.fetchMessages();
        }
    }
}
