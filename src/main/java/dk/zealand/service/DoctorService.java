package dk.zealand.service;

import dk.zealand.dal.DoctorRepository;
import dk.zealand.model.Doctor;

import java.sql.SQLException;
import java.util.List;

public class DoctorService {

    private final DoctorRepository repository;

    public DoctorService(DoctorRepository repository) {
        this.repository = repository;
    }

    // Hent alle
    public List<Doctor> getAllDoctors()
            throws SQLException {

        return repository.findAll();
    }

    // Opret
    public void addDoctor(String name, String actor)
            throws SQLException {

        validateText(name, "Navn");
        validateText(actor, "Skuespiller");

        Doctor doctor = new Doctor(
                0, name.trim(), actor.trim()
        );

        repository.save(doctor);
    }

    // Opdater
    public boolean updateActor(int id, String actor)
            throws SQLException {

        validateId(id);
        validateText(actor, "Skuespiller");

        return repository.updateActor(id, actor.trim());
    }

    // Slet
    public boolean deleteDoctor(int id)
            throws SQLException {

        validateId(id);

        return repository.deleteById(id);
    }

    // Søg
    public List<Doctor> searchDoctors(String search)
            throws SQLException {

        validateText(search, "Søgetekst");

        return repository.searchByName(search.trim());
    }

    // Validering af tekst
    private void validateText(String value, String field) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " må ikke være tomt"
            );
        }

        if (value.length() > 100) {
            throw new IllegalArgumentException(
                    field + " må højst være 100 tegn"
            );
        }
    }

    // Validering af ID
    private void validateId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID skal være større end 0"
            );
        }
    }
}