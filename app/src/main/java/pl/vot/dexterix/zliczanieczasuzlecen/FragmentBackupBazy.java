package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class FragmentBackupBazy extends FragmentPodstawowy {

    private TextView textViewStanBazy;
    private String baza;
    //private OSQLbackupDataBaseNaZywca osql = new OSQLbackupDataBaseNaZywca(getActivity());

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        changeTitle("StanBazy");

        return inflater.inflate(R.layout.fragment_backup_bazy, container, false);
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //String zz = ActivitySettings.this.getApplicationInfo().dataDir;
        ukryjFloatingButton();
        //poprosOUprawnienia();
        textViewStanBazy =  (TextView) view.findViewById(R.id.textViewStanBazy);

        addListenerOnButtonOpen(R.id.buttonOtworzBaze);
        addListenerOnButtonClose(R.id.buttonZamknijBaze);
        addListenerOnButtonZamknij(R.id.buttonZamknij);

    }

    private void addListenerOnButtonOpen(int button) {


        Button buttonGuzik = (Button) getActivity().findViewById(button);
        buttonGuzik.setText("Otwórz");
        buttonGuzik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //renumerujRekordy();
                //createFile("text/plain", "foobar.txt");
                OSQLbackupDataBaseNaZywca osql = new OSQLbackupDataBaseNaZywca(getActivity());
                osql.openDataBase2();
                textViewStanBazy.setText("Baza Otwarta");

                //Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void addListenerOnButtonClose(int button) {


        Button buttonGuzik = (Button) getActivity().findViewById(button);
        buttonGuzik.setText("Zamknij");
        buttonGuzik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //renumerujRekordy();
                //createFile("text/plain", "foobar.txt");
                if (textViewStanBazy.getText() == "Baza Otwarta") {
                    OSQLbackupDataBaseNaZywca osql = new OSQLbackupDataBaseNaZywca(getActivity());
                    osql.zamknijBaze(baza);
                    textViewStanBazy.setText("Baza Zamknięta");
                }
                //Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void addListenerOnButtonZamknij(int button) {


        Button buttonGuzik = (Button) getActivity().findViewById(button);
        buttonGuzik.setText("Synchronizacja");
        buttonGuzik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //renumerujRekordy();
                //createFile("text/plain", "foobar.txt");

                cofnijDoPoprzedniegoFragmentu();
                //Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();


            }
        });
    }
}
