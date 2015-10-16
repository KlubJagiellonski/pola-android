package pl.pola_app.model;

import org.parceler.Parcel;

@Parcel
public class Company {
    public String name;
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
}
