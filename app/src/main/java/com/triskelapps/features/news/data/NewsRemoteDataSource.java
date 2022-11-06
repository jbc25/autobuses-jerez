package com.triskelapps.features.news.data;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.triskelapps.base.BaseInteractor;

import java.util.ArrayList;
import java.util.List;

public class NewsRemoteDataSource {

    private final String TAG = this.getClass().getSimpleName();

    public static final String COLLECTION_NEWS = "news"/* + (BuildConfig.DEBUG ? "_test" : "")*/;
    private final FirebaseFirestore firebaseFirestore;

    public NewsRemoteDataSource(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    public void getNews(final BaseInteractor.CallbackGetList<News> callback) {

        final List<News> newsList = new ArrayList<>();

        firebaseFirestore.collection(COLLECTION_NEWS)
                .orderBy(News.FIELD_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            News news = document.toObject(News.class);
                            news.setId(document.getId());
                            newsList.add(news);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        callback.onListReceived(newsList);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        callback.onError("Error getting documents." + task.getException().getMessage());
                    }

                });
    }
}
