package pop3.client;

import pop3.client.states.ConnectionState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUI extends javax.swing.JFrame implements Observer, ActionListener {

    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JTextField textFieldUsername;
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldSubject;
    private JButton buttonSendEmail;
    private JTextArea textAreaContent;
    private JButton buttonCancel;

    public GUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panel = new JPanel();
        panel.add(splitPane);

        JPanel panelSplitTop = new JPanel(new BorderLayout());

        JPanel panelTopFields = new JPanel(new GridLayout(0,2));
        JLabel labelAddress = new JLabel("Adresse :");
        this.textFieldAddress = new JTextField("127.0.0.1");
        JLabel labelPort = new JLabel("Port :");
        this.textFieldPort = new JTextField("1337");
        JLabel labelUsername = new JLabel("Nom d'utilisateur :");
        this.textFieldUsername = new JTextField("Bob");

        panelTopFields.add(labelAddress);
        panelTopFields.add(this.textFieldAddress);
        panelTopFields.add(labelPort);
        panelTopFields.add(this.textFieldPort);
        panelTopFields.add(labelUsername);
        panelTopFields.add(this.textFieldUsername);

        panelSplitTop.add(panelTopFields, BorderLayout.CENTER);
        //splitPane.setTopComponent(panelSplitTop);

        JPanel panelSplitBottom = new JPanel(new BorderLayout());

        JPanel panelMessageAndHeader = new JPanel(new BorderLayout());
        JPanel panelHeader = new JPanel(new BorderLayout());
        JPanel panelHeaderCenter = new JPanel(new GridLayout(0,1));
        this.textFieldFrom = new JTextField("pierre@gmail.com");
        panelHeaderCenter.add(this.textFieldFrom);
        this.textFieldTo = new JTextField("Bob@polyp.com, Jacques@polyp.com");
        panelHeaderCenter.add(this.textFieldTo);
        this.textFieldSubject = new JTextField("Information importante");
        panelHeaderCenter.add(this.textFieldSubject);
        this.textAreaContent = new JTextArea("Bonjour !\n\nLa réunion aura lieu ce mercredi à 16h30. Merci de confirmer votre présence.\n\nCordialement, Pierre.");

        JPanel panelHeaderLeft = new JPanel(new GridLayout(0,1));
        panelHeaderLeft.add(new JLabel("From : "));
        panelHeaderLeft.add(new JLabel("To : "));
        panelHeaderLeft.add(new JLabel("Subject : "));

        this.buttonCancel = new JButton("Annuler");
        this.buttonCancel.addActionListener(this);

        panelHeader.add(panelHeaderLeft, BorderLayout.WEST);
        panelHeader.add(panelHeaderCenter, BorderLayout.CENTER);
        panelHeader.add(this.buttonCancel, BorderLayout.EAST);

        panelMessageAndHeader.add(panelHeader, BorderLayout.NORTH);
        panelMessageAndHeader.add(this.textAreaContent, BorderLayout.CENTER);

        this.buttonSendEmail = new JButton("Envoyer");

        panelSplitBottom.add(panelMessageAndHeader, BorderLayout.CENTER);
        panelSplitBottom.add(this.buttonSendEmail, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelSplitBottom);

        setContentPane(splitPane);

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 500);
        setVisible(true);

        this.buttonSendEmail.addActionListener(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        Notification notification = (Notification) arg;
        switch (notification.getType()) {
            case CONNECTION_FAILED:
                showErrorDialog("Impossible de se connecter au serveur.");
                break;
        }
    }

    private void clearFields() {
        this.textFieldSubject.setText("");
        this.textFieldTo.setText("");
        this.textFieldFrom.setText("");
        this.textAreaContent.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonSendEmail) {
            Message message = new Message();
            message.setFrom(this.textFieldFrom.getText());
            message.setSubject(this.textFieldSubject.getText());
            message.setBody(this.textAreaContent.getText());
            for (String s : getRecipients(this.textFieldTo.getText())) {
                if (!s.isEmpty()) {
                    message.addRecipient(s);
                }
            }

            if (!message.hasSomeRecipients()) {
                System.err.println("Veuillez spécifier le ou les destinataires du message.");
                return;
            }

            Client client = new Client("127.0.0.1", 1337, "gmail.com");
            client.addObserver(this);
            client.setMessage(message);

            client.setState(new ConnectionState(client));
            client.connect();

        } else if (e.getSource() == this.buttonCancel) {
            this.clearFields();
        }
    }

    public List<String> getRecipients(String fromLine) {
        return Arrays.asList(fromLine.replace(" ", "").split(","));
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

}
