/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project.build.validation.versiondependent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.rf.ide.core.environment.RobotVersion;
import org.rf.ide.core.testdata.model.RobotFile;
import org.rf.ide.core.testdata.model.table.KeywordTable;
import org.rf.ide.core.testdata.model.table.SettingTable;
import org.rf.ide.core.testdata.model.table.TestCaseTable;
import org.rf.ide.core.testdata.model.table.keywords.UserKeyword;
import org.rf.ide.core.testdata.model.table.testcases.TestCase;
import org.robotframework.ide.eclipse.main.plugin.project.build.validation.FileValidationContext;
import org.robotframework.ide.eclipse.main.plugin.project.build.validation.ValidationContext;

@RunWith(Enclosed.class)
public class VersionDependentValidatorsTest {

    public static class GeneralSettingsTableValidatorsTest {

        @Test
        public void properValidatorsAreReturnedForVersionUnder29() {
            assertThat(getGeneralSettingsTableValidators("2.8.0")).hasSize(9)
                    .hasOnlyElementsOfTypes(SettingsDuplicationInOldRfValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion29() {
            assertThat(getGeneralSettingsTableValidators("2.9")).hasSize(10)
                    .hasOnlyElementsOfTypes(SettingsDuplicationInOldRfValidator.class,
                            MetadataKeyInColumnOfSettingValidatorUntilRF30.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion30() {
            assertThat(getGeneralSettingsTableValidators("3.0.4")).hasSize(13)
                    .hasOnlyElementsOfTypes(DeprecatedGeneralSettingsTableHeaderValidator.class,
                            SettingsDuplicationValidator.class, DeprecatedGeneralSettingNameValidator.class,
                            TimeoutMessageValidator.class, LibraryAliasNotInUpperCaseValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion31() {
            assertThat(getGeneralSettingsTableValidators("3.1")).hasSize(11)
                    .hasOnlyElementsOfTypes(SettingsDuplicationValidator.class, TimeoutMessageValidator.class,
                            LibraryAliasNotInUpperCaseValidator31.class);
        }

        private Stream<VersionDependentModelUnitValidator> getGeneralSettingsTableValidators(final String version) {
            final VersionDependentValidators validators = new VersionDependentValidators(prepareContext(version), null);
            return validators.getGeneralSettingsTableValidators(mock(SettingTable.class));
        }

    }

    public static class KeywordTableValidatorsTest {

        @Test
        public void properValidatorsAreReturnedForVersionUnder30() {
            assertThat(getKeywordTableValidators("2.9")).isEmpty();
        }

        @Test
        public void properValidatorsAreReturnedForVersion30() {
            assertThat(getKeywordTableValidators("3.0.4")).hasSize(1)
                    .hasOnlyElementsOfTypes(DeprecatedKeywordTableHeaderValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion31() {
            assertThat(getKeywordTableValidators("3.1")).isEmpty();
        }

        private Stream<VersionDependentModelUnitValidator> getKeywordTableValidators(final String version) {
            final VersionDependentValidators validators = new VersionDependentValidators(prepareContext(version), null);
            return validators.getKeywordTableValidators(mock(KeywordTable.class));
        }

    }

    public static class TestCaseSettingsValidatorsTest {

        @Test
        public void properValidatorsAreReturnedForVersionUnder30() {
            assertThat(getTestCaseSettingsValidators("2.9")).hasSize(1)
                    .hasOnlyElementsOfTypes(LocalSettingsDuplicationInOldRfValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion30() {
            assertThat(getTestCaseSettingsValidators("3.0.4")).hasSize(8)
                    .hasOnlyElementsOfTypes(SettingsDuplicationValidator.class,
                            DeprecatedTestCaseSettingNameValidator.class, TimeoutMessageValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion31() {
            assertThat(getTestCaseSettingsValidators("3.1")).hasSize(7)
                    .hasOnlyElementsOfTypes(SettingsDuplicationValidator.class, TimeoutMessageValidator.class);
        }

        private Stream<VersionDependentModelUnitValidator> getTestCaseSettingsValidators(final String version) {
            final VersionDependentValidators validators = new VersionDependentValidators(prepareContext(version), null);
            final TestCase testCase = mock(TestCase.class);
            final TestCaseTable testCaseTable = mock(TestCaseTable.class);
            final RobotFile robotFile = mock(RobotFile.class);
            when(testCase.getParent()).thenReturn(testCaseTable);
            when(testCaseTable.getParent()).thenReturn(robotFile);
            return validators.getTestCaseSettingsValidators(testCase);
        }

    }

    public static class KeywordSettingsValidatorsTest {

        @Test
        public void properValidatorsAreReturnedForVersionUnder30() {
            assertThat(getKeywordSettingsValidators("2.9")).hasSize(1)
                    .hasOnlyElementsOfTypes(LocalSettingsDuplicationInOldRfValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion30() {
            assertThat(getKeywordSettingsValidators("3.0.4")).hasSize(8)
                    .hasOnlyElementsOfTypes(SettingsDuplicationValidator.class,
                            DeprecatedKeywordSettingNameValidator.class, TimeoutMessageValidator.class);
        }

        @Test
        public void properValidatorsAreReturnedForVersion31() {
            assertThat(getKeywordSettingsValidators("3.1")).hasSize(7)
                    .hasOnlyElementsOfTypes(SettingsDuplicationValidator.class, TimeoutMessageValidator.class);
        }

        private Stream<VersionDependentModelUnitValidator> getKeywordSettingsValidators(final String version) {
            final VersionDependentValidators validators = new VersionDependentValidators(prepareContext(version), null);
            final UserKeyword userKeyword = mock(UserKeyword.class);
            final KeywordTable keywordTable = mock(KeywordTable.class);
            final RobotFile robotFile = mock(RobotFile.class);
            when(userKeyword.getParent()).thenReturn(keywordTable);
            when(keywordTable.getParent()).thenReturn(robotFile);
            return validators.getKeywordSettingsValidators(userKeyword);
        }

    }

    private static FileValidationContext prepareContext(final String version) {
        final ValidationContext context = new ValidationContext(null, null, RobotVersion.from(version), null, null,
                null);
        return new FileValidationContext(context, mock(IFile.class));
    }
}
