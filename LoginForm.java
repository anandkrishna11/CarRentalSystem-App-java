import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    public LoginForm() {
        setTitle("Car Rental System - Login");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);
        setVisible(true);

        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> {
            dispose();
            new SignUpForm().setVisible(true);
        });
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        try (Connection con = CarRentalSystem.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed");
                return;
            }

            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    JOptionPane.showMessageDialog(this, "Welcome " + role + "!");
                    dispose();
                    if (role.equalsIgnoreCase("admin")) {
                        new AdminPanel().setVisible(true);
                    } else if (role.equalsIgnoreCase("customer")) {
                        new CarRentalDashboard().setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password!");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
