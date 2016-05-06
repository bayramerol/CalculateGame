package com.byrm.asuspc.quickcalculategame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView textView01, times;

    private int rightBox;
    private int points;
    private int countWrongAnswers = 0;
    ImageView heart1, heart2, heart3;
    public int highScore;




    private InterstitialAd gecisReklam;//geçiş reklam referansı
    private AdView bannerReklam2;//bannerReklam reklam referansı
    private AdRequest adRequest;//adRequest referansı





    int currentTime = 8000;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private CountDownTimer mCountDown;
    public Integer[] heartpicture = {R.drawable.heartempty};


    MediaPlayer mpCorrect;
    MediaPlayer mpIncorrect;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3894F32FDF323E46E7F0199F4A87215B")
                .build();



        //Burda bannerReklam objesini oluşturuyoruz ve activity_anasayfa.xml de oluşturduğumuz adView e bağlıyoruz
        bannerReklam2 = (AdView) this.findViewById(R.id.adView);
        // adRequest = new AdRequest.Builder().build();
        bannerReklam2.loadAd(adRequest); //bannerReklam ı yüklüyoruz.









        //***********Geçiş reklam işlemleri*********
        gecisReklam = new InterstitialAd(this);
        gecisReklam.setAdUnitId("ca-app-pub-2928097859559744/9236350914");//Reklam İd miz.Admob da oluşturduğumuz geçiş reklam id si
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



















        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        highScore = sharedpreferences.getInt("HighScore", 0);


        //change volume için
        SeekBar volumeControl = (SeekBar) findViewById(R.id.seekbar);//seekbar refer


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//get information from device
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curVolume);

        //seekbar for change
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                Log.i("Seekbar Value", Integer.toString(progress));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //butonları tanımladım

        Button buttonOption1 = (Button) findViewById(R.id.button1);
        Button buttonOption2 = (Button) findViewById(R.id.button2);
        Button buttonOption3 = (Button) findViewById(R.id.button3);
        Button buttonOption4 = (Button) findViewById(R.id.button4);


        //Imageview tanımlamaları

        heart1 = (ImageView) findViewById(R.id.heart1);
        heart2 = (ImageView) findViewById(R.id.heart2);
        heart3 = (ImageView) findViewById(R.id.heart3);


        // the timer
        times = (TextView) findViewById(R.id.timers);

        times.setText("00:02:00");


        //click listenerleri


        buttonOption1.setOnClickListener(this);
        buttonOption2.setOnClickListener(this);
        buttonOption3.setOnClickListener(this);
        buttonOption4.setOnClickListener(this);


        //soru oluşturma fonksiyonum
        countDown();
        newQuestion();


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





















    private void newQuestion() {

        Random rand = new Random();

        //operandlarımı diziye atadım
        String[] operators = new String[4];


        //operandlarımı tanımladım
        operators[0] = "+";
        operators[1] = "-";
        operators[2] = "*";
        operators[3] = "/";


        //random olacak şekilde num1 num2 ve operandlarıma random olarak atama yaptım
        int num1 = (int) (Math.random() * (100));
        int num2 = (int) (Math.random() * (100));
        String op = operators[(int) (Math.random() * (4))];


        //sorunun yazıldıgı textview i gelecek random sayı ve operandlarla değiştirme yaptım
        textView01 = (TextView) findViewById(R.id.txtQuestion);
        textView01.setText(new Integer(num1).toString() + " " + op + " " + new Integer(num2).toString());

        //doğru cevabı üretmek için calculaterightvalue fonksiyonuma parametrelerimi atadım
        int rightValue = calculateRightValue(op, num1, num2);

        //1-4 arasındaki kutucukların birine doğru cevabı koydum
        rightBox = 1 + rand.nextInt(4);


        int eklenecekRandom = 1 + rand.nextInt(3);


        //yanlış cevaplara birbirinden farklı  atamalarını yaptım
        int randWrongValue1 = rightValue + eklenecekRandom;
        int randWrongValue2 = rightValue + eklenecekRandom + 1;
        int randWrongValue3 = rightValue - eklenecekRandom - 1;


        while ((randWrongValue2 == randWrongValue1) || (randWrongValue2 == randWrongValue3)) {

            randWrongValue2 = rightValue + eklenecekRandom;

        }

        while ((randWrongValue3 == randWrongValue2) || (randWrongValue3 == randWrongValue1)) {

            randWrongValue3 = rightValue - eklenecekRandom;
        }


        //kalsın bi kenarda :) int randWrongValue1=(int)(Math.random()*100);


        Button buttonOption1 = (Button) findViewById(R.id.button1);
        Button buttonOption2 = (Button) findViewById(R.id.button2);
        Button buttonOption3 = (Button) findViewById(R.id.button3);
        Button buttonOption4 = (Button) findViewById(R.id.button4);


        //rightbozdan gelen değerlere göre butonlarıma doğru ve yanlışları atadım
        switch (rightBox) {
            case 1:
                buttonOption1.setText(String.valueOf(rightValue));
                buttonOption2.setText(String.valueOf(randWrongValue1));
                buttonOption3.setText(String.valueOf(randWrongValue2));
                buttonOption4.setText(String.valueOf(randWrongValue3));
                break;
            case 2:
                buttonOption2.setText(String.valueOf(rightValue));
                buttonOption1.setText(String.valueOf(randWrongValue1));
                buttonOption3.setText(String.valueOf(randWrongValue2));
                buttonOption4.setText(String.valueOf(randWrongValue3));
                break;
            case 3:
                buttonOption3.setText(String.valueOf(rightValue));
                buttonOption1.setText(String.valueOf(randWrongValue1));
                buttonOption2.setText(String.valueOf(randWrongValue2));
                buttonOption4.setText(String.valueOf(randWrongValue3));
                break;
            case 4:
                buttonOption4.setText(String.valueOf(rightValue));
                buttonOption1.setText(String.valueOf(randWrongValue1));
                buttonOption2.setText(String.valueOf(randWrongValue2));
                buttonOption3.setText(String.valueOf(randWrongValue3));
                break;
        }

    }


    private int calculateRightValue(String op, int num1, int num2) {

        int calculation = 0;

        if (op.equals("+")) {

            calculation = num1 + num2;
        } else if (op.equals("-")) {

            calculation = num1 - num2;

        } else if (op.equals("*")) {

            calculation = num1 * num2;
        } else if (op.equals("/")) {
            if(num2 != 0)
            {
                calculation = num1 / num2;
            }
            else {
                calculation = 0;
            }
        }

        return calculation;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        mCountDown.cancel();
        showGecisReklam();



        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)

                .setTitleText(MainActivity.this.getString(R.string.mainExitText))
                .setContentText(MainActivity.this.getString(R.string.mainExitContent))
                .setCancelText(MainActivity.this.getString(R.string.cancelText))

                .showCancelButton(true)

                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        showGecisReklam();


                        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                })

                .setConfirmText(MainActivity.this.getString(R.string.confirmText))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        newQuestion();
                        countDownTimer2();
                    }
                });



        if(!isFinishing()) {
            sweetAlertDialog.show();
            sweetAlertDialog.setCancelable(false);
        }
    }







    @Override
    public void onClick(View v) {

        if (mCountDown != null) {
            mCountDown.cancel();
        }

        boolean correct = false;
        boolean play = true;

        switch (v.getId()) {
            case R.id.button1:
                if (rightBox == 1) {
                    correct = true;
                }
                break;
            case R.id.button2:
                if (rightBox == 2) {
                    correct = true;
                }
                break;
            case R.id.button3:
                if (rightBox == 3) {
                    correct = true;
                }
                break;
            case R.id.button4:
                if (rightBox == 4) {
                    correct = true;
                }
                break;
        }
        if (correct) {
            mpCorrect = MediaPlayer.create(this, R.raw.tutorial_appear);
            mpCorrect.start();
            points++;
            currentTime = 8000;
            updateScore();

            mpCorrect.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

        } else {
            mpIncorrect = MediaPlayer.create(this, R.raw.short_buzzer);
            mpIncorrect.start();
            points--;
            currentTime = 8000;
            updateScore();

            mpIncorrect.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

            countWrongAnswers++;
            if (countWrongAnswers == 1) {

                heart3.setImageResource(R.drawable.heartempty);

            } else if (countWrongAnswers == 2) {

                heart2.setImageResource(R.drawable.heartempty);
            } else if (countWrongAnswers == 3) {

                heart1.setImageResource(R.drawable.heartempty);

                // if unlucky start activity and finish the game

                if (points > highScore) {
                    updateHighScore(points);
                    editor.putInt("HighScore", points);
                    editor.apply();
                }

                Bundle b = new Bundle();
                b.putInt("highScore", highScore);


                createDialog(MainActivity.this.getString(R.string.createDialogFirst) + points, MainActivity.this.getString(R.string.createDialogSecond) + highScore);
                mCountDown.cancel();

                return;

            }
        }

        if (play) {

            newQuestion();
            mCountDown = new CountDownTimer(currentTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    currentTime -= 1000;
                    times.setText(MainActivity.this.getString(R.string.timeSetText) + String.valueOf(millisUntilFinished / 1000));
                    updateScore();
                }

                @Override
                public void onFinish() {
                    times.setText(MainActivity.this.getString(R.string.timeFinish));


                    currentTime = 8000;


                    // if unlucky start activity and finish the game

                    if (points > highScore) {
                        updateHighScore(points);
                        editor.putInt("HighScore", points);
                        editor.apply();
                    }

                    Bundle b = new Bundle();
                    b.putInt("highScore", highScore);

                    createDialog(MainActivity.this.getString(R.string.createDialogFirst) , MainActivity.this.getString(R.string.createDialogSecond) + highScore);
                }
            }.start();

        }

    }


    public void countDownTimer2() {


        mCountDown = new CountDownTimer(currentTime, 1000) {

            public void onTick(long millisUntilFinished) {


                currentTime -= 1000;


                times.setText(MainActivity.this.getString(R.string.timeSetText) + String.valueOf(millisUntilFinished / 1000));
                updateScore();
            }


            @Override
            public void onFinish() {
                times.setText(MainActivity.this.getString(R.string.timeFinish));


                currentTime = 8000;


                // if unlucky start activity and finish the game

                if (points > highScore) {
                    updateHighScore(points);
                    editor.putInt("HighScore", points);
                    editor.apply();
                }

                Bundle b = new Bundle();
                b.putInt("highScore", highScore);

                createDialog(MainActivity.this.getString(R.string.createDialogFirst) , MainActivity.this.getString(R.string.createDialogSecond) + highScore);
            }
        }.start();


    }


    private void createDialog(String titleText, String contentText) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this)
                .setTitleText(titleText)
                .setContentText(contentText)
                .setCancelText(MainActivity.this.getString(R.string.mainMenu))
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent firstActivity = new Intent(MainActivity.this, FirstActivity.class);
                        firstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle b = new Bundle();
                        b.putInt("highScore", highScore);
                        firstActivity.putExtras(b);
                        startActivity(firstActivity);
                    }

                })
                .setConfirmText(MainActivity.this.getString(R.string.playaganin))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });
        if (!isFinishing()) {
            sweetAlertDialog.show();
        }
    }

    private void updateScore() {
        TextView txtPoints = (TextView) findViewById(R.id.txtPoints);
        txtPoints.setText(MainActivity.this.getString(R.string.score) + ((Integer) (points)).toString());
    }

    private void updateHighScore(int points) {
        highScore = points;
    }


    public void countDown() {


        mCountDown = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                times.setText(MainActivity.this.getString(R.string.timeSetText) + String.valueOf(millisUntilFinished / 1000));
                updateScore();

                currentTime -= 1000;

            }

            @Override
            public void onFinish() {

                times.setText(MainActivity.this.getString(R.string.timeFinish));


                Bundle b = new Bundle();
                b.putInt("highScore", highScore);


                createDialog(MainActivity.this.getString(R.string.createDialogFirst) + points , MainActivity.this.getString(R.string.createDialogSecond) + highScore);


            }
        }.start();

    }

}

