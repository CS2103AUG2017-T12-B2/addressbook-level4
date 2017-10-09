package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddMultipleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddMultipleCommand object
 */
public class AddMultipleCommandParser implements Parser<AddMultipleCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddMultipleCommand
     * then parse data from file name given {@param args} if it exists
     * and returns an AddMultipleCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddMultipleCommand parse(String args) throws ParseException {
        String fileName = args.trim();
        if (fileName.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMultipleCommand.MESSAGE_USAGE));
        }

        ArrayList<ReadOnlyPerson> personsList = new ArrayList<>();
        File fileToRead = new File(AddMultipleCommand.DEFAULT_FOLDER_PATH, fileName);
        String line;
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileToRead);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                String toAdd = " " + line;
                ArgumentMultimap argMultimap =
                        ArgumentTokenizer.tokenize(toAdd, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);
                if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)) {
                    throw new ParseException(String.format(MESSAGE_INVALID_PERSON_FORMAT, AddMultipleCommand.MESSAGE_PERSON_FORMAT));
                }
                try {
                    Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
                    Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).get();
                    Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).get();
                    Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).get();
                    Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
                    ReadOnlyPerson person = new Person(name, phone, email, address, tagList);
                    
                    personsList.add(person);
                } catch (IllegalValueException ive) {
                    throw new ParseException(ive.getMessage(), ive);
                }
            }
            
            bufferedReader.close();
        } catch (FileNotFoundException fnfe) {
            throw new ParseException("Unable to open file '" + fileName + "'");
        } catch (IOException ioe) {
            throw new ParseException("Error reading file '" + fileName + "'");
        }

        return new AddMultipleCommand(personsList);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
