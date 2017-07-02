package xyz.msa_inet.ratescbr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String chk_baseURL = prefs.getString("baseURL", "");
        if (chk_baseURL.equals("")) {
            editor.putString("baseURL", "http://www.cbr.ru/scripts/XML_daily.asp");
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        local_baseURL = prefs.getString("baseURL", "");
        if (local_baseURL.equals("")) {
            prefs.edit().putString("baseURL", "http://www.cbr.ru/scripts/XML_daily.asp");
            prefs.edit().apply();
        }
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.base_settings :
                Intent intent;
                intent = new Intent(this, SettingsActivity.class);
                try {
                    startActivity(intent);
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
