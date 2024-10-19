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
import sap.ass01.solution.backend.layered.presentation.HTTPServerVerticle;

public class LayeredBackendApp {
    public static void main(String[] args) throws IllegalArgumentException {
        DatabindCodec.mapper().registerModule(new Jdk8Module());
        var database = new FileSystemDatabaseImpl(new File("/Users/Alessandro/Desktop/testfolder"));
        var persistence = new FileSystemDBCollectionStorage(database);
        var businessLogic = new BusinessLogicImpl(persistence);

        try {
            businessLogic.signup(new CreateUserDTO(new UserId("user1")));
            businessLogic
                    .createEBike(new CreateEBikeDTO(new EBikeId("bike1"), new P2d(50, 100), new V2d(1, 0), 0, 100));
        } catch (IllegalArgumentException e) {
            // duplicates
        }

        Vertx
                .vertx()
                .deployVerticle(new HTTPServerVerticle(businessLogic))
                .onSuccess(res -> System.out.println("Verticle deployed"))
                .onFailure(err -> System.err.println("Verticle deployment failed with error: " + err.getMessage()));

        new RidesSimulator(businessLogic, 1000).start();
    }
}
