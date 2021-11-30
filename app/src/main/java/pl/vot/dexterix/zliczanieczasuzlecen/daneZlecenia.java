package pl.vot.dexterix.zliczanieczasuzlecen;

import java.io.Serializable;

/**
 * Created by dexterix on 2015-04-16.
 */
public class daneZlecenia extends daneKlasaPodstawowa implements Serializable {

    private Long czas_zawieszenia;
    private String czas_zawieszenia_string;
    private Long czas_rozpoczecia;
    private String czas_rozpoczecia_string;
    private String opis;
    private Integer firma_id;
    private Long czas_zakonczenia;
    private String czas_zakonczenia_string;
    private String status;
    private String rozliczona;
    private String firma_nazwa;
    private Integer kalendarz_id;
    private long kalendarz_id_long;
    private Long kalendarz_zadanie_id;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCzas_zawieszenia_string() {
        return czas_zawieszenia_string;
    }

    public long getKalendarz_id_long() {
        return kalendarz_id_long;
    }

    public void setKalendarz_id_long(long kalendarz_id_long) {
        this.kalendarz_id_long = kalendarz_id_long;
    }

    public void setCzas_zawieszenia_string(String czas_zawieszenia_string) {
        this.czas_zawieszenia_string = czas_zawieszenia_string;
    }

    public String getCzas_rozpoczecia_string() {
        return czas_rozpoczecia_string;
    }

    public void setCzas_rozpoczecia_string(String czas_rozpoczecia_string) {
        this.czas_rozpoczecia_string = czas_rozpoczecia_string;
    }

    public String getCzas_zakonczenia_string() {
        return czas_zakonczenia_string;
    }

    public void setCzas_zakonczenia_string(String czas_zakonczenia_string) {
        this.czas_zakonczenia_string = czas_zakonczenia_string;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Integer getFirma_id() {
        return firma_id;
    }

    public void setFirma_id(Integer firma_id) {
        this.firma_id = firma_id;
    }

    public Long getCzas_zawieszenia() {
        return czas_zawieszenia;
    }

    public void setCzas_zawieszenia(Long czas_zawieszenia) {
        this.czas_zawieszenia = czas_zawieszenia;
    }

    public Long getCzas_rozpoczecia() {
        return czas_rozpoczecia;
    }

    public void setCzas_rozpoczecia(Long czas_rozpoczecia) {
        this.czas_rozpoczecia = czas_rozpoczecia;
    }

    public Long getCzas_zakonczenia() {
        return czas_zakonczenia;
    }

    public void setCzas_zakonczenia(Long czas_zakonczenia) {
        this.czas_zakonczenia = czas_zakonczenia;
    }

    public Integer getKalendarz_id() {
        return kalendarz_id;
    }

    public void setKalendarz_id(Integer kalendarz_id) {
        this.kalendarz_id = kalendarz_id;
    }

    public Long getKalendarz_zadanie_id() {
        return kalendarz_zadanie_id;
    }

    public void setKalendarz_zadanie_id(Long kalendarz_zadanie_id) {
        this.kalendarz_zadanie_id = kalendarz_zadanie_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRozliczona() {
        return rozliczona;
    }

    public void setRozliczona(String rozliczona) {
        this.rozliczona = rozliczona;
    }

    public String getFirma_nazwa() {
        return firma_nazwa;
    }

    public void setFirma_nazwa(String firma_nazwa) {
        this.firma_nazwa = firma_nazwa;
    }

    public String toStringDoRecyclerView() {
        return   firma_nazwa +
                " P: " + czas_rozpoczecia_string +
                " K: " + czas_zakonczenia_string +
                " " + opis +
                " S: " + status +
                " U: " + uwagi;

    }

    public String toStringDoKalendarza() {
        return   "[" + firma_nazwa + "]" +
                " " + opis +
                " [Status] " + status +
                " [Uwagi] " + uwagi;

    }

    @Override
    public String toString() {
        return "daneZlecenia{" +
                "czas_zawieszenia='" + czas_zawieszenia + '\'' +
                "czas_zawieszenia_string='" + czas_zawieszenia_string + '\'' +
                ", czas_rozpoczecia='" + czas_rozpoczecia + '\'' +
                ", czas_rozpoczecia_string='" + czas_rozpoczecia_string + '\'' +
                ", opis='" + opis + '\'' +
                ", firma_id=" + firma_id +
                ", czas_zakonczenia='" + czas_zakonczenia + '\'' +
                ", czas_zakonczenia_string='" + czas_zakonczenia_string + '\'' +
                ", status='" + status + '\'' +
                ", rozliczona='" + rozliczona + '\'' +
                ", firma_nazwa='" + firma_nazwa + '\'' +
                ", id=" + id +
                ", uwagi='" + uwagi + '\'' +
                ", poprzedni_rekord_id=" + poprzedni_rekord_id +
                ", poprzedni_rekord_data_usuniecia='" + poprzedni_rekord_data_usuniecia + '\'' +
                ", poprzedni_rekord_powod_usuniecia='" + poprzedni_rekord_powod_usuniecia + '\'' +
                ", czy_widoczny=" + czy_widoczny +
                '}';
    }

    public void onCreate(){
        this.setId(0);
        this.setCzas_rozpoczecia(0L);
        this.setCzas_rozpoczecia_string("");
        this.setCzas_zawieszenia(0L);
        this.setCzas_zawieszenia_string("");
        this.setOpis("");
        this.setFirma_id(0);
        this.setFirma_nazwa("");
        this.setCzas_zakonczenia(0L);
        this.setCzas_zakonczenia_string("");
        this.setStatus("");
        this.setRozliczona("");
        this.setKalendarz_id(0);
        this.setKalendarz_zadanie_id(0L);

    }

    public String toStringForRaport() {
        return  ";;;" +
                czas_rozpoczecia_string + ';' +
                czas_zakonczenia_string + ';' +
                firma_nazwa + ';' +
                String.valueOf(czas_zakonczenia - czas_rozpoczecia) + ';' +
                opis + ';' +
                status + ';' +
                uwagi;
    }
    public String toStringForRaportNaglowek() {
        return "LP.;Data;Osoba;Czas Rozpoczęcia;Czas Zakonczeńia;Firma;Liczba h;Opis;Status;Uwagi;Stawka;Zarobek\n";
    }
}