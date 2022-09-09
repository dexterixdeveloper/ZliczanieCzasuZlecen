package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;

public class OSQLbackupDataBaseNaZywca extends ObslugaSQL{
    public OSQLbackupDataBaseNaZywca(Context context) {
        super(context);
    }

    public void openDataBase2() {
        openDataBase1();
    }

    //potrzebne do grzebania w bazie
    /*protected SQLiteDatabase openDataBase(){
        SQLiteDatabase db = getWritableDatabase();
        return db;
    }*/

    /*protected void zamknijBaze(SQLiteDatabase db){
        //SQLiteDatabase db = null;
        db.close();
    }//public void zamknijBaze(){*/
    //ene potrzebne do grzebania w bazie
}
