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

public class CallConstants
{
	public static interface CallType
	{
		/**
		 * 呼叫类型 呼出
		 */
		byte CALL_OUT = 0;
		/**
		 * 呼叫类型 呼入
		 */
		byte CALL_IN = 1;
	}

	public static interface CallStatus
	{
		/**
		 * 挂断状态
		 */
		int STATUS_CLOSE = 0;

		/**
		 * 通话状态
		 */
		int STATUS_TALKING = 1;

		/**
		 * 来电状态
		 */
		int STATUS_INCOMMING = 2;

		/**
		 * 呼叫状态
		 */
		int STATUS_CALLING = 3;

		/**
		 * 呼叫hold
		 */
		int STATUS_HOLD = 4;

		/**
		 * 呼叫被hold状态
		 */
		int STATUS_HELD = 5;

		/**
		 * 呼叫恢复过程中状态
		 */
		int STATUS_RETRIEVING = 6;

		/**
		 * 呼叫hold过程中状态
		 */
		int STATUS_HOLDING = 7;

		/**
		 * 视频通话初始化
		 */
		int STATUS_VIDEOINIT = 8;

		/**
		 * 视频通话进行中
		 */
		int STATUS_VIDEOING = 9;

		/**
		 * 视频通话接受 （此状态为接听，或音频请求视频阶段(被叫接受)但会话未真建立时状态）
		 */
		int STATUS_VIDEOACEPT = 10;
	}

	public static interface CallTransferStatus
	{
		/**
		 * 呼叫转移失败
		 */
		int TRANSFER_FAILURE = -1;

		/**
		 * 呼叫转移初始状态
		 */
		int TRANSFER_INIT = 0;

		/**
		 * 呼叫转移过程中
		 */
		int TRANSFERING = 1;

		/**
		 * 呼叫转移成功
		 */
		int TRANSFER_SUCCESS = 2;
	}
	
	public static interface BFCPStatus
	{
		String BFCP_RECEIVE = "receive";

		String BFCP_START = "start";

		String BFCP_END = "end";

		String BFCP_FAIL = "fail";
	}
}
