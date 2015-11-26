package com.huawei.esdk.te.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.huawei.application.BaseApp;
import com.huawei.esdk.te.data.Constants;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;


/**
 * 媒体接口
 */
public final class MediaUtil
{
	private static final String TAG = MediaUtil.class.getSimpleName();
    /*
     * 来电铃声文件名
     */
    private static final String CALL_COMING_RING = "call_coming_ring.ogg";
    
    /*
     * 对方接通后的铃声文件名
     */
    private static final String CALL_RSP_RING = "call_rsp_ring.wav";
    
    /*
     * 音乐流类型
     */
    public static final int STREAM_TYPE_MUSIC = AudioManager.STREAM_MUSIC;
    
    /*
     * 手机响铃流类型
     */
    public static final int STREAM_TYPE_RING  = AudioManager.STREAM_RING;
    
    /*
     * 手机通话流类型
     */
    public static final int STREAM_TYPE_CALL  = AudioManager.STREAM_VOICE_CALL;
    
    /**
     * 对象实例
     */
    private static MediaUtil instance;
    
    /**
     * 媒体Player
     */
    private MediaPlayer mediaPlayer;
    
    /**
     * 手机声音模式为正常
     */
    private static final int RINGER_MODE_NORMAL = 2;
    
    /**
     * 音频管理器
     */
    private static AudioManager audioMgr = (AudioManager) BaseApp.getApp()
            .getSystemService(Context.AUDIO_SERVICE);
    
    /**
     * 媒体线程池实例
     */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    /**
     * 震动
     */
    private Vibrator mVibtator;
    
    /*
     * 用于短促声
     */
    private SoundPool soundPool;
    
    /*
     * 消息铃声ID
     */
    private int messageId;
    
    /*
     * 通知铃声ID
     */
    private int notifyId;


    /**
     * 构造方法
     */
    private MediaUtil()
    {
        soundPool = new SoundPool(1, STREAM_TYPE_MUSIC, 0);
    }


    /**
     * 获取唯一实例
     * @return 实例对象
     */
    public static MediaUtil getIns()
    {
        synchronized (MediaUtil.class)
        {
            if (null == instance)
            {
                instance = new MediaUtil();
            }
            return instance;
        }
    }
    
//    /**
//     * 拨号盘声音控制类
//     * 
//     * Copyright (C) 2008-2013 华为技术有限公司(Huawei Tech.Co.,Ltd)
//     */
//    private static final class KeypadSounds
//    {
//        /*
//         * 实例
//         */
//        private static KeypadSounds ins;
//        
//        /*
//         * 按键资源ID对应媒体流的Map
//         */
//        private Map<Integer, Integer> soundSrc;
//        
//        /*
//         * 句柄
//         */
//        private SoundPool spool;
//        
//        static
//        {
//            initSoundResource(BaseApp.getApp());
//        }
//        
//        private KeypadSounds()
//        {
//        }
//        
//        /**
//         * 初始化按键对应的音频文件
//         * @param context 上下文
//         */
//        public static synchronized void initSoundResource(Context context)
//        {
//            int[] numId = {R.id.zero, R.id.one, R.id.two, R.id.three,
//                    R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight,
//                    R.id.nine, R.id.point, R.id.star, R.id.jing};
//                int[] soundId = {R.raw.m0, R.raw.m1, R.raw.m2, R.raw.m3, R.raw.m4,
//                    R.raw.m5, R.raw.m6, R.raw.m7, R.raw.m8, R.raw.m9,
//                    R.raw.m10, R.raw.m10, R.raw.m11};
//                
//            ins = new KeypadSounds();
//            ins.spool = new SoundPool(numId.length, STREAM_TYPE_MUSIC,
//                    0);
//            ins.soundSrc = new HashMap<Integer, Integer>(0);
//            for (int i = 0; i < soundId.length; i++)
//            {
//                ins.soundSrc.put(numId[i],
//                        ins.spool.load(context, soundId[i], 1));
//            }
//        }
//
//        /**
//         * 根据按钮ID铃声
//         * @param resId 按钮ID
//         */
//        public synchronized static void play(int resId)
//        {
//            if (null != ins && null != ins.spool)
//            {
//                ins.spool.play(ins.getSounds(resId), 1, 1, 1, 0, 1);
//            }
//        }
//
//        /**
//         * 根据按键资源id获取到媒体流
//         * @param resId 按键资源id
//         * @return
//         * @since  1.1 
//         * @history  
//         * 2013-11-21    v1.0.0    z00199735    create
//         */
//        private synchronized int getSounds(int resId)
//        {
//            Integer sid = null;
//            
//            if (ins.soundSrc != null)
//            {
//                sid = ins.soundSrc.get(resId);
//            }
//            
//            return null == sid ? -1 : sid;
//        }
//    }


