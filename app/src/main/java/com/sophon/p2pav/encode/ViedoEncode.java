package com.sophon.p2pav.encode;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class ViedoEncode {
    private final static String TAG = "MeidaCodec";
    private int TIMEOUT_USEC = 10000;
    private MediaCodec mediaCodec;
    private int m_width;
    private int m_height;
    public byte[] configbyte;
    private static int yuvqueuesize = 10;
    private static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);
    private Context mContext;
    private String mFilePath;
    private BufferedOutputStream outputStream;
    private boolean isRuning = false;
    private int frameRate = 30;

    @SuppressLint("NewApi")
    public ViedoEncode(Context context, String filePath, int width, int height) {

        m_width = width;
        m_height = height;
        mContext = context;
        mFilePath = filePath;

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width,height );
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        try {
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
        createfile();
    }


    private void createfile() {
        File file = new File(mFilePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void StopThread() {
        isRuning = false;
        try {
            try {
                mediaCodec.stop();
                mediaCodec.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void StartEncoderThread() {
        Thread EncoderThread = new Thread(() -> {
            isRuning = true;
            byte[] input = null;
            long pts = 0;
            long generateIndex = 0;

            while (isRuning) {
                if (YUVQueue.size() > 0) {
                    input = YUVQueue.poll();
                    byte[] yuv420sp = new byte[m_width*m_height*3/2];
                    NV21ToNV12(input,yuv420sp,m_width,m_height);
                    input = yuv420sp;
                }
                if (input != null) {
                    try {
                        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                        int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
                        if (inputBufferIndex >= 0) {
                            pts = computePresentationTime(generateIndex);
                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                            inputBuffer.clear();
                            inputBuffer.put(input);
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                            generateIndex += 1;
                        }

                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                        while (outputBufferIndex >= 0) {
                            //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
                            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                            byte[] outData = new byte[bufferInfo.size];
                            outputBuffer.get(outData);
                            if (bufferInfo.flags == 2) {
                                configbyte = new byte[bufferInfo.size];
                                configbyte = outData;
                            } else if (bufferInfo.flags == 1) {
                                byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                                System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                                System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);

                                outputStream.write(keyframe, 0, keyframe.length);
                            } else {
                                outputStream.write(outData, 0, outData.length);
                            }

                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        EncoderThread.start();

    }

    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
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

//    private byte[] NV21ToNV12(byte[] nv21) {
//        int size = nv21.length;
//        nv12 = new byte[size];
//        int len = size * 2 / 3;
//        System.arraycopy(nv21, 0, nv12, 0, len);
//        int i = len;
//        while (i < size -1){
//            nv12[i] = nv21[i+1];
//            nv12[i+1] = nv21[i];
//            i += 2;
//        }
//        return nv21;
//    }
//
//    private static void protraitData2Rwa(byte[] data,byte[] output,int width,int height) {
//        int y_len = width * height;
//        int uvHeight = height >> 1;
//        int k = 0;
//        for(int j = 0 ; j < width;j++){
//            for(int i = height -1 ; i >=0;j--){
//                output[k++] = data[width * i + j];
//            }
//        }
//        for(int j = 0; j < width; j +=2){
//            for(int i = uvHeight -1 ; i >=0;i--){
//                output[k++] = data[y_len + width * i + j];
//                output[k++] = data[y_len + width * i + j + 1];
//            }
//        }
//    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / frameRate;
    }

    public void putYUVData(byte[] buffer) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }
}