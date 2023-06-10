package practice.shrishri1108.mymusicplayer.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import practice.shrishri1108.mymusicplayer.Adapter.MusicListAdapter;
import practice.shrishri1108.mymusicplayer.R;
import practice.shrishri1108.mymusicplayer.Utils.AudioUtils;
import practice.shrishri1108.mymusicplayer.databinding.ActivityHomeBinding;

@RequiresApi(api = Build.VERSION_CODES.R)
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private List<File> audioLists = new ArrayList<>();
    private MusicListAdapter musicListAdapter;
    private static final String[] permissions = {Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};
    private boolean allpermissionGrant = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.activity_home);


        init();

    }

    private void init() {

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                Boolean isGranted = true;
                for (String key : result.keySet()) {
                    if (Boolean.FALSE.equals(result.get(key))) {
                        Log.d(TAG, key + " Permission is not granted");
                        isGranted = false;
                        break;
                    }
                    else {
                        Log.d(TAG, key + " Permission is granted");
                    }
                }
                if (!isGranted) {
                    allpermissionGrant = false ;
                }
            }
        });

        checkPermission();


        musicListAdapter = new

                MusicListAdapter(audioLists, position ->

        {
            Intent intent = new Intent(HomeActivity.this, MusicPlayer.class);
            intent.putExtra("audio", (Serializable) audioLists);
            intent.putExtra("selected_pos", position);
            startActivity(intent);
        });


        binding.audioListRecyclers.setHasFixedSize(true);
        binding.audioListRecyclers.setAdapter(musicListAdapter);

        binding.swipeRefreshLays.setOnRefreshListener(() ->

        {
            if (audioLists != null) {
                audioLists.clear();
            }
            audioLists = AudioUtils.getAudios(getApplicationContext());

            if (audioLists != null && audioLists.size() > 0) {
                binding.tvNoAudioFound.setVisibility(View.GONE);
                musicListAdapter.refreshLists(audioLists);
            } else {
                binding.tvNoAudioFound.setVisibility(View.VISIBLE);
            }

            binding.swipeRefreshLays.setRefreshing(false);
        });

    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            allpermissionGrant = true ;
            for (String permission : permissions) {
                if (checkSelfPermission(permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(permissions);
                }
            }

            if (allpermissionGrant ) {
                audioLists = AudioUtils.getAudios(getApplicationContext());
                if (audioLists != null && audioLists.size() > 0) {
                    binding.tvNoAudioFound.setVisibility(View.GONE);
                } else {
                    binding.tvNoAudioFound.setVisibility(View.VISIBLE);
                }

            }
        }
    }
}
