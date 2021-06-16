package com.sophon.p2pav.utils;

import com.sophon.p2pav.Config;

public class YuvUtil {

    public static byte[] rotateYUVDegree270AndMirror(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

// Rotate and mirror the Y luma

        int i = 0;

        int maxY = 0;

        for (int x = imageWidth - 1; x >= 0; x--) {
            maxY = imageWidth * (imageHeight - 1) + x * 2;

            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[maxY - (y * imageWidth + x)];

                i++;

            }

        }

// Rotate and mirror the U and V color components

        int uvSize = imageWidth * imageHeight;

        i = uvSize;

        int maxUV = 0;

        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            maxUV = imageWidth * (imageHeight / 2 - 1) + x * 2 + uvSize;

            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[maxUV - 2 - (y * imageWidth + x - 1)];

                i++;

                yuv[i] = data[maxUV - (y * imageWidth + x)];

                i++;

            }

        }

        return yuv;

    }

    /**
     * 此处为顺时针旋转270
     * @param data 旋转前的数据
     * @param imageWidth 旋转前数据的宽
     * @param imageHeight 旋转前数据的高
     * @return 旋转后的数据
     */
    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight){

        byte[] yuv =new byte[imageWidth*imageHeight*3/2];

        // Rotate the Y luma

        int i =0;

        for(int x = imageWidth-1;x >=0;x--){

            for(int y =0;y < imageHeight;y++){

                yuv[i]= data[y*imageWidth+x];
                i++;

            }
        }// Rotate the U and V color components
        i = imageWidth*imageHeight;

        for(int x = imageWidth-1;x >0;x=x-2){

            for(int y =0;y < imageHeight/2;y++){
                yuv[i]= data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i++;
                yuv[i]= data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i++;

            }

        }

        return yuv;

    }

    //顺时针旋转270度
    public static void YUV420spRotate270(byte[] des, byte[] src, int width, int height) {
        int n = 0;
        int uvHeight = height >> 1;
        int wh = width * height;
        //copy y
        for (int j = width - 1; j >= 0; j--) {
            for (int i = 0; i < height; i++) {
                des[n++] = src[width * i + j];
            }
        }

        for (int j = width - 1; j > 0; j -= 2) {
            for (int i = 0; i < uvHeight; i++) {
                des[n++] = src[wh + width * i + j - 1];
                des[n++] = src[wh + width * i + j];
            }
        }
    }

    //旋转180度（顺时逆时结果是同样的）
    public static void YUV420spRotate180(byte[] src, byte[] des, int width, int height) {

        int n = 0;
        int uh = height >> 1;
        int wh = width * height;
        //copy y
        for (int j = height - 1; j >= 0; j--) {
            for (int i = width - 1; i >= 0; i--) {
                des[n++] = src[width * j + i];
            }
        }


        for (int j = uh - 1; j >= 0; j--) {
            for (int i = width - 1; i > 0; i -= 2) {
                des[n] = src[wh + width * j + i - 1];
                des[n + 1] = src[wh + width * j + i];
                n += 2;
            }
        }
    }

    //顺时针旋转90
    public static void YUV420spRotate90Clockwise(byte[] src, byte[] dst, int srcWidth, int srcHeight) {
//        int wh = width * height;
//        int k = 0;
//        for (int i = 0; i < width; i++) {
//            for (int j = height - 1; j >= 0; j--) {
//                des[k] = src[width * j + i];
//                k++;
//            }
//        }
//        for (int i = 0; i < width; i += 2) {
//            for (int j = height / 2 - 1; j >= 0; j--) {
//                des[k] = src[wh + width * j + i];
//                des[k + 1] = src[wh + width * j + i + 1];
//                k += 2;
//            }
//        }

        int wh = srcWidth * srcHeight;
        int uvHeight = srcHeight >> 1;

        //旋转Y
        int k = 0;
        for (int i = 0; i < srcWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < srcHeight; j++) {
                dst[k] = src[nPos + i];
                k++;
                nPos += srcWidth;
            }
        }

        for (int i = 0; i < srcWidth; i += 2) {
            int nPos = wh;
            for (int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos + i];
                dst[k + 1] = src[nPos + i + 1];
                k += 2;
                nPos += srcWidth;
            }
        }

    }

    //逆时针旋转90
    public static  void YUV420spRotate90Anticlockwise(byte[] src, byte[] dst, int width, int height) {
        int wh = width * height;
        int uvHeight = height >> 1;

        //旋转Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            int nPos = width - 1;
            for (int j = 0; j < height; j++) {
                dst[k] = src[nPos - i];
                k++;
                nPos += width;
            }
        }

        for (int i = 0; i < width; i += 2) {
            int nPos = wh + width - 1;
            for (int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos - i - 1];
                dst[k + 1] = src[nPos - i];
                k += 2;
                nPos += width;
            }
        }

        //不进行镜像翻转
//        for (int i = 0; i < width; i++) {
//            int nPos = width - 1;
//            for (int j = 0; j < height; j++) {
//                dst[k] = src[nPos - i];
//                k++;
//                nPos += width;
//            }
//        }
//        for (int i = 0; i < width; i += 2) {
//            int nPos = wh + width - 2;
//            for (int j = 0; j < uvHeight; j++) {
//                dst[k] = src[nPos - i];
//                dst[k + 1] = src[nPos - i + 1];
//                k += 2;
//                nPos += width;
//            }
//        }

    }

    //镜像
    public static void Mirror(byte[] yuv_temp, int w, int h) {
        int i, j;

        int a, b;
        byte temp;
        //mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = yuv_temp[a];
                yuv_temp[a] = yuv_temp[b];
                yuv_temp[b] = temp;
                a++;
                b--;
            }
        }
        //mirror u
        int uindex = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = yuv_temp[a + uindex];
                yuv_temp[a + uindex] = yuv_temp[b + uindex];
                yuv_temp[b + uindex] = temp;
                a++;
                b--;
            }
        }
        //mirror v
        uindex = w * h / 4 * 5;
        for (i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = yuv_temp[a + uindex];
                yuv_temp[a + uindex] = yuv_temp[b + uindex];
                yuv_temp[b + uindex] = temp;
                a++;
                b--;
            }
        }
    }

    public static long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / Config.frameRate;
    }

    public static void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) {
            return;
        }
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }
}
