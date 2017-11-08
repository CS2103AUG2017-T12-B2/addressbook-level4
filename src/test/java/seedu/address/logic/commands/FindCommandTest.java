package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.ui.GuiUnitTest;

//@@author KhorSL

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest extends GuiUnitTest {
    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void equals() {
        HashMap<String, List<String>> firstPredicateMap = new HashMap<>();
        HashMap<String, List<String>> secondPredicateMap = new HashMap<>();
        firstPredicateMap.put("first", Collections.singletonList("first"));
        secondPredicateMap.put("second", Collections.singletonList("second"));

        PersonContainsKeywordsPredicate firstPredicate =
                new PersonContainsKeywordsPredicate(firstPredicateMap);
        PersonContainsKeywordsPredicate secondPredicate =
                new PersonContainsKeywordsPredicate(secondPredicateMap);

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() throws ParseException {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        FindCommand command = prepareCommand(" ");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());
    }

    @Test
    public void execute_singleKeyword() throws ParseException {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        FindCommand command = prepareCommand(" n/Kurz");
        assertCommandSuccess(command, expectedMessage, Collections.singletonList(CARL));

        expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        command = prepareCommand(" e/lydia");
        assertCommandSuccess(command, expectedMessage, Collections.singletonList(FIONA));

        expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        command = prepareCommand(" p/9482");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(ELLE, FIONA, GEORGE));

        expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        command = prepareCommand(" ap/12:12");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());

        expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        command = prepareCommand(" a/wall");
        assertCommandSuccess(command, expectedMessage, Collections.singletonList(CARL));

        expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        command = prepareCommand(" c/tetris");
        assertCommandSuccess(command, expectedMessage, Collections.singletonList(GEORGE));

        expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        command = prepareCommand(" r/friend");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() throws ParseException {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        FindCommand command = prepareCommand(" n/Kurz Elle Kunz r/dummy e/@dummy.com");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, ELLE, FIONA));

        command = prepareCommand(" e/lydia a/wall p/224");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, ELLE, FIONA));

        command = prepareCommand(" e/lydia werner a/tokyo wall c/swim ap/10:30");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, ELLE, FIONA));
    }

    /**
     * Parses {@code userInput} into a {@code FindCommand}.
     */
    private FindCommand prepareCommand(String userInput) throws ParseException {
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(userInput, PREFIX_NAME, PREFIX_TAG, PREFIX_EMAIL,
                PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_COMMENT, PREFIX_APPOINT);

        String trimmedArgsName;
        String trimmedArgsTag;
        String trimmedArgsEmail;
        String trimmedArgsPhone;
        String trimmedArgsAddress;
        String trimmedArgsComment;
        String trimmedArgsAppoint;

        String[] keywordNameList;
        String[] keywordTagList;
        String[] keywordEmailList;
        String[] keywordPhoneList;
        String[] keywordAddressList;
        String[] keywordCommentList;
        String[] keywordAppointList;

        HashMap<String, List<String>> mapKeywords = new HashMap<>();

        try {
            if (argumentMultimap.getValue(PREFIX_NAME).isPresent()) {
                trimmedArgsName = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_NAME)).get().trim();
                if (trimmedArgsName.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordNameList = trimmedArgsName.split("\\s+");
                mapKeywords.put(PREFIX_NAME.toString(), Arrays.asList(keywordNameList));
            }

            if (argumentMultimap.getValue(PREFIX_TAG).isPresent()) {
                trimmedArgsTag = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_TAG)).get().trim();
                if (trimmedArgsTag.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordTagList = trimmedArgsTag.split("\\s+");
                mapKeywords.put(PREFIX_TAG.toString(), Arrays.asList(keywordTagList));
            }

            if (argumentMultimap.getValue(PREFIX_EMAIL).isPresent()) {
                trimmedArgsEmail = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_EMAIL)).get().trim();
                if (trimmedArgsEmail.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordEmailList = trimmedArgsEmail.split("\\s+");
                mapKeywords.put(PREFIX_EMAIL.toString(), Arrays.asList(keywordEmailList));
            }

            if (argumentMultimap.getValue(PREFIX_PHONE).isPresent()) {
                trimmedArgsPhone = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_PHONE)).get().trim();
                if (trimmedArgsPhone.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordPhoneList = trimmedArgsPhone.split("\\s+");
                mapKeywords.put(PREFIX_PHONE.toString(), Arrays.asList(keywordPhoneList));
            }

            if (argumentMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
                trimmedArgsAddress = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_ADDRESS)).get().trim();
                if (trimmedArgsAddress.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordAddressList = trimmedArgsAddress.split("\\s+");
                mapKeywords.put(PREFIX_ADDRESS.toString(), Arrays.asList(keywordAddressList));
            }

            if (argumentMultimap.getValue(PREFIX_COMMENT).isPresent()) {
                trimmedArgsComment = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_COMMENT)).get().trim();
                if (trimmedArgsComment.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordCommentList = trimmedArgsComment.split("\\s+");
                mapKeywords.put(PREFIX_COMMENT.toString(), Arrays.asList(keywordCommentList));
            }

            if (argumentMultimap.getValue(PREFIX_APPOINT).isPresent()) {
                trimmedArgsAppoint = ParserUtil.parseKeywords(argumentMultimap.getValue(PREFIX_APPOINT)).get().trim();
                if (trimmedArgsAppoint.isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                keywordAppointList = trimmedArgsAppoint.split("\\s+");
                mapKeywords.put(PREFIX_APPOINT.toString(), Arrays.asList(keywordAppointList));
            }

        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        FindCommand command =
                new FindCommand(new PersonContainsKeywordsPredicate(mapKeywords));
        command.setData(model, new CommandHistory(), new UndoRedoStack(), null);
        return command;
    }
    //@@author

    /**
     * Asserts that {@code command} is successfully executed, and<br>
     * - the command feedback is equal to {@code expectedMessage}<br>
     * - the {@code FilteredList<ReadOnlyPerson>} is equal to {@code expectedList}<br>
     * - the {@code AddressBook} in model remains the same after executing the {@code command}
     */
    private void assertCommandSuccess(FindCommand command, String expectedMessage, List<ReadOnlyPerson> expectedList) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute();

        assertEquals(expectedMessage, commandResult.feedbackToUser);
        assertEquals(expectedList, model.getFilteredPersonList());
        assertEquals(expectedAddressBook, model.getAddressBook());
    }
}
