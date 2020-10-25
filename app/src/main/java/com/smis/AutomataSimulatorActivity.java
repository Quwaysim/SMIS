package com.smis;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AutomataSimulatorActivity extends AppCompatActivity {

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automata_simulator);

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setUpViewPager(mViewPager);

    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new automata1(), "automata1");
        adapter.addFragment(new automata2(), "automata2");
        adapter.addFragment(new automata3(), "automata3");
        adapter.addFragment(new automata4(), "automata4");
        adapter.addFragment(new automata5(), "automata5");
        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }
}
