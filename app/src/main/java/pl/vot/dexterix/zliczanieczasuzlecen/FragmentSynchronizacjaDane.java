package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

public class FragmentSynchronizacjaDane extends FragmentPodstawowy {

    private daneSynchronizacja danaKlasy;
    private daneSynchronizacja danaKlasyStara;
    private TextInputEditText textInputEditTextAdres;
    private TextInputEditText textInputEditTextHaslo;
    private TextInputEditText textInputEditTextAdresSerwera;
    private TextInputEditText textInputEditTextKodUrzadzenia;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle("Dane Synchronizacji");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_synchronizacja_dane, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textInputEditTextAdres = (TextInputEditText) view.findViewById(R.id.textInputEditTextAdres);
        textInputEditTextHaslo = (TextInputEditText) view.findViewById(R.id.textInputEditTextHaslo);
        textInputEditTextAdresSerwera = (TextInputEditText) view.findViewById(R.id.textInputEditTextAdresSerwera);
        textInputEditTextKodUrzadzenia = (TextInputEditText) view.findViewById(R.id.textInputEditTextKodUrzadzenia);

        OSQLdaneSynchronizacja osql = new OSQLdaneSynchronizacja(getActivity());
        danaKlasy = osql.dajOkreslonyRekord(1);//pobiermy 1 rekord, czyli aktualny jakby co
        danaKlasyStara = danaKlasy;
        Log.d("FragmentSynchronizacjaDane:  ", String.valueOf(1));

        //ustawiamy parametry klasy
        textInputEditTextAdres.setText(String.valueOf(danaKlasy.getLogin()));
        textInputEditTextHaslo.setText(String.valueOf(danaKlasy.getHaslo()));
        textInputEditTextAdresSerwera.setText(String.valueOf(danaKlasy.getLink()));
        textInputEditTextKodUrzadzenia.setText(String.valueOf(danaKlasy.getKod_urzadzenia()));

        ukryjFloatingButton();
        setNewDataButton();
    }

    private void setNewDataButton() {
        Button button = getActivity().findViewById(R.id.buttonZapisz);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zapiszDaneICofnijDoPoprzedniegofragmentu();
            }
        });
    }

    private void zapiszDaneICofnijDoPoprzedniegofragmentu() {
        Context context = getActivity();
        StringBuilder uzupelnijDane = new StringBuilder();
        uzupelnijDane.append("Uzupełnij dane: \n");
        int daneDoUzupelnienia = 0;

        danaKlasy.setLogin(String.valueOf(textInputEditTextAdres.getText()));
        danaKlasy.setHaslo(String.valueOf(textInputEditTextHaslo.getText()));
        danaKlasy.setLink(String.valueOf(textInputEditTextAdresSerwera.getText()));
        danaKlasy.setKod_urzadzenia(String.valueOf(textInputEditTextKodUrzadzenia.getText()));


        if (danaKlasy.getLogin().equals("")){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Login\n");
        }
        if (danaKlasy.getHaslo().equals("")){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Hasło\n");
        }
        if (danaKlasy.getLink().equals("")){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Link\n");
        }

        if (daneDoUzupelnienia > 0) {
            Toast.makeText(context, uzupelnijDane, Toast.LENGTH_SHORT).show();
            Log.d("do uzupelnienia", uzupelnijDane.toString());
        }else{
            zapiszDane();
            cofnijDoPoprzedniegoFragmentu();
        }
    }

    private void zapiszDane() {
        OSQLdaneSynchronizacja osql = new OSQLdaneSynchronizacja(getActivity());
        long idRekordu = -1;
        danaKlasyStara.setCzy_widoczny(0);
        idRekordu = osql.dodajDane(danaKlasyStara);
        danaKlasy.setPoprzedni_rekord_id((int) idRekordu);
        danaKlasy.setPoprzedni_rekord_powod_usuniecia("Update");
        osql.updateDane(danaKlasy);

    }


}
