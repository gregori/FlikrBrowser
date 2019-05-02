package tech.gregori.flikrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Esta classe tratará os cliques na lista.
 * No caso da RecyclerView, necessitamos implementar uma classe para isso.
 * Aqui, estendemos a classe SimpleOnItemTouchListener, que já traz uma série de métodos
 * implementados, que facilitam nosso trabalho.
 */
class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    // uma classe que implementa a interface que acabamos de criar
    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    /**
     * @param context contexto da view, necessário para o Gesture funcionar
     * @param recyclerView referência à recyclerView que irá receber os eventos de toque no item
     * @param listener a classe/método que tratará o clique
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener) {
        mListener = listener;
        mGestureDetector = null;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: começou");
        return super.onInterceptTouchEvent(rv, e);
    }
}
