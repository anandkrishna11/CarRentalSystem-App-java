import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RentCar extends JFrame {
    private JComboBox<Object> carSelectionBox;
    private JLabel priceDetailLabel;

    public RentCar() {
        setTitle("Rent a Car");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ======== TOP PANEL ========
        JLabel titleLabel = new JLabel("Select a Car to Rent", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // ======== CENTER PANEL ========
        JPanel mainContentPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel selectionRow = new JPanel();
        selectionRow.add(new JLabel("Choose Car:"));
        carSelectionBox = new JComboBox<>();
        loadAvailableCars();
        selectionRow.add(carSelectionBox);
        mainContentPanel.add(selectionRow);

        JPanel pricePanel = new JPanel();
        priceDetailLabel = new JLabel("Price details will appear here");
        pricePanel.add(priceDetailLabel);
        mainContentPanel.add(pricePanel);

        add(mainContentPanel, BorderLayout.CENTER);

        // ======== BOTTOM PANEL ========
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton bookingButton = new JButton("Proceed to Booking");
        bookingButton.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(bookingButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ======== COMBOBOX ACTION ========
        carSelectionBox.addActionListener(e -> {
            Object selectedObj = carSelectionBox.getSelectedItem();
            if (selectedObj instanceof CarItem) {
                CarItem selectedCar = (CarItem) selectedObj;
                priceDetailLabel.setText(getPriceDetails(selectedCar.model));
            } else {
                priceDetailLabel.setText("Price details will appear here");
            }
        });

        // ======== BUTTON ACTION ========
        bookingButton.addActionListener(e -> {
            Object selectedObj = carSelectionBox.getSelectedItem();
            if (!(selectedObj instanceof CarItem)) {
                JOptionPane.showMessageDialog(RentCar.this, "Please select a car to proceed.");
                return;
            }

            CarItem selectedCar = (CarItem) selectedObj;

            try (Connection con = CarRentalSystem.getConnection()) {
                if (con != null) {
                    dispose(); // close current window
                    new BookingPage(selectedCar.id, selectedCar.model).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(RentCar.this, "Database connection failed.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(RentCar.this, "Error: " + ex.getMessage());
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ======== INNER CLASS FOR CAR ITEMS ========
    private static class CarItem {
        int id;
        String model;

        CarItem(int id, String model) {
            this.id = id;
            this.model = model;
        }

        @Override
        public String toString() {
            return model; // what appears in JComboBox
        }
    }

    // ======== LOAD AVAILABLE CARS ========
    private void loadAvailableCars() {
        carSelectionBox.addItem("-- Select a Car --");
        try (Connection con = CarRentalSystem.getConnection()) {
            if (con != null) {
                String query = "SELECT id, model FROM vehicle WHERE status = 'Available'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String model = rs.getString("model");
                    carSelectionBox.addItem(new CarItem(id, model));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed while loading cars.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading cars: " + ex.getMessage());
        }
    }

    // ======== PRICE DETAILS METHOD ========
    private String getPriceDetails(String model) {
        String details = "";
        try (Connection con = CarRentalSystem.getConnection()) {
            if (con != null) {
                String query = "SELECT price_per_day FROM vehicle WHERE model = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, model);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    details = "Price: ₹" + rs.getInt("price_per_day") + " per day";
                } else {
                    details = "Price not found.";
                }
            }
        } catch (SQLException ex) {
            details = "Error: " + ex.getMessage();
        }
        return details;
    }

    // ======== MAIN METHOD ========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RentCar::new);
    }
}
