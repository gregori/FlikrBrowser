package tech.gregori.flikrbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable,
        RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
    private static final String LANG = "pt-br";
    private static final boolean MATCH_ALL = true;
    private FlickrRecyclerViewAdapter mFlickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Começou");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // não necessitamos do botão home aqui, pois é a activity principal
        activateToolbar(false);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        mFlickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this, new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecyclerViewAdapter);

        Log.d(TAG, "onCreate: Terminou");
    }

    /**
     * Fazemos o download do JSON em onResume
     * pois com a troca de Activities geralmente este
     * método será chamado, e não onCreate
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: começou");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this, BASE_URL, LANG, MATCH_ALL);
        //getFlickrJsonData.executeOnSameThread("android, nougat");
        getFlickrJsonData.execute("hearthstone, game");
        Log.d(TAG, "onResume: terminou");
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
    public void onDataAvailable(List<Photo> data, DownloadStatus status) {
        Log.d(TAG, "onDataAvailable: iniciou");
        if (status == DownloadStatus.OK) {
            mFlickrRecyclerViewAdapter.loadNewData(data);
        } else {
            // houve falha no processo do download
            Log.e(TAG, "onDataAvailable: falhou com status " + status);
        }

        Log.d(TAG, "onDataAvailable: terminou");
    }

    /**
     * Trata um clique normal na RecyclerView.
     * @param view A view que recebeu o clique
     * @param position a posição, na view, onde foi clicado
     */
    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: começou");
        Toast.makeText(this, "Tap normal na posição " + position, Toast.LENGTH_SHORT).show();
    }

    /**
     * Trata um clique longo na RecyclerView
     * @param view A view que recebeu o clique
     * @param position a posição, na view, onde foi clicado
     */
    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: começou");
//        Toast.makeText(this, "Clique longo na posição " + position, Toast.LENGTH_SHORT).show();
        // Criamos um Intent e passamos um objeto de Photo para a próxima activity
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        // Note que quem mantém uma lista de fotos é o Adapter!
        // Usamos uma constante para garantirmos que todas as classes usem a mesma String
        intent.putExtra(PHOTO_TRANSFER, mFlickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}
