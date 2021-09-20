package pl.vot.dexterix.zliczanieczasuzlecen;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class FragmentPodstawowy extends Fragment {
    private static final String CHANNEL_ID = "PowiadomienieZliczanieCzasuZlecen";

    protected void zmianaFragmentu(Fragment fragmencik, String tagBackStack, Boolean addToBackStack){
        // Begin the transaction
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment

        ft.replace(R.id.fragment_container_main, fragmencik, tagBackStack);
        Log.d("BackToStack", "Bez Add to back");

// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
    }

    protected void zmianaFragmentu(Fragment fragmencik, String tagBackStack){
        // Begin the transaction
        Log.d("Back2", "2");
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

// Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_container_main, fragmencik, tagBackStack);
        ft.addToBackStack(tagBackStack);

// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
    }

    protected Fragment getVisibleFragment(){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.d("Ilość fragmentów: ", String.valueOf(fragments.size()));
        if(fragments != null){
            for(Fragment fragment : fragments){
                Log.d("fragment1", fragment.getTag() + " " + fragment.getId());
                if(fragment != null && fragment.isVisible())
                    Log.d("fragment", fragment.toString());
                return fragment;
            }
        }
        return null;
    }

    protected void cofnijDoPoprzedniegoFragmentu() {
        //fragment Manager przenosi nas do poprzedniego fragmentu
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    protected void pokazPowiadomienie(String tytul, String opis, String opis2, int notificationId1 ){
        //takie tam powiadominie sobie wrzucamy
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                //.setSmallIcon(R.drawable.notification_icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(tytul)
                .setContentText(opis)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(opis2))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //powiadomienie

        //to pokazujemy powiadmomienie
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // notificationId is a unique int for each notification that you must define
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
        //to pokazaliśmy
    }
}
