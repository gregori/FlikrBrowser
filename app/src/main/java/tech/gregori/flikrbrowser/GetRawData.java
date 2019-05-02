package tech.gregori.flikrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Enum para conter os estados do download
 */

enum DownloadStatus { IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK }

/**
 * GetRawData. Classe assíncrona para download de texto.
 * Criamos agora uma classe separada, para mantê-la desacoplada do resto do código.
 * Pode fazer download de qualquer formato de texto, json, XML, texto plano, HTML
 */

class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";
    private DownloadStatus mDownloadStatus; // prefixo 'm' indica que a variável é 'M'embro da classe
    private final OnDownloadComplete mCallback; // trazemos uma classe que implemente a interface para chamarmos o método de callback


    /**
     * Interface que garante que uma classe deverá implementar o método de callback
     *
     */
    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }


    GetRawData(OnDownloadComplete callback) {
        mDownloadStatus = DownloadStatus.IDLE; // Inicializamos com IDLE pois no momento a classe
                                               // não está fazendo nada (idle = ocioso)
        mCallback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (strings == null) { // se não recebemos a URL para o dnwnload
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        try {
            mDownloadStatus = DownloadStatus.PROCESSING; // começamos a processar a URL
            URL url = new URL(strings[0]); // convertendo a string para a classe URL

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // faremos um "GET" na url
            connection.connect(); // conecta à URL
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: O código de resposta foi: " + response);

            StringBuilder result = new StringBuilder(); // result conterá os dados que baixamos

            // recebemos, no objeto de BufferedReader o inputStream, ou seja, o fluxo de
            // dados que estamos baixando.
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // enquanto houver texto a ser baixado
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                // adicionamos o texto ao StringBuilder
                result.append(line).append('\n');
            }

            mDownloadStatus = DownloadStatus.OK; // terminamos o download

            return result.toString(); // retornamos o StringBuilder como uma string

        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: URL inválida " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IOException lendo dados: " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: SecurityException: Falta permissão? " + e.getMessage());
        } finally { // mesmo aparecendo depois do return, é executado *ANTES* do return
            // se deu erro, ou não, devemos fechar as conexões
            if (connection != null) {
                connection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Erro fechando a Stream: " + e.getMessage());
                }
            }
        }

        // se chegamos aqui foi porque houve um erro ao fazer o download
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    /**
     * Este método faz com que o download não seja executado como uma tarefa em paralelo
     * @param s URL para download
     */
    @SuppressWarnings("unused")
    void runInSameThread(String s) {
        Log.d(TAG, "runInSameThread: começou");

        onPostExecute(doInBackground(s));

        Log.d(TAG, "runInSameThread: terminou");
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: começou");

        if (mCallback != null) {
            mCallback.onDownloadComplete(s, mDownloadStatus); // se temos um callback, chamamos
                                                            // o método de DownloadComplete
        }
        Log.d(TAG, "onPostExecute: terminou");
    }
}
