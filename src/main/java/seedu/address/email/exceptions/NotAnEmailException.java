//@@author phungtuanhoang1996
package seedu.address.email.exceptions;

/**
 * Exception when the given string is not a valid email
 */
public class NotAnEmailException extends Exception {
    public NotAnEmailException() {
        super("One or more of the given emails is not valid");
    }
}
//@@author phungtuanhoang1996
