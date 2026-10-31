package com.example;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent root = loader.load();
        CityController controller = loader.getController();

        scene = new Scene(root, 1200, 700);
        stage.setTitle("City Route Finder");
        stage.setScene(scene);
        stage.show();

        File flagFile = new File("take_screenshot.flag");
        if (flagFile.exists()) {
            PauseTransition prep = new PauseTransition(Duration.millis(500));
            prep.setOnFinished(e -> {
                try {
                    // Trigger Test Case 1: John's House -> Jim's House
                    Method m = CityController.class.getDeclaredMethod("handleTestCase1");
                    m.setAccessible(true);
                    m.invoke(controller);

                    PauseTransition snap = new PauseTransition(Duration.millis(500));
                    snap.setOnFinished(ev -> {
                        try {
                            WritableImage fxImage = root.snapshot(null, null);
                            int width = (int) fxImage.getWidth();
                            int height = (int) fxImage.getHeight();
                            BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                            PixelReader pr = fxImage.getPixelReader();
                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    bImage.setRGB(x, y, pr.getArgb(x, y));
                                }
                            }
                            File outputFile = new File("screenshot.png");
                            ImageIO.write(bImage, "png", outputFile);
                            System.out.println("SNAPSHOT_SAVED_SUCCESS");
                            flagFile.delete();
                            Platform.exit();
                            System.exit(0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Platform.exit();
                            System.exit(1);
                        }
                    });
                    snap.play();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.exit();
                    System.exit(1);
                }
            });
            prep.play();
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
