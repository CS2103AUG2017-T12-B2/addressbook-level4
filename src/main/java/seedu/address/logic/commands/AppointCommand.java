package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINT;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.Appoint;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Changes the appoint of an existing person in the address book.
 */
public class AppointCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "appoint";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the appoint of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing appoint will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_APPOINT + "[APPOINT]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_APPOINT + "20/10/2017 14:30";

    public static final String MESSAGE_ADD_APPOINT_SUCCESS = "Added appoint to Person: %1$s";
    public static final String MESSAGE_DELETE_APPOINT_SUCCESS = "Removed appoint from Person: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final Appoint appoint;

    /**
     * @param index of the person in the filtered person list to edit the appoint
     * @param appoint of the person
     */
    public AppointCommand(Index index, Appoint appoint) {
        requireNonNull(index);
        requireNonNull(appoint);

        this.index = index;
        this.appoint = appoint;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getComment(), appoint, personToEdit.getTags());

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredListToShowAll();

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    private String generateSuccessMessage(ReadOnlyPerson personToEdit) {
        if (!appoint.value.isEmpty()) {
            return String.format(MESSAGE_ADD_APPOINT_SUCCESS, personToEdit);
        } else {
            return String.format(MESSAGE_DELETE_APPOINT_SUCCESS, personToEdit);
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointCommand)) {
            return false;
        }

        // state check
        AppointCommand e = (AppointCommand) other;
        return index.equals(e.index)
                && appoint.equals(e.appoint);
    }
}