/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project.build.causes;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.rf.ide.core.environment.RobotVersion;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.keywords.KeywordDocumentRecognizer;
import org.rf.ide.core.testdata.text.read.recognizer.keywords.KeywordPostconditionRecognizer;
import org.robotframework.ide.eclipse.main.plugin.project.build.AdditionalMarkerAttributes;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.AddPrefixToKeywordUsage;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.ChangeToFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.CreateKeywordFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.ImportLibraryFixer;
import org.robotframework.ide.eclipse.main.plugin.project.build.fix.RemoveKeywordFixer;

import com.google.common.base.Splitter;

public enum KeywordsProblem implements IProblemCause {
    UNKNOWN_KEYWORD {

        @Override
        public String getProblemDescription() {
            return "Unknown keyword '%s'";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String keywordName = marker.getAttribute(AdditionalMarkerAttributes.NAME, null);
            final String keywordOriginalName = marker.getAttribute(AdditionalMarkerAttributes.ORIGINAL_NAME, null);
            final IFile suiteFile = (IFile) marker.getResource();

            final List<IMarkerResolution> fixers = new ArrayList<>();
            fixers.addAll(KeywordsImportsFixes.changeByImportingLibraryWithMissingKeyword(suiteFile, keywordName));
            fixers.addAll(CreateKeywordFixer.createFixers(keywordOriginalName));
            fixers.addAll(ChangeToFixer
                    .createFixers(new SimilaritiesAnalyst().provideSimilarAccessibleKeywords(suiteFile, keywordName)));
            return fixers;
        }
    },
    EMPTY_KEYWORD_NAME {

        @Override
        public String getProblemDescription() {
            return "Keyword name cannot be empty";
        }
    },
    AMBIGUOUS_KEYWORD {

        @Override
        public String getProblemDescription() {
            return "Ambiguous keyword '%s' reference. Matching keywords are defined in: %s";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String name = marker.getAttribute(AdditionalMarkerAttributes.NAME, null);
            final List<String> sources = Splitter.on(';')
                    .splitToList(marker.getAttribute(AdditionalMarkerAttributes.SOURCES, ""));
            return newArrayList(AddPrefixToKeywordUsage.createFixers(name, sources));
        }
    },
    DEPRECATED_KEYWORD {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DEPRECATED_API;
        }

