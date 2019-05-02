package tech.gregori.flikrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
