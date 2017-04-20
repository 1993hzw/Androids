package cn.forward.androids.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import cn.forward.androids.Image.ImageCache;

public class ImageUtils {


    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    //系统数据库存放图片的路径
    private static final Uri STORAGE_URI = Images.Media.EXTERNAL_CONTENT_URI;

    /**
     * 添加图片到系统数据库
     */
    public static Uri addImage(final ContentResolver cr, final String path) {
        File file = new File(path);
        String name = file.getName();
        int i = name.lastIndexOf(".");
        String title = name.substring(0, i);// 文件名称
        String filename = title + name.substring(i);
        int[] degree = new int[1];
        return ImageUtils.addImage(cr, title,
                System.currentTimeMillis(), null, file.getParent(),
                filename, degree);
    }


    /**
     * 添加图片到系统数据库
     */
    private static Uri addImage(ContentResolver cr, String title,
                                long dateTaken, Location location, String directory,
                                String filename, int[] degree) {
        // Read back the compressed file size.
        File file = new File(directory, filename);
        long size = file.length();
        ContentValues values = new ContentValues(9);
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, filename);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.ORIENTATION, degree[0]);
        values.put(Images.Media.DATA, file.getAbsolutePath());
        values.put(Images.Media.SIZE, size);

        if (location != null) {
            values.put(Images.Media.LATITUDE, location.getLatitude());
            values.put(Images.Media.LONGITUDE, location.getLongitude());
        }

        return cr.insert(STORAGE_URI, values);
    }

    /**
     * 添加视频到系统数据库
     */
    public static Uri addVideo(ContentResolver cr, String title,
                               long dateTaken, Location location, String directory, String filename) {
        String filePath = directory + "/" + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(directory, filename);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        // Read back the compressed file size.
        long size = new File(directory, filename).length();
        ContentValues values = new ContentValues(9);
        values.put(Video.Media.TITLE, title);
        values.put(Video.Media.DISPLAY_NAME, filename);
        values.put(Video.Media.DATE_TAKEN, dateTaken);
        values.put(Video.Media.MIME_TYPE, "video/3gpp");
        values.put(Video.Media.DATA, filePath);
        values.put(Video.Media.SIZE, size);

        if (location != null) {
            values.put(Images.Media.LATITUDE, location.getLatitude());
            values.put(Images.Media.LONGITUDE, location.getLongitude());
        }

        return cr.insert(STORAGE_URI, values);
    }


    /**
     * 旋转图片
     */
    public static Bitmap rotate(Bitmap bitmap,
                                int degree, boolean isRecycle) {
        Matrix m = new Matrix();
        m.setRotate(degree, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            if (isRecycle) bitmap.recycle();
            return bm1;
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取图片的Exif方向
     */
    public static int getBitmapExifRotate(String path) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        }
        return degree;
    }

    /*
     * 根据相片的Exif旋转图片
     */
    public static Bitmap rotateBitmapByExif(Bitmap bitmap, String path, boolean isRecycle) {
        int digree = getBitmapExifRotate(path);
        if (digree != 0) {
            // 旋转图片
            bitmap = ImageUtils.rotate(bitmap, digree, isRecycle);
        }
        return bitmap;
    }

    /**
     * @param path
     * @param context
     * @return
     */
    public static final Bitmap createBitmapFromPath(String path, Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int screenW = display.getWidth();
        int screenH = display.getHeight();
        return createBitmapFromPath(path, screenW, screenH);
    }

    /**
     * 获取一定尺寸范围内的的图片，防止oom。参考系统自带相机的图片获取方法
     *
     * @param path      路径
     * @param maxWidth  图片的最大宽
     * @param maxHeight 图片的最大高
     * @return 经过按比例缩放的图片
     * @throws IOException
     */
    public static final Bitmap createBitmapFromPath(String path, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        Options options = null;
        if (path.endsWith(".3gp")) {
            return ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MINI_KIND);
        } else {
            try {
                options = new Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int width = options.outWidth;
                int height = options.outHeight;
                options.inSampleSize = computeBitmapSimple(width * height, maxWidth * maxHeight * 2);
                options.inPurgeable = true;
                options.inPreferredConfig = Config.RGB_565;
                options.inDither = false;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, options);
                return ImageUtils.rotateBitmapByExif(bitmap, path, true);
            } catch (OutOfMemoryError error) {//内容溢出，则再次缩小图片
                options.inSampleSize *= 2;
                bitmap = BitmapFactory.decodeFile(path, options);
                return ImageUtils.rotateBitmapByExif(bitmap, path, true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public static final Bitmap createBitmapFromPath(byte[] data, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        Options options = null;
        try {

            options = new Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            int width = options.outWidth;
            int height = options.outHeight;
            options.inSampleSize = computeBitmapSimple(width * height, maxWidth * maxHeight * 2);
            options.inPurgeable = true;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = false;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            return bitmap;
        } catch (OutOfMemoryError error) {//内容溢出，则再次缩小图片
            options.inSampleSize *= 2;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算bitmap的simple值
     *
     * @param realPixels,图片的实际像素，
     * @param maxPixels,压缩后最大像素
     * @return simple值
     */
    public static int computeBitmapSimple(int realPixels, int maxPixels) {
        if (maxPixels <= 0) {
            return 1;
        }
        try {
            if (realPixels <= maxPixels) {//如果图片尺寸小于最大尺寸，则直接读取
                return 1;
            } else {
                int scale = 2;
                while (realPixels / (scale * scale) > maxPixels) {
                    scale *= 2;
                }
                return scale;
            }
        } catch (Exception e) {
            return 1;
        }
    }


    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int[] optimizeMaxSizeByView(View view, int maxImageWidth, int maxImageHeight) {
        int width = maxImageWidth;
        int height = maxImageHeight;

        if (width > 0 && height > 0) {
            return new int[]{width, height};
        }

        if (view != null) {
            final ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null) {
                if (params.width > 0) {
                    width = params.width;
                } else if (params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                    width = view.getWidth();
                }

                if (params.height > 0) {
                    height = params.height;
                } else if (params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                    height = view.getHeight();
                }
            }

            if (view instanceof ImageView) {
                if (width <= 0) {
                    Object obj = ReflectUtil.getValue(view, "mMaxWidth");
                    if (obj != null) {
                        int tempWidth = (int) obj;
                        if (tempWidth > 0 && tempWidth < Integer.MAX_VALUE) {
                            width = tempWidth;
                        }
                    }
                }
                if (height <= 0) {
                    Object obj = ReflectUtil.getValue(view, "mMaxHeight");
                    if (obj != null) {
                        int tempHeight = (int) obj;
                        if (tempHeight > 0 && tempHeight < Integer.MAX_VALUE) {
                            height = tempHeight;
                        }
                    }
                }
            }
        }

        if (width <= 0) {
            width = Util.getScreenWidth(view.getContext());
        }
        if (height <= 0) {
            height = Util.getScreenHeight(view.getContext());
        }

        return new int[]{width, height};
    }


    /**
     * 获取视频缩略图
     *
     * @param videoPath 视频路径
     * @param width
     * @param height
     * @param kind MediaStore.Images.Thumbnails.MINI_KIND or MediaStore.Images.Thumbnails.MICRO_KIND
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height, int kind, ImageCache imageCache) {
        Bitmap bitmap = null;
        String key = videoPath + width + height;
        if (imageCache != null) {
            bitmap = imageCache.getBitmap(key);
            if (bitmap != null) {
                return bitmap;
            }
        }
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap == null) {
            return null;
        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        if (imageCache != null) {
            if (bitmap != null) {
                imageCache.save(bitmap, key, Bitmap.CompressFormat.JPEG);
            }
        }
        return bitmap;
    }

}