        @Override
        public String getProblemDescription() {
            return "Keyword '%s' is deprecated";
        }
    },
    DUPLICATED_KEYWORD {

        @Override
        public String getProblemDescription() {
            return "Duplicated keyword definition '%s'";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new RemoveKeywordFixer(marker.getAttribute(AdditionalMarkerAttributes.NAME, null)));
        }
    },
    EMPTY_KEYWORD {

        @Override
        public String getProblemDescription() {
            return "Keyword '%s' contains no keywords to execute";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new RemoveKeywordFixer(marker.getAttribute(AdditionalMarkerAttributes.NAME, null)));
        }
    },
    KEYWORD_FROM_NESTED_LIBRARY {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.KEYWORD_FROM_NESTED_LIBRARY;
        }

        @Override
        public String getProblemDescription() {
            return "Keyword '%s' is from a library nested in a resource file";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String libName = marker.getAttribute(AdditionalMarkerAttributes.NAME, "");
            return newArrayList(new ImportLibraryFixer(libName));
        }
    },
    ARGUMENT_DEFINED_TWICE {

        @Override
        public String getProblemDescription() {
            return "The '%s' argument is defined multiple times";
        }
    },
    NON_DEFAULT_ARGUMENT_AFTER_DEFAULT {

        @Override
        public String getProblemDescription() {
            return "The non-default argument '%s' cannot occur after default one";
        }
    },
    ARGUMENT_AFTER_VARARG {

        @Override
        public String getProblemDescription() {
            return "The argument '%s' cannot occur after vararg";
        }
    },
    ARGUMENT_AFTER_KWARG {

        @Override
        public String getProblemDescription() {
            return "The argument '%s' cannot occur after kwarg";
        }
    },
    INVALID_KEYWORD_ARG_SYNTAX {

        @Override
        public String getProblemDescription() {
            return "The argument '%s' has invalid syntax";
        }
    },
    MISSING_KEYWORD {

        @Override
        public String getProblemDescription() {
            return "There is no keyword to execute specified";
        }
    },
    KEYWORD_OCCURRENCE_NOT_CONSISTENT_WITH_DEFINITION {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.KEYWORD_OCCURRENCE_NOT_CONSISTENT_WITH_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Given keyword name '%s' is not consistent with keyword definition '%s'";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String keywordOccurrence = marker.getAttribute(AdditionalMarkerAttributes.NAME, null);
            final String keywordDef = marker.getAttribute(AdditionalMarkerAttributes.ORIGINAL_NAME, null);
            final String keywordSource = marker.getAttribute(AdditionalMarkerAttributes.SOURCES, "");

            String keywordDefinition;
            if (keywordOccurrence != null && keywordDef != null && !keywordSource.isEmpty()
                    && keywordOccurrence.startsWith(keywordSource) && !keywordOccurrence.equals(keywordSource)) {
                keywordDefinition = keywordSource + "." + keywordDef;
            } else {
                keywordDefinition = keywordDef;
            }
            return newArrayList(new ChangeToFixer(keywordDefinition));
        }
    },
    FOR_OCCURRENCE_NOT_CONSISTENT_WITH_DEFINITION {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.KEYWORD_OCCURRENCE_NOT_CONSISTENT_WITH_DEFINITION;
        }

        @Override
        public String getProblemDescription() {
            return "Given for loop defining name '%s' is not consistent with definition ':FOR'";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(new ChangeToFixer(":FOR"));
        }
    },
    KEYWORD_NAME_WITH_DOTS {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.KEYWORD_NAME_WITH_DOTS;
        }

        @Override
        public String getProblemDescription() {
            return "Given keyword name '%s' contains dots. Use underscores instead of dots in keywords names.";
        }
    },
    KEYWORD_MASKS_OTHER_KEYWORD {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.MASKED_KEYWORD;
        }

        @Override
        public String getProblemDescription() {
            return "The keyword '%s' is masking the keyword '%s' defined in %s. "
                    + "Use '%s' in this suite file if the latter is desired";
        }
    },
    DEPRECATED_KEYWORD_SETTING_NAME {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DEPRECATED_API;
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public String getProblemDescription() {
            return "Keyword setting name '%s' is deprecated. Use '%s' instead";
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String targetName = marker.getAttribute(AdditionalMarkerAttributes.VALUE, "");
            return newArrayList(new ChangeToFixer(targetName));
        }
    },
    UNKNOWN_KEYWORD_SETTING {

        @Override
        public String getProblemDescription() {
            return "Unknown keyword's setting definition '%s'";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String name = marker.getAttribute(AdditionalMarkerAttributes.NAME, "");
            final RobotVersion robotVersion = Optional
                    .ofNullable(marker.getAttribute(AdditionalMarkerAttributes.ROBOT_VERSION, null))
                    .map(RobotVersion::from)
                    .orElse(new RobotVersion(3, 1));

            final Map<Pattern, String> oldSettingName = new HashMap<>();
            oldSettingName.put(KeywordDocumentRecognizer.EXPECTED,
                    RobotTokenType.KEYWORD_SETTING_DOCUMENTATION.getTheMostCorrectOneRepresentation(robotVersion)
                            .getRepresentation());
            oldSettingName.put(KeywordPostconditionRecognizer.EXPECTED,
                    RobotTokenType.KEYWORD_SETTING_TEARDOWN.getTheMostCorrectOneRepresentation(robotVersion)
                            .getRepresentation());

            return oldSettingName.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().matcher(name).matches())
                    .map(entry -> new ChangeToFixer(entry.getValue()))
                    .collect(toList());
        }
    },
    EMPTY_KEYWORD_SETTING {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.EMPTY_SETTINGS;
        }

        @Override
        public String getProblemDescription() {
            return "The %s keyword setting is empty";
        }
    },
    INVALID_FOR_KEYWORD {

        @Override
        public String getProblemDescription() {
            return "%s";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            return newArrayList(ChangeToFixer.createFixers(newArrayList("IN", "IN RANGE", "IN ENUMERATE", "IN ZIP")));
        }
    },
    VARIABLE_AS_KEYWORD_NAME {

        @Override
        public String getProblemDescription() {
            return "Variable '%s' is given as a keyword name";
        }
    },
    INVALID_NESTED_EXECUTABLES_SYNTAX {

        @Override
        public String getProblemDescription() {
            return "Invalid nested executables syntax. %s";
        }
    },
    FOR_IN_EXPR_WRONGLY_TYPED {

        @Override
        public ProblemCategory getProblemCategory() {
            return ProblemCategory.DEPRECATED_API;
        }

        @Override
        public String getProblemDescription() {
            return "Using '%s' is deprecated. Use '%s' instead";
        }

        @Override
        public boolean hasResolution() {
            return true;
        }

        @Override
        public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
            final String canonical = marker.getAttribute(AdditionalMarkerAttributes.NAME, "");
            return newArrayList(new ChangeToFixer(canonical));
        }
    },
    FOR_IS_EMPTY {

        @Override
        public String getProblemDescription() {
            return "For loop contains no keywords";
        }
    };

    @Override
    public List<? extends IMarkerResolution> createFixers(final IMarker marker) {
        return new ArrayList<>();
    }

    @Override
    public boolean hasResolution() {
        return false;
    }

    @Override
    public ProblemCategory getProblemCategory() {
        return ProblemCategory.RUNTIME_ERROR;
    }

    @Override
    public String getEnumClassName() {
        return KeywordsProblem.class.getName();
    }
}
