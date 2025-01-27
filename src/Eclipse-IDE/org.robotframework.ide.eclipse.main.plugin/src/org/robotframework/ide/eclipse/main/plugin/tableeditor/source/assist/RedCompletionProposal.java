/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.source.assist;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.robotframework.red.jface.viewers.Stylers;

import com.google.common.base.Preconditions;

public class RedCompletionProposal implements Comparable<RedCompletionProposal>, ICompletionProposal,
        ICompletionProposalExtension3, ICompletionProposalExtension6 {

    /** The string to be displayed in the completion proposal popup. */
    private final String displayString;

    /** The replacement string. */
    private final String replacementString;

    /** The replacement offset. */
    private final int replacementOffset;

    /** The replacement length. */
    private final int replacementLength;

    private final int prefixLength;

    /** The cursor position after this proposal has been applied. */
    private final int cursorPosition;

    private final int selectionLength;

    /** The image to be displayed in the completion proposal popup. */
    private final Image image;

    /** The additional info of this proposal. */
    private final String additionalProposalInfo;

    private final boolean additionalInfoAsHtml;

    private final boolean decoratePrefix;

    private final boolean activateAssistant;

    private final Collection<Runnable> operationsAfterAccepting;

    /**
     * Creates a new completion proposal. All fields are initialized based on the provided
     * information.
     *
     * @param replacementString
     *            the actual string to be inserted into the document
     * @param replacementOffset
     *            the offset of the text to be replaced
     * @param replacementLength
     *            the length of the text to be replaced
     * @param cursorPosition
     *            the position of the cursor following the insert relative to replacementOffset
     * @param image
     *            the image to display for this proposal
     * @param displayString
     *            the string to be displayed for the proposal
     * @param contextInformation
     *            the context information associated with this proposal
     * @param additionalProposalInfo
     *            the additional information associated with this proposal
     * @param additionalInfoForStyledLabel
     *            the additional information visible as styled label part
     * @param prefixLength
     * @param decoratePrefix
     * @param activateAssistant
     * @param additionalInfoAsHtml
     */
    RedCompletionProposal(final String replacementString, final int replacementOffset, final int replacementLength,
            final int prefixLength, final int cursorPosition, final int selectionLength, final Image image,
            final boolean decoratePrefix, final String displayString, final boolean activateAssistant,
            final Collection<Runnable> operationsAfterAccepting, final String additionalProposalInfo,
            final boolean additionalInfoAsHtml) {
        Preconditions.checkNotNull(replacementString);
        Preconditions.checkState(replacementOffset >= 0);
        Preconditions.checkState(replacementLength >= 0);
        Preconditions.checkState(cursorPosition >= 0);

        this.replacementString = replacementString;
        this.replacementOffset = replacementOffset;
        this.replacementLength = replacementLength;
        this.prefixLength = prefixLength;
        this.decoratePrefix = decoratePrefix;
        this.cursorPosition = cursorPosition;
        this.selectionLength = selectionLength;
        this.image = image;
        this.displayString = displayString;
        this.activateAssistant = activateAssistant;
        this.operationsAfterAccepting = operationsAfterAccepting;
        this.additionalProposalInfo = additionalProposalInfo;
        this.additionalInfoAsHtml = additionalInfoAsHtml;
    }

    @Override
    public int compareTo(final RedCompletionProposal that) {
        return this.getDisplayString().compareTo(that.getDisplayString());
    }

    @Override
    public void apply(final IDocument document) {
        try {
            document.replace(replacementOffset, replacementLength, replacementString);
        } catch (final BadLocationException x) {
            // ignore
        }
    }

    @Override
    public Point getSelection(final IDocument document) {
        return new Point(replacementOffset + cursorPosition, selectionLength);
    }

    @Override
    public String getAdditionalProposalInfo() {
        return additionalProposalInfo;
    }

    @Override
    public String getDisplayString() {
        if (displayString != null) {
            return displayString;
        }
        return replacementString;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        return additionalInfoAsHtml ? null : new IInformationControlCreator() {

            @Override
            public IInformationControl createInformationControl(final Shell parent) {
                return new DefaultInformationControl(parent);
            }
        };
    }

    @Override
    public CharSequence getPrefixCompletionText(final IDocument document, final int completionOffset) {
        return replacementString;
    }

    @Override
    public int getPrefixCompletionStart(final IDocument document, final int completionOffset) {
        return replacementOffset;
    }

    @Override
    public StyledString getStyledDisplayString() {
        final StyledString styledString = new StyledString();
        final String toDisplay = getDisplayString();

        if (decoratePrefix) {
            final String alreadyWrittenPrefix = toDisplay.substring(0, prefixLength);
            final String suffixWhichWillBeAdded = toDisplay.substring(prefixLength);
            styledString.append(alreadyWrittenPrefix, Stylers.Common.MARKED_PREFIX_STYLER);
            styledString.append(suffixWhichWillBeAdded);
        } else {
            styledString.append(toDisplay);
        }
        return styledString;
    }

    public boolean shouldActivateAssistantAfterAccepting() {
        return activateAssistant;
    }

    public Collection<Runnable> operationsToPerformAfterAccepting() {
        return operationsAfterAccepting;
    }

}
