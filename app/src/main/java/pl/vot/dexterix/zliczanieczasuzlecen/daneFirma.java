package pl.vot.dexterix.zliczanieczasuzlecen;

public class daneFirma extends daneKlasaPodstawowa{
    private String nazwa;
    private String numer;
    private Integer nr_telefonu;
    private String ulica_nr;
    private String miasto;
    //dane przypidsanego kalendarza do firmy
    private long kalendarz_id;
    private String kalendarz_nazwa;

    //private Integer siec_id;
    //zewnętrzne
    //private String siec_nazwa;
    //private String typ;

    public daneFirma() {
        this.nazwa = null;
        this.numer = null;
        this.nr_telefonu = null;
        this.ulica_nr = null;
        this.miasto = null;
        //this.siec_id = null;
        //this.siec_nazwa = null;
        //this.typ = null;
        this.uwagi = null;
        this.kalendarz_id = 0L;
        this.kalendarz_nazwa = null;

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
        return  "N: " + nazwa +
                 " U: " + uwagi +
                " K: " + kalendarz_id;

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

    /*public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        typ = typ;
    }*/
}
