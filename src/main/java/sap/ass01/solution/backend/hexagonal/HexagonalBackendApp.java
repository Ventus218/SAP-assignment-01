package sap.ass01.solution.backend.hexagonal;

import java.io.File;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;
import sap.ass01.solution.backend.hexagonal.domain.*;
import sap.ass01.solution.backend.hexagonal.domain.model.*;
import sap.ass01.solution.backend.hexagonal.domain.model.dto.*;
import sap.ass01.solution.backend.hexagonal.adapters.persistence.FileSystemRepositoryAdapter;
import sap.ass01.solution.backend.hexagonal.adapters.presentation.HTTPPresentationAdapter;
import sap.ass01.solution.backend.hexagonal.technologies.persistence.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class HexagonalBackendApp {

    public static void main(String[] args) {
        DatabindCodec.mapper().registerModule(new Jdk8Module());
        var homeDir = System.getProperty("user.home");
        var database = new FileSystemDatabaseImpl(new File(homeDir + "/Desktop/EBikeDB"));
        var userRepository = new FileSystemRepositoryAdapter<User>(database, "users", User.class);
        var ebikeRepository = new FileSystemRepositoryAdapter<EBike>(database, "ebikes", EBike.class);
        var rideRepository = new FileSystemRepositoryAdapter<Ride>(database, "rides", Ride.class);
        var domain = new DomainImpl(userRepository, ebikeRepository, rideRepository);

        // Create sample user and bike if not present
        var user1Id = new UserId("user1");
        domain.getUser(user1Id).orElseGet(() -> domain.signup(new CreateUserDTO(user1Id)));
        var bike1Id = new EBikeId("bike1");
        domain.getEBike(bike1Id).orElseGet(
                () -> domain.createEBike(new CreateEBikeDTO(bike1Id, new P2d(50, 100), new V2d(1, 0), 0, 100)));

        Vertx
                .vertx()
                .deployVerticle(new HTTPPresentationAdapter(domain))
                .onSuccess(res -> System.out.println("Verticle deployed"))
                .onFailure(err -> System.err.println("Verticle deployment failed with error: " + err.getMessage()));

        new RidesSimulator(domain, 1000).start();
    }
}
