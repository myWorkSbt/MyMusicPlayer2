package practice.shrishri1108.mymusicplayer.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import practice.shrishri1108.mymusicplayer.interfaces.ItemClickListener;
import practice.shrishri1108.mymusicplayer.R;
import practice.shrishri1108.mymusicplayer.databinding.MusicItemsBinding;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.myViewHolders> {

    private List<File > audioFileList ;
    private final ItemClickListener itemClickListener ;
   public MusicListAdapter(List<File > audioFileList , ItemClickListener itemClickListener ) {
        this.itemClickListener = itemClickListener ;
        this.audioFileList = audioFileList ;
    }



    @NonNull
    @Override
    public MusicListAdapter.myViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolders(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()) , R.layout.music_items , parent  , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListAdapter.myViewHolders holder, int position) {
        File singleAudios  = audioFileList.get(holder.getAdapterPosition());
        if (singleAudios != null ) {
            holder.itemMusicBinding.songName.setText(singleAudios.getName());
        }


        try {
        MediaMetadataRetriever dataRetriever = new MediaMetadataRetriever();
            dataRetriever.setDataSource(singleAudios.getAbsolutePath());
        byte[] pic = dataRetriever.getEmbeddedPicture();
        if (dataRetriever != null && pic != null) {

            InputStream is = new ByteArrayInputStream(dataRetriever.getEmbeddedPicture());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            holder.itemMusicBinding.songImgs.setImageBitmap(bitmap);
        } else {
            holder.itemMusicBinding.songImgs.setImageResource( R.drawable.no_photo);
        }
            dataRetriever.release();
        } catch (Exception e) {
            Log.e("error", "onBindViewHolder: ", e) ;
//            throw new RuntimeException(e);
        }
        holder.itemMusicBinding.musicItemCard.setOnClickListener( v -> {
             itemClickListener.onItemClicked(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return  audioFileList.size() ;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshLists(List<File> audioLists) {
       this.audioFileList = audioLists ;
       notifyDataSetChanged();
    }

    public static class myViewHolders extends RecyclerView.ViewHolder {
        private final MusicItemsBinding itemMusicBinding ;

        public myViewHolders(@NonNull MusicItemsBinding itemMusicBinding ) {
            super(itemMusicBinding.getRoot());
            this.itemMusicBinding =  itemMusicBinding ;
        }
    }
}
