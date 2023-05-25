package org.example.Gui;

import org.example.Database.Database;
import org.example.Security.PasswordManage;
import org.example.Security.RSA;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SignedIn implements ActionListener {
    private Database db = new Database();
    private JList<String> jList;
    private JButton deleteButton;
    private JButton addButton;
    private JTextField userField;
    private JTextField urlField;
    private JPasswordField passField;
    private JLabel status;
    private JCheckBox showPassword;
    private WebSite object;
    public void run() throws Exception {
        JFrame frame = new JFrame("Password Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setJMenuBar(createMenuBar(frame));

        jList = new JList<>(new DefaultListModel<>());
        // actionListener for when i click on different stored websites to view the data
        jList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                try {
                    object = db.getWebsiteInformationOnURL(jList.getSelectedValue());
                    if (!object.getUsername().equals("")) {
                        urlField.setText(object.getUrl());
                        userField.setText(object.getUsername());

                        String decrypted = new RSA().decrypt(object.getEncryptedPassword(), new RSA().getPrivateKey());
                        passField.setText(decrypted);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Double click the item to open it up in the browser.
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2){
                    String url = object.getUrl();

                    try {
                        // Open the default browser with the given URL
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        getJListData();

        JPanel panel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());

        panel.add(new JScrollPane(jList), BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.LINE_END);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        urlField = new JTextField(15);
        userField = new JTextField(15);
        passField = new JPasswordField(15);

        addButton = new JButton("Add");
        addButton.addActionListener(this::actionPerformed);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::actionPerformed);

        status = new JLabel();
        showPassword = new JCheckBox("Show password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()){
                passField.setEchoChar((char) 0);
            }else{
                passField.setEchoChar('*');
            }
        });

        addComponent(gbc, 0, 1, GridBagConstraints.LINE_END, 0.0, 0.0, rightPanel, new JLabel("Url: "));
        addComponent(gbc, 1, 1, GridBagConstraints.LINE_START, 1.0, 0.0, rightPanel, urlField);

        addComponent(gbc, 0, 2, GridBagConstraints.LINE_END, 0.0, 0.0, rightPanel, new JLabel("Username: "));
        addComponent(gbc, 1, 2, GridBagConstraints.LINE_START, 1.0, 0.0, rightPanel, userField);
        addComponent(gbc, 1, 4, GridBagConstraints.LINE_END, 0.0, 0.0, rightPanel, addButton);

        addComponent(gbc, 0, 3, GridBagConstraints.LINE_END, 0.0, 0.0, rightPanel, new JLabel("Password: "));
        addComponent(gbc, 1, 3, GridBagConstraints.LINE_START, 1.0, 0.0, rightPanel, passField);

        addComponent(gbc, 0, 4, GridBagConstraints.LINE_START, 1.0, 0.0, rightPanel, deleteButton);
        addComponent(gbc, 0, 5, GridBagConstraints.LINE_END, 0.0, 0.0, rightPanel, status);
        addComponent(gbc, 1, 5, GridBagConstraints.LINE_END, 0.0, 0.0, rightPanel, showPassword);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    private void getJListData() throws Exception {
        DefaultListModel<String> model = (DefaultListModel<String>) jList.getModel();
        for (String entry : db.getWebsiteInformation()){
            model.addElement(entry);
        }
    }
    private JMenuBar createMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Settings");
        JMenuItem logout = new JMenuItem("Logout");

        logout.addActionListener(e -> {
            try {
                frame.setVisible(false);
                new Login().run();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JMenuItem sliderValue = new JMenuItem("value: 0");
        JMenuItem slierMenuItem = createSliderMenuItem(sliderValue);

        JButton generatePassword = new JButton("Generate Password");
        generatePassword.addActionListener(e -> {
            int passwordLength = Integer.parseInt(sliderValue.getText().split(":")[1].trim());
            PasswordManage passwordManage = new PasswordManage();
            String password = passwordManage.generatePassword(passwordLength);
            saveToClipBoard(password);
        });

        menu.add(logout);
        menu.add(sliderValue);
        menu.add(slierMenuItem);
        menu.add(generatePassword);
        menuBar.add(menu);

        return menuBar;
    }
    private void saveToClipBoard(String password) {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(password);

        clipboard.setContents(stringSelection, null);
        status.setText("Password stored to clipboard!");
    }
    private JMenuItem createSliderMenuItem(JMenuItem sliderValue) {
        JMenuItem sliderMenuItem = new JMenuItem();
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);

        sliderMenuItem.setLayout(new BorderLayout());
        sliderMenuItem.add(slider, BorderLayout.CENTER);

        slider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()){
                int value = source.getValue();
                sliderValue.setText("value: " + value);
            }
        });

        sliderValue.setText("value: " + slider.getValue());

        return sliderMenuItem;
    }
    private static <T extends Component> void addComponent(GridBagConstraints gbc, int gridX, int gridY, int anchor, double weightx, double weighty, JPanel rightPanel, T tComponent) {
        // generic method to add all GUI components to the panel
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.anchor = anchor;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        rightPanel.add(tComponent, gbc);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        status.setText("");

        if (button.getText().equals("Add")) {
            if (urlField.getText().equals("") || userField.getText().equals("") || passField.getText().equals("")) {
                status.setText("Error: Fill all fields.");
            }else{
                DefaultListModel<String> model = (DefaultListModel<String>) jList.getModel();
                if (!model.contains(urlField.getText())){
                    try {
                        String encryption = new RSA().encrypt(new String(passField.getPassword()), new RSA().getPublicKey());
                        db.storeWebsiteInformation("https://"+urlField.getText(), userField.getText(), encryption);

                        model.addElement("https://"+urlField.getText());
                        clearUserFields();

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        if (button.getText().equals("Delete")){
            int index = jList.getSelectedIndex();
            if (index == -1) {
                status.setText("select and index to remove.");
            }
            else {
                DefaultListModel<String> model = (DefaultListModel<String>) jList.getModel();
                try {
                    db.deleteWebsiteInformation(model.get(index));
                    model.remove(index);
                    clearUserFields();

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    private void clearUserFields(){
        // clear fields
        urlField.setText("");
        userField.setText("");
        passField.setText("");
    }
}
