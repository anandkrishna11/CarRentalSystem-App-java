package project;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private JTable usersTable;
    private JTable vehiclesTable;

    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        usersTable = createUsersTable();
        vehiclesTable = createVehiclesTable();

        tabs.add("Users", new JScrollPane(usersTable));
        tabs.add("Vehicles", new JScrollPane(vehiclesTable));

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addVehicleBtn = new JButton("Add Vehicle");
        JButton editVehicleBtn = new JButton("Edit Vehicle");
        JButton deleteVehicleBtn = new JButton("Delete Vehicle");
        JButton rentalBtn = new JButton("View Rentals");

        btnPanel.add(addVehicleBtn);
        btnPanel.add(editVehicleBtn);
        btnPanel.add(deleteVehicleBtn);
        btnPanel.add(rentalBtn);

        add(tabs, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        addVehicleBtn.addActionListener(e -> addVehicle());
        editVehicleBtn.addActionListener(e -> editVehicle());
        deleteVehicleBtn.addActionListener(e -> deleteVehicle());
        rentalBtn.addActionListener(e -> new ViewRental());

        setVisible(true);
    }

    private JTable createUsersTable() {
        String[] columns = {"ID", "Username", "Email", "Password"};
        Object[][] data = new Object[100][4];
        int row = 0;

        try (Connection con = CarRentalSystem.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username, email, password FROM users");
            while (rs.next() && row < 100) {
                data[row][0] = rs.getInt("id");
                data[row][1] = rs.getString("username");
                data[row][2] = rs.getString("email");
                data[row][3] = rs.getString("password");
                row++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }

        return new JTable(data, columns);
    }

    private JTable createVehiclesTable() {
        String[] columns = {"ID", "Model", "Year", "Type", "Capacity", "Price/Day", "Status"};
        Object[][] data = new Object[100][7];
        int row = 0;

        try (Connection con = CarRentalSystem.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM vehicle");
            while (rs.next() && row < 100) {
                data[row][0] = rs.getInt("id");
                data[row][1] = rs.getString("model");
                data[row][2] = rs.getInt("year");
                data[row][3] = rs.getString("type");
                data[row][4] = rs.getInt("capacity");
                data[row][5] = rs.getDouble("price_per_day");
                data[row][6] = rs.getString("status");
                row++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + e.getMessage());
        }

        return new JTable(data, columns);
    }

    private void addVehicle() {
        JDialog dialog = new JDialog(this, "Add Vehicle", true);
        dialog.setSize(300, 350);
        dialog.setLayout(new GridLayout(7, 2, 8, 8));
        dialog.setLocationRelativeTo(this);

        JTextField model = new JTextField();
        JTextField year = new JTextField();
        JTextField type = new JTextField();
        JTextField capacity = new JTextField();
        JTextField price = new JTextField();
        JTextField status = new JTextField();

        JButton saveBtn = new JButton("Save");

        dialog.add(new JLabel("Model:")); dialog.add(model);
        dialog.add(new JLabel("Year:")); dialog.add(year);
        dialog.add(new JLabel("Type:")); dialog.add(type);
        dialog.add(new JLabel("Capacity:")); dialog.add(capacity);
        dialog.add(new JLabel("Price/Day:")); dialog.add(price);
        dialog.add(new JLabel("Status:")); dialog.add(status);
        dialog.add(new JLabel("")); dialog.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try (Connection con = CarRentalSystem.getConnection()) {
                String query = "INSERT INTO vehicle (model, year, type, capacity, price_per_day, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, model.getText());
                ps.setInt(2, Integer.parseInt(year.getText()));
                ps.setString(3, type.getText());
                ps.setInt(4, Integer.parseInt(capacity.getText()));
                ps.setDouble(5, Double.parseDouble(price.getText()));
                ps.setString(6, status.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Vehicle added successfully!");
                dialog.dispose();
                refreshPage();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void editVehicle() {
        int row = vehiclesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle to edit!");
            return;
        }

        int id = Integer.parseInt(vehiclesTable.getValueAt(row, 0).toString());
        String statusOld = vehiclesTable.getValueAt(row, 6).toString();

        String newStatus = JOptionPane.showInputDialog(this, "Enter new status:", statusOld);
        if (newStatus != null && !newStatus.isEmpty()) {
            try (Connection con = CarRentalSystem.getConnection()) {
                String query = "UPDATE vehicle SET status=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, newStatus);
                ps.setInt(2, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Vehicle status updated!");
                refreshPage();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating vehicle status: " + ex.getMessage());
            }
        }
    }

    private void deleteVehicle() {
        int row = vehiclesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle to delete!");
            return;
        }

        int id = Integer.parseInt(vehiclesTable.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this vehicle?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = CarRentalSystem.getConnection()) {
                String query = "DELETE FROM vehicle WHERE id=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Vehicle deleted!");
                refreshPage();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting vehicle: " + ex.getMessage());
            }
        }
    }

    private void refreshPage() {
        dispose();
        new AdminPanel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPanel::new);
    }
}
