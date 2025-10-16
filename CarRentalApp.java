import javax.swing.*;
import java.util.*;

public class CarRentalApp {

    static ArrayList<Vehicle> fleet = new ArrayList<>();
    static ArrayList<Rental> activeRentals = new ArrayList<>();
    static ArrayList<Rental> rentalHistory = new ArrayList<>();

    public static void main(String[] args) {
        preloadVehicles(); // Preload demo vehicles

        JOptionPane.showMessageDialog(null,
                "Welcome to SmartFleet Car Rental System",
                "Car Rental System", JOptionPane.INFORMATION_MESSAGE);

        boolean running = true;

        while (running) {
            String[] options = {"Add Vehicle", "View Fleet", "Rent Vehicle",
                    "Return Vehicle", "Generate Report", "View History", "Exit"};

            int choice = JOptionPane.showOptionDialog(null,
                    "Choose an option:",
                    "Main Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            switch (choice) {
                case 0:
                    addVehicle();
                    break;
                case 1:
                    viewFleet();
                    break;
                case 2:
                    rentVehicle();
                    break;
                case 3:
                    returnVehicle();
                    break;
                case 4:
                    generateReport();
                    break;
                case 5:
                    viewHistory();
                    break;
                default:
                    running = false;
                    JOptionPane.showMessageDialog(null, "Thank you for using SmartFleet Car Rental System!");
            }
        }
    }

    // === Preload sample vehicles ===
    public static void preloadVehicles() {
        fleet.add(new Vehicle("Car", "Toyota Corolla", "ABC123", 550));
        fleet.add(new Vehicle("Truck", "Isuzu KB250", "DEF456", 800));
        fleet.add(new Vehicle("Van", "Mercedes Sprinter", "GHI789", 950));
        fleet.add(new Vehicle("Car", "VW Polo", "JKL321", 600));
    }

