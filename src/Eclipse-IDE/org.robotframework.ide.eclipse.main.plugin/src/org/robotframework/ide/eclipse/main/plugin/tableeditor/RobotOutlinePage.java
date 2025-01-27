/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.robotframework.ide.eclipse.main.plugin.RedImages;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCodeHoldingElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotContainer;
import org.robotframework.ide.eclipse.main.plugin.model.RobotElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotFileInternalElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotFileInternalElement.DefinitionPosition;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSetting;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSetting.SettingsGroup;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;
import org.robotframework.ide.eclipse.main.plugin.navigator.ArtificialGroupingRobotElement;
import org.robotframework.ide.eclipse.main.plugin.navigator.NavigatorLabelProvider;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.source.SuiteSourceEditor;
import org.robotframework.red.jface.viewers.ViewerColumnsFactory;
import org.robotframework.red.viewers.Selections;
import org.robotframework.red.viewers.Viewers;

class RobotOutlinePage extends ContentOutlinePage {

    public static final String CONTEXT_ID = "org.robotframework.ide.eclipse.tableeditor.outline.context";

    private final RobotFormEditor editor;

    private final RobotSuiteFile suiteModel;

    private RobotOutlineContentProvider contentProvider;

    private ISelectionChangedListener outlineSelectionListener;

    private ISelectionChangedListener editorSelectionListener;

    private CaretListener caretListener;

    private final AtomicBoolean shouldUpdateEditorSelection = new AtomicBoolean(true);

    private Job modelQueryJob;

    RobotOutlinePage(final RobotFormEditor editor, final RobotSuiteFile suiteModel) {
        this.editor = editor;
        this.suiteModel = suiteModel;
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);

        contentProvider = new RobotOutlineContentProvider();
        getTreeViewer().setContentProvider(contentProvider);
        final NavigatorLabelProvider labelProvider = new NavigatorLabelProvider();
        ViewerColumnsFactory.newColumn("").withWidth(400).labelsProvidedBy(labelProvider).createFor(getTreeViewer());

        getTreeViewer().setInput(new Object[] { suiteModel });
        getTreeViewer().expandToLevel(3);

        final ISelectionProvider editorSelectionProvider = editor.getSite().getSelectionProvider();
        final StyledText editorSourceWidget = editor.getSourceEditor().getViewer().getTextWidget();

        editorSelectionListener = createEditorSelectionListener();
        editorSelectionProvider.addSelectionChangedListener(editorSelectionListener);

        outlineSelectionListener = createOutlineSelectionListener();
        getTreeViewer().addSelectionChangedListener(outlineSelectionListener);

        caretListener = createCaretListener();
        editorSourceWidget.addCaretListener(caretListener);

        getSite().getActionBars()
                .getToolBarManager()
                .add(new LinkWithEditorAction(editorSelectionProvider, editorSourceWidget));
        getSite().getActionBars().getToolBarManager().add(new SortOutlineAction(labelProvider));
        getSite().getActionBars().getToolBarManager().add(new ExpandAllAction());

