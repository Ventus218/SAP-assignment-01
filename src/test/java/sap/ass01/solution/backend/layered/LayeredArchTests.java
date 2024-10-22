package sap.ass01.solution.backend.layered;

import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class LayeredArchTests {

    final String layered = "sap.ass01.solution.backend.layered";
    final String presentation = layered + ".presentation";
    final String businesslogic = layered + ".businesslogic";
    final String persistence = layered + ".persistence";
    final String database = layered + ".database";
    final String java = "java";
    final String vertx = "io.vertx";
    final String json = vertx + ".core.json";

    final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages(
                    presentation,
                    businesslogic,
                    persistence,
                    database);

    @Test
    public void PresentationShouldDependOnlyOnBusinessLogic() {
        classes().that().resideInAPackage(presentation + "..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(java + "..", vertx + "..", presentation + "..", businesslogic + "..")
                .check(importedClasses);
    }

    @Test
    public void BusinessLogicShouldDependOnlyOnPersistence() {
        classes().that().resideInAPackage(businesslogic + "..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(java + "..", businesslogic + "..", persistence + "..")
                .check(importedClasses);
    }

    @Test
    public void PersistenceShouldDependOnlyOnDatabase() {
        classes().that().resideInAPackage(persistence + "..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(java + "..", json + "..", persistence + "..", database + "..")
                .check(importedClasses);
    }

    @Test
    public void DatabaseShouldNotDependOnAnything() {
        classes().that().resideInAPackage(database + "..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(java + "..", database + "..")
                .check(importedClasses);
    }
}
