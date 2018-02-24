package pop3.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GUI extends javax.swing.JFrame implements Observer, ActionListener, MouseListener {

    private final static int NO_MESSAGE_SELECTED = -1;

    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JTextField textFieldUsername;
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldCc;
    private JTextField textFieldSubject;
    private JPasswordField textFieldPassword;
    private JButton buttonOk;
    private JButton buttonGetEmails;
    private JTextArea textAreaResult;
    private DefaultListModel<String> model;
    private JList listMessages;
    private List<Message> messages;
    private JButton buttonDelete;
    private int selectedMessageId;

    private Client client;

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
        JLabel labelPassword = new JLabel("Mot de passe :");
        this.textFieldPassword = new JPasswordField("test");

        panelTopFields.add(labelAddress);
        panelTopFields.add(this.textFieldAddress);
        panelTopFields.add(labelPort);
        panelTopFields.add(this.textFieldPort);
        panelTopFields.add(labelUsername);
        panelTopFields.add(this.textFieldUsername);
        panelTopFields.add(labelPassword);
        panelTopFields.add(this.textFieldPassword);

        this.buttonOk = new JButton("Connexion");

        panelSplitTop.add(panelTopFields, BorderLayout.CENTER);
        panelSplitTop.add(this.buttonOk, BorderLayout.SOUTH);
        splitPane.setTopComponent(panelSplitTop);

        JPanel panelSplitBottom = new JPanel(new BorderLayout());

        JPanel panelMessageAndHeader = new JPanel(new BorderLayout());
        JPanel panelHeader = new JPanel(new BorderLayout());
        JPanel panelHeaderCenter = new JPanel(new GridLayout(0,1));
        this.textFieldTo = new JTextField("");
        panelHeaderCenter.add(this.textFieldTo);
        this.textFieldFrom = new JTextField("");
        panelHeaderCenter.add(this.textFieldFrom);
        this.textFieldCc = new JTextField("");
        panelHeaderCenter.add(this.textFieldCc);
        this.textFieldSubject = new JTextField("");
        panelHeaderCenter.add(this.textFieldSubject);
        this.textAreaResult = new JTextArea();

        this.buttonDelete = new JButton("Supprimer");
        this.buttonDelete.addActionListener(this);
        this.buttonDelete.setEnabled(false);

        panelHeader.add(panelHeaderCenter, BorderLayout.CENTER);
        panelHeader.add(this.buttonDelete, BorderLayout.EAST);

        panelMessageAndHeader.add(panelHeader, BorderLayout.NORTH);
        panelMessageAndHeader.add(this.textAreaResult, BorderLayout.CENTER);

        this.buttonGetEmails = new JButton("Relever les messages");
        this.buttonGetEmails.setEnabled(false);
        this.model = new DefaultListModel<>();
        this.listMessages = new JList(model);
        listMessages.setVisibleRowCount(5);
        JScrollPane scrollPane_1 = new JScrollPane(listMessages);
        Dimension d = listMessages.getPreferredSize();
        d.width = 200;
        scrollPane_1.setPreferredSize(d);

        listMessages.addMouseListener(this);

        panelSplitBottom.add(scrollPane_1, BorderLayout.WEST);
        panelSplitBottom.add(panelMessageAndHeader, BorderLayout.CENTER);
        panelSplitBottom.add(this.buttonGetEmails, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelSplitBottom);

        setContentPane(splitPane);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 500);
        setVisible(true);

        this.buttonOk.addActionListener(this);
        this.buttonGetEmails.addActionListener(this);

        this.selectedMessageId = NO_MESSAGE_SELECTED;
    }

    @Override
    public void update(Observable o, Object arg) {
        Notification notification = (Notification) arg;
        switch (notification.getType()) {
            case CONNECTION_FAILED:
                this.showErrorDialog("Impossible de se connecter au serveur.");
                break;
            case CONNECTION_OK:
                System.out.println("Connecté au serveur !");
                break;
            case APOP_OK:
                System.out.println("Identification réussie");
                this.buttonOk.setText("Déconnexion");
                this.buttonGetEmails.setEnabled(true);
                break;
            case APOP_FAILED:
                this.showErrorDialog("Identifiant ou mot de passe incorrect.");
                break;
            case RETR_FAILED:
                this.showErrorDialog("Impossible de relever le message.");
                break;
            case RETR_ALL_MESSAGES_OK:
                List<Message> messages = (List<Message>) notification.getArguments();
                for (Message message : messages) {
                    this.model.addElement(message.getSubjectWithoutPrefix());
                }
                this.messages = messages;
                break;
            case QUIT_OK:
                System.out.println("Déconnexion réussie !");
                this.buttonOk.setText("Connexion");
                this.buttonGetEmails.setEnabled(false);
                this.model = new DefaultListModel<>();
                this.listMessages.setModel(this.model);
                clearFields();
                this.client = null;
                break;
            case QUIT_FAILED:
                this.showErrorDialog("Erreur lors de la déconnexion. Certains messages marqués comme supprimés pourraient ne pas l'être.");
                break;
            case DELE_OK:
                this.selectedMessageId = NO_MESSAGE_SELECTED;
                this.clearFields();
                System.out.println("Message supprimé !");
                this.listMessages();
                break;
            case DELE_FAILED:
                this.showErrorDialog("Erreur lors de la suppression du message.");
                break;

        }
    }

    private void clearFields() {
        this.textFieldSubject.setText("");
        this.textFieldTo.setText("");
        this.textFieldFrom.setText("");
        this.textFieldCc.setText("");
        this.textAreaResult.setText("");
        this.buttonDelete.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonOk) {
            if (this.client == null || !this.client.isConnected()) {
                this.client = new Client(this.textFieldAddress.getText(), Integer.parseInt(this.textFieldPort.getText()));
                this.client.addObserver(this);
                this.client.connect(this.textFieldUsername.getText(), new String(this.textFieldPassword.getPassword()));
            } else {
                // The client exists and is connected to the server
                // If he is not logged in yet, let's try...

                if (!this.client.isLoggedIn()) {
                    this.client.performApop(this.textFieldUsername.getText(), new String(this.textFieldPassword.getPassword()));
                } else {
                    this.client.logout();
                }
            }

        } else if (e.getSource() == this.buttonGetEmails) {
            this.listMessages();
        } else if (e.getSource() == this.buttonDelete) {
            if (this.selectedMessageId != NO_MESSAGE_SELECTED) {
                this.client.deleteMessage(this.selectedMessageId);
            }
        }
    }

    public void listMessages() {
        this.model = new DefaultListModel<>();
        this.listMessages.setModel(this.model);
        this.client.listMessages();
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        JList list = (JList)evt.getSource();
        if (evt.getClickCount() == 2) {

            int index = list.locationToIndex(evt.getPoint());
            Message message = this.messages.get(index);
            this.textAreaResult.setText(message.getBody());
            this.textFieldTo.setText(message.getTo());
            this.textFieldFrom.setText(message.getFrom());
            this.textFieldCc.setText(message.getCc());
            this.textFieldSubject.setText(message.getSubject());
            this.selectedMessageId = message.getId();
            this.buttonDelete.setEnabled(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