        final MenuManager menuManager = new MenuManager("Outline popup", "RobotOutlinePage.popup");
        final Menu menu = menuManager.createContextMenu(getTreeViewer().getControl());
        getTreeViewer().getControl().setMenu(menu);
        getSite().registerContextMenu("RobotOutlinePage.popup", menuManager, getTreeViewer());
        Viewers.boundViewerWithContext(getTreeViewer(), getSite(), CONTEXT_ID);
    }

    private CaretListener createCaretListener() {
        return event -> {
            if (modelQueryJob != null && modelQueryJob.getState() == Job.SLEEPING) {
                modelQueryJob.cancel();
            }
            modelQueryJob = createModelQueryJob(event.display, event.caretOffset);
            modelQueryJob.schedule(300);
        };
    }

    private Job createModelQueryJob(final Display display, final int caretOffset) {
        final Job job = new Job("Looking for model element") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                if (display.isDisposed()) {
                    return Status.CANCEL_STATUS;
                }

                display.asyncExec(() -> {
                    final Optional<RobotFileInternalElement> selectableElement = suiteModel.findElement(caretOffset)
                            .filter(RobotFileInternalElement.class::isInstance)
                            .map(RobotFileInternalElement.class::cast)
                            .filter(e -> e != suiteModel);

                    shouldUpdateEditorSelection.set(false);

                    if (selectableElement.isPresent()) {
                        setSelectionInOutline(selectableElement.get());
                    } else {
                        clearSelectionInOutline();
                    }
                });

                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        return job;
    }

    private ISelectionChangedListener createEditorSelectionListener() {
        return event -> {
            if (event.getSelection() instanceof IStructuredSelection) {
                final Optional<RobotFileInternalElement> selectableElement = Selections
                        .getOptionalFirstElement((IStructuredSelection) event.getSelection(),
                                RobotFileInternalElement.class)
                        .filter(e -> e != suiteModel);

                shouldUpdateEditorSelection.set(false);

                if (selectableElement.isPresent()) {
                    setSelectionInOutline(selectableElement.get());
                } else {
                    clearSelectionInOutline();
                }
            }
        };
    }

    private ISelectionChangedListener createOutlineSelectionListener() {
        return event -> {
            final Optional<RobotFileInternalElement> element = Selections.getOptionalFirstElement(
                    (IStructuredSelection) event.getSelection(), RobotFileInternalElement.class);
            final Optional<RobotFileInternalElement> selectableElement = element.filter(e -> e != suiteModel);

            if (!shouldUpdateEditorSelection.getAndSet(true) || !element.isPresent()) {
                return;
            }

            if (selectableElement.isPresent()) {
                setSelectionInEditor(selectableElement.get());
            } else {
                clearSelectionInEditor();
            }
        };
    }

    private void clearSelectionInEditor() {
        if (editor.getActiveEditor() instanceof SuiteSourceEditor) {
            final ISelectionProvider selectionProvider = editor.getActiveEditor().getSite().getSelectionProvider();
            selectionProvider.setSelection(new TextSelection(0, 0));
        } else {
            final SelectionLayerAccessor accessor = editor.getSelectionLayerAccessor();
            if (accessor != null) {
                accessor.clear();
            }
        }
    }

    private void setSelectionInEditor(final RobotFileInternalElement element) {
        if (editor.getActiveEditor() instanceof SuiteSourceEditor) {
            final ISelectionProvider selectionProvider = editor.getActiveEditor().getSite().getSelectionProvider();
            final DefinitionPosition position = element.getDefinitionPosition();
            selectionProvider.setSelection(new TextSelection(position.getOffset(), position.getLength()));
        } else {
            final ISectionEditorPart activatedPage = editor.activatePage(element.getSection());
            if (activatedPage != null) {
                activatedPage.revealElement(element);
            }
        }
    }

    private void clearSelectionInOutline() {
        getTreeViewer().setSelection(StructuredSelection.EMPTY);
    }

    private void setSelectionInOutline(final RobotFileInternalElement element) {
        if (element instanceof RobotSetting && ((RobotSetting) element).getGroup() != SettingsGroup.NO_GROUP) {
            getTreeViewer().setSelection(createSelectionForGroupedSetting((RobotSetting) element));
        } else {
            getTreeViewer().setSelection(new StructuredSelection(new Object[] { element }));
        }
    }

    private ISelection createSelectionForGroupedSetting(final RobotSetting setting) {
        final List<Object> pathElements = newArrayList(setting);
        final ArtificialGroupingRobotElement groupingElement = contentProvider.getGroupingElement(setting.getGroup());
        if (groupingElement != null) {
            pathElements.add(groupingElement);
        }

        RobotElement current = setting.getParent();
        while (current != null && !(current instanceof RobotContainer)) {
            pathElements.add(current);
            current = current.getParent();
        }
        Collections.reverse(pathElements);
        return new TreeSelection(new TreePath(pathElements.toArray()));
    }

    @Override
    public void dispose() {
        editor.getSite().getSelectionProvider().removeSelectionChangedListener(editorSelectionListener);
        getTreeViewer().removeSelectionChangedListener(outlineSelectionListener);

        super.dispose();
    }

    private class ExpandAllAction extends Action {

        public ExpandAllAction() {
            super("Expand All", RedImages.getExpandAllImage());
        }

        @Override
        public void run() {
            getTreeViewer().expandAll();
        }
    }

    private class LinkWithEditorAction extends Action {

        private final ISelectionProvider editorSelectionProvider;

        private final StyledText editorSourceWidget;

        LinkWithEditorAction(final ISelectionProvider editorSelectionProvider, final StyledText editorSourceWidget) {
            super("Link with Editor", IAction.AS_CHECK_BOX);
            setImageDescriptor(RedImages.getSyncedImage());
            this.editorSelectionProvider = editorSelectionProvider;
            this.editorSourceWidget = editorSourceWidget;
            setChecked(true);
        }

        @Override
        public void run() {
            if (isChecked()) {
                editorSelectionProvider.addSelectionChangedListener(editorSelectionListener);
                editorSourceWidget.addCaretListener(caretListener);
            } else {
                editorSelectionProvider.removeSelectionChangedListener(editorSelectionListener);
                editorSourceWidget.removeCaretListener(caretListener);
            }
        }
    }

    private class SortOutlineAction extends Action {

        private final NavigatorLabelProvider labelProvider;

        SortOutlineAction(final NavigatorLabelProvider labelProvider) {
            super("Sort", IAction.AS_CHECK_BOX);
            setImageDescriptor(RedImages.getSortImage());
            this.labelProvider = labelProvider;
        }

        @Override
        public void run() {
            if (isChecked()) {
                getTreeViewer().setComparator(new ViewerComparator() {

                    @Override
                    public int compare(final Viewer viewer, final Object e1, final Object e2) {

                        if (((RobotElement) e1).getParent() instanceof RobotCodeHoldingElement
                                && ((RobotElement) e2).getParent() instanceof RobotCodeHoldingElement) {
                            // we don't want to sort the code inside keywords/test cases

                            final RobotElement el1 = (RobotElement) e1;
                            final RobotElement el2 = (RobotElement) e2;

                            final int index1 = el1.getParent().getChildren().indexOf(el1);
                            final int index2 = el2.getParent().getChildren().indexOf(el2);

                            return index1 - index2;
                        } else {
                            return labelProvider.getText(e1).compareToIgnoreCase(labelProvider.getText(e2));
                        }
                    }
                });
            } else {
                getTreeViewer().setComparator(null);
            }
        }
    }
}
