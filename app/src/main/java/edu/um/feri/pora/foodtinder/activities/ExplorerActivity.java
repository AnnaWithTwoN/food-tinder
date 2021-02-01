package edu.um.feri.pora.foodtinder.activities;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.foodtinder.adapters.SectionsPagerAdapter;

public class ExplorerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager fragment_placeholder = findViewById(R.id.fragment_placeholder);
        fragment_placeholder.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(fragment_placeholder);
    }
}