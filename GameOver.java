package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameOver {
    public static void display(Stage prime, int score){
        Stage window = new Stage();
        window.setTitle("Game Over");
        window.setMinWidth(400);
        window.initModality(Modality.APPLICATION_MODAL);

        Button tryAgain = new Button("Try again");
        Button exit = new Button("Exit");
        exit.setOnAction(e -> { prime.close();
                                window.close();
        });
        tryAgain.setOnAction(e -> {
            Game snake = new Game();
            try {
                snake.start(prime);
                window.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        Label lab1 = new Label();
        lab1.setText("Game Over");
        Label lab2 = new Label();
        lab2.setText("Your score is: " + score);

        VBox layout = new VBox(4);
        layout.getChildren().addAll(lab1, lab2, tryAgain, exit);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 400);
        window.setScene(scene);
        window.show();
    }

}
