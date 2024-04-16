package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class daneFirma extends daneKlasaPodstawowa{
    private String nazwa;
    private String numer;
    private Integer nr_telefonu;
    private String ulica_nr;
    private String miasto;
    private String typ;
    //dane przypidsanego kalendarza do firmy
    private long kalendarz_id;
    private String kalendarz_nazwa;
    private Integer zleceniodawca;
    private Integer czyZleceniodawca; //czy firma jest zleceniodawcą
    private String zleceniodawcaNazwa;

    //private Integer siec_id;
    //zewnętrzne
    //private String siec_nazwa;
    //private String typ;

    public daneFirma() {
        this.nazwa = "";
        this.numer = "";
        this.nr_telefonu = 0;
        this.ulica_nr = "";
        this.miasto = "";
        //this.siec_id = null;
        //this.siec_nazwa = null;
        this.typ = "";

        this.kalendarz_id = 0L;
        this.kalendarz_nazwa = null;
        this.czyZleceniodawca = 0;
        this.zleceniodawca = -1;

    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
/*public String getSiec_nazwa() {
        return siec_nazwa;
    }*/

    /*public void setSiec_nazwa(String siec_nazwa) {
        this.siec_nazwa = siec_nazwa;
    }*/

    /*public Integer getSiec_id() {
        return siec_id;
    }*/

    /*public void setSiec_id(Integer siec_id) {
        this.siec_id = siec_id;
    }*/

    public Integer getZleceniodawca() {
        return zleceniodawca;
    }

    public void setZleceniodawca(Integer zleceniodawca) {
        this.zleceniodawca = zleceniodawca;
    }

    public String getZleceniodawcaNazwa() {
        return zleceniodawcaNazwa;
    }

    public void setZleceniodawcaNazwa(String zleceniodawcaNazwa) {
        this.zleceniodawcaNazwa = zleceniodawcaNazwa;
    }

    public Integer getCzyZleceniodawca() {
        return czyZleceniodawca;
    }

    public void setCzyZleceniodawca(Integer czyZleceniodawca) {
        this.czyZleceniodawca = czyZleceniodawca;
    }

    public String getUlica_nr() {
        return ulica_nr;
    }

    public void setUlica_nr(String ulica_nr) {
        this.ulica_nr = ulica_nr;
    }

    public long getKalendarz_id() {
        return kalendarz_id;
    }
    public String getKalendarz_id_str() {
        return String.valueOf(kalendarz_id);
    }

    public void setKalendarz_id(long kalendarz_id) {
        this.kalendarz_id = kalendarz_id;
    }

    public String getKalendarz_nazwa() {
        return kalendarz_nazwa;
    }

    public void setKalendarz_nazwa(String kalendarz_nazwa) {
        this.kalendarz_nazwa = kalendarz_nazwa;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNumer() {
        return numer;
    }

    public void setNumer(String numer) {
        this.numer = numer;
    }

    public String getUlicaNr() {
        return ulica_nr;
    }

    public void setUlicaNr(String ulicaNr) {
        this.ulica_nr = ulicaNr;
    }

    public String getMiasto() {
        return miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public Integer getNr_telefonu() {
        return nr_telefonu;
    }

    public void setNr_telefonu(Integer nr_telefonu) {
        this.nr_telefonu = nr_telefonu;
    }

    public String toStringDoRecyclerView() {
        return  "id = " + id +
                "\nN: " + nazwa +
                 "\nU: " + uwagi;
                //" K: " + kalendarz_id;

    }

    public String toStringDoRecyclerView1() {
        return  "N: " + nazwa +
                " Nr: " + numer +
                " Tel: " + nr_telefonu +
                " A: " + ulica_nr +
                " M: " + miasto +
                "U: " + uwagi +
                " K: " + kalendarz_nazwa;

    }

    public void setFromJSON(JSONObject Jasonobject) {
        setFromJSONw(Jasonobject);

        this.setKalendarz_id(Jasonobject.optInt("kalendarz_id", 0));
        this.setTyp(Jasonobject.optString("typ", ""));
        this.setMiasto(Jasonobject.optString("miasto", ""));
        this.setUlica_nr(Jasonobject.optString("ulica_nr", ""));
        this.setNr_telefonu(Jasonobject.optInt("nr_telefonu", 0));
        this.setNumer(Jasonobject.optString("numer", ""));
        this.setNazwa(Jasonobject.optString("nazwa", ""));
        this.setCzyZleceniodawca(Jasonobject.optInt("czyZleceniodawca", 0));
        this.setZleceniodawca(Jasonobject.optInt("zleceniodawca",-1));
    }

    public Map<String, String> getMap() {
        Map<String, String> param = new HashMap<String, String>();

        param.put("nazwa", this.getNazwa());
        if (this.getNumer() != null) {
            param.put("numer", this.getNumer());
        } else {
            param.put("numer", "0");
        }
        param.put("nr_telefonu", String.valueOf(this.getNr_telefonu()));
        param.put("ulica_nr", this.getUlica_nr());
        param.put("miasto", this.getMiasto());
        param.put("typ", this.getTyp());
        param.put("kalendarz_id", String.valueOf(this.getKalendarz_id()));
        param.put("czyZleceniodawca", String.valueOf(this.getCzyZleceniodawca()));
        param.put("zleceniodawca", String.valueOf(this.getZleceniodawca()));

        param.putAll(getMapW());
        return param;
    }

    public String toString(){
         return  "id: " + id +
                 " N: " + nazwa +
             " Nr: " + numer +
             " Tel: " + nr_telefonu +
             " A: " + ulica_nr +
             " M: " + miasto +
             "U: " + uwagi +
             " K: " + kalendarz_nazwa +
                 " uwagi " + uwagi +
        " poprzedni_rekord_id " + poprzedni_rekord_id +
        " poprzedni_rekord_data_usuniecia " + poprzedni_rekord_data_usuniecia +
        " poprzedni_rekord_powod_usuniecia " + poprzedni_rekord_powod_usuniecia +
        " czy_widoczny " + czy_widoczny +
        " synchron " + synchron +
        " data_utworzenia " + data_utworzenia +
        " data_synchronizacji " + data_synchronizacji;
    }

    public ContentValues getContentValues1(){
        ContentValues wartosci = new ContentValues();

        wartosci.put("nazwa", this.getNazwa());
        //Log.d("OSQLdaneFirma: Nazwa: ", dane_funkcji.getNazwa());
        wartosci.put("numer", this.getNumer());
        wartosci.put("nr_telefonu", this.getNr_telefonu());
        //Log.d("OSQLdaneFirma: Numer Telefonu", String.valueOf(this.getNr_telefonu()));
        wartosci.put("ulica_nr", this.getUlicaNr());
        //Log.d("OSQLdaneFirma: UlicaNr: ", this.getUlicaNr());
        wartosci.put("miasto", this.getMiasto());
        wartosci.put("typ", this.getTyp());
        //Log.d("OSQLdaneFirma: Miasto: ", this.getMiasto());
        wartosci.put("kalendarz_id", this.getKalendarz_id_str());
        wartosci.put("czyZleceniodawca", this.getCzyZleceniodawca());
        wartosci.put("zleceniodawca", this.getZleceniodawca());
        //Log.d("OSQLdaneFirma: Kalendarz_id", String.valueOf(this.getKalendarz_id()));
        //wartosci.put("siec_id", this.getSiec_id());
        //wartosci.put("typ", this.getTyp());
        wartosci.putAll(contentValuesW());
        return wartosci;
    }
    /*public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        typ = typ;
    }*/
}
