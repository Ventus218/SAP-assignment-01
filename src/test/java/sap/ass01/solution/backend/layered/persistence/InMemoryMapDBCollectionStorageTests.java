package sap.ass01.solution.backend.layered.persistence;

import java.util.Optional;
import io.vertx.core.json.*;
import io.vertx.core.json.jackson.DatabindCodec;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import sap.ass01.solution.backend.layered.database.InMemoryMapDatabaseImpl;

public class InMemoryMapDBCollectionStorageTests {

    public static void main(String[] args) {
        DatabindCodec.mapper().registerModule(new Jdk8Module());

        var recordsCollectionName = "records";
        var classesCollectionName = "classes";
        var db = new InMemoryMapDatabaseImpl();
        var storage = new InMemoryMapDBCollectionStorage(db);

        storage.createCollection(recordsCollectionName);
        storage.createCollection(classesCollectionName);

        JsonObject jsonRecord = JsonObject.mapFrom(new TestRecord(10,
                Optional.of("ciao")));
        storage.insert(recordsCollectionName, "1", jsonRecord);
        TestRecord rec = storage.get(recordsCollectionName,
                "1").mapTo(TestRecord.class);
        System.out.println(rec);

        JsonObject jsonClass = JsonObject.mapFrom(new TestClass(10, Optional.of("ciao"), rec));
        storage.insert(classesCollectionName, "1", jsonClass);
        TestClass cla = storage.get(classesCollectionName, "1").mapTo(TestClass.class);
        System.out.println(cla);
    }

}
