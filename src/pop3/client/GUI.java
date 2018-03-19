package pop3.client;

import pop3.client.states.ConnectionState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GUI extends javax.swing.JFrame implements Observer, ActionListener {

    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JTextField textFieldUsername;
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldSubject;
    private JButton buttonLogin;
    private JButton buttonSendEmail;
    private JTextArea textAreaContent;
    private JButton buttonCancel;

    private Client client;

    public GUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panel = new JPanel();
        panel.add(splitPane);

        JPanel panelSplitTop = new JPanel(new BorderLayout());

        JPanel panelTopFields = new JPanel(new GridLayout(0,2));
        JLabel labelAddress = new JLabel("Adresse :");
        this.textFieldAddress = new JTextField("134.214.117.134");
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

        this.buttonLogin = new JButton("Connexion");

        panelSplitTop.add(panelTopFields, BorderLayout.CENTER);
        panelSplitTop.add(this.buttonLogin, BorderLayout.SOUTH);
        splitPane.setTopComponent(panelSplitTop);

        JPanel panelSplitBottom = new JPanel(new BorderLayout());

        JPanel panelMessageAndHeader = new JPanel(new BorderLayout());
        JPanel panelHeader = new JPanel(new BorderLayout());
        JPanel panelHeaderCenter = new JPanel(new GridLayout(0,1));
        this.textFieldFrom = new JTextField("");
        panelHeaderCenter.add(this.textFieldFrom);
        this.textFieldTo = new JTextField("");
        panelHeaderCenter.add(this.textFieldTo);
        this.textFieldSubject = new JTextField("");
        panelHeaderCenter.add(this.textFieldSubject);
        this.textAreaContent = new JTextArea();

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
        this.buttonSendEmail.setEnabled(false);

        panelSplitBottom.add(panelMessageAndHeader, BorderLayout.CENTER);
        panelSplitBottom.add(this.buttonSendEmail, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelSplitBottom);

        setContentPane(splitPane);

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 500);
        setVisible(true);

        this.buttonLogin.addActionListener(this);
        this.buttonSendEmail.addActionListener(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        Notification notification = (Notification) arg;
        switch (notification.getType()) {
            case CONNECTION_OK:
                buttonLogin.setEnabled(false);
                buttonSendEmail.setEnabled(true);
                break;
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
        if (e.getSource() == this.buttonLogin) {
            // TODO
            this.client = new Client(this.textFieldAddress.getText(), Integer.parseInt(this.textFieldPort.getText()));
            this.client.addObserver(this);
            this.client.connect();
        } else if (e.getSource() == this.buttonSendEmail) {
            // TODO
            Message message = new Message();
            message.setFrom(this.textFieldFrom.getText());
            message.setSubject(this.textFieldSubject.getText());
            message.setBody(this.textAreaContent.getText());
            message.addRecipient("test@test.fr");
            message.addRecipient("jacky@gamil.com");
            this.client.setState(new ConnectionState(this.client));
        } else if (e.getSource() == this.buttonCancel) {
            this.clearFields();
        }
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

}
