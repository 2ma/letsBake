package hu.am2.letsbake;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

//based of: https://stackoverflow.com/questions/28700391/using-cache-in-exoplayer
@Singleton
public class CacheDataSourceFactory implements DataSource.Factory {

    private final DefaultDataSourceFactory defaultDataSourceFactory;
    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;
    private final SimpleCache simpleCache;

    @Inject
    public CacheDataSourceFactory(Context context) {
        final String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        defaultDataSourceFactory = new DefaultDataSourceFactory(context, bandwidthMeter,
            new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
        final LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE);
        simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
    }

    @Inject


    @Override
    public DataSource createDataSource() {

        return new CacheDataSource(simpleCache, defaultDataSourceFactory.createDataSource(),
            CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, MAX_CACHE_SIZE);
    }
}
