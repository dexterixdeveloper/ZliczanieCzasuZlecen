package pl.vot.dexterix.zliczanieczasuzlecen;

import android.database.Cursor;

public class daneKlasaPodstawowa {
    protected Integer id;
    protected String uwagi;
    protected Integer poprzedni_rekord_id;
    protected String poprzedni_rekord_data_usuniecia;
    protected String poprzedni_rekord_powod_usuniecia;
    protected Integer czy_widoczny;
    protected Integer synchron;
    protected long data_utworzenia;
    protected long data_synchronizacji;

    public long getData_utworzenia() {
        return data_utworzenia;
    }

    public void setData_utworzenia(long data_utworzenia) {
        this.data_utworzenia = data_utworzenia;
    }

    public long getData_synchronizacji() {
        return data_synchronizacji;
    }

    public void setData_synchronizacji(long data_synchronizacji) {
        this.data_synchronizacji = data_synchronizacji;
    }

    public Integer getSynchron() {
        return synchron;
    }

    public void setSynchron(Integer synchron) {
        this.synchron = synchron;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUwagi() {
        return uwagi;
    }

    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }

    public Integer getPoprzedni_rekord_id() {
        return poprzedni_rekord_id;
    }

    public void setPoprzedni_rekord_id(Integer poprzedni_rekord_id) {
        this.poprzedni_rekord_id = poprzedni_rekord_id;
    }

    public String getPoprzedni_rekord_data_usuniecia() {
        return poprzedni_rekord_data_usuniecia;
    }

    public void setPoprzedni_rekord_data_usuniecia(String poprzedni_rekord_data_usuniecia) {
        this.poprzedni_rekord_data_usuniecia = poprzedni_rekord_data_usuniecia;
    }

    public String getPoprzedni_rekord_powod_usuniecia() {
        return poprzedni_rekord_powod_usuniecia;
    }

    public void setPoprzedni_rekord_powod_usuniecia(String poprzedni_rekord_powod_usuniecia) {
        this.poprzedni_rekord_powod_usuniecia = poprzedni_rekord_powod_usuniecia;
    }

    public Integer getCzy_widoczny() {
        return czy_widoczny;
    }

    public void setCzy_widoczny(Integer czy_widoczny) {
        this.czy_widoczny = czy_widoczny;
    }

    public void addWspolne(Cursor kursor){

        if (kursor.getColumnIndex("_id") > -1) {
            this.setId(kursor.getInt(kursor.getColumnIndex("_id")));
        }
        if (kursor.getColumnIndex("uwagi") > -1) {
            this.setUwagi(kursor.getString(kursor.getColumnIndexOrThrow("uwagi")));
        }
        if (kursor.getColumnIndex("poprzedni_rekord_id") > -1) {
            this.setPoprzedni_rekord_id(kursor.getInt(kursor.getColumnIndex("poprzedni_rekord_id")));
        }
        if (kursor.getColumnIndex("poprzedni_rekord_data_usuniecia") > -1) {
            this.setPoprzedni_rekord_data_usuniecia(kursor.getString(kursor.getColumnIndex("poprzedni_rekord_data_usuniecia")));
        }
        if (kursor.getColumnIndex("poprzedni_rekord_powod_usuniecia") > -1) {
            this.setPoprzedni_rekord_powod_usuniecia(kursor.getString(kursor.getColumnIndex("poprzedni_rekord_powod_usuniecia")));
        }
        if (kursor.getColumnIndex("czy_widoczny") > -1) {
            this.setCzy_widoczny(kursor.getInt(kursor.getColumnIndex("czy_widoczny")));
        }
        if (kursor.getColumnIndex("synchron") > -1) {
            this.setSynchron(kursor.getInt(kursor.getColumnIndex("synchron")));
        }
        if (kursor.getColumnIndex("data_utworzenia") > -1) {
            this.setData_utworzenia(kursor.getLong(kursor.getColumnIndex("data_utworzenia")));
        }
        if (kursor.getColumnIndex("data_synchronizacji") > -1) {
            this.setData_synchronizacji(kursor.getLong(kursor.getColumnIndex("data_synchronizacji")));
        }
    }
}
