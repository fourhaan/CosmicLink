package com.example.volunteerapp.AdminPanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.volunteerapp.AdminPanel.Fragments.ReportedPostsFragment;
import com.example.volunteerapp.AdminPanel.Fragments.ReportedUsersFragment;
import com.example.volunteerapp.R;

public class MainAdminPage extends AppCompatActivity {

    private Toolbar adminToolbar;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin_page);

        adminToolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(adminToolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.logorocket);
        }

        fragmentContainer = findViewById(R.id.fragmentContainer);

        loadFragment(new ReportedPostsFragment());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuReportedPosts) {
            loadReportedPostsFragment();
            return true;
        } else if (itemId == R.id.menuReportedUsers) {
            loadReportedUsersFragment();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadReportedPostsFragment() {
        loadFragment(new ReportedPostsFragment());
    }

    private void loadReportedUsersFragment() {
        loadFragment(new ReportedUsersFragment());
    }
}
