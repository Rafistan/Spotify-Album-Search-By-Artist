/**
 * Author: Rafi Stepanians & Dan Florin Raiu
 */

package spotify;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author RaffiStepanians
 */
public class TrackList
{
    private SimpleStringProperty number;
    private SimpleStringProperty name;
    private SimpleStringProperty duration;
    
    public TrackList(String number, String name, String duration)
    {
        this.number = new SimpleStringProperty(number);
        this.name = new SimpleStringProperty(name);
        this.duration = new SimpleStringProperty(duration);
    }

    public String getNumber()
    {
        return number.get();
    }

    public String getName()
    {
        return name.get();
    }
   
    public String getDuration()
    {
        return duration.get();
    }
}
