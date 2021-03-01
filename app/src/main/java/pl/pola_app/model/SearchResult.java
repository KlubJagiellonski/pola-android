package pl.pola_app.model;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class SearchResult {
    public Integer product_id;
    public String code;
    public String name;
    public String card_type;
    public String altText;
    public ArrayList<Company> companies;

    public ReportV4 report; 

    public String friend_text;

    public Donate donate;

    public boolean askForSupport() {
        return donate != null;
    }


}
