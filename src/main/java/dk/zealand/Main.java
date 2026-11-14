package dk.zealand;

import dk.zealand.cli.DoctorCliApp;
import dk.zealand.dal.DoctorRepository;
import dk.zealand.service.DoctorService;

public class Main {

    public static void main(String[] args) {

        DoctorRepository repository =
                new DoctorRepository();

        DoctorService service =
                new DoctorService(repository);

        DoctorCliApp app =
                new DoctorCliApp(service);

        app.start();
    }
}