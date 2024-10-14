package sap.ass01.solution.backend.layered.persistence;

import java.io.File;
import java.util.Optional;
import io.vertx.core.json.jackson.DatabindCodec;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import sap.ass01.solution.backend.layered.database.FileSystemDatabaseImpl;

public class FileSystemDBCollectionStorageTests {

    public static void main(String[] args) {
        DatabindCodec.mapper().registerModule(new Jdk8Module());

        var path = "/Users/Alessandro/Desktop/testfolder";
        var recordsCollectionName = "records";
        var classesCollectionName = "classes";
        var db = new FileSystemDatabaseImpl(new File(path));
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
        TestRecord r = storage.get(recordsCollectionName, "1", TestRecord.class);
        System.out.println(r);

        TestClass class1 = new TestClass(10, Optional.of("ciao"), record);
        TestClass class2 = new TestClass(11, Optional.empty(), record);
        storage.insert(classesCollectionName, "1", class1);
        storage.insert(classesCollectionName, "2", class2);
        TestClass cla = storage.get(classesCollectionName, "1", TestClass.class);
        System.out.println(cla);

        var allClasses = storage.getAllFromCollection(classesCollectionName, TestClass.class);
        System.out.println(allClasses);
    }

}
