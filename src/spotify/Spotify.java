/**
 * Author: Rafi Stepanians & Dan Florin Raiu
 */

package spotify;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author RaffiStepanians
 */
public class Spotify extends Application
{
    Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception
    {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println("Something Went Wrong!");
        }
    }
    
    public Stage getStage()
    {
        return stage;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
