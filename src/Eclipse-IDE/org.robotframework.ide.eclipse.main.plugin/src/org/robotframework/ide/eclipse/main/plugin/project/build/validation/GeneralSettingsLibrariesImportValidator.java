/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project.build.validation;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rf.ide.core.libraries.ArgumentsDescriptor;
import org.rf.ide.core.libraries.LibraryDescriptor;
import org.rf.ide.core.libraries.LibrarySpecification;
import org.rf.ide.core.project.RobotProjectConfig;
import org.rf.ide.core.project.RobotProjectConfig.RemoteLocation;
import org.rf.ide.core.testdata.model.table.setting.LibraryImport;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.validation.RobotTimeFormat;
import org.robotframework.ide.eclipse.main.plugin.RedWorkspace;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;
import org.robotframework.ide.eclipse.main.plugin.project.build.AdditionalMarkerAttributes;
import org.robotframework.ide.eclipse.main.plugin.project.build.RobotProblem;
import org.robotframework.ide.eclipse.main.plugin.project.build.ValidationReportingStrategy;
import org.robotframework.ide.eclipse.main.plugin.project.build.causes.ArgumentProblem;
import org.robotframework.ide.eclipse.main.plugin.project.build.causes.GeneralSettingsProblem;
import org.robotframework.ide.eclipse.main.plugin.project.build.causes.IProblemCause;
import org.robotframework.ide.eclipse.main.plugin.project.build.libs.RemoteArgumentsResolver;

import com.google.common.collect.ImmutableMap;

public class GeneralSettingsLibrariesImportValidator extends GeneralSettingsImportsValidator {

    public GeneralSettingsLibrariesImportValidator(final FileValidationContext validationContext,
            final RobotSuiteFile suiteFile, final List<LibraryImport> imports,
            final ValidationReportingStrategy reporter) {
        super(validationContext, suiteFile, imports, reporter);
    }

    @Override
    protected IProblemCause getCauseForMissingImportArguments() {
        return GeneralSettingsProblem.MISSING_LIBRARY_NAME;
    }

    @Override
    protected IProblemCause getCauseForNonExistingImport() {
        return GeneralSettingsProblem.NON_EXISTING_LIBRARY_IMPORT;
    }

    @Override
    protected boolean isPathImport(final String pathOrName) {
        return new Path(pathOrName).isAbsolute() || pathOrName.endsWith("/") || pathOrName.endsWith(".py")
                || pathOrName.endsWith(".class") || pathOrName.endsWith(".java");
    }

    @Override
    protected void validateResource(final IResource resource, final String path, final RobotToken pathToken,
            final List<RobotToken> arguments) {
        final IPath candidate = resource.getLocation();

        final LibrarySpecification spec = findSpecification(candidate);
        validateWithSpec(spec, path, pathToken, arguments, true);
    }

    @Override
    protected void validateFile(final File file, final String path, final RobotToken pathToken,
            final List<RobotToken> arguments) {
        final IPath candidate = new Path(file.getAbsolutePath());

        final LibrarySpecification spec = findSpecification(candidate);
        validateWithSpec(spec, path, pathToken, arguments, true);
    }

    private LibrarySpecification findSpecification(final IPath candidate) {
        final Map<LibraryDescriptor, LibrarySpecification> libs = validationContext
                .getReferencedLibrarySpecifications();
        for (final LibraryDescriptor descriptor : libs.keySet()) {
            final IPath entryPath = new Path(descriptor.getPath());
            IPath libPath = RedWorkspace.Paths.toAbsoluteFromWorkspaceRelativeIfPossible(entryPath);
            if (candidate.equals(libPath)) {
                return libs.get(descriptor);
            }
            if ("__init__.py".equals(entryPath.lastSegment())) {
                libPath = RedWorkspace.Paths.toAbsoluteFromWorkspaceRelativeIfPossible(entryPath.removeLastSegments(1));
                if (candidate.equals(libPath)) {
                    return libs.get(descriptor);
                }
            }
        }
        return null;
    }

    @Override
    protected void validateNameImport(final String name, final RobotToken nameToken, final List<RobotToken> arguments) {
        if (name.contains(" ")) {
            final RobotProblem problem = RobotProblem.causedBy(GeneralSettingsProblem.LIBRARY_NAME_WITH_SPACES)
                    .formatMessageWith(name, name.replaceAll("\\s", ""));
            final Map<String, Object> additional = ImmutableMap.of(AdditionalMarkerAttributes.NAME, name);
            reporter.handleProblem(problem, validationContext.getFile(), nameToken, additional);
        } else if (name.equals("Remote")) {
            final RemoteArgumentsResolver resolver = new RemoteArgumentsResolver(arguments);
            final Optional<String> address = resolver.getUri();
            if (address.isPresent()) {
                final LibrarySpecification specification = validationContext.getLibrarySpecifications(name,
                        address.get());
                validateWithSpec(specification, name, nameToken, arguments, false);
            } else {
                reportProblemOnRemoteLibraryArguments(nameToken, arguments);
            }
        } else {
            final List<String> args = arguments.stream().map(RobotToken::getText).collect(toList());
            final LibrarySpecification specification = validationContext.getLibrarySpecifications(name, args);
            validateWithSpec(specification, name, nameToken, arguments, false);
        }
    }

