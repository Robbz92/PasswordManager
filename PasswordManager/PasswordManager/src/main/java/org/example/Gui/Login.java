package org.example.Gui;

import org.example.Database.Database;
import org.example.Security.HashLogin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login implements ActionListener {
    private Database db;
    private JFrame frame;
    private JTextField username;
    private JPasswordField password;
    private JButton login;
    private JLabel status;
    public Login() {
         db = new Database();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (button.getText().equals("Login")){
            try {
                String hash = new HashLogin().hash(new String(password.getPassword()));
                if (db.login(username.getText(), hash)){
                    frame.setVisible(false);
                    new SignedIn().run();
                }

                else {
                    status.setText("Incorrect username/password");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void run(){
        // TODO: Add menu bar : create register, forgot password.
        frame = new JFrame("Password Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setJMenuBar(createMenuBar());

        JPanel panel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());

        panel.add(rightPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        username = new JTextField(15);
        password = new JPasswordField(15);

        login = new JButton("Login");
        login.addActionListener(this::actionPerformed);

        status = new JLabel();

        addComponent(gbc,0,1, GridBagConstraints.CENTER, rightPanel, new JLabel("Username: "));
        addComponent(gbc,1,1, GridBagConstraints.CENTER, rightPanel, username);
        addComponent(gbc,1,3, GridBagConstraints.CENTER, rightPanel, login);

        addComponent(gbc,0,2, GridBagConstraints.CENTER, rightPanel, new JLabel("Password: "));
        addComponent(gbc,1,2, GridBagConstraints.CENTER, rightPanel, password);

        addComponent(gbc,1,4, GridBagConstraints.LINE_END, rightPanel, status);

        frame.add(panel);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Settings");
        JMenuItem register = new JMenuItem("Register");
        register.addActionListener(e -> {
            try {
                db.createDB();
                String hash = new HashLogin().hash(new String(password.getPassword()));
                db.registerUser(username.getText(), hash);
                status.setText("User created!");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JMenuItem resetLogin = new JMenuItem("Reset login");
        resetLogin.addActionListener(e -> {
            // TODO: reset password via mail
        });

        menu.add(register);
        menu.add(resetLogin);
        menuBar.add(menu);

        return menuBar;
    }

    private static <T extends Component> void addComponent(GridBagConstraints gbc, int gridX, int gridY, int anchor, JPanel panel, T component) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.anchor = anchor;

        panel.add(component, gbc);
    }
}
