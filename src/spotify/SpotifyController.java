/**
 * Author: Rafi Stepanians & Dan Florin Raiu
 */

package spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;


public class SpotifyController
{
    final static private String SPOTIFY_CLIENT_ID     = "9589db5c38e84f38bd7e8a3643fe3e1f";
    final static private String SPOTIFY_CLIENT_SECRET = "235da6f1f7034b12a3f0bdb1a0cb3a1b";
    private static int size, count, itemSize;
    private static ArrayList<File> albumsURL;
    private static String[][] albumInfo;
    private static String[] albumID;
    private static VBox numberBox, nameBox, timeBox;
    
    public static VBox getNumberBox()
    {
        return numberBox;
    }
    
    public static VBox getNameBox()
    {
        return nameBox;
    }
    
    public static VBox getTimeBox()
    {
        return timeBox;
    }
    
    public static void setNumberBox(VBox box)
    {
        numberBox = box;
    }
    
    public static void setNameBox(VBox box)
    {
        nameBox = box;
    }
    
    public static void setTimeBox(VBox box)
    {
        timeBox = box;
    }
    
    public static void setCount(int count)
    {
        SpotifyController.count = count;
    }
    
    public static int getCount()
    {
        return count;
    }
    
    public static int getAlbumSize()
    {
        return albumsURL.size();
        //return 10;
    }
    
    public static int getImageSize()
    {
        return size;
    }
    
    public static void setImageSize(int size)
    {
        SpotifyController.size = size;
    }
    
    public static int getItemSize()
    {
        return itemSize;
    }
     
    public static String getArtistId(String artistName)
    {
        try
        {
            String endpoint = "https://api.spotify.com/v1/search";
            String params = "type=artist&q=" + artistName;
            String jsonOutput = spotifyEndpointToJson(endpoint, params);
            
            // TODO - Parse the JSON output in order to retrieve the artist id
            JsonElement jElement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jElement.getAsJsonObject();
            JsonObject artists = rootObject.get("artists").getAsJsonObject();
            JsonArray items = artists.get("items").getAsJsonArray();
            JsonObject item0 = items.get(0).getAsJsonObject();
            String id = item0.get("id").toString();
            
            id = id.replace("\"", "");
            
            getAlbumCoversFromArtist(id, artistName);
            
            return id;
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public static String[][] getTrackInfo()
    {
        try
        {
            String endpoint = "https://api.spotify.com/v1/albums/" + albumID[getCount()] + "/tracks";
            String params = "offset=0&limit=40";
            String jsonOutput = spotifyEndpointToJson(endpoint, params);
            
            JsonElement jElement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jElement.getAsJsonObject();
            JsonArray items = rootObject.get("items").getAsJsonArray();
            
            itemSize = items.size();
            
            albumInfo = new String[items.size()][3];
            
            for(int i = 0; i < items.size(); i++)
            {
                //Set the track number
                String trackNumber = items.get(i).getAsJsonObject().get("track_number").toString();
                albumInfo[i][0] = trackNumber;
                
                //Set the track name
                String trackName = items.get(i).getAsJsonObject().get("name").toString();
                albumInfo[i][1] = trackName;
                
                //Set the track duration
                String trackDuration = items.get(i).getAsJsonObject().get("duration_ms").toString();
                albumInfo[i][2] = trackDuration;
            }
            
            return albumInfo;
        }catch(Exception e){
            return null;
        }
        
    }
    
    public static ArrayList<File> getAlbumCoversFromArtist(String spotifyArtistId, String artistName)
    {
        try{
            String endpoint = "https://api.spotify.com/v1/artists/" + spotifyArtistId + "/albums";
            String params = "market=CA&limit=50&album_type=album";
            String jsonOutput = spotifyEndpointToJson(endpoint, params);

            JsonElement jElement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jElement.getAsJsonObject();
            JsonArray items = rootObject.get("items").getAsJsonArray();
               
            albumID = new String[items.size()];
            
            albumsURL = new ArrayList<File>();
            
            for(int i = 0; i < items.size(); i++)
            {
                JsonArray images = items.get(i).getAsJsonObject().get("images").getAsJsonArray();
                String url = images.get(0).getAsJsonObject().get("url").toString();
                url = url.replace("\"", "");
                
                String albumsid = items.get(i).getAsJsonObject().get("id").toString();
                albumsid = albumsid.replace("\"", "");
                albumID[i] = albumsid;
                
                setImageSize(images.size());
                
                try{
                    URL imageURL = new URL(url);
                    File outputFile = new File("images/" + artistName.toLowerCase() + i + ".png");
                    FileUtils.copyURLToFile(imageURL, outputFile);
                    
                    albumsURL.add(outputFile);
                }catch(Exception e){
                    System.out.println("Something went wrong...");
                }
            }
            
            return albumsURL;
        }catch(Exception e){
            System.out.println("Something went wrong");
            return null;
        }
    }
    

    private static String spotifyEndpointToJson(String endpoint, String params)
    {
        params = params.replace(' ', '+');

        try
        {
            String fullURL = endpoint;
            if (params.isEmpty() == false)
            {
                fullURL += "?"+params;
            }
            
            URL requestURL = new URL(fullURL);
            
            HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
            String bearerAuth = "Bearer " + getSpotifyAccessToken();
            connection.setRequestProperty ("Authorization", bearerAuth);
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            String jsonOutput = "";
            while((inputLine = in.readLine()) != null)
            {
               jsonOutput += inputLine;
            }
            in.close();
            
            return jsonOutput;
        }
        catch(Exception e)
        {
            return null;
        }
    }


    // This implements the Client Credentials Authorization Flows
    // Based on the Spotify API documentation
    // 
    // It retrieves the Access Token based on the client ID and client Secret  
    //
    // You shouldn't have to modify any of this code...          
    private static String getSpotifyAccessToken()
    {
        try
        {
            URL requestURL = new URL("https://accounts.spotify.com/api/token");
            
            HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
            String keys = SPOTIFY_CLIENT_ID+":"+SPOTIFY_CLIENT_SECRET;
            String postData = "grant_type=client_credentials";
            
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(keys.getBytes()));
            
            // Send header parameter
            connection.setRequestProperty ("Authorization", basicAuth);
            
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Send body parameters
            OutputStream os = connection.getOutputStream();
            os.write( postData.getBytes() );
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String inputLine;
            String jsonOutput = "";
            while((inputLine = in.readLine()) != null)
            {
                jsonOutput += inputLine;
            }
            in.close();
            
            JsonElement jelement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jelement.getAsJsonObject();
            String token = rootObject.get("access_token").getAsString();

            return token;
        }
        catch(Exception e)
        {
            System.out.println("Something wrong here... make sure you set your Client ID and Client Secret properly!");
            e.printStackTrace();
        }
        
        return "";
    }
}
