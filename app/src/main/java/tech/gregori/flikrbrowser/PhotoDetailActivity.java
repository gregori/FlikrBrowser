package tech.gregori.flikrbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // necessitamos do botão Home, pois estamos em uma activity diferente
        activateToolbar(true);

        Intent intent = getIntent();
        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if (photo != null) {
            TextView photoTitle = findViewById(R.id.photo_title);
            photoTitle.setText("Título: " + photo.getTitle());

            TextView photoTags = findViewById(R.id.photo_tags);
            photoTags.setText("Tags: " + photo.getTags());

            TextView photoAuthor = findViewById(R.id.photo_author);
            photoAuthor.setText(photo.getAuthor());

            ImageView photoImage = findViewById(R.id.photo_image);
            // picasso baixará a imagem pelo link e a aplicará no imageView (thumbnail)
            // esta classe é um singleton, só há uma instância. Ela também baixa
            // as imagens em background. Além disso, faz cache das imagens.
            Picasso.with(this).load(photo.getLink())
                    .error(R.drawable.placeholder) // se houver um erro, põe o placeholder no lugar da imagem
                    .placeholder(R.drawable.placeholder) // enquanto baixa a imagem, mostra o placeholder
                    .into(photoImage);
        }
    }

}
