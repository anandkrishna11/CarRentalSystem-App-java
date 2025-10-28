import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BrowseVehicle extends JFrame {

    private JTable vehicleTable;
    private DefaultTableModel model;

    public BrowseVehicle() {
        setTitle("Browse Vehicles");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
            new String[]{"ID", "Model", "Year", "Type", "Capacity", "Price/Day", "Status"}, 0
        );
        vehicleTable = new JTable(model);
        loadVehicles();

        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton rentBtn = new JButton("Rent Selected Vehicle");
        JButton refreshBtn = new JButton("Refresh");
        bottomPanel.add(rentBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        rentBtn.addActionListener(e -> rentVehicle());
        refreshBtn.addActionListener(e -> loadVehicles());

        setVisible(true);
    }

    private void loadVehicles() {
        model.setRowCount(0);
        try (Connection con = CarRentalSystem.getConnection()) {
            String query = "SELECT * FROM vehicle WHERE status = 'Available'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_day"),
                    rs.getString("status")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage());
        }
    }

    private void rentVehicle() {
        int row = vehicleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to rent!");
            return;
        }

        int vehicleId = (int) model.getValueAt(row, 0);
        String modelName = (String) model.getValueAt(row, 1);
        double pricePerDay = (double) model.getValueAt(row, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to rent " + modelName + " for ₹" + pricePerDay + " per day?",
                "Confirm Rental", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = CarRentalSystem.getConnection()) {
                String update = "UPDATE vehicle SET status = 'Rented' WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(update);
                ps.setInt(1, vehicleId);
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle rented successfully!");
                    loadVehicles();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not update vehicle status!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error renting vehicle: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new BrowseVehicle();
    }
}
