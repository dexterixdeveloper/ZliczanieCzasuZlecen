package pl.vot.dexterix.zliczanieczasuzlecen;

public class daneSynchronizacja extends daneKlasaPodstawowa{
    private String login;
    private String haslo;
    private String link;
    private String kod_urzadzenia;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getKod_urzadzenia() {
        return kod_urzadzenia;
    }

    public void setKod_urzadzenia(String kod_urzadzenia) {
        this.kod_urzadzenia = kod_urzadzenia;
    }

    public void onCreate(){
        this.setId(0);
        this.setLogin("");
        this.setHaslo("");
        this.setUwagi("");
        this.setLink("");
        this.setKod_urzadzenia("");
        this.setSynchron(0);
        this.setData_synchronizacji(0);
        this.setData_utworzenia(0);
    }


}
