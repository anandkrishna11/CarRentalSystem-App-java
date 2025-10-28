import javax.swing.*;
import java.awt.*;

public class CarRentalDashboard extends JFrame {
    public CarRentalDashboard() {
        setTitle("Car Rental System Dashboard");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton browseBtn = new JButton("Browse Vehicles");
        JButton rentBtn = new JButton("Rent a Car");

        mainPanel.add(browseBtn);
        mainPanel.add(rentBtn);

        add(mainPanel);

        browseBtn.addActionListener(e -> {
            new BrowseVehicle();
        });

        rentBtn.addActionListener(e -> {
            new RentCar();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarRentalDashboard().setVisible(true));
    }
}
