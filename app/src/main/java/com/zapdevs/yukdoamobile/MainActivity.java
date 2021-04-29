package com.zapdevs.yukdoamobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Adapter.DoaAdapter;
import ModelViewDoa.ModelDoa;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar mainToolbar;
    ArrayList<ModelDoa> modelDoas = new ArrayList<>();
    DoaAdapter doaAdapter;
    RecyclerView mainRecycler;
    public static SharedPreferences sharedPreferences = null;
    DrawerLayout main_drawerlayout;
    NavigationView main_navigationview;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);

        init();
        try {
            logic();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        mainRecycler = (RecyclerView) findViewById(R.id.mainRecycler);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Yuk Doa");
        }
        main_drawerlayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        main_navigationview = (NavigationView) findViewById(R.id.main_navigationview);
        toggle = new ActionBarDrawerToggle(MainActivity.this, main_drawerlayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        main_drawerlayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void logic() throws FileNotFoundException, JSONException {
        main_navigationview.setNavigationItemSelectedListener(this);
        @SuppressLint("StaticFieldLeak")
        class LC extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseDoaSecondary(read(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                StringBuilder sb = new StringBuilder();
                sb.append("android.resource://");
                sb.append(getPackageName());
                sb.append("/");
                sb.append(R.raw.doa);
                return sb.toString();
            }
        }
        LC lc = new LC();
        lc.execute();
    }

    public String read(String sb) {
        String res = "";

        try {
            Uri uri = Uri.parse(sb.toString());
            InputStream is = getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                res += line;
                System.out.println("[]byte : " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public void parseDoaSecondary(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);

        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            ModelDoa modelDoa = new ModelDoa();
            modelDoa.setId(String.valueOf(object.getInt("id")));
            modelDoa.setArab(object.getString("arabic"));
            modelDoa.setLatin(object.getString("latin"));
            modelDoa.setTitle(object.getString("title"));
            modelDoa.setTranslation(object.getString("translation"));
            modelDoas.add(modelDoa);
        }
        setAdapterData();
    }

    public void setAdapterData() {
        doaAdapter = new DoaAdapter(MainActivity.this, modelDoas);
        mainRecycler.setAdapter(doaAdapter);
        mainRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchview = (SearchView) mSearch.getActionView();
        mSearchview.setQueryHint("Search");
        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    doaAdapter.filter(query);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    doaAdapter.filter(newText);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void daynight_mode() {
        Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        dialog.setContentView(R.layout.daynight_layout);
        final SwitchCompat switchCompat = (SwitchCompat) dialog.findViewById(R.id.switchCompat);
        switchCompat.setChecked(getResTheme());

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("theme_mode", true);
                    editor.apply();
                    editor.commit();
                    switchCompat.setChecked(true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("theme_mode", false);
                    editor.apply();
                    editor.commit();
                    switchCompat.setChecked(false);
                }
            }
        });
        dialog.show();
    }

    public static boolean getResTheme() {
        return sharedPreferences.getBoolean("theme_mode", true);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.daynightmode:
                daynight_mode();
                break;
            case R.id.send_feedback:
                Uri uri = Uri.parse("https://facebook.com/rizal.lolicondesu");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                MainActivity.this.startActivity(intent);
                break;
            default:
                break;
        }
        return false;
    }
    protected void onDestroy(){
        super.onDestroy();
    }
    protected void onResume(){
        super.onResume();
    }
}