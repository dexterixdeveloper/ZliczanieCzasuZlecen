package pl.vot.dexterix.zliczanieczasuzlecen;

public class daneStawka extends daneKlasaPodstawowa{

    private String firma_nazwa;
    private Integer firma_id;
    private float stawka;
    private String poczatek;
    private String koniec;

    public String getFirma_nazwa() {
        return firma_nazwa;
    }

    public void setFirma_nazwa(String firma_nazwa) {
        this.firma_nazwa = firma_nazwa;
    }

    public Integer getFirma_id() {
        return firma_id;
    }

    public void setFirma_id(Integer firma_id) {
        this.firma_id = firma_id;
    }

    public float getStawka() {
        return stawka;
    }

    public void setStawka(float stawka) {
        this.stawka = stawka;
    }

    public String getPoczatek() {
        return poczatek;
    }

    public void setPoczatek(String poczatek) {
        this.poczatek = poczatek;
    }

    public String getKoniec() {
        return koniec;
    }

    public void setKoniec(String koniec) {
        this.koniec = koniec;
    }

    /*public daneStawki(String firma_nazwa, Integer firma_id, float stawka, String poczatek, String koniec) {
        this.firma_nazwa = firma_nazwa;
        this.firma_id = firma_id;
        this.stawka = stawka;
        this.poczatek = poczatek;
        this.koniec = koniec;
    }*/

    public void onCreate(){
        this.setId(0);
        this.setStawka(0.00f);
        this.setPoczatek("00:00");
        this.setKoniec("00:00");
        this.setFirma_id(0);
        this.setFirma_nazwa("");
    }

    public String toStringDoRecyclerView() {
        return "W: " + getStawka() + "od" + getPoczatek();
    }
}
