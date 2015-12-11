package com.huawei.esdk.te.call;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import tupsdk.TupCall;

import com.huawei.esdk.te.util.LogUtil;
import com.huawei.voip.IpCallNotification;
import com.huawei.voip.data.CameraViewRefresh;
import com.huawei.voip.data.EventData;
import com.huawei.voip.data.SessionBean;
import common.AuthType;
import common.DeviceStatus;

public class IpCallNotificationImpl implements IpCallNotification
{

	private static final String TAG = IpCallNotificationImpl.class.getSimpleName();

	private static IpCallNotificationImpl instance = new IpCallNotificationImpl();

	private CopyOnWriteArrayList<CallNotification> mCallNotificationListeners = new CopyOnWriteArrayList<CallNotification>();

	public static IpCallNotificationImpl getInstance()
	{
		return instance;
	}

	public synchronized void registerNotification(CallNotification listener)
	{
		LogUtil.d(TAG, "registerNotification() listener->" + listener.toString());
		if (listener != null && !mCallNotificationListeners.contains(listener))
			mCallNotificationListeners.add(listener);
	}

	public synchronized void unRegisterNotification(CallNotification listener)
	{
		if (listener == null)
		{
			LogUtil.d(TAG, "CallManager remove all CallBack");
			mCallNotificationListeners.clear();
		} else
		{
			mCallNotificationListeners.remove(listener);
		}
	}

	@Override
	public void onAdConfirmation(AuthType authType)
	{
		LogUtil.d(TAG, "IpCallNotification - onAdConfirmation() - AuthType : " + authType);
	}

	@Override
	public void onAudioQuality(EventData eventData)
	{
		LogUtil.d(TAG, "IpCallNotification - onAudioQuality()");
		// TODO
		// CallActionNotifyActivty.getIns().notifyCallViewNetSigal(((VoiceQuality)
		// eventData).getLevel());
		// for (Iterator iterator = mCallNotificationListeners.iterator();
		// iterator.hasNext();)
		// {
		// CallNotification listener = (CallNotification) iterator.next();
		// try
		// {
		// listener.onAudioQuality(eventData);
		// } catch (Exception exception)
		// {
		// LogUtil.e(TAG, "onCallViedoResult catch exception:" +
		// exception.toString());
		// }
		// }
	}

