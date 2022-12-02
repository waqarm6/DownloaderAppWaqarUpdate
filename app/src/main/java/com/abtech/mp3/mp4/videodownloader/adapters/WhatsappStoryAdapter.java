package com.abtech.mp3.mp4.videodownloader.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.models.WAStoryModel;
import com.abtech.mp3.mp4.videodownloader.utils.Constants;
import com.abtech.mp3.mp4.videodownloader.utils.FilePathUtility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class WhatsappStoryAdapter extends RecyclerView.Adapter<WhatsappStoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Object> filesList;

    public WhatsappStoryAdapter(Context context, ArrayList<Object> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @NonNull
    @Override
    public WhatsappStoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row_statussaver, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WhatsappStoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //    int viewType = getItemViewType(position);
        try {
            final WAStoryModel files = (WAStoryModel) filesList.get(position);
            final Uri uri = Uri.parse(files.getUri().toString());
            holder.userName.setText(files.getName());
            if (files.getUri().toString().endsWith(".mp4")) {
                holder.playIcon.setVisibility(View.VISIBLE);
            } else {
                holder.playIcon.setVisibility(View.INVISIBLE);
            }
            Glide.with(context)
                    .load(files.getUri())
                    .into(holder.savedImage);
            holder.linlayoutclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkFolder();

                    final String path = files.getPath();
                    System.out.println("mypath is 0 " + path);


                    if (Build.VERSION.SDK_INT >= 30) {
                        DocumentFile fromTreeUri1 = DocumentFile.fromSingleUri(context, Uri.parse(path));
                        String despath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME;

                        try {

                            FilePathUtility.moveFile(context, fromTreeUri1.getUri().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(context, context.getString(R.string.saved_to) + despath + files.getFilename(), Toast.LENGTH_LONG).show();
                    } else {
                        final File file = new File(path);

                        String destPath = "";

                        destPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME;

                        File destFile = new File(destPath);
                        try {
                            FileUtils.copyFileToDirectory(file, destFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Toast.makeText(context, context.getString(R.string.saved_to) + destPath + files.getFilename(), Toast.LENGTH_LONG).show();

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME;
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            Log.d("Folder", "Already Created");
        }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView savedImage;
        ImageView playIcon;
        LinearLayout linlayoutclick;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.profileUserName);
            savedImage = itemView.findViewById(R.id.mainImageView);
            playIcon = itemView.findViewById(R.id.playButtonImage);
            linlayoutclick = itemView.findViewById(R.id.linlayoutclick);
        }
    }
}
