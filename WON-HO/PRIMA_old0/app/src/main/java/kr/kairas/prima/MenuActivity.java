package kr.kairas.prima;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

/**
 * Created by karas on 2017-08-01.
 */

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void post(View v) {
        Intent intent = new Intent(MenuActivity.this, PostActivity.class);
        startActivity(intent);
    }

    public void list(View v) {
        Intent intent = new Intent(MenuActivity.this, ListActivity.class);
        startActivity(intent);
    }

}
