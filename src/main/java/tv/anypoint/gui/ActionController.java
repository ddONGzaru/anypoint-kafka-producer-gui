package tv.anypoint.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tv.anypoint.kafka.producer.KafkaMessageProducer;

@Slf4j
@Component
public class ActionController {

    @FXML
    private TextField page;

    @FXML
    private ChoiceBox size;

    @FXML
    private CheckBox isTruncateTable;

    @FXML
    private TextArea console;

    @FXML
    private Button execBtn;

    @Autowired
    private KafkaMessageProducer producer;

    @FXML
    protected void handleExecuteButtonAction(ActionEvent e) {

        int pageParam = Integer.valueOf(page.getText());

        int sizeParam = Integer.valueOf(size.getSelectionModel().getSelectedItem().toString().replaceAll(",", ""));
        sizeParam = sizeParam / 10;

        boolean enableTruncateTableJob = isTruncateTable.isSelected();

        console.setText("");

        //console.getParent().getScene().setCursor(Cursor.WAIT);

        //Platform.runLater(() -> console.getParent().getScene().setCursor(Cursor.WAIT));

        producer.process(console, pageParam, sizeParam, enableTruncateTableJob);

        //Platform.runLater(() -> console.getParent().getScene().setCursor(Cursor.DEFAULT));

        //console.getParent().getScene().setCursor(Cursor.DEFAULT);
    }

    public void handleMouseEnteredAction() {
        execBtn.setCursor(Cursor.HAND);
    }

    public void handleMouseExitAction() {
        execBtn.setCursor(Cursor.DEFAULT);
    }
}
