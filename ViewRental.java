
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewRental extends JFrame {

    private JTable rentalTable;
    private DefaultTableModel tableModel;
    private JLabel totalEarningsLabel;

    public ViewRental() {
        setTitle("View Rentals");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{
            "ID", "Customer ID", "Vehicle ID", "Start Date", "End Date", "Total Cost", "Payment ID"
        });

        rentalTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rentalTable);
        add(scrollPane, BorderLayout.CENTER);

        totalEarningsLabel = new JLabel("Total Earnings: ₹0.00");
        totalEarningsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalEarningsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(totalEarningsLabel, BorderLayout.SOUTH);

        loadRentalData();
        setVisible(true);
    }

    private void loadRentalData() {
        double totalEarnings = 0.0;

        try (Connection conn = CarRentalSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, customer_id, vehicle_id, start_date, end_date, total_cost, payment_id FROM rental")) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("id");
                row[1] = rs.getInt("customer_id");
                row[2] = rs.getInt("vehicle_id");
                row[3] = rs.getString("start_date");
                row[4] = rs.getString("end_date");
                row[5] = rs.getDouble("total_cost");
                row[6] = rs.getInt("payment_id");

                tableModel.addRow(row);
                totalEarnings += rs.getDouble("total_cost");
            }

            totalEarningsLabel.setText(String.format("Total Earnings: ₹%.2f", totalEarnings));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rental data:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewRental::new);
    }
}
