package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class FragmentAbout extends FragmentPodstawowy{
    private TextView textViewChangeLog;
    private String version = "1.0.2021.12.20.2359 BETA";

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle(getString(R.string.about));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ukryjFloatingButton();
        TextView textViewChangeLog = getActivity().findViewById(R.id.textViewChangeLog);
        textViewChangeLog.append(zmianyWprogramie());
        TextView textViewVersion = getActivity().findViewById(R.id.textViewVersion);
        textViewVersion.append(version);

    }

    private StringBuilder zmianyWprogramie(){
        String[][] zmiany = {
                {"2021.12.02", "Dodanie wymagań po pierwszym uruchomieniu aplikacji"},
                {"2021.12.02", "Ususnięcie możliwości dodania firmy z pustą nazwą"},
                {"2021.12.02", "Ususnięcie możliwości dodania zadania z pustym opisem"},
                {"2021.12.02", "Automatyczne dodawanie dat w polach dat w  raportach"},
                {"2021.12.02", "Naprawa nie działających raportów"},
                {"2021.12.06", "Usunięcie błędu przy ustawieniu statusu podczas 1 uruchomienia zadania"},
                {"2021.12.06", "Usunięcie błędu ze złym odwołaniem do kalendarzy"},
                {"2021.12.13", "Naprawa mechanizmu zapisu i wysyłania plików"},
                {"2021.12.13", "Usunięcie guzika FAB z formatek, gdzie jest bezużyteczny"},
                {"2021.12.14", "Poprawienie mechanizmu tworzenia kopii zapasowej"},
                {"2021.12.16", "Dodanie filtrów na listach zadań"},
                {"2021.12.16", "Automatyczne dodawanie stawek przy tworzeniu nowej firmy"},
                {"2021.12.20", "Naprawa wyświetlania dat przy poprawianiu zleceń"},
                {"2022.01.31", "Wdrażanie synchronizacji danych z zewnętrznym serwerem"},
                {"2022.02.24", "Wdrożenie synchronizacji danych z zewnętrznym serwerem 3 + N, N = {0, 1, 2, 3, ...} "},
                {"2022.04.22", "Naprawa synchronizacji zwrotnej, wyelimionwanie kilku błędów"},
                {"2022.09.09", "Stworzenie interfejsu do grzebania w bazie"},
                {"2022.09.13", "Poprawa eksportu i wysyłki backupu bazy"},
                {"2022.09.13", "Poprawa raportów wyliczeniowych"},
                {"2022.09.13", "Poprawa nagłówka raportów"}
        };
        StringBuilder uzupelnijDane = new StringBuilder();
        uzupelnijDane.append("\n");
        for (String[] zmiana: zmiany) {
            uzupelnijDane.append(zmiana[0] + " " + zmiana[1] + "\n");
        }
        return  uzupelnijDane;
    }

}
