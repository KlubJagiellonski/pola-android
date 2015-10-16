package pl.pola_app.model;

import org.parceler.Parcel;

@Parcel
public class Product {
    public Integer id;
    public Company company;
    public boolean verified;
    public Integer plScore;
    public String report;
    public String code;
}
