package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import pl.vot.dexterix.zliczanieczasuzlecen.R;

import java.io.File;

public class FileSelectActivity extends Activity{
    // The path to the root of this app's internal storage
    private File privateRootDir;
    // The path to the "images" subdirectory
    private File imagesDir;
    // Array of files in the images subdirectory
    File[] imageFiles;
    // Array of filenames corresponding to imageFiles
    String[] imageFilenames;
    // Initialize the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set up an Intent to send back to apps that request a file
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        addListenerOnButtonRestore(R.id.buttonZrobione);
        Intent resultIntent = new Intent("com.example.myapp.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        privateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        imagesDir = new File(privateRootDir, "backup");
        // Get the files in the images subdirectory
        imageFiles = imagesDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);
        /*
         * Display the file names in the ListView fileListView.
         * Back the ListView with the array imageFilenames, which
         * you can create by iterating through imageFiles and
         * calling File.getAbsolutePath() for each File
         */
        ListView fileListView = (ListView) findViewById(R.id.listviewSelectFiles);
        String[] imageFilenames = new String[imageFiles.length];
        for (int i = 0; i < imageFilenames.length; ++i) {
            imageFilenames[i] = imageFiles[i].getName();
            Toast.makeText(this,
                    imageFilenames[i] + "<-- imageFilenames[i]" + " FOR",
                    Toast.LENGTH_LONG).show();

        }

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.list_view_rows, R.id.listviewSendFile, imageFilenames);

            fileListView.setAdapter(adapter);
            // listViewSelectStudent.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "ArrayAdapter Error :" + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }
        // Define a listener that responds to clicks on a file in the ListView
        fileListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    /*
                     * When a filename in the ListView is clicked, get its
                     * content URI and send it to the requesting app
                     */
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view,
                                            int position,
                                            long rowId) {
                        /*
                         * Get a File for the selected file name.
                         * Assume that the file names are in the
                         * imageFilename array.
                         */
                        File requestFile = new File(imageFilenames[position]);
                        /*
                         * Most file-related method calls need to be in
                         * try-catch blocks.
                         */
                        // Use the FileProvider to get a content URI
                        Uri fileUri = null;
                        try {
                             fileUri = FileProvider.getUriForFile(
                                    FileSelectActivity.this,
                                    "com.example.myapp.fileprovider",
                                    requestFile);
                        } catch (IllegalArgumentException e) {
                            Log.e("File Selector",
                                    "The selected file can't be shared: " + requestFile.toString());
                        }

                        if (fileUri != null) {
                            // Grant temporary read permission to the content URI
                            resultIntent.addFlags(
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            // Put the Uri and MIME type in the result Intent
                            resultIntent.setDataAndType(
                                    fileUri,
                                    getContentResolver().getType(fileUri));
                            // Set the result
                            FileSelectActivity.this.setResult(Activity.RESULT_OK,
                                    resultIntent);
                        } else {
                            resultIntent.setDataAndType(null, "");
                            FileSelectActivity.this.setResult(RESULT_CANCELED,
                                    resultIntent);
                        }

                    }


                });



    }
    private void addListenerOnButtonRestore(int buttonZrobione) {
        Button buttonBackupRestore = (Button) findViewById(buttonZrobione);
        buttonBackupRestore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();

                /*ObslugaSQL osql = new ObslugaSQL(ActivitySettings.this);
                Log.d("Katalog: ", yy);
                osql.zrobKopieBazy(yy, ActivitySettings.this);*/
            }
        });
    }
    public void onDoneClick(View v) {
        // Associate a method with the Done button
        finish();
    }
}


