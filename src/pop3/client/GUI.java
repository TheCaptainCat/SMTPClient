package pop3.client;

import pop3.client.states.ConnectionState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

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

    private Stack<Client> clients;
    private List<String> unreachableRecipients;

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
        this.textFieldTo = new JTextField("Bob@polyp.com, Jacques@polyp.com, Yannis@polyp.com, Martin@cheese.com, A@A.A");
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
        setSize(700, 400);
        setVisible(true);

        this.buttonSendEmail.addActionListener(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        Notification notification = (Notification) arg;
        switch (notification.getType()) {
            case CONNECTION_FAILED:
                showWarningDialog("Impossible de se connecter au serveur.");
                break;
            case UNREACHBLE_RECIPIENT:
                this.unreachableRecipients.add(notification.getArguments().toString());
                break;
            case ENDED:
                if (!this.connectToNextClient()) {
                    if (this.unreachableRecipients.isEmpty()) {
                        this.showSuccessDialog("Le message a bien été délivré à tous les destinataires.");
                    } else {
                        displayUnreachableRecipients();
                    }
                }
                break;
        }
    }

    private void displayUnreachableRecipients() {
        this.showWarningDialog("Le message n'a pas pu être délivré à : " + this.unreachableRecipients.stream().collect(Collectors.joining(", ")));
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

            this.unreachableRecipients = new LinkedList<>();
            Message message = getMessageFromFields();

            this.clients = new Stack<>();
            for (String s : getRecipients(this.textFieldTo.getText())) {
                if (!s.isEmpty()) {
                    message.addRecipient(s);
                }
            }

            if (!message.hasSomeRecipients()) {
                System.err.println("Veuillez spécifier le ou les destinataires du message.");
                return;
            }

            for (String s : message.getAllDomains()) {
                int port = PortFactory.getPort(s);
                if (port != -1) {
                    Client client = new Client("127.0.0.1", port, s);
                    client.addObserver(this);
                    client.setMessage(message);
                    client.setState(new ConnectionState(client));
                    this.clients.add(client);
                } else {
                    this.unreachableRecipients.addAll(message.getAllRecipientsOfADomain(s));
                }
            }

            if (this.unreachableRecipients.size() == message.getNumberOfRecipients()) {
                this.displayUnreachableRecipients();
                return;
            }

            this.connectToNextClient();

        } else if (e.getSource() == this.buttonCancel) {
            this.clearFields();
        }
    }

    private boolean connectToNextClient() {
        if (!this.clients.empty()) {
            this.clients.pop().connect();
            return true;
        }
        return false;
    }

    private Message getMessageFromFields() {
        Message message = new Message();
        message.setFrom(this.textFieldFrom.getText());
        message.setSubject(this.textFieldSubject.getText());
        message.setBody(this.textAreaContent.getText());
        return message;
    }

    public List<String> getRecipients(String fromLine) {
        return Arrays.asList(fromLine.replace(" ", "").split(","));
    }

    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Avertissement", JOptionPane.WARNING_MESSAGE);
    }
}
