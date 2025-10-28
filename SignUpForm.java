import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUpForm extends JFrame implements ActionListener {

    private JTextField nameField, emailField, secretCodeField;
    private JPasswordField passwordField;
    private JCheckBox adminCheckBox;
    private JButton signupButton, clearButton;

    private Connection con;
    private PreparedStatement ps;

    private static final String ADMIN_SECRET_CODE = "ADMIN123";
    private static final long serialVersionUID = 1L;

    public SignUpForm() {
        setTitle("Car Rental System - Sign Up");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Sign up as Admin:"));
        adminCheckBox = new JCheckBox();
        panel.add(adminCheckBox);

        panel.add(new JLabel("Secret Code (Admin only):"));
        secretCodeField = new JTextField();
        secretCodeField.setEnabled(false);
        panel.add(secretCodeField);

        adminCheckBox.addActionListener(e -> {
            secretCodeField.setEnabled(adminCheckBox.isSelected());
        });

        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        signupButton = new JButton("Sign Up");
        clearButton = new JButton("Clear");

        panel.add(signupButton);
        panel.add(clearButton);

        add(panel);

        signupButton.addActionListener(this);
        clearButton.addActionListener(e -> clearFields());

        con = CarRentalSystem.getConnection();
    }

    public void actionPerformed(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = "CUSTOMER";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        // ✅ Simple email format check (no regex)
        if (!email.contains("@") || !email.contains(".") || email.startsWith("@") || email.endsWith("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!");
            return;
        }

        // ✅ Admin secret code check
        if (adminCheckBox.isSelected()) {
            String secretCode = secretCodeField.getText().trim();
            if (!secretCode.equals(ADMIN_SECRET_CODE)) {
                JOptionPane.showMessageDialog(this, "Invalid secret code!");
                return;
            }
            role = "ADMIN";
        }

        try {
            String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);

            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Registered successfully as " + role + "!");
                clearFields();

                // ✅ Go to Login page after signup
                this.dispose(); // close signup window
                new LoginForm().setVisible(true);
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "This email is already registered!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        adminCheckBox.setSelected(false);
        secretCodeField.setText("");
        secretCodeField.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpForm().setVisible(true));
    }
}
