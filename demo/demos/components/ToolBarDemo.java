package demos.components;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXToolbar;

public class ToolBarDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			JFXToolbar jfxToolbar = new JFXToolbar();
			jfxToolbar.setLeftItems(new Label("Left"));
			jfxToolbar.setRightItems(new Label("Right"));
			StackPane main = new StackPane();
			
			main.getChildren().add(jfxToolbar);
			Scene scene = new Scene(main, 600, 400);
			scene.getStylesheets().add(ToolBarDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