    /**
     * 释放MediaPlayer资源
     */
    private void releaseMediaPlayer()
    {
        Log.d(TAG, "enter releaseMediaPlayer.");
        
//        if (null != executorService)
//        {
//            Log.d(TAG, "executorService is not null.");
//            getExecutorService().shutdownNow();
//            executorService = null;
//        }
        
        /*-------------Step:release------------*/
        synchronized (MediaUtil.class)
        {
            Log.d(TAG, "Enter Step release.");
            if (null != mediaPlayer)
            {
                Log.d(TAG, "enter release player.");
                if (mediaPlayer.isPlaying())
                {
                    Log.d(TAG, "stop play.");
                    mediaPlayer.stop();
                }

                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "leave release player.");
            }
            else 
            {
                Log.d(TAG, "mediaPlayer is null.");
            }
            Log.d(TAG, "Leave Step release.");
        }
        
        Log.d(TAG, "leave releaseMediaPlayer.");
    }


    /**
     * 指定路径铃声
     * @param assetsFileName 路径名
     * @param isLooping 是否循环
     * @param streamType 媒体流类型
     * @param alwaysPhonic 是否不判断启动直接
     */
    private void playRing(String assetsFileName, boolean isLooping, int streamType, boolean alwaysPhonic)
    {
        Log.d(TAG, "playRing.");
        if (isCallPhonic() || alwaysPhonic)
        {
            AssetFileDescriptor afd = null;
            
            try
            {
                afd = BaseApp.getApp().getAssets().openFd(assetsFileName);
                pausePlayer();
                if (null != executorService && !executorService.isShutdown())
                {
                    executorService.execute(new RingRunnable(afd, isLooping, streamType));
                }
            }
            catch(FileNotFoundException e)
            {
                Log.e(TAG, "play ring error,no ring file.");
            }
            catch (IllegalStateException e)
            {
                Log.e(TAG, "play ring error.");
            }
            catch (IOException e)
            {
                Log.e(TAG, "Progress get an Exception");
            }
        }
    }

    
    /**
     * 是否启动来电铃声 
     */
    private boolean isCallPhonic()
    {
        return true;
    }
    
    
    /**
     * 是否启动消息铃声
     */
    private boolean isImPhonic()
    {
        return false;
    }
    
    
    /**
     * 是否启动通知铃声
     */
    private boolean isNotifyPhonic()
    {
        return false;
    }

    
    /**
     * 铃声task
     */
    private class RingRunnable implements Runnable
    {
        private AssetFileDescriptor afd;
        private final boolean isLooping;
        private int type;
        RingRunnable(AssetFileDescriptor afd, boolean isLooping, int streamType)
        {
            this.afd = afd;
            this.isLooping = isLooping;
            type = streamType;
        }

        @Override
        public void run()
        {
            Log.d(TAG, "run.");
            synchronized (MediaUtil.class)
            {
                
                if (afd == null)
                {
                    return;
                }
                Log.d(TAG, "Enter Step Create.");
                if (null == mediaPlayer)
                {
                    Log.d(TAG, "create mediaPlayer.");
                    createPlayer();
                    mediaPlayer.reset();
                    Log.d(TAG, "create mediaPlayer ok.");
                }
                Log.d(TAG, "Leave Step Create.");
                Log.d(TAG, "Enter Step Start.");
                // 已被停止，不需要进行
                if (null == executorService)
                {
                    Log.w(TAG, "executorService is null. has been shutdown, release player.");
//                    releaseMediaPlayer();
                    return;
                }
                
                mediaPlayer.setAudioStreamType(type);
                
                try
                {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(),
                            afd.getStartOffset(), afd.getLength());
                    
                    mediaPlayer.prepare();
                }
                catch (IllegalArgumentException e)
                {
                    Log.e(TAG, "error.");
                }
                catch (IllegalStateException e)
                {
                    Log.e(TAG, e.toString());
                }
                catch (IOException e)
                {
                    Log.e(TAG, "error.");
                }
                
                mediaPlayer.setLooping(isLooping);
                mediaPlayer.start();
                
                Log.d(TAG, "Leave Step Start.");
            }
            
        }
    }


    /**
     * 获取音频管理器对象
     */
    private AudioManager getAudioManager()
    {
        return audioMgr;
    }


    /**
     * 创建MediaPlayer实例
     */
    private void createPlayer()
    {
        synchronized (MediaUtil.class)
        {
            if (null == mediaPlayer)
            {
                mediaPlayer = new MediaPlayer();
            }
        }
    }
    
