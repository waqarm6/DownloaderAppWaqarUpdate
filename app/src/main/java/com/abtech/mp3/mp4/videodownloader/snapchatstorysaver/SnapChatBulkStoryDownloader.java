package com.abtech.mp3.mp4.videodownloader.snapchatstorysaver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.databinding.ActivityBulkDownloaderSnapchatProfileBinding;
import com.abtech.mp3.mp4.videodownloader.models.snapchatmodels.SnapChatStoryModel;
import com.abtech.mp3.mp4.videodownloader.snapchatstorysaver.adapters.SnapChatSubStoriesListRecyclerAdapter;
import com.abtech.mp3.mp4.videodownloader.utils.iUtils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SnapChatBulkStoryDownloader extends AppCompatActivity {
    private ActivityBulkDownloaderSnapchatProfileBinding binding;
    private static ProgressDialog pdsnap;
    private SnapChatSubStoriesListRecyclerAdapter snachatMainList;
    private List<String> storyModelSubList;
    private SnapChatStoryModel snapChatStoryModel;
    private String url="";

    //TODO fix profile data loading
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBulkDownloaderSnapchatProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {
            getSupportActionBar().setTitle("MP4 Downloader");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }




        try {
            pdsnap = new ProgressDialog(SnapChatBulkStoryDownloader.this);
            pdsnap.setMessage(getResources().getString(R.string.genarating_download_link));
            pdsnap.setCancelable(false);
            pdsnap.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pdsnap.dismiss();//dismiss dialog
                }
            });





            if (getIntent() != null) {
                url = getIntent().getStringExtra("urlsnap");
                System.out.println("myresponseis111 snapurl= " + url);



                pdsnap.show();

                new CallSnapChatStoryData().execute(url);
            } else {

                binding.profileLongProgress.setVisibility(View.GONE);

                Toast.makeText(SnapChatBulkStoryDownloader.this, "Error Getting Detail !!!", Toast.LENGTH_SHORT).show();
            }

        } catch (Throwable e) {
            Toast.makeText(this, "" + getString(R.string.error_occ), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public class CallSnapChatStoryData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {

                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
                pdsnap.dismiss();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {


            try {

                pdsnap.dismiss();

                System.out.println("myresponseis111 exp166 " + document);

                String data = "";

                Elements elements = document.select("script");
                for (Element element : elements) {
                    if (element.attr("id").equals("__NEXT_DATA__")) {
                        //Save As you want to


                        System.out.println("myresponseis111 list_of_qualities" + element.html());


                        snapChatStoryModel = (SnapChatStoryModel) new Gson().fromJson(element.html(), SnapChatStoryModel.class);
                        storyModelSubList = new ArrayList<>();

                        List<String> stringList = iUtils.extractUrls(element.html());
                        System.out.println("myresponseis111 lgfgf " + stringList.size());
                        TreeSet treeSet = new TreeSet();
                        for (int i = 0; i < stringList.size(); i++) {
                            if (stringList.get(i).contains("sc-cdn.net") && stringList.get(i).contains(".400") || stringList.get(i).contains(".80") || stringList.get(i).contains(".111")) {

                                String urlf = StringEscapeUtils.unescapeJava(URLDecoder.decode(stringList.get(i), "UTF-8"));
                                treeSet.add(urlf);
                                System.out.println("myresponseis111 urlsnap  " + i + " " + urlf);

                            }
                        }

                        List<String> allurlslist_filtered = new ArrayList<>(treeSet);
                        System.out.println("myresponseis111 lgfgf " + allurlslist_filtered.size());





                        SnapChatBulkStoryDownloader.this.runOnUiThread(() -> {
                            String purl = "";
                            try {

                                // System.out.println("myresponseis111 lgfgf " +snapChatStoryModel.getProps().getPageProps().getUserProfile().getPublicProfileInfo().getProfilePictureURL());

                                try {
                                    purl = snapChatStoryModel.getProps().getPageProps().getUserProfile().getPublicProfileInfo().getProfilePictureURL();
                                    Glide.with(SnapChatBulkStoryDownloader.this)
                                            .load(purl)
                                            .into(binding.profileLongCircle);

                                } catch (Exception e1e) {
                                    Glide.with(SnapChatBulkStoryDownloader.this)
                                            .load(document.select("meta[property=\"og:image\"]").first().attr("content"))
                                            .into(binding.profileLongCircle);
                                }

                                try {
                                    binding.profileSubscriberTextview.setText(String.format("%s", snapChatStoryModel.getProps().getPageProps().getUserProfile().getPublicProfileInfo().getSubscriberCount()));

                                } catch (Exception e1e) {

                                }

                                try {
                                    binding.profileBioTextview.setText(String.format("%s", snapChatStoryModel.getProps().getPageProps().getUserProfile().getPublicProfileInfo().getBio()));
                                } catch (Exception e1e) {
                                    binding.profileBioTextview.setText(String.format("%s", document.select("meta[name=\"description\"]").first().attr("content")));

                                }
                                try {
                                    binding.profileAddressTextview.setText(String.format("%s", snapChatStoryModel.getProps().getPageProps().getUserProfile().getPublicProfileInfo().getAddress()));
                                } catch (Exception e1e) {
                                    binding.profileAddressTextview.setText(String.format("%s", "None"));

                                }
                                try {
                                    binding.profileUsernameTextview.setText(String.format("%s", snapChatStoryModel.getProps().getPageProps().getUserProfile().getPublicProfileInfo().getUsername()));
                                } catch (Exception e1e) {
                                    binding.profileUsernameTextview.setText(String.format("%s", !url.equals("")?url.split("/")[4]:"Username Not Found"));

                                }
                                storyModelSubList.addAll(allurlslist_filtered);



                                snachatMainList = new SnapChatSubStoriesListRecyclerAdapter(SnapChatBulkStoryDownloader.this, storyModelSubList);
                                binding.recyclerviewStories.setLayoutManager(new GridLayoutManager(SnapChatBulkStoryDownloader.this, 4));
                                binding.recyclerviewStories.setAdapter(snachatMainList);

                            } catch (Exception e) {

                                e.printStackTrace();

                                Toast.makeText(SnapChatBulkStoryDownloader.this, "Profile Has No Public Data Found, Try Another Profile", Toast.LENGTH_LONG).show();
                            }
                        });

//                    JSONObject obj = new JSONObject(element.html());
//
//
//                    System.out.println("myresponseis111 fdsfsfds " + obj.getJSONObject("props")
//                            .getJSONObject("pageProps")
//                            .getString("gaid"));


                    }
                }

            } catch(Throwable f){
                System.out.println("myresponseis111 exp " + f.getMessage());

                f.printStackTrace();
                pdsnap.dismiss();

            }
        }


    }

}