package com.sophon.p2pav.encode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.sophon.p2pav.decode.MyExtractor;

import java.io.IOException;

public class ViedoEncode {

    public ViedoEncode(Surface surface) {
//        try {
//            //获取 MediaExtractor
//            extractor = new MyExtractor(path);
//            //判断是音频还是视频
//            int type = decodeType();
//            //拿到音频或视频的 MediaFormat
//            mediaFormat = (type == VIDEO ? extractor.videoFormat : extractor.audioFormat);
//
//            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
//            //选择要解析的轨道
//            extractor.selectTrack(type == VIDEO ? extractor.videoTrackId : extractor.audioTrackId);
//            //创建 MediaCodec
//            mediaCodec = MediaCodec.createDecoderByType(mime);
//            //由子类去配置
//            configure(surface);
//            mediaCodec.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
