package pl.pola_app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {
    public int id;
    public String name;
    public int plCapital;
    public String plCapital_notes;
    public String nip;
    public String address;

    protected Company(android.os.Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
        this.plCapital = source.readInt();
        this.plCapital_notes = source.readString();
        this.nip = source.readString();
        this.address = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(plCapital);
        dest.writeString(plCapital_notes);
        dest.writeString(nip);
        dest.writeString(address);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };
}
