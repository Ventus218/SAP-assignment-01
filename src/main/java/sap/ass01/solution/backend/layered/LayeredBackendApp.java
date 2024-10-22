package sap.ass01.solution.backend.layered;

import java.io.File;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import sap.ass01.solution.backend.layered.businesslogic.BusinessLogicImpl;
import sap.ass01.solution.backend.layered.businesslogic.RidesSimulator;
import sap.ass01.solution.backend.layered.businesslogic.model.*;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.*;
import sap.ass01.solution.backend.layered.database.*;
import sap.ass01.solution.backend.layered.persistence.*;
import sap.ass01.solution.backend.layered.presentation.HTTPServerPresentation;

public class LayeredBackendApp {
    public static void main(String[] args) throws IllegalArgumentException {
        DatabindCodec.mapper().registerModule(new Jdk8Module());
        var homeDir = System.getProperty("user.home");
        var database = new FileSystemDatabaseImpl(new File(homeDir + "/Desktop/EBikeDB"));
        var persistence = new FileSystemDBCollectionStorage(database);
        var businessLogic = new BusinessLogicImpl(persistence);

        // Create sample user and bike if not present
        var user1Id = new UserId("user1");
        businessLogic.getUser(user1Id).orElseGet(() -> businessLogic.signup(new CreateUserDTO(user1Id)));
        var bike1Id = new EBikeId("bike1");
        businessLogic.getEBike(bike1Id).orElseGet(
                () -> businessLogic.createEBike(new CreateEBikeDTO(bike1Id, new P2d(50, 100), new V2d(1, 0), 0, 100)));

        Vertx
                .vertx()
                .deployVerticle(new HTTPServerPresentation(businessLogic))
                .onSuccess(res -> System.out.println("Verticle deployed"))
                .onFailure(err -> System.err.println("Verticle deployment failed with error: " + err.getMessage()));

        new RidesSimulator(businessLogic, 1000).start();
    }
}
