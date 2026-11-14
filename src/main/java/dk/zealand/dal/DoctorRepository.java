package dk.zealand.dal;

import dk.zealand.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DoctorRepository {

    // READ: Hent alle Doctors
    public List<Doctor> findAll() throws SQLException {

        List<Doctor> doctors = new ArrayList<>();

        String sql = "SELECT id, name, actor FROM doctors";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                doctors.add(mapDoctor(result));
            }
        }

        return doctors;
    }

    // CREATE: Gem en Doctor
    public void save(Doctor doctor) throws SQLException {

        String sql =
                "INSERT INTO doctors (name, actor) VALUES (?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getActor());

            statement.executeUpdate();
        }
    }

    // UPDATE: Ændr skuespiller
    public boolean updateActor(int id, String actor)
            throws SQLException {

        String sql =
                "UPDATE doctors SET actor = ? WHERE id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, actor);
            statement.setInt(2, id);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        }
    }

    // DELETE: Slet en Doctor
    public boolean deleteById(int id)
            throws SQLException {

        String sql = "DELETE FROM doctors WHERE id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        }
    }

    // SEARCH: Find Doctors ud fra navn
    public List<Doctor> searchByName(String search)
            throws SQLException {

        List<Doctor> doctors = new ArrayList<>();

        String sql =
                "SELECT id, name, actor FROM doctors WHERE name LIKE ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + search + "%");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    doctors.add(mapDoctor(result));
                }
            }
        }

        return doctors;
    }

    // Hjælpemetode: Omdan en databaserække til et objekt
    private Doctor mapDoctor(ResultSet result)
            throws SQLException {

        return new Doctor(
                result.getInt("id"),
                result.getString("name"),
                result.getString("actor")
        );
    }
}