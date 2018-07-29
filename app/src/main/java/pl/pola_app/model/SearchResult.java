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

    public Ai ai;

    public boolean askForPics() {
        return ai != null && ai.ask_for_pics;
    }

    public String askForPicsPreview() {
        if (ai == null) {
            return "";
        }
        return ai.ask_for_pics_preview;
    }

    public String askForPicsTitle() {
        if (ai == null) {
            return "";
        }
        return ai.ask_for_pics_title;
    }

    public String askForPicsText() {
        if (ai == null) {
            return "";
        }
        return ai.ask_for_pics_text;
    }

    public String askForPicsButtonStart() {
        if (ai == null) {
            return "";
        }
        return ai.ask_for_pics_button_start;
    }
    public int maxPicSize() {
        if (ai == null) {
            return 0;
        }
        return ai.max_pic_size;
    }

    public String askForPicsProduct(){
        if (ai == null) {
            return "";
        }
        return ai.ask_for_pics_product;
    }
}
