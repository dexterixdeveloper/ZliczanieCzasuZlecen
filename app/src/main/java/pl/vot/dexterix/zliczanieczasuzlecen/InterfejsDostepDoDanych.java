package pl.vot.dexterix.zliczanieczasuzlecen;

import java.util.List;

public interface InterfejsDostepDoDanych {

    String getTableName();
    <T> List<T> dajWszystkie();
    void zerujDateSynchronizacji();
    void zerujDateUtworzenia();
    //<T> void updateDane(T t);
    //<T> List<T> dajDoSynchronizacji();
    <T> T dajOkreslonyRekord(Integer _id);
    <T> List<T> dajWszystkieDoRecyclerView();
    //<T> long dodajZastapDane(T t);
    //<T> long dodajDane(T t);
    //<T> Class<T> wykopTablice();
    default String getNazwaKlasyDanych(String nazwa){
        return nazwa;
    }

}
