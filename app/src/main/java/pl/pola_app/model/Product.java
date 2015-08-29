package pl.pola_app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    public int id;
    public String code;
    public Company company;

    protected Product(Parcel in) {
        id = in.readInt();
        code = in.readString();
        company = (Company) in.readValue(Company.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeValue(company);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
