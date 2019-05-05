package tech.gregori.flikrbrowser;

import android.os.Bundle;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // necessitamos do botão Home, pois estamos em uma activity diferente
        activateToolbar(true);
    }

}
