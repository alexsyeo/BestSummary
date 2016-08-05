package com.gigstudios.newssummary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsListAdapter extends BaseAdapter {

    private ArrayList<Article> newsArticles;
    LayoutInflater inflater;
    Context context;


    public NewsListAdapter(Context context, ArrayList<Article> newsArticles) {
        this.newsArticles = newsArticles;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return newsArticles.size();
    }

    @Override
    public Article getItem(int position) {
        return newsArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Article newsArticle = getItem(position);

        mViewHolder.titleTextView.setText(newsArticle.getTitle());
        mViewHolder.summaryTextView.setText(newsArticle.getSummary());

        mViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsArticle.getUrl()));
                context.startActivity(browserIntent);
            }
        });

        return convertView;
    }

    private class MyViewHolder {
        CardView cardView;
        TextView titleTextView, summaryTextView;

        public MyViewHolder(View item) {
            cardView = (CardView) item.findViewById(R.id.card_view);
            titleTextView = (TextView) item.findViewById(R.id.title);
            summaryTextView = (TextView) item.findViewById(R.id.summary);
        }
    }
}
