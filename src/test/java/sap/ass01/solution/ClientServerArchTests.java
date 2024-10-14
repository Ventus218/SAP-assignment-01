package sap.ass01.solution;

import java.util.*;
import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ClientServerArchTests {
    @Test
    public void clientAndServerShouldNotDependOnOneAnother() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass01.solution");

        List<ArchRule> rules = new ArrayList<>();
        rules.add(
                noClasses().that().resideInAPackage("..backend..")
                        .should().dependOnClassesThat().resideInAPackage("..frontend..")
                        .as("Backend should not depend on frontend"));
        rules.add(
                noClasses().that().resideInAPackage("..frontend..")
                        .should().dependOnClassesThat().resideInAPackage("..backend..")
                        .as("Frontend should depend from backend only through web APIs and not code."));

        rules.forEach(r -> r.check(importedClasses));
    }
}