	/**
	 * 音、视频互转，变更结果通知
	 */
	@Override
	public void onCallViedoResult(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallViedoResult() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallViedoResult()");

		CallLogic.getInstance().processCallNtfModified(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallViedoResult(new Call(sessionBean));
			} catch (Exception exception)
			{
				LogUtil.e(TAG, "onCallViedoResult catch exception:" + exception.toString());
			}
		}
	}

	/**
	 * 对端降音频通知
	 */
	@Override
	public void onCallDelViedo(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallDelViedo() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallDelViedo()");

		CallLogic.getInstance().processCallNtfModified(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallDelViedo(new Call(sessionBean));
			} catch (Exception exception)
			{
				LogUtil.e(TAG, "onCallViedoResult catch exception:" + exception.toString());
			}
		}
	}

	/**
	 * 添加视频请求
	 */
	@Override
	public void onCallAddVideo(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallAddVideo() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallAddVideo()");

		CallLogic.getInstance().processCallNtfModifyAlert(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallAddVideo(new Call(sessionBean));
			} catch (Exception exception)
			{
				LogUtil.e(TAG, "onCallViedoResult catch exception:" + exception.toString());
			}
		}
	}

	@Override
	public void onSessionModified(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onSessionModified() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onSessionModified()");

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onSessionModified(new Call(sessionBean));
			} catch (Exception exception)
			{
			}
		}
	}

	@Override
	public void onCallComing(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallComing() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallComing()");

		CallLogic.getInstance().processCallNtfComing(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallComing(new Call(sessionBean));
			} catch (Exception exception)
			{
			}
		}
	}

	@Override
	public void onCallConnect(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallConnect() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallConnect()");

		CallLogic.getInstance().processCallNtfTalk(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallConnect(new Call(sessionBean));
			} catch (Exception exception)
			{
			}
		}
	}

	/**
	 * 收到通话结束释放资源后消息
	 */
	@Override
	public void onCallDestroy(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallDestroy() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallDestroy()");

		CallLogic.getInstance().processCallNtfClosed(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallDestroy(new Call(sessionBean));
			} catch (Exception exception)
			{
			}
		}
	}

	/**
	 * 收到呼叫转移的refer消息后在此处理
	 */
	@Override
	public void onCallGoing(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallGoing() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallGoing()");

	}

	/**
	 * view刷新通知
	 */
	@Override
	public void onCallRefreshView(CameraViewRefresh data)
	{
		LogUtil.d(TAG, "IpCallNotification - onCallRefreshView()");

		CallLogic.getInstance().processCallNtfRefreshView(data);
	}

	/**
	 * 对方挂断
	 */
	@Override
	public void onCallend(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onCallend() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onCallend()");

		CallLogic.getInstance().processCallNtfEnded(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onCallend(new Call(sessionBean));
			} catch (Exception exception)
			{
			}
		}
	}

	// 协商结果处理
	// BFCP（共享）
	@Override
	public void onDataReady(int callId, int bfcpRet)
	{
		LogUtil.d(TAG, "IpCallNotification - onDataReady()");
		boolean isBfcpEnabled = (bfcpRet == 1 ? true : false);
		CallLogic.getInstance().processBFCPConsultRet(callId + "", isBfcpEnabled);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onDataReady(callId, bfcpRet);
			} catch (Exception exception)
			{
			}
		}
	}

	@Override
	public void onCallReferSetConfCtrlDesable()
	{
		LogUtil.d(TAG, "IpCallNotification - onCallReferSetConfCtrlDesable()");
	}

	// BFCP（共享）接收开始
	@Override
	public void onDataReceiving(int callId)
	{
		LogUtil.d(TAG, "IpCallNotification - onDataReceiving()");
		CallLogic.getInstance().processBFCPAccptedStart(String.valueOf(callId));

		// for (Iterator iterator = mCallNotificationListeners.iterator();
		// iterator.hasNext();)
		// {
		// CallNotification listener = (CallNotification) iterator.next();
		// try
		// {
		// listener.onDataReceiving(String.valueOf(callId));
		// } catch (Exception exception)
		// {
		// }
		// }
	}

	// BFCP（共享）
	@Override
	public void onDataStopped(int callId)
	{
		LogUtil.d(TAG, "IpCallNotification - onDataStopped()");
		CallLogic.getInstance().processBFCPStoped(String.valueOf(callId));

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onDataStopped(String.valueOf(callId));
			} catch (Exception exception)
			{
			}
		}
	}

	// BFCP（共享）
	@Override
	public void onDataSending(int callId)
	{
		LogUtil.d(TAG, "IpCallNotification - onDataSending()");
		// processRequestBfcpResult(String.valueOf(callId), true, "");
	}

	// BFCP（共享）
	@Override
	public void onDataStartErr(int callId, int errorCode)
	{
		LogUtil.d(TAG, "IpCallNotification - onDataStartErr()");
		// processRequestBfcpResult(String.valueOf(callId), false,
		// String.valueOf(errorCode));
	}

	@Override
	public void onDecodeSuccess(int arg0)
	{
		LogUtil.d(TAG, "IpCallNotification - onDecodeSuccess()");

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onDataReceiving(String.valueOf(CallLogic.getInstance().getCurrentCallID()));
			} catch (Exception exception)
			{
			}
		}
	}

	@Override
	public void onDeviceStatusChanged(DeviceStatus status)
	{
		LogUtil.d(TAG, "IpCallNotification - onDeviceStatusChanged()");
	}

	@Override
	public void onMobileRouteChanged(TupCall arg0)
	{
		LogUtil.d(TAG, "IpCallNotification - onMobileRouteChanged()");
	}

	@Override
	public void onNotifyEnterpriseAddressBookType(boolean arg0)
	{
		LogUtil.d(TAG, "IpCallNotification - onNotifyEnterpriseAddressBookType()");
	}

	/**
	 * 通知密码修改成功失败
	 */
	@Override
	public void onPasswordSuccess(int success)
	{
		LogUtil.d(TAG, "IpCallNotification - onPasswordSuccess()");
	}

	/**
	 * 收到对端振铃消息
	 */
	@Override
	public void onRingBack(SessionBean sessionBean)
	{
		if (null == sessionBean)
		{
			LogUtil.e(TAG, "IpCallNotification - onRingBack() error -> sessionBean is null");
			return;
		}
		LogUtil.d(TAG, "IpCallNotification - onRingBack()");

		CallLogic.getInstance().processCallNtfRinging(sessionBean);

		for (Iterator iterator = mCallNotificationListeners.iterator(); iterator.hasNext();)
		{
			CallNotification listener = (CallNotification) iterator.next();
			try
			{
				listener.onRingBack(new Call(sessionBean));
			} catch (Exception exception)
			{
			}
		}
	}

	@Override
	public void onVideoQuality(EventData arg0)
	{
		LogUtil.d(TAG, "IpCallNotification - onVideoQuality()");
	}

	@Override
	public void onVideoStatisticNetinfo(EventData arg0)
	{
		LogUtil.d(TAG, "IpCallNotification - onVideoStatisticNetinfo()");
	}

	/**
	 * 方法名称：reportNofitication
	 * 
	 * @param comId
	 *            组件id
	 * @param funid
	 *            通知id
	 */
	@Override
	public int reportNofitication(String comId, int funid, EventData data)
	{
		LogUtil.e(TAG, "IpCallNotification - reportNofitication()");
		return 0;
	}
}
