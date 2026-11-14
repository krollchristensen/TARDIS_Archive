package dk.zealand.cli;

import dk.zealand.model.Doctor;
import dk.zealand.service.DoctorService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class DoctorCliApp {

    private final DoctorService service;
    private final Scanner scanner = new Scanner(System.in);

    public DoctorCliApp(DoctorService service) {
        this.service = service;
    }

    public void start() {

        boolean running = true;

        while (running) {

            showMenu();

            String choice = scanner.nextLine();

            try {
                switch (choice) {

                    case "1" -> showDoctors();
                    case "2" -> addDoctor();
                    case "3" -> updateDoctor();
                    case "4" -> deleteDoctor();
                    case "5" -> searchDoctor();

                    case "6" -> {
                        running = false;
                        System.out.println("Farvel!");
                    }

                    default ->
                            System.out.println("Ugyldigt valg.");
                }

            } catch (SQLException e) {

                System.out.println(
                        "Der opstod en databasefejl."
                );

                // Kun til debugging under udvikling
                e.printStackTrace();

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Fejl: " + e.getMessage()
                );
            }
        }

        scanner.close();
    }

    private void showMenu() {

        System.out.println("\n--- TARDIS Archive ---");
        System.out.println("1. Vis alle Doctors");
        System.out.println("2. Tilføj Doctor");
        System.out.println("3. Opdater skuespiller");
        System.out.println("4. Slet Doctor");
        System.out.println("5. Søg efter Doctor");
        System.out.println("6. Afslut");
        System.out.print("Vælg: ");
    }

    // READ
    private void showDoctors() throws SQLException {

        List<Doctor> doctors = service.getAllDoctors();

        printDoctors(doctors);
    }

    // CREATE
    private void addDoctor() throws SQLException {

        System.out.print("Navn: ");
        String name = scanner.nextLine();

        System.out.print("Skuespiller: ");
        String actor = scanner.nextLine();

        service.addDoctor(name, actor);

        System.out.println("Doctor blev gemt!");
    }

    // UPDATE
    private void updateDoctor() throws SQLException {

        int id = readId();

        System.out.print("Ny skuespiller: ");
        String actor = scanner.nextLine();

        boolean updated = service.updateActor(id, actor);

        if (updated) {
            System.out.println("Doctor blev opdateret.");
        } else {
            System.out.println("Doctor findes ikke.");
        }
    }

    // DELETE
    private void deleteDoctor() throws SQLException {

        int id = readId();

        boolean deleted = service.deleteDoctor(id);

        if (deleted) {
            System.out.println("Doctor blev slettet.");
        } else {
            System.out.println("Doctor findes ikke.");
        }
    }

    // SEARCH
    private void searchDoctor() throws SQLException {

        System.out.print("Søg efter navn: ");
        String search = scanner.nextLine();

        List<Doctor> doctors =
                service.searchDoctors(search);

        printDoctors(doctors);
    }

    // Læs et ID fra brugeren
    private int readId() {

        System.out.print("Indtast ID: ");

        String input = scanner.nextLine();

        try {
            return Integer.parseInt(input);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "ID skal være et heltal"
            );
        }
    }

    // Udskriv en liste af Doctors
    private void printDoctors(List<Doctor> doctors) {

        if (doctors.isEmpty()) {
            System.out.println("Ingen Doctors fundet.");
            return;
        }

        for (Doctor doctor : doctors) {
            System.out.println(doctor);
        }
    }
}