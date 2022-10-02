package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;

import java.util.List;

public interface InterfejsDostepDoDanych {

    default ContentValues contentValuesW(daneKlasaPodstawowa dane_funkcji){
        ContentValues wartosci = new ContentValues();

        wartosci.put("_id", dane_funkcji.getId());

        wartosci.put("uwagi", dane_funkcji.getUwagi());
        wartosci.put("poprzedni_rekord_id", dane_funkcji.getPoprzedni_rekord_id());
        wartosci.put("poprzedni_rekord_data_usuniecia", dane_funkcji.getPoprzedni_rekord_data_usuniecia());
        wartosci.put("poprzedni_rekord_powod_usuniecia", dane_funkcji.getPoprzedni_rekord_powod_usuniecia());
        wartosci.put("synchron", dane_funkcji.getSynchron());
        if(dane_funkcji.getData_synchronizacji() > 0){
            wartosci.put("data_synchronizacji", dane_funkcji.getData_synchronizacji());
        }else{
            wartosci.put("data_synchronizacji", 0);
        }
        if(dane_funkcji.getData_utworzenia() > 0){
            wartosci.put("data_utworzenia", dane_funkcji.getData_utworzenia());
        }else{
            wartosci.put("data_utworzenia", 0);
        }
        //Log.d("OSQLdaneFirma: Uwagi: ", dane_funkcji.getUwagi());
        return wartosci;
    }

    String getTableName();
    <T> List<T> dajWszystkie();
    void zerujDateSynchronizacji();
    void zerujDateUtworzenia();
    //<T> void updateDane(T t);
    <T> List<T> dajDoSynchronizacji();
    <T> T dajOkreslonyRekord(Integer _id);
    <T> List<T> dajWszystkieDoRecyclerView();
    //<T> long dodajZastapDane(T t);
    //<T> long dodajDane(T t);

    default String getNazwaKlasyDanych(String nazwa){
        return nazwa;
    }

}
