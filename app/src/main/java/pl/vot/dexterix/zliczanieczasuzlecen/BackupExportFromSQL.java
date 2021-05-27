package pl.vot.dexterix.zliczanieczasuzlecen;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.opencsv.CSVWriter;


/**
 * Can export an sqlite databse into a csv file.
 *
 * The file has on the top dbVersion and on top of each table data the name of the table
 *
 * Inspired by
 * https://stackoverflow.com/questions/31367270/exporting-sqlite-database-to-csv-file-in-android
 * and some other SO threads as well.
 *
 */
public class BackupExportFromSQL {

    private static final String TAG = BackupExportFromSQL.class.getSimpleName();

    public static final String DB_BACKUP_DB_VERSION_KEY = "dbVersion";
    public static final String DB_BACKUP_TABLE_NAME = "table";

    public static void export(SQLiteDatabase db, String sciezkaB, Context context) throws IOException{
    //public static String export(SQLiteDatabase db, String sciezkaB, Context context) throws IOException{
        if( !FileUtils.isExternalStorageWritable() ){
            throw new IOException("Cannot write to external storage");
        }
        //File backupDir = FileUtils.createDirIfNotExist(FileUtils.getAppDir(sciezkaB) + "/backup");
        //ActivitySettings.this.getApplicationInfo().dataDir
        //File backupDir = FileUtils.createDirIfNotExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        File backupDir = FileUtils.createDirIfNotExist(  context.getApplicationInfo().dataDir + "/backup");
        Log.d("Path: ", context.getApplicationInfo().dataDir + "/backup");
        //Log.d("Path: ", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        String fileName = createBackupFileName();

        File backupFile = new File(backupDir, fileName);

        //File backupFile = new File(sciezkaB.getPath());

        //File backupFile = new File(sciezkaB);
        //backupFile = sciezkaB;
        boolean success = backupFile.createNewFile();
        if(!success){
            throw new IOException("Failed to create the backup file");
        }
        List<String> tables = getTablesOnDataBase(db);
        Log.d(TAG, "Started to fill the backup file in " + backupFile.getAbsolutePath());
        Toast.makeText(context, "Started to fill the backup file in " + backupFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        long starTime = System.currentTimeMillis();
        writeCsv(backupFile, db, tables);
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Creating backup took " + (endTime - starTime) + "ms.");
        Toast.makeText(context, "Creating backup took " + (endTime - starTime) + "ms.", Toast.LENGTH_SHORT).show();

        //return backupFile.getAbsolutePath();
    }

    public static String createBackupFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        return "db_backup_" + sdf.format(new Date()) + "_ZliczanieCzasuZlecen.csv";
    }

    /**
     * Get all the table names we have in db
     *
     * @param db
     * @return
     */
    public static List<String> getTablesOnDataBase(SQLiteDatabase db){
        Cursor c = null;
        List<String> tables = new ArrayList<>();
        try{
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    if (c.getString(0) != "android_metadata" & c.getString(0) != "sqlite_sequence" ) {//zabezpieczamy sie przed zapisaniem tabel systemowych
                        tables.add(c.getString(0));
                        c.moveToNext();
                    }
                }
            }
        }
        catch(Exception throwable){
            Log.e(TAG, "Could not get the table names from db", throwable);
        }
        finally{
            if(c!=null)
                c.close();
        }
        return tables;
    }

    private static void writeCsv(File backupFile, SQLiteDatabase db, List<String> tables){
        CSVWriter csvWrite = null;
        Cursor curCSV = null;
        try {
            csvWrite = new CSVWriter(new FileWriter(backupFile));
            writeSingleValue(csvWrite, DB_BACKUP_DB_VERSION_KEY + "=" + db.getVersion());
            for(String table: tables){
                writeSingleValue(csvWrite, DB_BACKUP_TABLE_NAME + "=" + table);
                curCSV = db.rawQuery("SELECT * FROM " + table,null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    int columns = curCSV.getColumnCount();
                    String[] columnArr = new String[columns];
                    for( int i = 0; i < columns; i++){
                        columnArr[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(columnArr);
                }
            }
        }
        catch(Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }finally {
            if(csvWrite != null){
                try {
                    csvWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( curCSV != null ){
                curCSV.close();
            }
        }
    }


    private static void writeSingleValue(CSVWriter writer, String value){
        writer.writeNext(new String[]{value});
    }
}