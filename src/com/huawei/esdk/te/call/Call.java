/*
 *    Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.
 *    eSDK is licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
