    package com.mygdx.game.Gtab3;

    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.lang.reflect.Array;
    import java.util.ArrayList;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.media.AudioFormat;
    import android.os.Bundle;
    import android.os.Environment;
    import android.util.Log;
    import android.view.Menu;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.TextView;

    import com.mygdx.game.R;
    import com.mygdx.game.Gtab3.Algorithmes.Filtrage;
    import com.mygdx.game.Gtab3.sound.AudioPlayer;
    import com.mygdx.game.Gtab3.sound.AudioRecorder;
    import com.mygdx.game.Gtab3.sound.Process;
    import com.mygdx.game.Gtab3.tablature.activities.TabActivity;

    import be.tarsos.dsp.AudioDispatcher;
    import be.tarsos.dsp.AudioEvent;
    import be.tarsos.dsp.AudioProcessor;
    import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
    import be.tarsos.dsp.io.android.AudioDispatcherFactory;

    import be.tarsos.dsp.pitch.PitchDetectionHandler;
    import be.tarsos.dsp.pitch.PitchDetectionResult;
    import be.tarsos.dsp.pitch.PitchProcessor;

    public class IHMActivity extends Activity {

        private AudioPlayer mPlayer = null;
        private AudioRecorder audio = null;
        private Process process = null;
        private ImageButton record;
        private ImageButton play;
        private ImageButton yin;
        private ImageButton tablature;
        private EditText text;
        private TextView console;
        private boolean isRecording = true;
        private boolean isPlaying = true;
        private String mFileIn = "";
        private String mFileOut = "";
        private String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/SoundProject";


        private ArrayList<String> freq;
        private AudioDispatcher dispatcher;
        private FileWriter fwo;
        private BufferedWriter out;

        public IHMActivity() {
        }


        @Override
        protected void onCreate(Bundle savedInstanceState)  {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ihm);
            record = (ImageButton) findViewById(R.id.startRecord);
         //   play = (ImageButton) findViewById(R.id.startPlay);
           // yin = (ImageButton) findViewById(R.id.startYin);
            tablature = (ImageButton)findViewById(R.id.startTablature);
            text = (EditText) findViewById(R.id.status_text);
            console = (TextView) findViewById(R.id.textView);
            (new File(path)).mkdirs();
            text.setSingleLine(true);
            text.setEnabled(true);

            freq = new ArrayList<String>();

            // A vérifier
            initAudioDispatcher();
        }

        // Pas sûr que ça soit bon
        public void initAudioDispatcher(){
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                    final float pitchInHz = pitchDetectionResult.getPitch();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // if (pitchInHz != -1)
                            try {
                                freq.add(Float.toString(pitchInHz));
                                out.write(Float.toString(pitchInHz));
                                out.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            AudioProcessor p  = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
            dispatcher.addAudioProcessor(p);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.ihm, menu);
            return true;
        }

        public void onRecord(final View v) {
            if (isRecording) {

                mFileIn = path + "/" + text.getText().toString() + ".wav";
                Log.i("before", "avant linstanciation");
                audio = AudioRecorder.getInstance();
                Log.i("after", "apres linstanciation");
                if ((new File(mFileIn)).exists()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Be careful !")
                            .setMessage(
                                    "This file already exists.\n Do you want to overwrite it?")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {
                                            dialog.cancel();// if yes, then do as a
                                                            // normal record
                                           // play.setEnabled(false);
                                           // yin.setEnabled(false);
                                            text.setEnabled(false);
                                            record.setBackgroundResource(R.drawable.utilities_closemic);
                                            audio.setOutputFile(mFileIn);
                                            audio.prepare();
                                            audio.start();
                                            fwo = null;
                                            try {
                                                fwo = new FileWriter(path+"/"+text.getText().toString() + ".txt");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            out = new BufferedWriter(fwo);
                                            new Thread(dispatcher,"AudioDispatcher").start();
                                            //console.setText("Enregistrement switch on\n"
                                            //		+ console.getText());
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {
                                            dialog.cancel();// if no, then abort the
                                                            // recording.
                                            record.setBackgroundResource(R.drawable.oldmic);
                                           // play.setEnabled(true);
                                          //  yin.setEnabled(true);
                                            text.setEnabled(true);
                                            isRecording = !isRecording;
                                        }
                                    }).show();
                } else {

                    audio = AudioRecorder.getInstance();
                    //record.setBackgroundResource(R.drawable.utilities_closemic);
                    audio.setOutputFile(mFileIn);
                    audio.prepare();
                    audio.start();
                    fwo = null;
                    Log.d("JEEEEE","LOOOOLO");
                    try {
                       // fwo = new FileWriter(path+"/test.txt");
                        fwo = new FileWriter(path+"/"+text.getText().toString() + ".txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("JEEEEE","LOOOOLO "+fwo);

                    out = new BufferedWriter(fwo);
                    new Thread(dispatcher,"AudioDispatcher").start();
                    //console.setText("Enregistrement switch on\n"
                    //		+ console.getText());
//                    play.setEnabled(false);
                //    yin.setEnabled(false);
                    text.setEnabled(false);
                }
            } else {
                audio.stop();
                audio.release();
                try {

                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dispatcher.stop();


                FileWriter fwo2 = null;
                try {
                    //fwo2 = new FileWriter(path+"/FilteredTest.txt");
                    fwo2 = new FileWriter(path+"/"+text.getText().toString() + "Filtre.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedWriter out2 = new BufferedWriter(fwo2);

                ArrayList<String> filteredList = Filtrage.filtre(freq);
                System.out.println(filteredList.size());
                for(String s: filteredList){
                    try {
                        out2.write(s);
                        out2.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    out2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //console.setText("Enregistrement terminé\n" + console.getText());
                record.setBackgroundResource(R.drawable.oldmic);
             //   play.setEnabled(true);
              //  yin.setEnabled(true);
                text.setEnabled(true);
            }
            isRecording = !isRecording;
        }

       /* public void onPlay(View v) {
            if (isPlaying) {
                mFileIn = path + "/" + text.getText().toString() + ".wav";

                if ((new File(mFileIn)).exists()) {
                    play.setBackgroundResource(R.drawable.utilities_audio_stop);
                    record.setEnabled(false);
                    yin.setEnabled(false);
                    text.setEnabled(false);
                    mPlayer = new AudioPlayer(mFileIn);
                    //console.setText("Lecture on\n" + console.getText());
                    mPlayer.startPlaying();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Be careful !")
                            .setMessage(
                                    "This file doesn't exist.\n Please choose another file")
                            .setNeutralButton("Close", null).show();
                    play.setBackgroundResource(R.drawable.videoplay);
                    record.setEnabled(true);
                    yin.setEnabled(true);
                    text.setEnabled(true);
                    isPlaying = !isPlaying;
                }
            } else {
                mPlayer.stopPlaying();
                play.setBackgroundResource(R.drawable.videoplay);
                //console.setText("Lecture off\n" + console.getText());
                play.setBackgroundResource(R.drawable.videoplay);
                record.setEnabled(true);
                yin.setEnabled(true);
                text.setEnabled(true);
            }
            isPlaying = !isPlaying;
        }

        public void onYin(View v) {
            long start = 0;
            long end = 0;
            long duration = 0;
            record.setEnabled(false);
            yin.setEnabled(false);
            text.setEnabled(false);
            process = new Process();
            mFileIn = path + "/" + text.getText().toString() + ".wav";
            mFileOut = path + "/" + text.getText().toString() + ".txt";

            if ((new File(mFileIn)).exists()) {
                try {
                    //console.setText("Yin switch on \n" + console.getText());
                    start = System.currentTimeMillis();
                    process.process(mFileIn, mFileOut);
                    end = System.currentTimeMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //console.setText("Yin créé \n" + console.getText());
                duration = (end - start) / 1000;
                //console.setText("Temps d'exécution : " + duration + "s\n"
                //		+ console.getText());

                /*new AlertDialog.Builder(this)
                        .setTitle("Continue")
                        .setMessage(
                                "Do you want to generate the corresponding tablature?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        //lance mainactivity
                                         Intent intent = new Intent(IHMActivity.this, MainActivity.class);
                                        // intent.putExtra("fileName", mFileOut);
                                         startActivity(intent);
                                        //dialog.cancel();// a enlever
                                    }
                                }).setNegativeButton("No", null).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Be careful !")
                        .setMessage(
                                "This file doesn't exist.\n Please choose another file")
                        .setNeutralButton("Close", null).show();

            }
            record.setEnabled(true);
            yin.setEnabled(true);
            text.setEnabled(true);
            onTablature(v);
        }*/

        public void onTablature(View v) {
            Intent intent = new Intent(IHMActivity.this, TabActivity.class);
             //Intent intent = new Intent(IHMActivity.this,MainActivity.class);
             startActivity(intent);
        }

        public void onPause(){
            super.onPause();
            Log.d("Pause", "Jai fait pause");
            if(dispatcher!=null) {
                dispatcher.stop();
                isPlaying = false;
            }
        }

        @Override
        public void onResume(){
            super.onResume();
            Log.d("Resume", "Debut onResume");
            if(dispatcher!=null && !isPlaying) {
                initAudioDispatcher();
                isPlaying=true;
                Log.d("Resume","Not null");
            }
            Log.d("Resume", "Fin onResume");
        }

    }
