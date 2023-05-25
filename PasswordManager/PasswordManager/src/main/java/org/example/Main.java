package org.example;

import org.example.Gui.Login;
import javax.swing.*;

public class Main {
    /*
       2. forgot password - send mail
       4. 2FA login/reset password.
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().run();
        });
    }
}