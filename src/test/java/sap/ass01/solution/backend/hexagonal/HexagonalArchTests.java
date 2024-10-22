package sap.ass01.solution.backend.hexagonal;

import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class HexagonalArchTests {
    final String hexagonal = "sap.ass01.solution.backend.hexagonal";
    final String domain = hexagonal + ".domain";
    final String ports = hexagonal + ".ports";
    final String adapters = hexagonal + ".adapters";
    final String technologies = hexagonal + ".technologies";
    final String java = "java";

    JavaClasses importedClasses = new ClassFileImporter().importPackages(
            domain,
            ports,
            adapters,
            technologies);

    @Test
    public void DomainCanDependOnlyOnPorts() {
        classes().that().resideInAPackage(domain + "..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(java + "..", domain + "..", ports + "..")
                .check(importedClasses);
    }

    @Test
    public void PortsCanDependOnlyOnDomain() {
        classes().that().resideInAPackage(ports + "..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(java + "..", ports + "..", domain + "..")
                .check(importedClasses);

    }

    @Test
    public void TechnologiesShouldNotDependOnAnything() {
        noClasses().that().resideInAPackage(technologies + "..")
                .should().dependOnClassesThat().resideInAnyPackage(domain + "..", ports + "..", adapters + "..")
                .check(importedClasses);
        ;
    }
}
