package pl.pola_app.model;

import org.parceler.Parcel;

@Parcel
public class SearchResult {
    public Integer product_id;
    public String code;
    public String name;
    public String card_type;
    public Integer plScore;
    public String altText;
    public Integer plCapital;
    public Integer plWorkers;
    public Integer plRnD;
    public Integer plRegistered;
    public Integer plNotGlobEnt;
    public Boolean is_friend;

    public String description;

    public String report_text;
    public String report_button_text;
    public String report_button_type;

    public String friend_text;

    public Donate donate;

    public boolean askForSupport() {
        return donate != null;
    }


}