    private void validateWithSpec(final LibrarySpecification specification, final String pathOrName,
            final RobotToken pathOrNameToken, final List<RobotToken> importArguments, final boolean isPath) {
        if (specification != null) {
            final ArgumentsDescriptor descriptor = specification.getConstructor() == null
                    ? ArgumentsDescriptor.createDescriptor()
                    : specification.getConstructor().createArgumentsDescriptor();
            new KeywordCallArgumentsValidator(validationContext, pathOrNameToken, reporter, descriptor,
                    importArguments).validate(new NullProgressMonitor());
        } else {
            if (pathOrName.equals("Remote")) {
                reportProblemOnRemoteLibraryArguments(pathOrNameToken, importArguments);
            } else {
                final RobotProblem problem = RobotProblem.causedBy(GeneralSettingsProblem.NON_EXISTING_LIBRARY_IMPORT)
                        .formatMessageWith(pathOrName);
                final Map<String, Object> additional = isPath
                        ? ImmutableMap.of(AdditionalMarkerAttributes.PATH, pathOrName)
                        : ImmutableMap.of(AdditionalMarkerAttributes.NAME, pathOrName);
                reporter.handleProblem(problem, validationContext.getFile(), pathOrNameToken, additional);
            }
        }
    }

    private void reportProblemOnRemoteLibraryArguments(final RobotToken nameToken, final List<RobotToken> arguments) {
        final RemoteArgumentsResolver resolver = new RemoteArgumentsResolver(arguments);

        final Optional<String> address = resolver.getUri();
        if (address.isPresent()) {
            final RobotToken uriOrNameToken = resolver.getUriToken().orElse(nameToken);
            reportProblemOnRemoteLocation(uriOrNameToken, address.get());
        } else {
            new KeywordCallArgumentsValidator(validationContext, nameToken, reporter,
                    resolver.getDescriptor(), arguments).validate(new NullProgressMonitor());
        }

        final Optional<String> timeout = resolver.getTimeout();
        if (timeout.isPresent() && !RobotTimeFormat.isValidRobotTimeArgument(timeout.get())) {
            final RobotToken timeoutToken = resolver.getTimeoutToken().get();
            final RobotProblem problem = RobotProblem.causedBy(ArgumentProblem.INVALID_TIME_FORMAT)
                    .formatMessageWith(timeout.get());
            reporter.handleProblem(problem, validationContext.getFile(), timeoutToken);
        }
    }

    private void reportProblemOnRemoteLocation(final RobotToken markerToken, final String address) {
        try {
            final String uriScheme = URI.create(address.toLowerCase()).getScheme();
            if (!uriScheme.equals("http") && !uriScheme.equals("https")) {
                final RobotProblem problem = RobotProblem
                        .causedBy(GeneralSettingsProblem.NOT_SUPPORTED_URI_PROTOCOL_IN_REMOTE_LIBRARY_IMPORT)
                        .formatMessageWith(uriScheme);
                reporter.handleProblem(problem, validationContext.getFile(), markerToken);
                return;
            }
        } catch (final IllegalArgumentException e) {
            final RobotProblem problem = RobotProblem
                    .causedBy(GeneralSettingsProblem.INVALID_URI_IN_REMOTE_LIBRARY_IMPORT)
                    .formatMessageWith(e.getCause().getMessage());
            reporter.handleProblem(problem, validationContext.getFile(), markerToken);
            return;
        }

        if (isInRemoteLocations(address)) {
            final RobotProblem problem = RobotProblem
                    .causedBy(GeneralSettingsProblem.NON_REACHABLE_REMOTE_LIBRARY_IMPORT)
                    .formatMessageWith(address);
            reporter.handleProblem(problem, validationContext.getFile(), markerToken);
        } else {
            final RobotProblem problem = RobotProblem
                    .causedBy(GeneralSettingsProblem.NON_EXISTING_REMOTE_LIBRARY_IMPORT)
                    .formatMessageWith(address);
            final Map<String, Object> additional = ImmutableMap.of(AdditionalMarkerAttributes.PATH, address);
            reporter.handleProblem(problem, validationContext.getFile(), markerToken, additional);
        }
    }

    private boolean isInRemoteLocations(final String address) {
        final RobotProjectConfig robotProjectConfig = validationContext.getProjectConfiguration();
        final List<RemoteLocation> remoteLocations = robotProjectConfig.getRemoteLocations();

        final String strippedAddress = RemoteArgumentsResolver.stripLastSlashAndProtocolIfNecessary(address);

        return remoteLocations.stream()
                .anyMatch(location -> strippedAddress
                        .equals(RemoteArgumentsResolver.stripLastSlashAndProtocolIfNecessary(location.getUri())));
    }
}
