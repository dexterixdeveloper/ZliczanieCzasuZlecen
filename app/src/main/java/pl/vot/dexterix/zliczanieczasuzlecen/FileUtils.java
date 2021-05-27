package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Environment;

import java.io.File;

//import pl.vot.dexterix.elektronicznaksiazkaautabydex.App;

public class FileUtils {

    public static String getAppDir(String sciezkaDoAplikacji){
        //return String.format("%s/%s", (FileUtils.this).getAppDir().getContext().getExternalFilesDir(null), FileUtils.this.getContext().getString(R.string.app_name));
        //Environment..getExternalFilesDir(null);


        //return (FileUtils.this).getAppDir();
        return sciezkaDoAplikacji;
        //new context();
    }


    public static File createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdir();
        }
        return dir;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
