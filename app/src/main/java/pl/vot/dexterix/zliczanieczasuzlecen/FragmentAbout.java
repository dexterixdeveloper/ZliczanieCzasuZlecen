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
        TextView textViewChangeLog = getActivity().findViewById(R.id.textViewChangeLog);
        textViewChangeLog.append(zmianyWprogramie());

    }
    private StringBuilder zmianyWprogramie(){
        String[][] zmiany = {
                {"2021.12.02", "Dodanie wymagań po pierwszym uruchomieniu aplikacji"},
                {"2021.12.02", "Ususnięcie możliwości dodania firmy z pustą nazwą"},
                {"2021.12.02", "Ususnięcie możliwości dodania zadania z pustym opisem"},
                {"2021.12.02", "Automatyczne dodawanie dat w polach dat w  raportach"},
                {"2021.12.02", "Naprawa nie działających raportów"}
        };
        StringBuilder uzupelnijDane = new StringBuilder();
        uzupelnijDane.append("\n");
        for (String[] zmiana: zmiany) {
            uzupelnijDane.append(zmiana[0] + " " + zmiana[1] + "\n");
        }
        return  uzupelnijDane;
    }

}
