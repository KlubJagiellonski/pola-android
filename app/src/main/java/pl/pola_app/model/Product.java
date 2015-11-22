package pl.pola_app.model;

import org.parceler.Parcel;

@Parcel
public class Product {
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

    public String plCapital_notes;
    public String plWorkers_notes;
    public String plRnD_notes;
    public String plRegistered_notes;
    public String plNotGlobalEnt_notes;

    public String report_text;
    public String report_button_text;
    public String report_button_type;
}
