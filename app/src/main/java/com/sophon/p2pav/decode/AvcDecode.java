package com.sophon.p2pav.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.sophon.p2pav.Config;
import com.sophon.p2pav.utils.YucUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AvcDecode {

    private MediaCodec mediaCodec;
    private long presentationTimeUs = 0;

    public AvcDecode(Surface surface) {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(
                MediaFormat.MIMETYPE_VIDEO_AVC, Config.mImageWidth, Config.mImageHeight);
        try {
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaCodec.configure(mediaFormat, surface, null, 0);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean decodeH264(byte[] h264) {
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(Config.TIMEOUT_USEC);

        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(h264);

            //计算pts
            long pts = YucUtils.computePresentationTime(presentationTimeUs);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, h264.length, pts, 0);
            presentationTimeUs += 1;

        } else {
            return false;
        }

        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, Config.TIMEOUT_USEC);
        while (outputBufferIndex >= 0) {
            sleepRender(bufferInfo);
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);//到这里为止应该有图像显示了
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, Config.TIMEOUT_USEC);
        }

        return true;

    }


    private long startMs = -1;
    /**
     * 数据的时间戳对齐
     **/
    protected void sleepRender(MediaCodec.BufferInfo info) {
        if (startMs == -1) {
            startMs = System.currentTimeMillis();
        }
        long ptsTimes = info.presentationTimeUs / 1000;
        long systemTimes = System.currentTimeMillis() - startMs;
        long timeDifference = ptsTimes - systemTimes;
        // 如果当前帧比系统时间差快了，则延时以下
        if (timeDifference > 0) {
            try {
                Thread.sleep(timeDifference);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}