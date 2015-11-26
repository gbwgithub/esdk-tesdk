package com.huawei.esdk.te.call;

import com.huawei.voip.data.SessionBean;

public class Call 
{
	private SessionBean bean;
	
	public Call(SessionBean sessionBean)
	{
		this.bean = sessionBean;
	}
	
	public SessionBean getValue()
	{
		return bean;
	}
	
	public int getVideoModifyState()
	{
		return bean.getVideoModifyState();
	}


	public String getOperation()
	{
		return bean.getOperation();
	}


	public String getReferNumber()
	{
		return bean.getReferNumber();
	}



	public String getCallID()
	{
		return bean.getCallID();
	}



	public boolean isSdp()
	{
		return bean.isSdp();
	}


	public String getCallerNumber()
	{
		return bean.getCallerNumber();
	}



	public String getHistoryNumber()
	{
		return bean.getHistoryNumber();
	}



	public String getReleaseReason()
	{
		return bean.getReleaseReason();
	}



	public String getSessionID()
	{
		return bean.getSessionID();
	}


	public String getCalleeNumber()
	{
		return bean.getCalleeNumber();
	}



	public boolean isRecord()
	{
		return bean.isRecord();
	}



	public String getNewCallID()
	{
		return bean.getNewCallID();
	}



	public boolean isVideoCall()
	{
		return bean.isVideoCall();
	}



	public String getReasonHeader()
	{
		return bean.getReasonHeader();
	}



	public boolean isBFCPSuccess()
	{
		return bean.isBFCPSuccess();
	}



	public String getBfcpErrorCode()
	{
		return bean.getBfcpErrorCode();
	}



	public int getRemoteVideoState()
	{
		return bean.getRemoteVideoState();
	}



	public String getCallerDisplayname()
	{
		return bean.getCallerDisplayname();
	}
	
	
}
