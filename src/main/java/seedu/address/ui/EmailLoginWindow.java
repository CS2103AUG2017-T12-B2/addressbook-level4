package seedu.address.ui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.email.Email;
import seedu.address.email.exceptions.LoginFailedException;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.UserPrefs;

import java.util.logging.Logger;

public class EmailLoginWindow extends UiPart<Region> {
    private final Logger logger = LogsCenter.getLogger(EmailLoginWindow.class);
    private static final String FXML = "EmailLoginWindow.fxml";

    private Stage primaryStage;
    private UserPrefs prefs;
    private Button loginButton;
    private Logic logic;

    @FXML
    private Text loginText;

    @FXML
    private Text passwordText;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button emailWindowLoginButton;

    @FXML
    private Label feedbackLabel;

    public EmailLoginWindow(Button loginButton, Logic logic) {
        super(FXML);

        this.primaryStage = new Stage();
        Scene scene = new Scene(getRoot());
        this.primaryStage.setScene(scene);
        this.loginButton = loginButton;
        this.logic = logic;

        setOnCloseEvent();
    }

    /**
     * When the login button is pressed
     */
    @FXML
    private void onLoginButtonPressed() {
        String emailString = emailField.getText();
        String passwordString = passwordField.getText();

        try {
            CommandResult commandResult = logic.execute("email_login "
                                    + "\"" + emailString + "\"" + " "
                                    + "\"" + passwordString + "\"");
            logger.info("Result: " + commandResult.feedbackToUser);
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser));
            //primaryStage.close();
        } catch (CommandException e) {
            feedbackLabel.setText(e.getMessage());
        } catch (ParseException e) {
            feedbackLabel.setText(e.getMessage());
        }
    }

    /**
     * Enable the login / logout button when this window closes
     */
    public void setOnCloseEvent() {
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                loginButton.setDisable(false);
            }
        });
    }

    public void show() {
        primaryStage.show();
    }
}
