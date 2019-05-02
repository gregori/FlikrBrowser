package tech.gregori.flikrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe helper para baixar Json do flickr
 * Recebe a URL para o flikr, não faz progresso e retorna uma lista de fotos
 * Trabalhamos como uma AsyncTask pois a conversão do JSON pode demorar.
 */
class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null; // lista de fotos que buscaremos
    private String mBaseURL; // URL base para a busca
    private String mLanguage; // língua a buscar
    private boolean mMatchAll; // todas as tags de busca? ou qualquer uma delas

    private final OnDataAvailable mCallback;
    private boolean mRunningOnSameThread = false; // Estamos rodando este código na mesma thread?
                                                  // ou em uma thread separada?

    /**
     * Pode enviar um callback quando tiver dados para enviar
     * para a classe que deseja recebê-los
     */
    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    GetFlickrJsonData(OnDataAvailable callback, String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: chamada!");
        mCallback = callback;
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
    }

    /**
     * Este método só é executado se for chamado de uma thread diferente
     * @param photos a lista de fotos retornada por doInBackground
     */
    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: começou");
        if (mCallback != null) {
            mCallback.onDataAvailable(photos, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: terminou");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: começou");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);

        Log.d(TAG, "doInBackground: terminou");
        return mPhotoList;
    }

    /**
     * Executa uma busca na mesma thread
     * @param searchCriteria critério para a busca
     */
    @SuppressWarnings("unused")
    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "executeOnSameThread: inicia");
        mRunningOnSameThread = true;  // com este método, não estamos em threads separadas
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
                    String tags = jsonPhoto.getString("tags");

                    // retorna uma imagem em tamanho de tumbnail
                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    // a URL do maior tamanho possível, por isso trocamos _m da url por _b
                    // https://www.flickr.com/services/api/misc.urls.html
                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Erro processndo dados JSON " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        // só é executado se rodar em uma mesma thread
        if (mRunningOnSameThread && mCallback != null) {
            // vamos informar a classe que solicitou o parse de que que o processo está pronto
            // possivelmente retornando null se houve um erro
            mCallback.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete: terminou");
    }
}
