package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class FragmentAbout extends FragmentPodstawowy{
    private TextView textViewChangeLog;
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
                {"2021.12.14", "Poprawienie mechanizmu tworzenia kopii zapasowej"}
        };
        StringBuilder uzupelnijDane = new StringBuilder();
        uzupelnijDane.append("\n");
        for (String[] zmiana: zmiany) {
            uzupelnijDane.append(zmiana[0] + " " + zmiana[1] + "\n");
        }
        return  uzupelnijDane;
    }

}
