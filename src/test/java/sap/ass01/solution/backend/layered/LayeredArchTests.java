package sap.ass01.solution.backend.layered;

import java.util.*;
import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class LayeredArchTests {
    /**
     * Testing that in a layered architecture each layer can depend only on the
     * layer below (without skipping layers)
     */
    @Test
    public void test() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass01.solution.backend.layered");

        List<ArchRule> rules = new ArrayList<>();
        // TODO: Uncomment after writing presentation classes
        // rules.add(
        // noClasses().that().resideInAPackage("..presentation..")
        // .should().dependOnClassesThat().resideInAnyPackage(
        // "..persistence..",
        // "..database.."));
        rules.add(
                noClasses().that().resideInAPackage("..businesslogic..")
                        .should().dependOnClassesThat().resideInAnyPackage(
                                "..presentation..",
                                "..database.."));
        rules.add(
                noClasses().that().resideInAPackage("..persistence..")
                        .should().dependOnClassesThat().resideInAnyPackage(
                                "..presentation..",
                                "..businesslogic.."));
        rules.add(
                noClasses().that().resideInAPackage("..database..")
                        .should().dependOnClassesThat().resideInAnyPackage(
                                "..presentation..",
                                "..businesslogic..",
                                "persistence"));

        rules.forEach(r -> r.check(importedClasses));
    }
}
