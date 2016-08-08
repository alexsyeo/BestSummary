package com.gigstudios.newssummary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;

import org.apache.commons.io.*;
import java.io.ByteArrayInputStream;

public class MainActivity extends AppCompatActivity {

    public static String[] sectionUrls = {"https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=w&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=n&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=s&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=b&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=tc&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=e&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=snc&output=rss",
            "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=m&output=rss"};

    public static int currentSection = 0;
    public static int SUMMARY_SENTENCES;
    public static POSModel posModel;
    public static SentenceModel sentenceModel;
    private boolean isRefreshing = true;

    private Toolbar toolbar;
    private ListView listView;
    private ImageView refreshImageView;

    private static ArrayList<Article> newsArticles = new ArrayList<>();
    private static NewsListAdapter listAdapter;
    public static ArrayList<AsyncJob<ArticleReceiver>> runningTasks = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();

        //load settings
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SUMMARY_SENTENCES = Integer.parseInt(SP.getString(getString(R.string.num_sentences_key), "3"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load settings
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SUMMARY_SENTENCES = Integer.parseInt(SP.getString(getString(R.string.num_sentences_key), "3"));

        //initialize layouts
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listView);
        refreshImageView = (ImageView) toolbar.findViewById(R.id.refresh_image_view);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshImageView.setAnimation(rotation);

        //refresh button on click
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRefreshing) {
                    fetchNews();
                    isRefreshing = true;
                }
            }
        });

        //toolbar setup
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        //setup drawer
        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();
        //titles
        final String[] sectionTitles = getResources().getStringArray(R.array.section_titles);
        //icons
        int[] sectionIcons = {R.drawable.top_stories, R.drawable.world, R.drawable.us,
                R.drawable.sports, R.drawable.business, R.drawable.technology,
                R.drawable.entertainment, R.drawable.science, R.drawable.health};
        for (int i = 0; i < sectionTitles.length; i++) {
            drawerItems.add(new PrimaryDrawerItem().withIdentifier(i).withName(sectionTitles[i]).withIcon(sectionIcons[i]));
        }

        final Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerItems(drawerItems)
                .withSelectedItem(0)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        toolbar.setTitle(sectionTitles[position]);
                        currentSection = position;
                        fetchNews();
                        return false;
                    }
                })
                .build();

        toolbar.setTitle(sectionTitles[drawer.getCurrentSelectedPosition()]);

        //setup listview
        listAdapter = new NewsListAdapter(this, newsArticles);
        listView.setAdapter(listAdapter);

        fetchNews();
    }

    public void fetchNews() {
        //show refresh icon
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshImageView.setAnimation(rotation);
        refreshImageView.getAnimation().start();

        //stop all other tasks
        if (!runningTasks.isEmpty()) {
            for (int i = 0; i < runningTasks.size(); i++) {
                runningTasks.remove(i);
            }
        }

        //clear articles
        newsArticles.clear();
        listAdapter.notifyDataSetChanged();

        AsyncJob<ArticleReceiver> builder = new AsyncJob.AsyncJobBuilder<ArticleReceiver>()
                .doInBackground(new AsyncJob.AsyncAction<ArticleReceiver>() {
                    @Override
                    public ArticleReceiver doAsync() {
                        // Do some background work
                        //setup POSModel
                        if (posModel == null) {
                            //load tagger
                            long time1 = System.currentTimeMillis();
                            posModel = setupPOSModel();
                            long time2 = System.currentTimeMillis();
                            System.out.println("POSMODEL TIME TAKEN IN msecs: " + (time2-time1));
                        }

                        //setup model
                        if (sentenceModel == null) {
                            //load sentence detector
                            long time1 = System.currentTimeMillis();
                            sentenceModel = setupSentenceModel();
                            long time2 = System.currentTimeMillis();
                            System.out.println("SENTENCEMODEL TIME TAKEN IN msecs: " + (time2-time1));
                        }

                        //AT LEAST 3, MOST 10
                        try {
                            return new ArticleReceiver(10, sectionUrls[currentSection], getApplicationContext(), MainActivity.this);
                        } catch (BoilerpipeProcessingException | SAXException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<ArticleReceiver>() {
                    @Override
                    public void onResult(ArticleReceiver result) {
                        if (result.getSectionLink().equals(sectionUrls[currentSection])) {
                            if (newsArticles.size() == 0) {
                                Toast.makeText(MainActivity.this, R.string.failed_to_gather_error, Toast.LENGTH_SHORT).show();
                            }

                            refreshImageView.getAnimation().cancel();
                        }

                        isRefreshing = false;
                    }
                })
                .create();

        //add to running tasks list and start task
        runningTasks.add(builder);
        runningTasks.get(0).start();
    }

    //sets up the part of speech tagger
    public POSModel setupPOSModel() {
        //ByteBufferInputStream modelIn = null;
        InputStream modelIn = null;
        POSModel model = null;
        try {
            InputStream stream = getResources().openRawResource(R.raw.en_pos_maxent);
            //byte[] b = IOUtils.toByteArray(stream);
            //ByteBuffer buf = ByteBuffer.wrap(b);
            final byte[] data = IOUtils.toByteArray(stream);
            modelIn = new ByteArrayInputStream(data);
            model = new POSModel(modelIn);
        } catch (IOException e) {
            // Model loading failed, handle the error
            System.out.println("---------beginning of error stacktrace-----------");
            e.printStackTrace();
            System.out.println("---------end of error stacktrace-----------------");
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("testing123 testing123");
        System.out.println("------------POS MODEL: " + model);
        return model;
    }

    public SentenceModel setupSentenceModel() {
        SentenceModel model = null;
        InputStream modelIn = null;

        try {
            modelIn = getResources().openRawResource(R.raw.en_sent);
            model = new SentenceModel(modelIn);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return model;
    }

    public static void addArticleToList(Article article) {
        newsArticles.add(article);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.about:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
