package com.example.lab4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class NoteTest {

    @Test
    public void testNoteCreationWithParameters() {
        String expectedName = "Test Note";
        String expectedContent = "Test Content";

        Note note = new Note(expectedName, expectedContent);

        assertNotNull(note);
        assertEquals("Note name should match", expectedName, note.getName());
        assertEquals("Note content should match", expectedContent, note.getContent());
        assertNotNull("Note ID should not be null", note.getId());
        assertTrue("Timestamp should be positive", note.getTimestamp() > 0);

        assertFalse("Note ID should not be empty", note.getId().isEmpty());
        assertNotEquals("Name and content should be different", note.getName(), note.getContent());
    }
}