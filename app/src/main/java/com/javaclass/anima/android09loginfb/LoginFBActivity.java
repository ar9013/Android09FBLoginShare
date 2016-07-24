package com.javaclass.anima.android09loginfb;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginFBActivity extends FragmentActivity {

    CallbackManager callbackManager;
    TextView fbName, fbLink, fbId, fbEmaial, fbGender, fbBirthday;
    ImageView fbicon;
    String id;
    Button fb, btnShare;
    LoginButton loginButton;
    private AccessToken accessToken;
    Profile profile;
    Boolean isLogin = false;

    String Tag = "LoginFBActivity";
    URL profile_pic;

    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        fbName = (TextView) findViewById(R.id.fbname);
        fbLink = (TextView) findViewById(R.id.fblink);
        fbId = (TextView) findViewById(R.id.fbid);
        fbEmaial = (TextView) findViewById(R.id.fbemail);
        fbGender = (TextView) findViewById(R.id.fbgender);
        fbBirthday = (TextView) findViewById(R.id.fbbirthday);
        fbicon = (ImageView) findViewById(R.id.imgIcon);

        fb = (Button) findViewById(R.id.fb);

        shareDialog = new ShareDialog(this);
        final ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
        btnShare = (Button) findViewById(R.id.btnShare);


        profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            // user has logged in
            fb.setText("LOGOUT");
            isLogin = true;
        } else {
            // user has not logged in
            fb.setText("LOGIN");
            isLogin = false;
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(Tag, "onSuccess");
                accessToken = loginResult.getAccessToken(); // 取得 AccessToken

                String strRaccessToken = accessToken.getToken();
                Log.i(Tag, strRaccessToken);


                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    //當RESPONSE回來的時候
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //讀出姓名 ID FB個人頁面連結

                        Log.d(Tag, "complete");
                        Log.d(Tag, response.toString());

                        try {
                            id = object.getString("id");
                            try {
                                // 取得大頭貼的圖片
                                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                Log.i("rural", profile_pic.toString());

                                Picasso.with(getApplicationContext())
                                        .load("https://graph.facebook.com/" + id + "/picture?type=large")
                                        .into(fbicon);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("rural", object.optString("id"));
                            Log.d("rural", object.optString("name"));
                            Log.d("rural", object.optString("link"));
                            Log.d("rural", object.optString("email"));
                            Log.d("rural", object.optString("gender"));
                            Log.d("rural", object.optString("birthday"));

                            fbName.setText("name : " + object.optString("name"));
                            fbLink.setText("link : " + object.optString("link"));
                            fbId.setText("id : " + object.optString("id"));
                            fbEmaial.setText("email : " + object.optString("email"));
                            fbGender.setText("gender : " + object.optString("gender"));
                            fbBirthday.setText("birthday : " + object.optString("birthday"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //包入你想要得到的資料 送出request

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender, birthday");

                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                Log.d("rural", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Tag, "onError");

            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //        if (ShareDialog.canShow(ShareLinkContent.class)) {
//            ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                    .setContentTitle("Hello Facebook")
//                    .setContentDescription(
//                            "The 'Hello Facebook' sample  showcases simple Facebook integration")
//                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
//                    .build();
//
//            //shareDialog.show(linkContent);
//        }

        // 分享 連結
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Test")
                .setContentUrl(Uri.parse("https://www.youtube.com/watch?v=3q6D0wMX_H0"))
                .setContentDescription("Play Something with your facebook friends and many more people!")
                .build();

        // 分享 圖片
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.git);
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bmp).build();
        SharePhotoContent PhotoContent = new SharePhotoContent.Builder().addPhoto(photo).build();

        // 分享 影片
        VideoView view = (VideoView) findViewById(R.id.video);
        Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rosie);
        File file = new File(url.toString());
        ShareVideo video = new ShareVideo.Builder().setLocalUrl(url).build();
        ShareVideoContent videoContent = new ShareVideoContent.Builder().setContentTitle("A123").build();

        view.setVideoURI(url);
        view.start();

        //分享 Multimedia 最多6 張圖片 跟一段影片(影片的部份沒有試出來)
        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.goonie);
        SharePhoto sharePhoto1 = new SharePhoto.Builder().setBitmap(bmp1).build();

        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.lovely_cat);
        SharePhoto sharePhoto2 = new SharePhoto.Builder().setBitmap(bmp2).build();

        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.man);
        SharePhoto sharePhoto3 = new SharePhoto.Builder().setBitmap(bmp3).build();

        Bitmap bmp4 = BitmapFactory.decodeResource(getResources(), R.drawable.mushroo_icon);
        SharePhoto sharePhoto4 = new SharePhoto.Builder().setBitmap(bmp4).build();

        Bitmap bmp5 = BitmapFactory.decodeResource(getResources(), R.drawable.mushroom_bee_icon);
        SharePhoto sharePhoto5 = new SharePhoto.Builder().setBitmap(bmp5).build();

        Bitmap bmp6 = BitmapFactory.decodeResource(getResources(), R.drawable.mushroom_spring);
        SharePhoto sharePhoto6 = new SharePhoto.Builder().setBitmap(bmp6).build();

        ShareContent shareContent = new ShareMediaContent.Builder()
                .addMedium(sharePhoto1)
                .addMedium(sharePhoto2)
                .addMedium(sharePhoto3)
                .addMedium(sharePhoto4)
                .addMedium(sharePhoto5)
                .addMedium(sharePhoto6)
                .build();

// 另外一種分享的寫法
// Create an object
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "books.book")
                .putString("og:title", "A Game of Thrones")
                .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
                .putString("books:isbn", "0-553-57340-3")
                .build();

// Create an action
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("books.reads")
                .putObject("book", object)
                .build();

// Create the content
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("book")
                .setAction(action)
                .build();


        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

        shareButton.setShareContent(videoContent);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(Intent.createChooser(sendIntent, "share..."));
                shareButton.performClick();

//                Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.git);
//                SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
//
//                SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
//                shareDialog.show(content);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v) {
        if (v == fb) {
            if (isLogin) {
                LoginManager.getInstance().logOut();
                fb.setText("lOGIN");

                fbName.setText("name : ");
                fbLink.setText("link : ");
                fbId.setText("id : ");
                fbEmaial.setText("email : ");
                fbGender.setText("gender : ");
                fbBirthday.setText("birthday : ");
                fbicon.setImageResource(R.drawable.git);

                isLogin = false;
            } else {
                loginButton.performClick();
                fb.setText("lOGOUT");
                isLogin = true;
            }
        }
    }


}