//    /**
//     * 获取MediaPlayer实例
//     * @return MediaPlayer实例
//     * @since  1.1 
//     * @history  
//     * 2013-11-21    v1.0.0    z00199735    create
//     */
//    private MediaPlayer getMediaPlayer()
//    {        
//        synchronized (MediaUtil.class)
//        {
//            return mediaPlayer;
//        }
//    }

    
    /**
     * 消息铃声
     */
    public void playMessageRing()
    {
        if (RINGER_MODE_NORMAL == getAudioManager().getRingerMode() && isImPhonic())
        {
            soundPool.play(messageId, 1, 1, 1, 0, 1);
        }
    }
    
    
    /**
     * 通知铃声
     */
    public void playNotifyRing()
    {
        if (RINGER_MODE_NORMAL == getAudioManager().getRingerMode() && isNotifyPhonic())
        {
            soundPool.play(notifyId, 1, 1, 1, 0, 1);
        }
    }
    
    
//    /**
//     * 按键铃声
//     * @param keyResId 按键id
//     */
//    public void playKeypadSound(int keyResId)
//    {
//        if (AudioManager.RINGER_MODE_NORMAL == getAudioManager().getRingerMode())
//        {
//            KeypadSounds.play(keyResId);
//        }
//    }
//   

    /**
     * 对方接通回铃声
     * @since  1.1 
     * @history  
     * 2013-11-21    v1.0.0    z00199735    create
     */
    public void playCallRspRing()
    {
        playRing(CALL_RSP_RING, true, STREAM_TYPE_CALL, true);
    }

    
    /**
     * 来电铃声
     */
    public void playCallComingRing()
    {
        playRing(CALL_COMING_RING, true, STREAM_TYPE_RING, false);
    }


    /**
     * 暂停
     */
    public void pausePlayer()
    {
        synchronized (MediaUtil.class)
        {
            if (null != mediaPlayer && mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }


    /**
     * 停止声音
     */
    public synchronized void stopPlayer()
    {
        Log.d(TAG, "Enter stopPlayer.");
        releaseMediaPlayer();
        Log.d(TAG, "Leave stopPlayer.");
    }
    

    /**
     * 震动
     * @param vibrateTime 振动周期
     */
    public synchronized void vibrate(int vibrateTime)
    {
        if (mVibtator == null)
        {
            mVibtator = (Vibrator) BaseApp.getApp().getSystemService(
                    Context.VIBRATOR_SERVICE);
        }
        mVibtator.vibrate(vibrateTime);
    }


    /**
     * 循环震动
     */
    public synchronized void loopVibrator()
    {
        if (mVibtator == null)
        {
			mVibtator = (Vibrator) BaseApp.getApp().getSystemService(
                    Context.VIBRATOR_SERVICE);
        }
        mVibtator.vibrate(new long[] {0, 2000, 500, 2000, 500, 2000, 500 }, 0);
    }


    /**
     * 取消震动
     */
    public synchronized void cancelVibrate()
    {
        if (null == mVibtator)
        {
            return;
        }
        Log.d(TAG, "Cancel Vibrate.");
        mVibtator.cancel();
    }

}
