package seedu.address.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

import java.util.logging.Logger;

public class EmailSendWindow extends UiPart<Region> {
    private static final String FXML = "EmailSendWindow.fxml";
    private final Logger logger = LogsCenter.getLogger(EmailSendWindow.class);

    private Stage primaryStage;
    private Button sendButtonMainWindow;
    private Logic logic;

    @FXML
    private Label recipientsLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label bodyLabel;

    @FXML
    private TextField recipientsField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField bodyField;

    @FXML
    private Button sendButton;

    @FXML
    private Label feedbackLabel;

    public EmailSendWindow(Button sendButtonMainWindow, Logic logic) {
        super(FXML);

        this.primaryStage = new Stage();
        Scene scene = new Scene(getRoot());
        this.primaryStage.setScene(scene);
        this.sendButtonMainWindow = sendButtonMainWindow;
        this.logic = logic;

        setOnCloseEvent();
    }

    /**
     * Enable the login / logout button when this window closes
     */
    public void setOnCloseEvent() {
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                sendButtonMainWindow.setDisable(false);
            }
        });
    }

    /**
     * Show the window
     */
    public void show() {
        primaryStage.show();
    }

    @FXML
    private void onSendButtonClicked() {
        String recipients = recipientsField.getText();
        String title = titleField.getText();
        String body = bodyField.getText();

        try {
            CommandResult commandResult = logic.execute("email_send "
                                        + "\"" + recipients + "\"" + " "
                                        + "\"" + title + "\"" + " "
                                        + "\"" + body + "\"");
            logger.info("Result: " + commandResult.feedbackToUser);
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser));
            feedbackLabel.setText(commandResult.feedbackToUser);
        } catch (ParseException e) {
            feedbackLabel.setText(e.getMessage());
        } catch (CommandException e) {
            feedbackLabel.setText(e.getMessage());
        }
    }
}