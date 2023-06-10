package practice.shrishri1108.mymusicplayer.Utils;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioUtils {
    private static final String[] AUDIO_EXT = {".wav", ".mp3" };

    public static List<File> getAudios(Context context) {
        List<File> audioList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File storageDir = Environment.getExternalStorageDirectory();
            try {
                getAudioFiles(storageDir, audioList);
            }
            catch (Exception ex ) {
                Log.e("error", "getAudios: ", ex );
            }
        } else {
            try {

                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            catch (Exception ex ) {
                Log.e("error", "getAudios: ", ex);
            }
        }

        return audioList;
    }

    private static void getAudioFiles(File directory, List<File> audioFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAudioFiles(file, audioFiles);
                } else {
                    String audioName = file.getName().toLowerCase();
                    for (String extension : AUDIO_EXT) {
                        if (audioName.endsWith(extension)) {
                            audioFiles.add(file);
                            break;
                        }
                    }
                }
            }
        }
    }
}

