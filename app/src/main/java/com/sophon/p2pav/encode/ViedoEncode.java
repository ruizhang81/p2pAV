package com.sophon.p2pav.encode;


import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.sophon.p2pav.Config;
import com.sophon.p2pav.utils.YuvUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ViedoEncode {
    private MediaCodec mediaCodec;
    private byte[] configbyte;

    @SuppressLint("NewApi")
    public ViedoEncode() {

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, Config.mImageHeight,Config.mImageWidth );
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, Config.mImageWidth * Config.mImageHeight);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, Config.frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
    }


    public void StopThread() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encoder(byte[] input,OnGetH264Listener onGetH264Listener) {
        long pts;
        long generateIndex = 0;

        byte[] yuv420sp = new byte[Config.mImageWidth * Config.mImageHeight*3/2];
        YuvUtil.NV21ToNV12(input,yuv420sp,Config.mImageHeight,Config.mImageWidth);

        byte[] rotateYuv420 = new byte[Config.mImageWidth * Config.mImageHeight*3/2];
        YuvUtil.YUV420spRotate90Anticlockwise(yuv420sp, rotateYuv420,Config.mImageWidth,Config.mImageHeight);
        input = rotateYuv420;
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(Config.TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                pts = YuvUtil.computePresentationTime(generateIndex);
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(input);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
            }else{
                return;
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, Config.TIMEOUT_USEC);
            while (outputBufferIndex >= 0) {
                //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    configbyte = new byte[bufferInfo.size];
                    configbyte = outData;
                } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                    byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                    System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                    System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                    onGetH264Listener.getH264(keyframe);
                } else {
                    onGetH264Listener.getH264(outData);
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, Config.TIMEOUT_USEC);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }





    public interface OnGetH264Listener{
        void getH264(byte[] h264);
    }


}