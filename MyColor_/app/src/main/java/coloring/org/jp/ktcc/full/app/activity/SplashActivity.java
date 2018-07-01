package coloring.org.jp.ktcc.full.app.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.util.CommonUtil;

public class SplashActivity extends AppCompatActivity {
    int pausePosition;

    @BindView(R.id.video)
    VideoView mVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
            if(mVideo!=null) {
            mVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + getVideoName()));
            mVideo.setMediaController(null);
            mVideo.start();

            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            mVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });

        }
    }
    private int getVideoName(){
        double raio = CommonUtil.getScreenRatio(this);
        int screenHeight = CommonUtil.getScreenWidth(this);
        int video =R.raw.splash_3_2_480_320;
        if(raio>= 16/(double)9 && screenHeight>= 720){
            video = R.raw.splash_16_9_1280_720;
        }else  if(raio>= 5/(double)3 && screenHeight>=480){
            video = R.raw.splash_5_3_800_480;
        }else if(raio>= 8/(double)5 && screenHeight >= 800){
            video = R.raw.splash_8_5_1280_800;
        }else if(raio>= 3/(double)2 && screenHeight >=853){
            video = R.raw.splash_3_2_1280_853;
        }else if(raio>= 3/(double)2 && screenHeight >=320){
            video = R.raw.splash_3_2_480_320;
        }else  if(raio>= 4/(double)3 && screenHeight >= 768){
            video = R.raw.splash_4_3_1024_768;
        }else if(raio>= 4/(double)3 && screenHeight >= 360){
            video = R.raw.splash_4_3_480_360;
        }
        return video;

        /*int video =R.raw.splash_320_240;
        if(screenHeight >=2560){
            video = R.raw.splash_2560_1440;
        }else if(screenHeight >= 1280){
            video = R.raw.splash_1280_800;
        }else if(screenHeight >=960){
            video = R.raw.splash_960_640;
        }else if(screenHeight>=800){
            video = R.raw.splash_800_480;
        }else if(screenHeight >= 320){
            video = R.raw.splash_320_240;
        }
        return video;
*/
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePosition = mVideo.getCurrentPosition();
        if (mVideo.isPlaying())
            mVideo.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideo != null) {
            mVideo.seekTo(pausePosition);
            mVideo.start();
        }
    }
}
