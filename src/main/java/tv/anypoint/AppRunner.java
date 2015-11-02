package tv.anypoint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationContext;
import tv.anypoint.gui.ActionController;
import tv.anypoint.utils.FileUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

@SpringBootApplication
public class AppRunner extends Application {

    private static ApplicationContext context;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = findRoot();

        Scene scene = new Scene(root, 600, 450);
        stage.setScene(scene);
        stage.setTitle("Anypoint Log Connector ver-1.0.0");
        stage.show();

    }

    private Parent findRoot() {

        ActionController controller = context.getBean(ActionController.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/application.fxml"));

        loader.setResources(ResourceBundle.getBundle("lang", new Locale("en", "EN")));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            public Object call(Class<?> aClass) {
                return controller;
            }
        });

        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return root;
    }


    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(AppRunner.class);

        app.addListeners(getConfigFileApplicationListener(args));
        context = app.run(args);

        launch(args);
    }

    private static ConfigFileApplicationListener getConfigFileApplicationListener(String[] args) {

        String configFilePath;

        String userDir = System.getProperty("user.dir");

        String configFile = "/build/classes/main/application.yml";

        if (FileUtils.existsFile(userDir + configFile)) {
            configFilePath = userDir + configFile;
        } else {
            configFilePath = userDir + "/anypoint-kafka-producer.conf";
        }

        ConfigFileApplicationListener listener = new ConfigFileApplicationListener();

        listener.setSearchLocations(configFilePath);

        return listener;
    }
}
