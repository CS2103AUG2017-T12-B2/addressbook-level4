package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.email.Email;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.storage.AddressBookStorage;

public class MergeCommand extends Command {

    public static final String COMMAND_WORD = "merge";
    
    public static String MESSAGE_USAGE = COMMAND_WORD + ": merge the file given with the default storage file\n"
            + "Parameters: "
            + "XML_FILE_PATH\n"
            + "Example: " + COMMAND_WORD + " ./data/newFile.xml";
    public static final String MESSAGE_SUCCESS = "File merged successfully.";
    
    private final String newFilePath;
    
    public MergeCommand(String newFilePath) {
        this.newFilePath = newFilePath;
    }
    
    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(addressBookStorage);
        try {
            addressBookStorage.mergeAddressBook(newFilePath);
        } catch (DataConversionException|IOException ex){
            throw new CommandException(ex.getMessage());
        }
        
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public void setData(Model model, CommandHistory commandHistory, UndoRedoStack undoRedoStack, Email emailManager, AddressBookStorage addressBookStorage) {
        this.model = model;
        this.addressBookStorage = addressBookStorage;
    }

}
