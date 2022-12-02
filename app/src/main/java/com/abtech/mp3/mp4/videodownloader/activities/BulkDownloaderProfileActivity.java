package com.abtech.mp3.mp4.videodownloader.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.adapters.ListAllProfilePostsInstagramUserAdapter;
import com.abtech.mp3.mp4.videodownloader.databinding.ActivityBulkDownloaderProfileBinding;
import com.abtech.mp3.mp4.videodownloader.models.bulkdownloader.EdgeInfo;
import com.abtech.mp3.mp4.videodownloader.models.bulkdownloader.UserProfileDataModelBottomPart;
import com.abtech.mp3.mp4.videodownloader.models.instawithlogin.ModelInstagramPref;
import com.abtech.mp3.mp4.videodownloader.utils.SharedPrefsForInstagram;
import com.abtech.mp3.mp4.videodownloader.utils.iUtils;
import com.abtech.mp3.mp4.videodownloader.webservices.api.RetrofitApiInterface;
import com.abtech.mp3.mp4.videodownloader.webservices.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Keep
public class BulkDownloaderProfileActivity extends AppCompatActivity {

    public static String INSTAGRAM_endcursorval = "";
    ListAllProfilePostsInstagramUserAdapter listAllProfilePostsInstagramUserAdapter;
    private String myUserName;
    private String myUserPKID;
    private ActivityBulkDownloaderProfileBinding binding;
    private List<EdgeInfo> storyModelInstaItemList;
    private String mycookies = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBulkDownloaderProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            storyModelInstaItemList = new ArrayList<>();


            listAllProfilePostsInstagramUserAdapter = new ListAllProfilePostsInstagramUserAdapter(BulkDownloaderProfileActivity.this, storyModelInstaItemList);
            binding.recyclerviewProfileLong.setLayoutManager(new GridLayoutManager(BulkDownloaderProfileActivity.this, 3));
            binding.recyclerviewProfileLong.setAdapter(listAllProfilePostsInstagramUserAdapter);

            if (getIntent() != null) {
                myUserName = getIntent().getStringExtra("username");
                myUserPKID = getIntent().getStringExtra("pkid");
                loadAllprofileData(myUserName, myUserPKID);
            } else {

                binding.profileLongProgress.setVisibility(View.GONE);

                Toast.makeText(BulkDownloaderProfileActivity.this, "Error Getting Detail !!!", Toast.LENGTH_SHORT).show();
            }


            binding.recyclerviewProfileLong.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        binding.floatingloadmore.setVisibility(View.VISIBLE);

