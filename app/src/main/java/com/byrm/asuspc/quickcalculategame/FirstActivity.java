package com.byrm.asuspc.quickcalculategame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FirstActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private int highScore;

    private Toast toast;
    private long lastBackPressTime = 0;



    private InterstitialAd gecisReklam;//geçiş reklam referansı
    private AdView bannerReklam;//bannerReklam reklam referansı
    private AdRequest adRequest;//adRequest referansı






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);



        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3894F32FDF323E46E7F0199F4A87215B")
                .build();



        //Burda bannerReklam objesini oluşturuyoruz ve activity_anasayfa.xml de oluşturduğumuz adView e bağlıyoruz
        bannerReklam = (AdView) this.findViewById(R.id.adView);
        // adRequest = new AdRequest.Builder().build();
        bannerReklam.loadAd(adRequest); //bannerReklam ı yüklüyoruz.








        //***********Geçiş reklam işlemleri*********
        gecisReklam = new InterstitialAd(this);
        gecisReklam.setAdUnitId("ca-app-pub-2928097859559744/1713084114");//Reklam İd miz.Admob da oluşturduğumuz geçiş reklam id si
        gecisReklam.setAdListener(new AdListener() { //Geçiş reklama listener ekliyoruz
            @Override
            public void onAdLoaded() { //Geçiş reklam Yüklendiğinde çalışır
                //Toast.makeText(getApplicationContext(), "Reklam Yüklendi.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAdFailedToLoad(int errorCode) { //Geçiş Reklam Yüklenemediğinde Çalışır
                //Toast.makeText(getApplicationContext(), "Reklam Yüklenirken Hata Oluştu.", Toast.LENGTH_LONG).show();
            }

            public void onAdClosed(){ //Geçiş Reklam Kapatıldığında çalışır
                // Toast.makeText(getApplicationContext(), "Reklam Kapatıldı.", Toast.LENGTH_LONG).show();

                //Geçiş reklam kapatıldığı zamanda yeni reklam yükleme işlemimizi başlatabiliriz.
                //Böylelikle geçiş reklamımız gösterilmek iöçin hazırda bekler.
                loadGecisReklam();
            }
        });

        loadGecisReklam();//Geçiş reklamı yüklüyoruz












        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        final ImageView btnPlay = (ImageView) findViewById(R.id.play);
        final ImageView btnInfo = (ImageView) findViewById(R.id.info);
        final ImageView btnAchievenet = (ImageView) findViewById(R.id.achievement);
        final ImageView btnHelp = (ImageView) findViewById(R.id.help);

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        highScore = sharedpreferences.getInt("HighScore", 0);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(FirstActivity.this)
                        .setTitleText(FirstActivity.this.getString(R.string.aboutUsSetText))
                        .setContentText(FirstActivity.this.getString(R.string.aboutUsContentText))
                        .show();
            }
        });

        btnAchievenet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText(FirstActivity.this.getString(R.string.achievementSetText))
                        .setContentText(FirstActivity.this.getString(R.string.achievementHighScore) + highScore)
                        .setCustomImage(R.drawable.medal)
                        .show();
            }
        });


        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(FirstActivity.this)
                        .setTitleText(FirstActivity.this.getString(R.string.helpSetText))
                        .setContentText(FirstActivity.this.getString(R.string.helpContentText))
                        .show();
            }
        });

    }




    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            showGecisReklam();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, this.getString(R.string.exitToast), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }




    public void loadGecisReklam() {//Geçiþ reklamý Yüklemek için





        //Device id mizi yazıyoruz ki reklamımızı test ederken istedimiz kadar tıklayalım
        //Google bu device id den tıklanan reklamlara ücret ödemeyecek bunun test için kullanıldığını bilecek
        //Eğer bunu yazmazsak Google haksız kazanç elde edeceğimizi düşünüp hesabımızı banlayabilir.

        //Device id yi bulmak için uygulamanızı çalıştırdıktan sorna LogCat i açıyoruz
        //Filtreleme Kısmına AdRequest veya device yazıyoruz.
        //Filtreleme sonucu olarak   "Use AdRequest.Builder.addTestDevice("C521B8BE91B4860C229030D8E3CEA254") to get test ads on this device."
        //yukardaki gibi bir sonuç çıkacaktır. Yukarda C521... ile başlayan kısım device id nizdir
        //Bunu yapmayı kesinlikle unutmayın yoksa banlanırsınız.

        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3894F32FDF323E46E7F0199F4A87215B")
                .build();







        //Reklam Yükleniyor
        gecisReklam.loadAd(adRequest);
    }
    public void showGecisReklam() {//Geçiþ reklamý Göstermek için

        if (gecisReklam.isLoaded()) {//Eðer reklam yüklenmiþse kontrol ediliyor
            gecisReklam.show(); //Reklam yüklenmiþsse gösterilecek
        } else {//reklam yüklenmemiþse
            //Toast.makeText(getApplicationContext(), "Reklam Gösterim Ýçin Hazýr Deðil.", Toast.LENGTH_LONG).show();
        }
    }














}
