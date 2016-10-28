package com.example.liubo.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipException;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv_imageview = (ImageView) findViewById(R.id.iv_imageview);
        final ImageView iv_imageview_two = (ImageView) findViewById(R.id.iv_imageview_two);
        Glide.with(this).load(Uri.parse("http://packages.asiatravel.com/packageImage/Tour/AddtlImages/137/Night%20Safari.jpg"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("onLoadFailed = %s", e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv_imageview);


//        Picasso.with(this).load("http://packages.asiatravel.com/packageImage/Tour/AddtlImages/137/Night%20Safari.jpg")
//                .placeholder(R.drawable.default_image_big)
//                .into(iv_imageview_two);
//        Glide.with(this).load("http://packages.asiatravel.com/packageImage/Tour/AddtlImages/2542/S.E.A%20Aquarium%206.jpg")
//                .into(iv_imageview_two);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv_imageview_two,
                R.drawable.default_image_big, R.drawable.default_image_big);
        imageLoader.get("http://packages.asiatravel.com/packageImage/Tour/AddtlImages/137/Night%20Safari.jpg", listener);
    }

    public void showHello(View view) {
        try {
            Log.d("liubo", "comment = " + readApkComment(new File(getPackagePath(this))));
            //((TextView) findViewById(R.id.tv_butterknife_test)).setText(readApkComment(new File(getPackagePath(this))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPackagePath(Context context) {
        if (context != null) {
            return context.getPackageCodePath();
        }
        return null;
    }

    static int ENDHDR = 22;
    public static final long LOCSIG = 0x4034b50, EXTSIG = 0x8074b50,
            CENSIG = 0x2014b50, ENDSIG = 0x6054b50;

    private static String readApkComment(File file) {
        String comment = "";
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long scanOffset = raf.length() - ENDHDR;
            if (scanOffset < 0) {
                throw new ZipException("File too short to be a zip file: " + raf.length());
            }

            raf.seek(0);
            final int headerMagic = Integer.reverseBytes(raf.readInt());
            if (headerMagic == ENDSIG) {
                throw new ZipException("Empty zip archive not supported");
            }
            if (headerMagic != LOCSIG) {
                throw new ZipException("Not a zip archive");
            }

            long stopOffset = scanOffset - 65536;
            if (stopOffset < 0) {
                stopOffset = 0;
            }

            long eocdOffset;
            while (true) {
                raf.seek(scanOffset);
                if (Integer.reverseBytes(raf.readInt()) == ENDSIG) {
                    eocdOffset = scanOffset;
                    break;
                }

                scanOffset--;
                if (scanOffset < stopOffset) {
                    throw new ZipException("End Of Central Directory signature not found");
                }
            }

            final long zip64EocdRecordOffset = parseZip64EocdRecordLocator(raf, eocdOffset);

            // Seek back past the eocd signature so that we can continue with our search.
            // Note that we add 4 bytes to the offset to skip past the signature.
            EocdRecord record = parseEocdRecord(raf, eocdOffset + 4, (zip64EocdRecordOffset != -1) /* isZip64 */);
            // Read the comment now to avoid an additional seek. We also know the commentLength
            // won't change because that information isn't present in the zip64 eocd record.
            if (record.commentLength > 0) {
                byte[] commentBytes = new byte[record.commentLength];
                raf.readFully(commentBytes);
                comment = new String(commentBytes, 0, commentBytes.length, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {

        }
        return comment;
    }

    static class EocdRecord {
        final long numEntries;
        final long centralDirOffset;
        final int commentLength;

        EocdRecord(long numEntries, long centralDirOffset, int commentLength) {
            this.numEntries = numEntries;
            this.centralDirOffset = centralDirOffset;
            this.commentLength = commentLength;
        }
    }
    private static EocdRecord parseEocdRecord(RandomAccessFile raf, long offset, boolean isZip64) throws IOException {
        raf.seek(offset);

        // Read the End Of Central Directory. ENDHDR includes the signature bytes,
        // which we've already read.
        byte[] eocd = new byte[ENDHDR - 4];
        raf.readFully(eocd);

        BufferIterator it = HeapBufferIterator.iterator(eocd, 0, eocd.length, ByteOrder.LITTLE_ENDIAN);
        final long numEntries;
        final long centralDirOffset;
        if (isZip64) {
            numEntries = -1;
            centralDirOffset = -1;

            // If we have a zip64 end of central directory record, we skip through the regular
            // end of central directory record and use the information from the zip64 eocd record.
            // We're still forced to read the comment length (below) since it isn't present in the
            // zip64 eocd record.
            it.skip(16);
        } else {
            // If we don't have a zip64 eocd record, we read values from the "regular"
            // eocd record.
            int diskNumber = it.readShort() & 0xffff;
            int diskWithCentralDir = it.readShort() & 0xffff;
            numEntries = it.readShort() & 0xffff;
            int totalNumEntries = it.readShort() & 0xffff;
            it.skip(4); // Ignore centralDirSize.

            centralDirOffset = ((long) it.readInt()) & 0xffffffffL;
            if (numEntries != totalNumEntries || diskNumber != 0 || diskWithCentralDir != 0) {
                throw new ZipException("Spanned archives not supported");
            }
        }

        final int commentLength = it.readShort() & 0xffff;
        return new EocdRecord(numEntries, centralDirOffset, commentLength);
    }
    /**
     * The header ID of the zip64 extended info header. This value is used to identify
     * zip64 data in the "extra" field in the file headers.
     */
    private static final short ZIP64_EXTENDED_INFO_HEADER_ID = 0x0001;


    /*
     * Size (in bytes) of the zip64 end of central directory locator. This will be located
     * immediately before the end of central directory record if a given zipfile is in the
     * zip64 format.
     */
    private static final int ZIP64_LOCATOR_SIZE = 20;

    /**
     * The zip64 end of central directory locator signature (4 bytes wide).
     */
    private static final int ZIP64_LOCATOR_SIGNATURE = 0x07064b50;

    /**
     * The zip64 end of central directory record singature (4 bytes wide).
     */
    private static final int ZIP64_EOCD_RECORD_SIGNATURE = 0x06064b50;

    public static long parseZip64EocdRecordLocator(RandomAccessFile raf, long eocdOffset)
            throws IOException {
        // The spec stays curiously silent about whether a zip file with an EOCD record,
        // a zip64 locator and a zip64 eocd record is considered "empty". In our implementation,
        // we parse all records and read the counts from them instead of drawing any size or
        // layout based information.
        if (eocdOffset > ZIP64_LOCATOR_SIZE) {
            raf.seek(eocdOffset - ZIP64_LOCATOR_SIZE);
            if (Integer.reverseBytes(raf.readInt()) == ZIP64_LOCATOR_SIGNATURE) {
                byte[] zip64EocdLocator = new byte[ZIP64_LOCATOR_SIZE  - 4];
                raf.readFully(zip64EocdLocator);
                ByteBuffer buf = ByteBuffer.wrap(zip64EocdLocator).order(ByteOrder.LITTLE_ENDIAN);

                final int diskWithCentralDir = buf.getInt();
                final long zip64EocdRecordOffset = buf.getLong();
                final int numDisks = buf.getInt();

                if (numDisks != 1 || diskWithCentralDir != 0) {
                    throw new ZipException("Spanned archives not supported");
                }

                return zip64EocdRecordOffset;
            }
        }

        return -1;
    }


}