    // === Add new vehicle ===
    public static void addVehicle() {
        String type = JOptionPane.showInputDialog("Enter vehicle type (Car / Truck / Van):");
        if (type == null || type.isEmpty()) return;

        String model = JOptionPane.showInputDialog("Enter vehicle model:");
        if (model == null || model.isEmpty()) return;

        String regNo = JOptionPane.showInputDialog("Enter registration number:");
        if (regNo == null || regNo.isEmpty()) return;

        double dailyRate = 0;
        try {
            dailyRate = Double.parseDouble(JOptionPane.showInputDialog("Enter daily rental rate (R):"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid rate entered. Vehicle not added.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vehicle newVehicle = new Vehicle(type, model, regNo, dailyRate);
        fleet.add(newVehicle);
        JOptionPane.showMessageDialog(null, "Vehicle added successfully!");
    }

    // === View fleet ===
    public static void viewFleet() {
        if (fleet.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No vehicles in fleet.");
            return;
        }

        StringBuilder sb = new StringBuilder("Fleet Overview:\n\n");
        for (Vehicle v : fleet) {
            sb.append(v).append("\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // === Rent a vehicle ===
    public static void rentVehicle() {
        if (fleet.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No vehicles available for rent.");
            return;
        }

        StringBuilder sb = new StringBuilder("Available Vehicles:\n");
        for (Vehicle v : fleet) {
            if (v.isAvailable) {
                sb.append(v.regNo).append(" - ").append(v.model)
                  .append(" (R").append(v.dailyRate).append("/day)\n");
            }
        }

        String reg = JOptionPane.showInputDialog(sb + "\nEnter registration number to rent:");
        if (reg == null) return;

        Vehicle selected = null;
        for (Vehicle v : fleet) {
            if (v.regNo.equalsIgnoreCase(reg) && v.isAvailable) {
                selected = v;
                break;
            }
        }

        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Vehicle not found or already rented.");
            return;
        }

        // === Customer details ===
        String name = JOptionPane.showInputDialog("Enter customer full name:");
        if (name == null || name.isEmpty()) return;

        String email = JOptionPane.showInputDialog("Enter customer email address:");
        if (email == null || email.isEmpty()) return;

        String phone = JOptionPane.showInputDialog("Enter customer phone number:");
        if (phone == null || phone.isEmpty()) return;

        // === Rental duration ===
        int days = 0;
        try {
            days = Integer.parseInt(JOptionPane.showInputDialog("Enter rental duration (days):"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number of days.");
            return;
        }

        double totalCost = selected.dailyRate * days;
        selected.isAvailable = false;

        Customer customer = new Customer(name, email, phone);
        Rental rental = new Rental(selected, customer, days, totalCost, new Date());
        activeRentals.add(rental);
        rentalHistory.add(rental);

        JOptionPane.showMessageDialog(null,
                "Vehicle rented successfully to " + name +
                        "\nTotal cost: R" + totalCost +
                        "\nEmail: " + email +
                        "\nPhone: " + phone);
    }

    // === Return vehicle ===
    public static void returnVehicle() {
        if (activeRentals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No active rentals to return.");
            return;
        }

        StringBuilder sb = new StringBuilder("Active Rentals:\n");
        for (Rental r : activeRentals) {
            sb.append(r.vehicle.regNo).append(" - ").append(r.vehicle.model)
                    .append(" (").append(r.customer.name).append(")\n");
        }

        String reg = JOptionPane.showInputDialog(sb + "\nEnter registration number to return:");
        if (reg == null) return;

        Rental foundRental = null;
        for (Rental r : activeRentals) {
            if (r.vehicle.regNo.equalsIgnoreCase(reg)) {
                foundRental = r;
                break;
            }
        }

        if (foundRental == null) {
            JOptionPane.showMessageDialog(null, "Rental not found.");
            return;
        }

        foundRental.vehicle.isAvailable = true;
        activeRentals.remove(foundRental);
        JOptionPane.showMessageDialog(null, "Vehicle returned successfully!");
    }

    // === Generate report ===
    public static void generateReport() {
        if (fleet.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No vehicles in the system.");
            return;
        }

        StringBuilder report = new StringBuilder("Active Rentals Report:\n\n");
        double totalRevenue = 0;

        for (Rental r : activeRentals) {
            report.append(r).append("\n");
            totalRevenue += r.totalCost;
        }

        report.append("\nTotal Active Rentals: ").append(activeRentals.size());
        report.append("\nTotal Revenue (Active): R").append(String.format("%.2f", totalRevenue));

        JOptionPane.showMessageDialog(null, report.toString());
    }

    // === View Rental History ===
    public static void viewHistory() {
        if (rentalHistory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No rental history available.");
            return;
        }

        StringBuilder sb = new StringBuilder("Rental History:\n\n");
        for (Rental r : rentalHistory) {
            sb.append(r.vehicle.regNo).append(" - ").append(r.vehicle.model)
                    .append(" | Customer: ").append(r.customer.name)
                    .append(" | Days: ").append(r.days)
                    .append(" | Cost: R").append(r.totalCost)
                    .append(" | Date: ").append(r.rentDate).append("\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // === Vehicle Class ===
    static class Vehicle {
        String type, model, regNo;
        double dailyRate;
        boolean isAvailable;

        Vehicle(String type, String model, String regNo, double dailyRate) {
            this.type = type;
            this.model = model;
            this.regNo = regNo;
            this.dailyRate = dailyRate;
            this.isAvailable = true;
        }

        @Override
        public String toString() {
            return regNo + " - " + model + " (" + type + ") - R" + dailyRate + "/day - "
                    + (isAvailable ? "Available" : "Rented");
        }
    }

    // === Customer Class ===
    static class Customer {
        String name, email, phone;

        Customer(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
    }

    // === Rental Class ===
    static class Rental {
        Vehicle vehicle;
        Customer customer;
        int days;
        double totalCost;
        Date rentDate;

        Rental(Vehicle vehicle, Customer customer, int days, double totalCost, Date rentDate) {
            this.vehicle = vehicle;
            this.customer = customer;
            this.days = days;
            this.totalCost = totalCost;
            this.rentDate = rentDate;
        }

        @Override
        public String toString() {
            return vehicle.regNo + " - " + vehicle.model +
                    " | Customer: " + customer.name +
                    " | " + days + " days | Total: R" + totalCost;
        }
    }
}
