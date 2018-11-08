package com.example.xyzreader.ui;

//import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//import android.app.FragmentManager;
//import android.app.LoaderManager;
//import android.content.Loader;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
//import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends  AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    //private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    //private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    //private View mUpButtonContainer;
    //private View mUpButton;

    private OnPageChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(0, null, this);
        //getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        final FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(Intent.createChooser(
                        ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                                .setType("text/plain").setText("Some sample text")
                                .getIntent(), getString(R.string.action_share)));

            }

        });
        /*
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mUpButton.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
                //mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                //updateUpButtonPosition();
                String photoUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
                ImageView backdrop = findViewById(R.id.backdrop);
                Glide.with(ArticleDetailActivity.this).load(photoUrl)
                        .transition(DrawableTransitionOptions.withCrossFade()).into(backdrop);
            }
        });

        //mUpButtonContainer = findViewById(R.id.up_container);

       // mUpButton = findViewById(R.id.action_up);
        /*
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });
        */
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    view.onApplyWindowInsets(windowInsets);
                    mTopInset = windowInsets.getSystemWindowInsetTop();
                    mUpButtonContainer.setTranslationY(mTopInset);
                    updateUpButtonPosition();
                    return windowInsets;
                }
            });
        }
        */

        listener = new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mCursor.moveToPosition(position);

                String photoUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
                ImageView backdrop = findViewById(R.id.backdrop);
                Glide.with(ArticleDetailActivity.this).load(photoUrl)
                        .transition(DrawableTransitionOptions.withCrossFade()).into(backdrop);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        fab.hide();
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        fab.show();
                        break;
                }

            }

        };
        mPager.addOnPageChangeListener(listener);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                //mSelectedItemId = mStartId;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mPagerAdapter.notifyDataSetChanged();

        mPager.post(new Runnable() {

            @Override
            public void run() {

                listener.onPageSelected(mPager.getCurrentItem());

            }

        });

        // Select the start ID
        if (mStartId > 0) {

            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {

                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {

                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;

                }
                mCursor.moveToNext();

            }
            mStartId = 0;

        }
    }



    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
        if (itemId == mSelectedItemId) {
            //mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
            //updateUpButtonPosition();
        }
    }


    private void updateUpButtonPosition() {
        //int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
        //mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
                //mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
                //updateUpButtonPosition();
            }
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
