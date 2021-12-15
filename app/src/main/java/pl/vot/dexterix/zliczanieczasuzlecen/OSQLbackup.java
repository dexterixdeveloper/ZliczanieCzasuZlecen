package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OSQLbackup extends ObslugaSQL{
    public OSQLbackup(Context context) {
        super(context);
    }

    public List<String> getTablesOnDataBase(){
        Cursor c = null;
        List<String> tables = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try{
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    if(!c.getString(0).contains("android_metadata") && !c.getString(0).contains("sqlite_sequence")) {
                        tables.add(c.getString(0));
                    }
                    c.moveToNext();
                }
            }
        }
        catch(Exception throwable){
            Log.e("ExportPodajTabele", "Could not get the table names from db", throwable);
        }
        finally{
            if(c!=null)
                c.close();
        }
        db.close();
        return tables;
    }

}

