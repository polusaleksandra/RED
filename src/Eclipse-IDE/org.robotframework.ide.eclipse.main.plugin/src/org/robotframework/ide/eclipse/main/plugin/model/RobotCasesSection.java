package org.robotframework.ide.eclipse.main.plugin.model;

import java.util.List;

import org.robotframework.ide.core.testData.model.table.ARobotSectionTable;
import org.robotframework.ide.core.testData.model.table.TestCaseTable;
import org.robotframework.ide.core.testData.model.table.testCases.TestCase;

public class RobotCasesSection extends RobotSuiteFileSection {

    public static final String SECTION_NAME = "Test Cases";

    RobotCasesSection(final RobotSuiteFile parent) {
        super(parent, SECTION_NAME);
    }

    public RobotCase createTestCase(final String name, final String comment) {
        return createTestCase(getChildren().size(), name, comment);
    }

    public RobotCase createTestCase(final int index, final String name, final String comment) {
        final RobotCase testCase = new RobotCase(this, name, comment);
        elements.add(index, testCase);
        return testCase;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RobotCase> getChildren() {
        return (List<RobotCase>) super.getChildren();
    }
    
    @Override
    public void link(ARobotSectionTable table) {
        TestCaseTable testCaseTable = (TestCaseTable) table;
        for (final TestCase testCase : testCaseTable.getTestCases()) {
            RobotCase newTestCase = new RobotCase(this, testCase.getTestName().getText().toString(), "");
            elements.add(newTestCase);
        }
    }
}
