/**
 * Author: Rafi Stepanians & Dan Florin Raiu
 */

package spotify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable
{
    @FXML
    private TextField artistTF;
    @FXML
    private Label bottomMessage;
    @FXML
    private Button goBack;
    @FXML
    private Button seeTracks;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private ImageView imageView;
    @FXML
    private ProgressBar bar;
    @FXML
    private TableView<TrackList> table;
    @FXML
    private TableColumn<TrackList, String> numberCol;
    @FXML
    private TableColumn<TrackList, String> nameCol;
    @FXML
    private TableColumn<TrackList, String> durationCol;

    private boolean b = true;
    private int count;
    
    @FXML
    private void seeTracksActionPerformed(ActionEvent event) throws IOException
    {
        Stage stage; 
        Parent root;
        if(event.getSource() == seeTracks)
        {
           //get reference to the button's stage         
           stage = (Stage) seeTracks.getScene().getWindow();
           //load up OTHER FXML document
           root = FXMLLoader.load(getClass().getResource("TrackerFXML.fxml"));
        }
        else
        {
            stage = (Stage) goBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        }
        
        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void exitProgramActionPerformed(ActionEvent event)
    {
        System.exit(-1);
    }
    
    @FXML
    public void searchButton(ActionEvent event) throws IOException
    {   
        
        try{
            //To avoid getting an error
            String id = SpotifyController.getArtistId(artistTF.getText());
        
            
            for(int i = 0; i < SpotifyController.getAlbumSize(); i++)
            {
                if(SpotifyController.getAlbumSize() == 1)
                    nextButton.setDisable(true);
                try
                {
                    count = 0;

                    File logoFile = new File("images/" + artistTF.getText().toLowerCase()  + "0.png");
                    String thum = logoFile.toURI().toURL().toString();
                    Image logoImage = new Image(thum);
                    imageView.setImage(logoImage);

                    bar.setProgress(1 / ((double)SpotifyController.getAlbumSize()));
                    bottomMessage.setText("Cover " + (count + 1) + " / " + (SpotifyController.getAlbumSize()));

                    previousButton.setDisable(true);
                }
                catch(IOException e)
                {
                    bottomMessage.setText("Error search button!");
                }
            }

            SpotifyController.setCount(count);
        }catch(Exception e){
            bottomMessage.setText("Error: Artist Name");
        }
        
        
    }
    
    @FXML
    public void nextButton(ActionEvent event)
    {
        
        if(count < SpotifyController.getAlbumSize() - 1)
        {
            previousButton.setDisable(false);
            count++;
            try
            {
                File logoFile = new File("images/" + artistTF.getText().toLowerCase()  + count +  ".png");
                String thum = logoFile.toURI().toURL().toString();
                Image logoImage = new Image(thum);
                imageView.setImage(logoImage);
                
                bottomMessage.setText("Cover " + (count + 1) + " / " + (SpotifyController.getAlbumSize()));
                
                bar.setProgress((count + 1)/((double)SpotifyController.getAlbumSize()));
            }
            catch(Exception e)
            {
                System.out.println("Error");
            }
            
        }
        if(count == SpotifyController.getAlbumSize() - 1)
        {
            nextButton.setDisable(true);
            previousButton.setDisable(false);
        }
        
        SpotifyController.setCount(count);
    }
    
    @FXML
    public void previousButton(ActionEvent event)
    {
        
        if(count > 0)
        {
            nextButton.setDisable(false);
            count--;
            try
            {
                File logoFile = new File("images/" + artistTF.getText().toLowerCase()  + count +  ".png");
                String thum = logoFile.toURI().toURL().toString();
                Image logoImage = new Image(thum);
                imageView.setImage(logoImage);
                
                bottomMessage.setText("Cover " + (count + 1 ) + " / " + (SpotifyController.getAlbumSize()));
                
                bar.setProgress((count + 1)/((double)SpotifyController.getAlbumSize()));
            
            }
            catch(Exception e)
            {
                System.out.println("Error");
            }
            
        }
        if(count == 0)
        {
            nextButton.setDisable(false);
            previousButton.setDisable(true);
        }
        
        SpotifyController.setCount(count);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
            try{
               File file = new File("images/spotify.png");
               String thum = file.toURI().toURL().toString();
               Image image = new Image(thum);
               imageView.setImage(image);

            }catch(Exception e){
                System.out.println();
            } 
            try{
                numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
                durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

                table.setItems(getList());
            }catch(Exception e){
                System.out.println();
            }
    }  
    
    public static String milisecondToMin(String miliseconds)
    {
        boolean oneDigit = false;
        
        int mil = Integer.parseInt(miliseconds);
        
        int seconds = mil / 1000;
        
        int number = 0;
        int testSec = seconds;
        seconds %= 60;
        
        if(seconds / 10 == 0)
        {
           oneDigit = true;
        }
        
        while(testSec >= 60)
        {
            testSec -= 60;
            number++;
        }
        
        String minutesNumber = String.format("%1d" ,number);
        String secondsNumber = String.format("%1d", seconds);
        
        String minutes = "";
        if(oneDigit == true)
        {
            minutes = minutesNumber + ":0" + secondsNumber;
        }
        else
        {
            minutes = minutesNumber + ":" + secondsNumber;
        }
        
        
        return minutes;
    }
    
    public ObservableList<TrackList> getList()
    {
        ObservableList<TrackList> list = FXCollections.observableArrayList();
        
        String[][] info = SpotifyController.getTrackInfo();
        for(int i = 0; i < SpotifyController.getItemSize(); i++)
        {
            String number = info[i][0];
            String name = info[i][1];
            //String duration = info[i][2];
            String duration = milisecondToMin(info[i][2]);

            list.add(new TrackList(number, name, duration));
        }
        
        return list;
    }
    
}
