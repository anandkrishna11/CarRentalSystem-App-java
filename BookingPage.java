import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BookingPage extends JFrame {

    private JTextField firstNameField, lastNameField, addressField, phoneField, emailField, daysField;
    private JComboBox<String> genderBox;
    private JButton confirmButton, cancelButton;
    private JLabel carNameLabel;
    private int selectedVehicleId = -1;

    public BookingPage(int vehicleId, String model) {
        setTitle("Booking - " + model);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        selectedVehicleId = vehicleId;

        JLabel titleLabel = new JLabel("Book Your Car", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        formPanel.add(new JLabel("Car Selected:"));
        carNameLabel = new JLabel(model);
        formPanel.add(carNameLabel);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[] { "M", "F", "Other" });
        formPanel.add(genderBox);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Number of Days:"));
        daysField = new JTextField();
        formPanel.add(daysField);

        confirmButton = new JButton("Confirm Booking");
        cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> confirmBooking(model));
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void confirmBooking(String model) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String gender = (String)genderBox.getSelectedItem();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String daysText = daysField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() ||
            phone.isEmpty() || email.isEmpty() || daysText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all details before proceeding.");
            return;
        }
        int days;
        try {
            days = Integer.parseInt(daysText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number of days entered.");
            return;
        }

        double rate;
        if (model.toLowerCase().contains("suv")) rate = 3000;
        else if (model.toLowerCase().contains("sedan")) rate = 2200;
        else rate = 1500;
        double totalCost = rate * days;

        try (Connection con = CarRentalSystem.getConnection()) {
            if (con != null) {
                // Insert customer
                String customerSQL = "INSERT INTO customer (first_name, last_name, gender, address, phone_no, email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement custPS = con.prepareStatement(customerSQL, Statement.RETURN_GENERATED_KEYS);
                custPS.setString(1, firstName);
                custPS.setString(2, lastName);
                custPS.setString(3, gender);
                custPS.setString(4, address);
                custPS.setString(5, phone);
                custPS.setString(6, email);
                custPS.executeUpdate();
                ResultSet generatedKeys = custPS.getGeneratedKeys();
                int customerId = -1;
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                }

                // insert booking into rental table (vehicle_id is 'id' in vehicle table)
                String rentalSQL = "INSERT INTO rental (customer_id, vehicle_id, days, total_cost) VALUES (?, ?, ?, ?)";
                PreparedStatement rentalPS = con.prepareStatement(rentalSQL);
                rentalPS.setInt(1, customerId);
                rentalPS.setInt(2, selectedVehicleId);
                rentalPS.setInt(3, days);
                rentalPS.setDouble(4, totalCost);
                rentalPS.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking Confirmed! Total cost: ₹" + totalCost);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    // For demo/testing only—update to pass correct ID and model in production!
    public static void main(String[] args) {
        // Example: new BookingPage(1, "SUV (Mahindra XUV700)");
        SwingUtilities.invokeLater(() -> new BookingPage(1, "SUV (Mahindra XUV700)"));
    }
}
