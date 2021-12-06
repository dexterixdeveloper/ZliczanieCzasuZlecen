package pl.vot.dexterix.zliczanieczasuzlecen;

public class daneUstawienia extends daneKlasaPodstawowa{
    private String ustawienie;
    private String wartosc;
    private String typDanych;

    public String getUstawienie() {
        return ustawienie;
    }

    public void setUstawienie(String ustawienie) {
        this.ustawienie = ustawienie;
    }

    public String getWartosc() {
        return wartosc;
    }

    public void setWartosc(String wartosc) {
        this.wartosc = wartosc;
    }

    public String getTypDanych() {
        return typDanych;
    }

    public void setTypDanych(String typDanych) {
        this.typDanych = typDanych;
    }

    public void onCreate(){
        this.setId(0);
        this.setUstawienie("");
        this.setWartosc("");
        this.setUwagi("");
        this.setTypDanych("");
    }
}
