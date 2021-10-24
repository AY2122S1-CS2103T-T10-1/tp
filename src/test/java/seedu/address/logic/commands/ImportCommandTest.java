package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FILENAME_JSON;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ImportCommandTest {
    private static final String PATH_EMPTY_FOLDER = "src/test/data/ExportImportCommandTest/EmptyFolder/";
    private static final String PATH_FOLDER_WITH_JSON = "src/test/data/ExportImportCommandTest/FolderWithJson/";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), null);
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs(), null);

    @Test
    public void equals() {
        ImportCommand importFirstCommand = new ImportCommand("first");
        ImportCommand importSecondCommand = new ImportCommand("second");

        // same object -> returns true
        assertTrue(importFirstCommand.equals(importFirstCommand));

        // same filename -> returns true
        assertTrue(importFirstCommand.equals(new ImportCommand("first")));

        // different types -> returns false
        assertFalse(importFirstCommand.equals(1));

        // null -> returns false
        assertFalse(importFirstCommand.equals(null));

        // different filename -> returns false
        assertFalse(importFirstCommand.equals(importSecondCommand));

    }

    /**
     * Imports the JSON file with the filename provided.
     * Test occurs in src/test/data/ExportImportCommandTest/FolderWithJson.
     */
    @Test
    public void execute_fileName_importSuccess() {
        Person newPerson = new PersonBuilder().build();
        expectedModel.addPerson(newPerson);
        ImportCommand importNewPerson = new ImportCommand(PATH_FOLDER_WITH_JSON, VALID_FILENAME_JSON);
        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_SUCCESS, VALID_FILENAME_JSON);
        assertCommandSuccess(importNewPerson, model, expectedMessage, expectedModel);
    }

    /**
     * Import command fails when there is no file with the provided file name.
     * Test occurs in src/test/data/ExportImportCommandTest/EmptyFolder.
     */
    @Test
    public void execute_fileName_fileMissing() {
        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_FILE_NOT_FOUND, VALID_FILENAME_JSON);
        ImportCommand importNewPerson = new ImportCommand(PATH_EMPTY_FOLDER, VALID_FILENAME_JSON);
        assertCommandFailure(importNewPerson, model, expectedMessage);
    }
}

