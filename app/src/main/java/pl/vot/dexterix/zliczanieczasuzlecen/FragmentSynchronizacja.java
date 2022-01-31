package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

public class FragmentSynchronizacja extends FragmentPodstawowy {
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle("Synchronizacja");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_synchronizacja, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Fragment z1 = getVisibleFragment();

        ukryjFloatingButton();
        addListenerOnButtonSynchronizujWszystko();

    }

    private void addListenerOnButtonSynchronizujWszystko() {
        Button button = getActivity().findViewById(R.id.buttonSynchronizujWszystko);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronizujFirmy(R.id.textViewSynchronizacja);
                synchronizujStawki(R.id.textViewSynchronizacja);
                synchronizujZlecenia(R.id.textViewSynchronizacja);
            }
        });
    }
}
