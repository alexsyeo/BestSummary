package com.gigstudios.newssummary;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.uima.postag.POSTagger;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TOP_STORIES_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss";
    public static final String SPORTS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=s&output=rss";
    public static final String BUSINESS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=b&output=rss";
    public static final int SUMMARY_SENTENCES = 3;
    public static POSModel posModel;
    public static SentenceModel sentenceModel;

    private Toolbar toolbar;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<Article> newsArticles = new ArrayList<>();
    private NewsListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize layouts
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        try {
                                            fetchNews();
                                        } catch(BoilerpipeProcessingException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
        );

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
                        try {
                            fetchNews();
                        } catch(BoilerpipeProcessingException e){
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .build();

        toolbar.setTitle(sectionTitles[drawer.getCurrentSelectedPosition()]);

        //setup listview
        listAdapter = new NewsListAdapter(this, newsArticles);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onRefresh()  {
        try {
            fetchNews();
        } catch(BoilerpipeProcessingException e){
            e.printStackTrace();
        }
    }

    public void fetchNews() throws BoilerpipeProcessingException{
        //show refresh icon
        swipeRefreshLayout.setRefreshing(true);

        new AsyncJob.AsyncJobBuilder<ArticleReceiver>()
                .doInBackground(new AsyncJob.AsyncAction<ArticleReceiver>() {
                    @Override
                    public ArticleReceiver doAsync() {
                        // Do some background work
                        //setup POSTagger
                        if(posModel == null){
                            //load tagger
                            posModel = setupPOSTagger();
                        }
                        if(sentenceModel == null){
                            //load sentence detector
                            sentenceModel = setupSentenceModel();
                        }

                        //AT LEAST 3, MOST 10
                        try {
                            return new ArticleReceiver(10, TOP_STORIES_URL, getApplicationContext());
                        } catch(BoilerpipeProcessingException e1) {
                            e1.printStackTrace();
                        } catch(SAXException e2){
                            e2.printStackTrace();
                        }
                        return null;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<ArticleReceiver>() {
                    @Override
                    public void onResult(ArticleReceiver result) {
                        if(result.getArticles().size() != 0) {
                            //success!
                            newsArticles.clear();
                            newsArticles.addAll(result.getArticles());
                            listAdapter.notifyDataSetChanged();
                        }else{
                            //failed
                            Toast.makeText(MainActivity.this, R.string.failed_to_gather_error, Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }).create().start();
    }

    //sets up the part of speech tagger
    public POSModel setupPOSTagger() {
        InputStream modelIn = null;
        POSModel model = null;
        try {
            modelIn = getResources().openRawResource(R.raw.en_pos_maxent);
            model = new POSModel(modelIn);
        } catch (IOException e) {
            // Model loading failed, handle the error
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
            } else {
                //Error
            }
        }
        return model;
    }

    public static POSModel getPOSModel(){
        return posModel;
    }

    public static SentenceModel getSentenceModel(){
        return sentenceModel;
    }


}
