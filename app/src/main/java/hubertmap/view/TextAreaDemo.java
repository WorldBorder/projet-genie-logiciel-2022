package hubertmap.view;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * TextAreaDemo.java requires no other files.
 */

import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/** A class that creates a text area with auto-complete functionality based on a list of words. */
public class TextAreaDemo extends JPanel implements DocumentListener {

    /** the text area where the user types */
    private JTextField textArea;

    /** the action command for committing a word */
    private static final String COMMIT_ACTION = "commit";

    /** an enum for the insert and completion modes */
    private static enum Mode {
        INSERT,
        COMPLETION
    };

    /** a list of words to use for autocomplete */
    private List<String> words;

    /** the current mode */
    private Mode mode = Mode.INSERT;

    /**
     * Constructs a TextAreaDemo object with a list of words for auto-completion.
     *
     * @param data A list of words for auto-completion
     */
    public TextAreaDemo(List<String> data) {

        initComponents();

        textArea.getDocument().addDocumentListener(this);

        InputMap im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
        im.put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
        am.put(COMMIT_ACTION, new CommitAction());

        words = data;

        textArea.addFocusListener(
                new FocusListener() {
                    public void focusGained(FocusEvent e) {
                        if (textArea.getText().contains("departure")
                                || textArea.getText().contains("arrival")) textArea.setText("");
                    }

                    public void focusLost(FocusEvent e) {}
                });
    }

    /** Initializes the parameters of the textArea. */
    private void initComponents() {
        textArea = new JTextField();
        textArea.setColumns(20);
        this.add(textArea);
    }

    // Listener methods
    public void changedUpdate(DocumentEvent ev) {}

    public void removeUpdate(DocumentEvent ev) {}

    /** {@inheritDoc} */
    public void insertUpdate(DocumentEvent ev) {
        if (ev.getLength() != 1) {
            return;
        }

        int pos = ev.getOffset();
        String content = null;
        try {
            content = textArea.getText(0, pos + 1);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Find where the word starts
        int w = -1;

        if (pos - w < 2) {
            // Too few chars
            return;
        }

        String prefix = content.substring(w + 1).toLowerCase();
        int n = Collections.binarySearch(words, prefix);
        if (n < 0 && -n <= words.size()) {
            String match = words.get(-n - 1);
            if (match.startsWith(prefix)) {
                // A completion is found
                String completion = match.substring(pos - w);
                // We cannot modify Document from within notification,
                // so we submit a task that does the change later
                SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
            }
        } else {
            // Nothing found
            mode = Mode.INSERT;
        }
    }

    /** A runnable task for completing text in the text area. */
    private class CompletionTask implements Runnable {
        String completion;
        int position;

        /**
         * Constructs a CompletionTask object.
         *
         * @param completion The text to be completed
         * @param position The position
         */
        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }

        /** Runs the task by inserting the completed text into the JTextArea */
        public void run() {
            textArea.setText(textArea.getText() + completion);
            textArea.setCaretPosition(position + completion.length());
            textArea.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }

    /**
     * This class represents an action that will be executed when the "commit" action occurs in the
     * JTextArea component of TextAreaDemo.
     */
    private class CommitAction extends AbstractAction {
        /**
         * Performs the action by either inserting a space character if the current mode is
         * COMPLETION, or a new line character if the current mode is INSERT.
         *
         * @param ev The action event.
         */
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = textArea.getSelectionEnd();
                textArea.setText(textArea.getText() + " ");
                textArea.setCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
                textArea.replaceSelection("\n");
            }
        }
    }

    /**
     * Returns the text content of the JTextArea component of TextAreaDemo.
     *
     * @return The text content of the JTextArea component.
     */
    public String getValue() {
        return textArea.getText().trim().toLowerCase();
    }
    /**
     * Set the Text using the string parameter
     *
     * @param string the string used to set the text
     */
    public void setText(String string) {
        textArea.setText(string);
    }

    /**
     * Updates words with new data
     *
     * @param data the list of strings used to set in words
     */
    public void setData(List<String> data) {
        words = data;
    }
}
