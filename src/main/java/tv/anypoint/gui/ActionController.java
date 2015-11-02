package tv.anypoint.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tv.anypoint.kafka.producer.KafkaMessageProducer;

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

    @Autowired
    private KafkaMessageProducer producer;

    @FXML
    protected void handleExecuteButtonAction(ActionEvent e) {

        int pageParam = Integer.valueOf(page.getText());

        int sizeParam = Integer.valueOf(size.getSelectionModel().getSelectedItem().toString().replaceAll(",", ""));

        boolean enableTruncateTableJob = isTruncateTable.isSelected();

        console.setText("");

        /*if (producer == null) {
            producer = new KafkaMessageProducer();
        }*/

        producer.process(console, pageParam, sizeParam, enableTruncateTableJob);
    }
}
