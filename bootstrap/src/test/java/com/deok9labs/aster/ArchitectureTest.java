package com.deok9labs.aster;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** 문서화된 adapter 경계를 코드 의존성에서도 강제한다. */
@AnalyzeClasses(packages = "com.deok9labs.aster")
class ArchitectureTest {

    @ArchTest
    static final ArchRule webMustNotDependOnPersistence = noClasses()
            .that().resideInAPackage("..ember.adapter.in.web..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..ember.adapter.out.persistence..",
                    "..ember.adapter.out.persistence.entity..",
                    "..ember.adapter.out.persistence.repository..");

    @ArchTest
    static final ArchRule domainMustNotDependOnFrameworks = noClasses()
            .that().resideInAPackage("..ember.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta.persistence..");
}
