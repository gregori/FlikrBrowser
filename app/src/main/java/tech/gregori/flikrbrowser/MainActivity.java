package tech.gregori.flikrbrowser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements GetRawData.OnDownloadComplete {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Começou");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute("https://api.flickr.com/services/feeds/photos_public.gne?tags-android,nougat,sdk&tagmode=any&format=json&lang=pt-br&nojsoncallback=1");

        Log.d(TAG, "onCreate: Terminou");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() retornou: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected() retornou: retornou");

        return super.onOptionsItemSelected(item);
    }


    /**
     * onDownloadComplete. Trata o download após efetuado.
     * @param data Dado que foi baixado
     * @param status O status do download
     */
    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete: data é " + data);
        } else {
            Log.e(TAG, "onDownloadComplete: falhou com status " + status);
        }
    }
}
