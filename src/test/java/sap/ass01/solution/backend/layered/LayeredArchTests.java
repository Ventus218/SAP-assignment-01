package sap.ass01.solution.backend.layered;

import java.util.*;
import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class LayeredArchTests {
    /**
     * Testing that in a layered architecture each layer can depend only on the
     * layer below (without skipping layers)
     */
    @Test
    public void test() {
        String layered = "sap.ass01.solution.backend.layered";
        String presentation = layered + ".presentation";
        String businesslogic = layered + ".businesslogic";
        String persistence = layered + ".persistence";
        String database = layered + ".database";
        JavaClasses importedClasses = new ClassFileImporter().importPackages(
                presentation,
                businesslogic,
                persistence,
                database);

        List<ArchRule> rules = new ArrayList<>();

        rules.add(
                classes().that().resideInAPackage(presentation + "..")
                        .should().onlyBeAccessed().byAnyPackage(presentation + ".."));
        rules.add(
                classes().that().resideInAPackage(businesslogic + "..")
                        .should().onlyBeAccessed().byAnyPackage(businesslogic + "..", presentation + ".."));
        rules.add(
                classes().that().resideInAPackage(persistence + "..")
                        .should().onlyBeAccessed().byAnyPackage(persistence + "..", businesslogic + ".."));
        rules.add(
                classes().that().resideInAPackage(database + "..")
                        .should().onlyBeAccessed().byAnyPackage(database + "..", persistence + ".."));

        rules.forEach(r -> r.check(importedClasses));
    }
}
