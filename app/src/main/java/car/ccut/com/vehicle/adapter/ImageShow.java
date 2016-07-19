package car.ccut.com.vehicle.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import car.ccut.com.vehicle.util.MemoryCache;

public class ImageShow {

    private Context context;
    private MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    public ImageShow(Context context) {
        executorService = Executors.newFixedThreadPool(5);
        this.context = context;
    }

    public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else if (!isLoadOnlyFromCache) {
            Log.v("image", "1");
            queuePhoto(url, imageView);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        Log.v("image", "222" + url);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        try {
            Bitmap bitmap = null;
            File file = new File(getAlbumArt(Integer.parseInt(url)));
            bitmap = decodeFile(file);
            return bitmap;
        } catch (Exception ex) {
            Log.e("image", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
            return null;
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
            Log.v("image", "4");
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                Log.v("image", "5");
            }
        }
    }

    public String getAlbumArt(int trackId) {// trackId�����ֵ�id
        String mUriTrack = "content://media/external/audio/media/#";
        String[] projection = new String[]{"album_id"};
        String selection = "_id = ?";
        String[] selectionArgs = new String[]{Integer.toString(trackId)};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriTrack), projection, selection, selectionArgs, null);
        int album_id = 0;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_id = cur.getInt(0);
        }
        cur.close();
        cur = null;

        if (album_id < 0) {
            return null;
        }
        String mUriAlbums = "content://media/external/audio/albums";
        projection = new String[]{"album_art"};
        cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);

        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    public void clearCache() {
        memoryCache.clear();
    }
}