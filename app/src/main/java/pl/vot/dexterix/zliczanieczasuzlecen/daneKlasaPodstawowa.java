package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class daneKlasaPodstawowa implements InterfejsDane{
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

    public daneKlasaPodstawowa(){
        this.uwagi = "";
        Log.d("uwagi: ", getUwagi());
        this.poprzedni_rekord_data_usuniecia = "0";
        this.id = 0;
        this.poprzedni_rekord_id = 0;
        this.poprzedni_rekord_powod_usuniecia = "0";
        this.czy_widoczny = 1;
        this.synchron = 0;
        this.data_synchronizacji = 0;
        this.data_utworzenia = 0;
    }

    protected void ustawDanePoczatkoweKlasyW(){
        this.uwagi = "uwagi345";
        Log.d("uwagi: ", getUwagi());
        this.poprzedni_rekord_data_usuniecia = "0";
        this.id = 0;
        this.poprzedni_rekord_id = 0;
        this.poprzedni_rekord_powod_usuniecia = "0";
        this.czy_widoczny = 1;
        this.synchron = 0;
        this.data_synchronizacji = 0;
        this.data_utworzenia = 0;
    }


    public void setFromJSONw(JSONObject Jasonobject){
        this.setData_utworzenia(Jasonobject.optLong("data_utworzenia", 0));
        this.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));
        this.setSynchron(1);
        this.setCzy_widoczny(Jasonobject.optInt("czy_widoczny", 0));
        this.setPoprzedni_rekord_powod_usuniecia(Jasonobject.optString("poprzedni_rekord_powod_usuniecia", ""));
        this.setPoprzedni_rekord_data_usuniecia(Jasonobject.optString("poprzedni_rekord_data_usuniecia", ""));
        this.setPoprzedni_rekord_id(Jasonobject.optInt("poprzedni_rekord_id", 0));
        this.setUwagi(Jasonobject.optString("uwagi", ""));
        this.setId(Jasonobject.optInt("id", 0));
    }

    public void setFromJSON(JSONObject Jasonobject) {

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

    @Override
    public Map<String, String> getMap() {
        return null;
    }

    protected Map<String, String> getMapW() {
        Map<String, String> param = new HashMap<String, String>();

            param.put("_id", String.valueOf(this.getId()));
            param.put("uwagi", this.getUwagi());
            if (this.getPoprzedni_rekord_id() != null) {
                param.put("poprzedni_rekord_id", String.valueOf(this.getPoprzedni_rekord_id()));
            } else {
                param.put("poprzedni_rekord_id", "0");
            }
            if (this.getPoprzedni_rekord_data_usuniecia() != null) {
                param.put("poprzedni_rekord_data_usuniecia", this.getPoprzedni_rekord_data_usuniecia());
            } else {
                param.put("poprzedni_rekord_data_usuniecia", "0");
            }
            if (this.getPoprzedni_rekord_powod_usuniecia() != null) {
                param.put("poprzedni_rekord_powod_usuniecia", this.getPoprzedni_rekord_powod_usuniecia());
            } else {
                param.put("poprzedni_rekord_powod_usuniecia", "");
            }
            param.put("czy_widoczny", String.valueOf(this.getCzy_widoczny()));
            param.put("data_utworzenia", String.valueOf(this.getData_utworzenia()));
            param.put("data_synchronizacji", String.valueOf(this.getData_synchronizacji()));

        return param;
    }

    protected ContentValues contentValuesW(){

            ContentValues wartosci = new ContentValues();

            wartosci.put("_id", this.getId());

            wartosci.put("uwagi", this.getUwagi());
            wartosci.put("poprzedni_rekord_id", this.getPoprzedni_rekord_id());
            wartosci.put("poprzedni_rekord_data_usuniecia", this.getPoprzedni_rekord_data_usuniecia());
            wartosci.put("poprzedni_rekord_powod_usuniecia", this.getPoprzedni_rekord_powod_usuniecia());
            wartosci.put("synchron", this.getSynchron());
            wartosci.put("czy_widoczny", this.getCzy_widoczny());
            if(this.getData_synchronizacji() > 0){
                wartosci.put("data_synchronizacji", this.getData_synchronizacji());
            }else{
                wartosci.put("data_synchronizacji", 0);
            }
            if(this.getData_utworzenia() > 0){
                wartosci.put("data_utworzenia", this.getData_utworzenia());
            }else{
                wartosci.put("data_utworzenia", 0);
            }
            //Log.d("OSQLdaneFirma: Uwagi: ", this.getUwagi());
            return wartosci;
    }
}