                        // Toast.makeTextBulkDownloaderProfileActivity.this, R.string.taptoloadmore, Toast.LENGTH_SHORT).show();
                        iUtils.ShowToast(BulkDownloaderProfileActivity.this, getString(R.string.taptoloadmore));
                    }
                }
            });


            binding.floatingloadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.floatingloadmore.setVisibility(View.GONE);
                    loadAllPostsData();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadAllprofileData(String username, String pkid) {


        System.out.println("hvjksdhfhdkd fffffff ");


        SharedPrefsForInstagram sharedPrefsFor = new SharedPrefsForInstagram(BulkDownloaderProfileActivity.this);
        ModelInstagramPref map = sharedPrefsFor.getPreference();
        if (map != null && map.getPREFERENCE_USERID() != null && !map.getPREFERENCE_USERID().equals("oopsDintWork") && !map.getPREFERENCE_USERID().equals("")) {
            mycookies = "ds_user_id=" + map.getPREFERENCE_USERID() + "; sessionid=" + map.getPREFERENCE_SESSIONID();
            System.out.println("hvjksdhfhdkd userpkid yhyhy ");


        } else {
            System.out.println("hvjksdhfhdkd userpkid 3434 ");

            mycookies = iUtils.myInstagramTempCookies;
        }

        if (TextUtils.isEmpty(mycookies)) {
            mycookies = "";
        }

        RetrofitApiInterface apiService = RetrofitClient.getClient();

        Call<JsonObject> callResult = apiService.getInstagramProfileDataBulk("https://www.instagram.com/" + username + "/?__a=1&__d=dis", mycookies,
                "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");


        callResult.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                System.out.println("hvjksdhfhdkd response1122334455_jsomobj:   " + response);


                //   binding.dfsadasdas.setText(response.toString());
                System.out.println("hvjksdhfhdkd userpkid 00 " + pkid + " username " + username);
                // System.out.println("hvjksdhfhdkd usernamedata click  idis" + response.toString());
                //4162923872
                //3401888503

                try {


                    JsonObject userdata = response.body().getAsJsonObject("graphql").getAsJsonObject("user");
                    binding.profileFollowersNumberTextview.setText(userdata.getAsJsonObject("edge_followed_by").get("count").getAsString());
                    binding.profileFollowingNumberTextview.setText(userdata.getAsJsonObject("edge_follow").get("count").getAsString());
                    binding.profilePostNumberTextview.setText(userdata.getAsJsonObject("edge_owner_to_timeline_media").get("count").getAsString());
                    binding.profileLongIdTextview.setText(userdata.get("username").getAsString());
//                                ObjectMapper om = new ObjectMapper();
//                                UserProfileTopData root = om.readValue(response.toString(), UserProfileTopData.class);
//                                System.out.println("hvjksdhfhdkd followed by  " + root.getGraphql().getUser().getEdge_followed_by().getCount());

                    //   binding.profileLongIdTextview.setText(root.getGraphql().getUser().getUsername());
                    //  binding.profileFollowersNumberTextview.setText(root.getGraphql().getUser().getEdge_followed_by().getCount() + "");
                    //    binding.profileFollowingNumberTextview.setText(root.getGraphql().getUser().getEdge_follow().getCount() + "");
                    //  binding.profilePostNumberTextview.setText(root.getGraphql().getUser().getEdge_owner_to_timeline_media().getCount() + "");

                    if (userdata.get("is_verified").getAsBoolean()) {
                        binding.profileLongApprovedImageview.setVisibility(View.VISIBLE);
                    } else {
                        binding.profileLongApprovedImageview.setVisibility(View.GONE);

                    }

                    if (userdata.get("is_private").getAsBoolean()) {
                        binding.rowSearchPrivateImageviewPrivate.setVisibility(View.VISIBLE);
                    } else {
                        binding.rowSearchPrivateImageviewPrivate.setVisibility(View.GONE);

                    }

                    Glide.with(BulkDownloaderProfileActivity.this).load(userdata.get("profile_pic_url").getAsString()).into(binding.profileLongCircle);

//                                System.out.println("hvjksdhfhdkd endcursoris= " + root.getLogging_page_id());
//                                System.out.println("hvjksdhfhdkd endcursoris img = " + root.getGraphql().getUser().getProfile_pic_url());


                } catch (Exception e) {

                    System.out.println("hvjksdhfhdkd eeee errr 00 " + e.getMessage());


                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                System.out.println("hvjksdhfhdkd:   " + "Failed0");

            }
        });

        loadAllPostsData();


    }


    void loadAllPostsData() {
        binding.profileLongProgress.setVisibility(View.VISIBLE);


        SharedPrefsForInstagram sharedPrefsFor = new SharedPrefsForInstagram(BulkDownloaderProfileActivity.this);
        ModelInstagramPref map = sharedPrefsFor.getPreference();

        if (map != null && map.getPREFERENCE_USERID() != null && !map.getPREFERENCE_USERID().equals("oopsDintWork") && !map.getPREFERENCE_USERID().equals("")) {
            mycookies = "ds_user_id=" + map.getPREFERENCE_USERID() + "; sessionid=" + map.getPREFERENCE_SESSIONID();
            System.out.println("hvjksdhfhdkd userpkid yhyhy 2");


        } else {
            System.out.println("hvjksdhfhdkd userpkid 3434 ");

            mycookies = iUtils.myInstagramTempCookies;
        }


        if (TextUtils.isEmpty(mycookies)) {
            mycookies = "";
        }

        RetrofitApiInterface apiService = RetrofitClient.getClient();

        Call<JsonObject> callResult = apiService.getInstagramProfileDataAllImagesAndVideosBulk("https://instagram.com/graphql/query/?query_id=17888483320059182&id=" + myUserPKID + "&first=20&after=" + INSTAGRAM_endcursorval, mycookies,
                iUtils.UserAgentsList[0]);


        callResult.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                System.out.println("hvjksdhfhdkd response1122334455_jsomobj: vvv  " + response.body().toString());

                binding.profileLongProgress.setVisibility(View.GONE);


                System.out.println("hvjksdhfhdkd userpkid vv " + myUserPKID + " username");
                //  System.out.println("hvjksdhfhdkd model click  idis" + response.toString());
                //4162923872
                //3401888503
                // binding.dfsadasdas.setText(response.toString());

                try {
                    Gson gson = new Gson();

                    UserProfileDataModelBottomPart root = gson.fromJson(response.body().toString(), UserProfileDataModelBottomPart.class);

                    storyModelInstaItemList.addAll(root.getData().getUser().getEdge_owner_to_timeline_media().getEdges());


                    INSTAGRAM_endcursorval = root.getData().getUser().getEdge_owner_to_timeline_media().getPage_info().getEnd_cursor();


                } catch (Exception e) {
                    System.out.println("hvjksdhfhdkd eeee  vv errrrrrrr" + e.getMessage());
                }
                listAllProfilePostsInstagramUserAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                binding.profileLongProgress.setVisibility(View.GONE);

                System.out.println("hvjksdhfhdkd eeee  vv " + t.getLocalizedMessage());

            }
        });


    }


    @Override
    public void onBackPressed() {
        INSTAGRAM_endcursorval = "";
        //startActivity(new Intent(BulkDownloader_ProfileActivity.this,InstagramBulkDownloader.class));
        finish();
    }
}