package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SQLSynchMySingleton {

    private static SQLSynchMySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;



    private SQLSynchMySingleton(Context context){
        mContext= context;
        requestQueue = getRequestQueue();

    }


    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized SQLSynchMySingleton getmInstance(Context context){
        if(mInstance ==null){
            mInstance= new SQLSynchMySingleton(context);
        }
        return mInstance;
    }


    public <T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

}



