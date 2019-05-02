package tech.gregori.flikrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter para o RecyclerView. Similar ao que fizemos no ListView
 * A diferença é que o RecyclerView nos obriga a utilizar um ViewHolder
 * Neste caso, o ViewHolder é parametrizado no extends.
 */
class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";

    private List<Photo> mPhotoList; // Lista de fotos que vamos armazenar, para mostrar na view
    private Context mContext; // o contexto no qual a view se encontra

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photoList) {
        mContext = context;
        mPhotoList = photoList;
    }

    /**
     * Chamado pelo LayoutManager quando uma nova view, dentro da lista, é necessária
     * @param parent a referência à view que chamou este método. No caso o RecyclerView
     * @param viewType a posição em que a View estará. No nosso caso será também a posição do arrayList
     * @return
     */
    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: nova view requisitada");

        // Vamos "inflar" a view para podermos utilizá-la e passamos por parâmetro para o viewHolder
        // o ViewHolder buscará os campos do leiaute e permitirá que o utilizemos nesta classe;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder flickrImageViewHolder, int position) {

    }

    /**
     * @return o tamanho da lista.
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: foi chamado");
        // se temos uma lista e seu tamanho é maior que zero, retornamos o tamanho,
        // senão, retornamos 0
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.size() : 0);
    }

    /**
     * Carrega uma lista de fotos.
     * @param newPhotos Lista de fotos a ser inserida nesta classe
     */
    void loadNewData(List<Photo> newPhotos) {
        mPhotoList = newPhotos;
        notifyDataSetChanged(); // Notificamos à RecyclerView que atualizamos os dados
                                // e que a view deve ser redesenhada
    }

    /**
     * Retorna uma foto que será exibida no PhotoDetailsActivity
     * @param position Posição da foto a retornar
     * @return a foto da posição desejada.
     */
    public Photo getPhoto(int position) {
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }

    /**
     * Esta classe é estática pois só será instanciada uma vez, para todas as instâncias de
     * FlickrRecyclerViewAdapter
     * Na prática esta classe vai se comportar de forma similar a uma classe em um arquivo
     * separado.
     * Note que para referenciá-la, devemos chamá-la com o nome da classe pai antes, no formato
     * FlickrRecyclerViewAdapter.FlickrImageViewHolder.
     */
    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrImageViewHolder";

        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: começou");
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.title = itemView.findViewById(R.id.title);
        }
    }
}
