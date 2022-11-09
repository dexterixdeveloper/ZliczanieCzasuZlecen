package pl.vot.dexterix.zliczanieczasuzlecen;

import java.util.HashMap;
import java.util.Map;

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
    //tak ma kurwa być jak na dole
    public daneSynchronizacja(){
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

    public Map<String, String> getDanaMap(){
        Map<String, String> param = new HashMap<String, String>();
        param.put("login", login);
        param.put("haslo", haslo);
        param.put("link", link);
        param.put("kod_urzadzenia", kod_urzadzenia);
        return  param;

    }

    @Override
    public String toString() {
        return "daneSynchronizacja{" +
                "login='" + login + '\'' +
                ", haslo='" + haslo + '\'' +
                ", link='" + link + '\'' +
                ", kod_urzadzenia='" + kod_urzadzenia + '\'' +
                ", id=" + id +
                ", uwagi='" + uwagi + '\'' +
                ", poprzedni_rekord_id=" + poprzedni_rekord_id +
                ", poprzedni_rekord_data_usuniecia='" + poprzedni_rekord_data_usuniecia + '\'' +
                ", poprzedni_rekord_powod_usuniecia='" + poprzedni_rekord_powod_usuniecia + '\'' +
                ", czy_widoczny=" + czy_widoczny +
                ", synchron=" + synchron +
                ", data_utworzenia=" + data_utworzenia +
                ", data_synchronizacji=" + data_synchronizacji +
                '}';
    }
}
