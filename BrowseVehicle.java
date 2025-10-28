package project;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        JButton refreshBtn = new JButton("Refresh");
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

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

    public static void main(String[] args) {
        new BrowseVehicle();
    }
}
