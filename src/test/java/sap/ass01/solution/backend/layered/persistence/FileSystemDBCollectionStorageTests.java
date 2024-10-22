package sap.ass01.solution.backend.layered.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import io.vertx.core.json.jackson.DatabindCodec;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import sap.ass01.solution.backend.layered.database.FileSystemDatabaseImpl;
import sap.ass01.solution.backend.layered.persistence.exceptions.DuplicateIdException;

public class FileSystemDBCollectionStorageTests {

    @Test
    public void fileSystemDBCollectionStorageWorks() throws DuplicateIdException {
        DatabindCodec.mapper().registerModule(new Jdk8Module());

        var recordsCollectionName = "records";
        var classesCollectionName = "classes";
        var homeDir = System.getProperty("user.home");
        var db = new FileSystemDatabaseImpl(new File(homeDir + "/Desktop/EBikeTests"));
        var storage = new FileSystemDBCollectionStorage(db);

        try {
            storage.deleteCollection(recordsCollectionName);
            storage.deleteCollection(classesCollectionName);
        } catch (Exception e) {
        }
        storage.createCollection(recordsCollectionName);
        storage.createCollection(classesCollectionName);

        TestRecord record = new TestRecord(10, Optional.of("ciao"));
        storage.insert(recordsCollectionName, "1", record);
        Optional<TestRecord> r = storage.find(recordsCollectionName, "1", TestRecord.class);
        assertEquals(record, r.get());

        TestClass class1 = new TestClass(10, Optional.of("ciao"), record);
        TestClass class2 = new TestClass(11, Optional.empty(), record);
        storage.insert(classesCollectionName, "1", class1);
        storage.insert(classesCollectionName, "2", class2);
        Optional<TestClass> c1 = storage.find(classesCollectionName, "1", TestClass.class);
        assertEquals(class1, c1.get());

        var allClasses = storage.getAllFromCollection(classesCollectionName, TestClass.class);
        assertEquals(allClasses.size(), 2);
    }

}
