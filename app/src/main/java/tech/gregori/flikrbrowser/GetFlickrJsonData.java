package tech.gregori.flikrbrowser;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe helper para baixar Json do flickr
 */
class GetFlickrJsonData implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null; // lista de fotos que buscaremos
    private String mBaseURL; // URL base para a busca
    private String mLanguage; // língua a buscar
    private boolean mMatchAll; // todas as tags de busca? ou qualquer uma delas

    private final OnDataAvailable mCallback;

    /**
     * Pode enviar um callback quando tiver dados para enviar
     * para a classe que deseja recebê-los
     */
    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(String baseURL, OnDataAvailable callback, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: chamada!");
        mBaseURL = baseURL;
        mCallback = callback;
        mLanguage = language;
        mMatchAll = matchAll;
    }

    /**
     * Executa uma busca na mesma thread
     * @param searchCriteria critério para a busca
     */
    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "executeOnSameThread: inicia");
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: terminou");
    }

    private String createUri(String searchCriteria, String language, boolean matchAll) {
        Log.d(TAG, "createUri: iniciou");

        // Constrói uma URI de maneira conveniente,
        // com métodos appendQueryParameter
        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("lang", language)
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }


    /**
     * Converte o JSON que foi baixado em uma Lista de fotos
     * @param data String correspondente ao JSON baixado.
     * @param status status do download.
     */
    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: inicia. Status = " + status);

        if (status == DownloadStatus.OK) {
            mPhotoList = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tagws = jsonPhoto.getString("tags");

                    // retorna uma imagem em tamanho de tumbnail
                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    // a URL do maior tamanho possível, por isso trocamos _m da url por _b
                    String link = photoUrl.replaceFirst("_m.", "_b.");
                }
            }
        }
    }
}
