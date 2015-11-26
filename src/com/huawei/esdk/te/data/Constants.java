package com.huawei.esdk.te.data;

public class Constants {

	/**
	 * List 默认容量
	 */
	public static final int LIST_DEFAULT_CAPABILITY = 10;

	/**
	 * Map 默认容量
	 */
	public static final int MAP_DEFAULT_CAPABILITY = 16;

	/**
	 * 匹配表情的正则表达式
	 */
	public static final String FACE_IMAGE_REG = "(/:D|/:\\)|/:\\*" + "|/:8|/D~|/\\-\\(|/\\-O|/:\\$|/CO|/YD|/;\\)|/;P"
			+ "|/:!|/:0|/GB|/:S|/:\\?|/:Z|/88|/SX" + "|/TY|/OT|/NM|/\\:X|/DR|/:<|/ZB|/BH|/HL" + "|/XS|/YH|/KI|/DX|/KF|/KL|/LW|/PG|/XG"
			+ "|/CF|/TQ|/DH|/\\*\\*|/@@|/:\\{|/FN|/0\\(|/;>" + "|/FD|/ZC|/JC|/ZK|/:\\(|/LH|/SK|/\\$D|/CY" + "|/\\%S|/LO|/PI|/DB|/MO|/YY|/FF|/ZG|/;I"
			+ "|/XY|/MA|/GO|/\\%@|/ZD|/SU|/MI|/BO" + "|/GI|/DS|/YS|/DY|/SZ|/DP|/:\\\\|/00)";

	/**
	 * 默认时间戳
	 */
	public static final String DEFAULT_TIME_STAMP = "00000000000000";

	/**
	 * 索引常量值-1
	 */
	public static final int NEGATIVE_ONE = -1;

	/**
	 * sip:
	 */
	public static final String SIGN_SIP = "sip:";
	/**
	 * @
	 */
	public static final String SIGN_AT = "@";

	/**
	 * 字符 #
	 */
	public static final String SIGN_STRING = "#";

	/**
	 * 聊天界面显示的最大消息数
	 */
	public static final int MAX_COUNT_OF_MSG = 99;

	/**
	 * 企业搜索一页显示数量(页面显示，不是搜索)
	 */
	public static final int PAGE_COUNT = 5000;

	/**
	 * 搜索联系人 搜索一页数量 取值 (0,50]
	 */
	public static final int SEARCH_PAGE_COUNT = 50;

	/**
	 * 5000ms
	 */
	public static final int FIVE_THOUSAND_MS = 5000;

	/**
	 * 登录成功，但同步联系未完成时，联系人列表显示默认头像，同步联系人完成后，通知UI刷新头像
	 */
	public static final String ESPACENUMBER = "HWeSpaceHD";

	/**
	 * 无缝切换所需号码字符串前缀
	 */
	public static final String REFETO_PREFIX = "sip:UI_";

	/**
	 * 在线
	 */
	public static final String STATUS_ONLINE = "online";
	/**
	 * 离线
	 */
	public static final String STATUS_OFFLINE = "offline";

	/**
	 * 用于存取 聊天界面跳转时关联的 数据的 key值
	 */
	public static final String DATA_KEY_FOR_SHOW_CHAT_VIEW = "dataKeyForShowChatView";

	/**
	 * 消息卡片 包括 通话 记录
	 */
	public final static int CARD_SINGLE_CHAT = 1;

	/**
	 * 群聊卡片
	 */
	public final static int CARD_GROUP_CHAT = 2;

	/**
	 * 会议卡片
	 */
	public final static int CARD_CONFERENCE = 3;

	/**
	 * 陌生电话卡片 - 点击后进入拨号界面
	 */
	public final static int CARD_STRANGER_PHONE = 4;

	/**
	 * 部门通知 - 点击后进入部门通知详情
	 */
	public final static int CARD_DEPART_NOTIFY = 5;

	/**
	 * tag for EspaceNumber
	 */
	public final static int TAG_ESPACENUMBER = 0;

	/**
	 * Tag for card type
	 */
	public final static int TAG_CARD_TYPE = 1;

	/**
	 * 默认卡片数量
	 */
	public final static int CARD_TOTAL_DEFAULT = 3;

	/**
	 * 手机新手引导 数量
	 */
	public final static int CARD_TOTAL_PHONE = 4;

	/******************************** 消息类型 ***********************************/
	/**
	 * 单聊
	 */
	public final static String CHAT_SINGLE = "1";

	/**
	 * 群聊
	 */
	public final static String CHAT_GROUP = "2";

	/**
	 * 临时群消息通知前缀
	 */
	public final static String TEMP_GROUP_CHAT_MSG_PREFIX = "TempGroupId:";

	/**
	 * 固定群ID前缀
	 */
	public final static String CONST_GROUP_ID_PREFIX = "ConstGroup_";

	/**
	 * 隐藏群消息
	 */
	public static final int CONST_GROUP_MESSAGE_HIDE = 0;

	/**
	 * 显示群消息
	 */
	public static final int CONST_GROUP_MESSAGE_SHOW = 1;

	/**
	 * 自定义联系人前缀
	 */
	public final static String CUSTOM_PC_PREFIX = "CustomPc_";

	/**
	 * 临时群名长度
	 */
	public final static int TEMPGROUP_POSTFIX_LENGTH = 6;

	/********************************
	 * homeActivity 页面常量
	 ****************************************/
	/**
	 * 向左滑动
	 */
	public final static int MOVE_LEFT = -1;

	/**
	 * 向右滑动
	 */

	public final static int MOVE_RIGHT = 1;

	/******************************** 消息常量 ********************************/

	/**
	 * 最近联系人变化
	 */
	public static final int MSG_RECENT_CHANGE = 1001;

	/**
	 * to 显示聊天界面
	 */
	public static final int MSG_SHOW_CHATVIEW = 10;

	/**
	 * to 显示拨号界面
	 */
	public static final int MSG_SHOW_DIALVIEW = 11;

	/**
	 * to 显示呼叫中界面，需要显示shad
	 */
	public static final int MSG_SHOW_AUDIOVIEW = 12;

	/**
	 * 来电接听后显示通话中界面，需要显示shad
	 */
	public static final int MSG_INCOMING_SHOW_AUDIOVIEW = 13;

	/**
	 * 网络状态变更通知
	 */
	public static final int MSG_NETWORK_CHANGED = 14;

	/**
	 * to 显示语音界面，需要显示shad
	 */
	public static final int MSG_SHOW_VIDEOVIEW = 15;

	/**
	 * 通过卡片拨出电话，需要隐藏主页卡片
	 */
	public static final int MSG_DISSMISS_HOMEVIEW = 18;

	/**
	 * 首页卡片（titile）背景刷新
	 */
	public static final int MSG_FRESH_CARD_BG = 19;

	/**
	 * 来电或数据会议邀请,准备启动activity
	 */
	public static final int MSG_INCOMING_INVITE = 20;

	/**
	 * 跳转至导航指引界面
	 */
	public static final int MSG_SHOW_NAVIGATIONVIEW = 21;

	/**
	 * 执行首页导航栏划出动画
	 */
	public static final int MSG_SLIDE_OUT_NAVIGATIONBAR = 22;

	/**
	 * to 部门通知界面
	 */
	public static final int MSG_SHOW_DEPARTNOTICE_VIEW = 23;

	/**
	 * 界面Toast显示
	 */
	public static final int MSG_SHOW_TOAST = 24;

	/**
	 * 收到超时
	 */
	public static final int MSG_ONREVTERMINATE = 25;

	/**
	 * 拉会时，拨号盘和通话记录按钮要置灰
	 */
	public static final int MSG_NEED_SET_GRAY = 26;

	/**
	 * 拉会时，拨号盘和通话记录按钮不要置灰
	 */
	public static final int MSG_NO_NEED_SET_GRAY = 27;

	/**
	 * 显示会议 default 电视机
	 */
	public static final int MSG_SHOW_CONFERENCE_DEFAULT = 101;

	/**
	 * 显示 数据会议共享桌面
	 */
	public static final int MSG_SHOW_CONFERENCE_SHARVIEW = 102;

	/**
	 * 显示 放大 回退 按钮
	 */
	public static final int MSG_SHOW_TOMINI_TYPE = 103;

	/**
	 * HomeSlideView 数据更新， 需要重置 页面 。
	 */
	public static final int MSG_NOTIFY_HOMESLIDEVIEW = 104;

	/**
	 * 数据会议主持人设置刷新主持人标志
	 */
	public static final int MSG_SET_PRESENT = 105;

	/**
	 * 数据会议设置断开连接
	 */
	public static final int MSG_SET_DISCONNECT = 106;

	/**
	 * 停止共享
	 */
	public static final int MSG_SHOW_CONFERENCE_STOP_SHARVIEW = 107;

	/**
	 * 更新HomeBtn新消息条数
	 */
	public static final int MSG_NOTIFY_HOMEBTNVIEW = 108;

	/**
	 * 设置静音
	 */
	public static final int MSG_SET_MUTE = 109;

	/**
	 * 设置删除
	 */
	public static final int MSG_SET_DELETE = 110;

	/**
	 * 再次邀请
	 */
	public static final int MSG_SET_INVITE_AGAIN = 111;

	/**
	 * 刷新自己的状态
	 */
	public static final int MSG_SELF_CHANGE_STATE = 114;

	/**
	 * 全屏显示
	 */
	public static final int MSG_FULL_SCREEN = 1142;

	/**
	 * 取消全屏
	 */
	public static final int MSG_PART_SCREEN = 1143;

	/**
	 * 挂断
	 */
	public static final int MSG_CALL_CLOSE_BACK_TO_HOME = 1145;

	/**
	 * 设置视频预览按钮可用
	 */
	public static final int MSG_ENABLE_PREVIEWBTN = 1146;

	/**
	 * 初始化视频数据
	 */
	public static final int MSG_INIT_VIDEO = 1140;

	/**
	 * 销毁视频参数
	 */
	public static final int MSG_UNINIT_VIDEO = 1141;

	/**
	 * 显示公告界面
	 */
	public static final int MSG_SHOW_BULLETIN = 115;

	/**
	 * 新公告通知
	 */
	public static final int MSG_NEW_BULLETIN = 116;

	/**
	 * 取消新公告通知
	 */
	public static final int MSG_CLEAR_BULLETIN = 117;

	/**
	 * 更新聊天卡片
	 */
	public static final int MSG_NOTIFY_CHAT = 118;

	/**
	 * 更新部门通知卡片
	 */
	public static final int MSG_NOTIFY_DEPARTMENTNOTICE = 119;

	// begin add by l00208218 9.04 通知重新设置分辨率后刷新视频窗口
	public static final int MSG_NOTIFY_FRAMESIZE_RESET = 131;
	// end add by l00208218 9.04 通知重新设置分辨率后刷新视频窗口

	// begin add by cwx176935 reason: DTS2013111209672 挂机按钮结束辅流后，下次音视频通话时长显示不对
	public static final int MSG_NOTIFY_CALL_END = 141;
	// end add by cwx176935 reason: DTS2013111209672 挂机按钮结束辅流后，下次音视频通话时长显示不对
	/*********** 界面跳转请求常量 ***********/

	/**
	 * 跳转至语音界面
	 */
	public static final int REQUEST_GOTO_AUDIOVIEW = 10;

	/**
	 * 跳转至搜索界面
	 */
	public static final int REQUEST_GOTO_SEARCHVIEW = 12;

	/**
	 * 跳转至聊天界面
	 */
	public static final int REQUEST_GOTO_CHATVIEW = 13;
	/**
	 * 跳转至聊天界面
	 */
	public static final int REQUEST_GOTO_SYSNOTICE_VIEW = 17;

	/**
	 * 跳转到公告
	 */
	public static final int REQUEST_GOTO_AFFICHEVIEW = 9000;

	/**
	 * 跳转到设置页面
	 */
	public static final int REQUEST_GOTO_SETTINGVIEW = 14;

	/**
	 * 跳转到服务器通话记录页面
	 */
	public static final int REQUEST_GOTO_SHOW_ALL_CALLLOG = 15;

	/**
	 * 跳转到服务器部门通知页面
	 */
	public static final int REQUEST_GOTO_DEPARTMENT_NOTICE = 16;

	/*********** 通话记录模块操作的常量 ***********/

	/**
	 * 跳转到通话记录界面
	 */
	public static final int REQUEST_GOTO_SHOW_CALLRECORD = 1711;

	/**
	 * 返回结果：未读未接来电条数
	 */
	public static final int RESULT_UNREAD_MISSCALL_COUNT = 1712;

	/*********** 界面跳转结果常量 ***********/

	/**
	 * 返回结果：跳转至聊天界面
	 */
	public static final int RESULT_GOTO_CHATVIEW = 10;

	/**
	 * 返回结果：跳转至视频会议界面
	 */
	public static final int RESULT_GOTO_VIDEOVIEW = 14;

	/**
	 * 返回结果：跳转至语音拨号界面
	 */
	public static final int RESULT_GOTO_DIALVIEW = 16;

	/**
	 * ChatListActivy 返回值给HomeActivity
	 */
	public static final String RESULT_FLAG = "ResultFlag";

	// begin add by zkf70917 2013/8/15 reason： 增加通话记录操作的事件
	/**
	 * 跳到新建联系人界面的返回码
	 */
	public static final int REQUEST_CODE_CONTACTDILOG = 180;

	/**
	 * 跳到选择联系人界面的返回码
	 */
	public static final int REQUEST_CODE_CHOOSECONTACT = 181;

	/**
	 * 跳到保存到已有联系人界面的返回码
	 */
	public static final int REQUEST_CODE_ADDTOCONTACT = 182;

	// end add

	/**
	 * 聊天页面触发 卡片刷新
	 */
	public static final int RESULT_NOTIFY_BY_CHATVIEW = 203;

	/**
	 * 多条消息推送到客户端时，触发HomeAcitivy刷新，跳转到最新页面
	 */
	public static final int RESULT_NOTIFY_BY_MULTI_MSG = 204;

	/**
	 * 刷新会议界面
	 */
	public static final int REFESH_CONFERENCE = 120;

	/**
	 * 获取会议列表后刷新界面
	 */
	public static final int GET_CONFLIST_RESULT = 122;

	/**
	 * 会议详情
	 */
	public static final int REFRESH_CONF_DETAILS = 123;

	/**
	 * 删除预定会议
	 */
	public static final int CTC_DELECT_CONFER = 125;

	/**
	 * 会议详情查询后，缓存与会人同步通知
	 */
	public static final int SYNC_CONF_USERS = 126;

	/**
	 * 显示会议列表
	 */
	public static final int CTC_SHOW_LIST = 127;

	/**
	 * 获取部门公告
	 */
	public static final int GET_NEWSLIST = 128;

	/**
	 * 显示首页卡片过滤view
	 */
	public static final int SHOW_FILTER_CARD_VIEW = 129;

	// begin add by cwx176935 2013/8/15 reason： 增加联系人操作的事件
	/******************** 对联系人的操作 ******************************/
	public static final int CONTACT_EXPORT_OPERATE = 1002;
	public static final int CONTACT_IMPORT_OPERATE = 1003;
	// end add by cwx176935 2013/8/15 reason： 增加联系人操作的事件

	/**
	 * PDF预览
	 */
	public static final int CONTACT_DOC_SHARE = 1004;

	/******************* Voip界面的常量 ******************/

	/** voip主动挂断的reason **/
	/**
	 * 其他reason
	 */
	public static final int CALL_OTHER_ERROR = -1;

	/**
	 * 拒绝接听
	 */
	public static final int CALL_REFUSE_ANSWER = -2;

	/**
	 * 找不到呼叫对象
	 */
	public static final int CALL_NOT_FOUND_CALL_OBJECT = -6;

	/**
	 * Voip 呼叫号码校验正则表达式
	 */
	public static final String PATTERN_EXPRESSION = "^[*0-9]\\d{0,20}$";

	/**
	 * 接受消息提示类型
	 */
	public static final String COMING_VIEW_TYPE = "comingType";

	/**
	 * 来电
	 */
	public static final int COMING_CALL = 0;

	/**
	 * 数据会议来电（uc1.0 hwuc uc2.0）
	 */
	public static final int COMING_DATA_INVITE = 1;

	/**
	 * 语音会议来电（uc2.0）
	 */
	public static final int COMING_AUDIO_INVITE = 2;

	/**
	 * UAP+ECONF
	 */
	public static final int UAP_ECONF = 3;

	/**
	 * 视频呼叫
	 */
	public static final int COMING_VIDEO_CALL = 4;

	/**
	 * 从popWindow跳转到语音界面
	 */
	public static final int MSG_GOTO_AUDIOVIEW = 105;

	/**
	 * 恢复语音按钮的背景
	 */
	public static final int MSG_CLEAR_BTN_EFFECT = 106;

	/**
	 * 从呼叫转接popWindow跳转到IM界面
	 */
	public static final int MSG_CALL_FORWARD = 107;

	/**
	 * voip语音状态
	 */
	public static final String VOIP_STATUS = "indicator";

	/**
	 * 语音界面呼叫类型
	 */
	public static final String CALL_TYPE = "callType";

	/**
	 * 按住record按钮显示
	 */
	public static final int RECORD_BUTTON = 100;

	/**
	 * record时间变更
	 */
	public static final int RECORD_TIME_PROMPT = 0x10;
	/**
	 * record音量变更
	 */
	public static final int RECORD_VOLUME_CHANGE = 0x20;

	/**
	 * 注册成功消息
	 */
	public static final int REGIST_SUCCES = 1147;

	/**
	 * 是否ad认证
	 */
	public static final int ADCONFIRMATION = 1158;

	/**
	 * 匿名时登录用户名
	 */
	public static final String ANONYMOUS_ACCOUNT = "_ANONYMITY_";

	/**
	 * 选择横向布局
	 */
	public static final String USELANDLAYOUT = "land layout";
	// begin modified by pwx178217 2013/11/25
	// reason:小手机上关于界面点返回后，会暗下再亮起来（左上角图标点击出现的不会有问题）
	/**
	 * 关于
	 */
	public static final int HOME_ABOUT_MGS = 1050;
	// end modified by pwx178217 2013/11/25
	// reason:小手机上关于界面点返回后，会暗下再亮起来（左上角图标点击出现的不会有问题）

	/**
	 * 用户反馈
	 */
	public static final int USER_FEEDBACK = 1149;

	/**
	 * 返回主界面
	 */
	public static final int BACK_TO_MAIN_VIEW = 1148;

	/**
	 * 取消about显示
	 */
	public static final int DISMISS_ABOUT = 1159;

	/**
	 * 操作摄像头
	 */
	public static final int OPERATECAMERA = 1160;

	/**
	 * 操作声音
	 */
	public static final int OPERATEVOLUME = 1161;

	/**
	 * 点击tag
	 */
	public static final String CLICK = "click";

	/**
	 * 显示会场列表
	 */
	public static final int SHOW_CONF_LIST = 1162;

	// BEGIN modified by pwx178217 2013/11/6 Reason:保存是否匿名登录.

	// BEGIN ADDED by pwx178217 2013/11/1 reason:DTS2013110101206
	// 系统设置取消记住密码，注销以后再登录界面还会记住登录密码；
	/**
	 * 是否要删除登录界面密码框输入
	 */
	private static boolean needToDelete = false;
	// END ADDED by pwx178217 2013/11/1 reason:DTS2013110101206
	// 系统设置取消记住密码，注销以后再登录界面还会记住登录密码；

	/**
	 * 最大选中联系人数
	 */
	public static final int MAX_CONF_COUNT = 400;

	public static boolean isNeedToDelete() {
		return needToDelete;
	}

	public static void setNeedToDelete(boolean needToDelete) {
		Constants.needToDelete = needToDelete;
	}

	/**
	 * 是否匿名登录
	 */
	private static boolean isAnonymousAccount = false;
	// END modified by pwx178217 2013/11/6 Reason:保存是否匿名登录

	public static boolean isAnonymousAccount() {
		return isAnonymousAccount;
	}

	public static void setAnonymousAccount(boolean isAnonymousAccount) {
		Constants.isAnonymousAccount = isAnonymousAccount;
	}

	// BEGIN modified by pwx178217 2013/11/7 Reason:重启状态
	public static final String RESTART_EVENT = "restartEvent";
	// END modified by pwx178217 2013/11/7 Reason:重启状态

	// BEGIN modified by pwx178217 2013/11/7 Reason:是否华为默认证书
	public static final String ISHUAWEI_CER = "ishuaweicer";
	// END modified by pwx178217 2013/11/7 Reason:是否华为默认证书

	// END modified by cWX183956 2013/11/5 Reason:ANDROID-126 添加用户反馈功能
	// begin added by pwx178217 2013/11/15 reason:DTS2013111800031
	// 添加SIP端口设置和媒体端口
	/**
	 * 本地Sip端口
	 */
	public static final String LOCAL_PORT = "localPort";

	/**
	 * 媒体端口
	 */
	public static final String MEDIA_PORT = "mediaPort";
	// end added by pwx178217 2013/11/15 reason:DTS2013111800031 添加SIP端口设置和媒体端口

	public static final String ISPROCESSKILLED = "processKilled";

	/**
	 * tls tcp udp端口
	 */
	public static final String TLS_PORT = "tlsport";
	public static final String TCP_PORT = "tcpport";
	public static final String UDP_PORT = "udpport";

	/**
	 * 俄罗斯定制版本
	 */
	public final static String RUSSIA_VERSIONS = "Russia";

	/**
	 * 本次登录的号码
	 */
	public static final String LOGIN_NUMBER = "loginNumber";
	// begin removed by pwx178217 2014/2/7 reason: DTS2014011706781 登录设置中界面问题
	/**
	 * dns正则字符串
	 */
	public static final String DNSREG = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9_]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9_]{0,62}){1,3}\\.?$";

	// end removed by pwx178217 2014/2/7 reason: DTS2014011706781 登录设置中界面问题
	/**
	 * 类描述：电话类型
	 */
	public interface TPTECALL {
		/**
		 * 呼出
		 */
		String CALL_OUT = "0";
		/**
		 * 呼入
		 */
		String CALL_IN = "1";
		/**
		 * 未接
		 */
		String CALL_MISS = "2";
	}

	/**
	 * 类描述： 聊天页面显示类型
	 */
	public static interface CHAT_TYPE {
		/**
		 * 单人聊天
		 */
		int SINGLE_CHAT = 1;

		/**
		 * 群组聊天
		 */
		int GROUP_CHAT = 2;

		/**
		 * 单人语音
		 */
		int SINGLE_AUDIO = 3;

		/**
		 * 群组语音
		 */
		int GROUP_AUDIO = 4;

		/**
		 * 单人视频
		 */
		int SINGLE_VIDEO = 5;

		/**
		 * 群组视频
		 */
		int GROUP_VIDEO = 6;

		/**
		 * 固定群（暂不使用）
		 */
		int CONST_GROUP_CHAT = 7;
		/**
		 * 部门通知
		 */
		int DEPART_NOTICE = 8;

	}

	/**
	 * 类名称：SESSION_SHOWING_TYPE 作者： LiQiao 创建时间：2012-5-26 类描述：聊天会话界面显示类型 版权声明 :
	 * Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd) 修改时间：下午3:32:49
	 *
	 */
	public static interface SESSION_SHOWING_TYPE {
		/**
		 * IM界面
		 */
		int IM_VIEW = 1;

		/**
		 * 语音界面
		 */
		int AUDIO_VIEW = 2;

		/**
		 * 视频界面
		 */
		int VIDEO_VIEW = 3;

		/**
		 * 部门通知界面
		 */
		int DEPART_NOTICE_VIEW = 4;
	}

	/**
	 * 类名称：Constant.java 类描述： 字符串常量
	 */
	public static interface STRING_CONSTANG {
		/**
		 * 聊天页面 传入 数据
		 */
		String CHAT_SESSION_DATA = "chatSession";

		/**
		 * 聊天页面传入 类型
		 */

		String CHAT_SESSION_TYPE = "chatType";

		/**
		 * 数据共享组件类型（新组件需要以opengl绘图）通过intent取得的类型值判定
		 */
		String DATA_SCREEN_TYPE = "dataScreenType";

		/**
		 * 共享类型 屏幕 白版 文档
		 */
		String SHARE_TYPE = "shareType";

	}

	/**
	 * 类名称：Constant.BROADCAST_PATH 作者： l00186254 创建时间：May 7, 2012 类描述： 定义UI层的 广播
	 * 版权声明 : Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd) 修改时间：7:33:16
	 * PM
	 *
	 */
	public static interface BROADCAST_PATH {

		/**
		 * 更新 Home页面卡片
		 */
		String ACTION_UPDATE_CARDS = "com.huawei.TEMobile.updateHomeCards";

		/**
		 * 不发送Logout请求 返回到登录页面 UI上层发送的广播 在 -6 -9 和断网的情况下使用
		 */
		String ACTION_LOGOUT = "com.huawei.TEMobile.loginOut";

		/**
		 * 退出 eSpace HD 程序
		 */
		String ACTION_EXIT = "com.huawei.TEMobile.exit";

		/**
		 * 重启eSpace
		 */
		String ACTION_RESTART = "com.huawei.TEMobile.restart";

		/**
		 * 更新 ChatBaseActivity 来新消息，来电。。。
		 */
		String ACTION_UPDATE_CHAT_ACTIVITY = "com.huawei.TEMobile.updateChatActivity";

		/**
		 * 更新头像广播
		 * 
		 * @deprecated 废弃UI层发送头像刷新的广播， 直接通过 ContacgStausFunction 分发
		 */
		String ACTION_UPDATE_HEADPHOTO = "com.huawei.TEMobile.updateHeadImage";

		/**
		 * HomeActivity 显示到窗口
		 */
		String ACTION_HOMEACTIVITY_SHOW = "com.huawei.TEMobile.HomeActivityShow";
		/**
		 * 显示部门通知
		 */
		String ACTION_SHOW_DEPARTNOTICE = "com.huawei.TEMobile.showDepartNotice";

		/**
		 * im消息号码链接
		 */
		String ACTION_MSGNUM_CLICK = "com.huawei.TEMobile.msgLinkNumber.onClick";
	}

	/**
	 * HomeActivity 消息处理
	 */
	public static interface MSG_FOR_HOMEACTIVITY {
		/**
		 * 设置个人状态
		 */
		int SET_SELF_STATUS = 201;

		/**
		 * 设置个人状态中退出
		 */
		int SET_SELF_EXIT = 202;

		/**
		 * 设置个人状态中设置
		 */
		int SET_SELF_SYSTEM_SETTING = 203;

		/**
		 * 设置个人状态中签名
		 */
		int SET_SELF_SIGNTRUE = 205;

		/**
		 * 设置个人状态中Dismiss
		 */
		int SET_SELF_DISMISS = 206;
		/**
		 * 广播
		 */
		int BROADCAST_EVENT = 207;
		/**
		 * 左上角个人图标隐藏
		 */
		int HIDESELFICON_EVENT = 208;
		/**
		 * 左上角个人图标显示
		 */
		int SHOWSELFICON_EVENT = 209;

		/**
		 * IP变动重注册事件
		 */
		int IPCHANGE_RELOGIN_EVENT = 210;

		/**
		 * 此消息用于通知来电通知界面 ——来电挂断 DTS2013112001553
		 */
		int MSG_NOTIFY_CALLCLOSE = 211;

		int MSG_LOGOUT_AND_REGISTE = 212;
		/**
		 * 主叫呼集结果
		 */
		int MSG_BOOK_CONF_RESULT = 214;

		/**
		 * 会场信息
		 */
		int MSG_NOTIFY_CONF_INFO = 213;

		/**
		 * 观看会场结果
		 */
		int MSG_NOTIFY_WATCH_ATTENDEE_RESULT = 215;

		/**
		 * 本地被广播状态
		 */
		int MSG_NOTIFY_LOCAL_BROADCAST_STATUS = 217;

		/**
		 * 本端会场被闭音
		 */
		int MSG_NOTIFY_MUTE_CONF = 216;

		/**
		 * 被观看会场通知
		 */
		int MSG_NOTIFY_BROADCASTED_ATTENDEE = 218;

		/**
		 * 保存自身M号码和T号码
		 */
		int MSG_NOTIFY_CONF_SELF_MTNUMBER = 219;
	}

	/**
	 * 设置界面 消息处理
	 */
	public static interface MSG_FOR_SETTING {
		/**
		 * 和设置界面状态设置的消息区分
		 */
		int PASSWORD_CHANGE_SUCCESS = 10;

	}

	/**
	 * 通知界面地址本服务器的类型
	 */
	public static interface ENTERPRISE_BOOK_TYPE {
		int LDAP = 112;

		int FTPS = 113;
	}

	/**
	 * ChatSessionActivity_ 消息处理 IM使用最后一个字节 0x000000** VOIP使用倒数第二个字节 0x0000**00
	 * 会议使用第二个字节 0x00**0000 如果各自类别的消息数目过大，扩展第一个字节 如:0x010000ff
	 */
	public static interface MSG_FOR_CHATSESSIONACTIVITY {
		/**
		 * 会话内容改变
		 */
		int SESSION_CONTENT_CHANGED = 0x00000001;

		/**
		 * 当前选中会话改变
		 */
		int SESSION_SELECT_CHANGED = 0x00000002;

		/**
		 * 删除当前会话 包括 数据会议
		 */
		int DELETE_CURRENT_ITEM = 0x00000003;

		/**
		 * 未读的消息被清除
		 */
		int UNREAD_MSG_CLEARED = 0x00000004;

		/**
		 * 添加会话
		 */
		int ADD_SESSION_ITEM_BY_INDEX = 0x00000005;

		/**
		 * 删除会话
		 */
		int DELETE_SESSION_ITEM_BY_INDEX = 0x00000006;

		/**
		 * 改变会话的类型
		 */
		int SESSION_TYPE_CHANGE_TO_GROUP = 0x00000007;

		/**
		 * 更新session的title
		 */
		int UPDATE_SESSION_TITLE = 0x00000008;

		/**
		 * 刷新聊天记录
		 */
		int UPDATE_CHAT_CONTENT = 0x00000009;

		/**
		 * 聊天界面头像未读消息+1
		 */
		int UNREAD_MSG_COUNT_PLUS = 0x0000000a;

		/**
		 * 处理新消息
		 */
		int HANDLE_INCOMING_MSG = 0x0000000b;

		/**
		 * 多条消息通知被点击
		 */
		int MUTIPLE_MSG_CLICKED = 0x0000000c;

		/**
		 * 群成员UPDATE
		 */
		int GROUP_MEMBER_UPDATE = 0x0000000d;

		/**
		 * 显示IM聊天界面
		 */
		int SHOW_IM_VIEW = 0x0000000e;

		/**
		 * 网络断开
		 */
		int MSG_NETWORK_CHANGED = 0x0000000f;

		/**
		 * 更新头像
		 */
		int MSG_UPDATE_HEADVIEW_STATUS = 0x00000010;

		/******************* VOIP **************************/

		/**
		 * Voip通话状态更新 通话开始 挂断
		 */
		int VOIP_UPDATE_UI = 0x00000100;

		/**
		 * 更新通话时间
		 */
		int VOIP_UPDATE_TIME = 0x00000200;

		/**
		 * 网络信号
		 */
		int VOIP_UPDATE_SINGLE = 0x00000300;

		/**
		 * 振铃通知
		 */
		int VOIP_RINGING = 0x00000400;

		/**
		 * 语音会议来电接受
		 */
		int VOIP_CONF_ACCEPT = 0x00000500;

		/**
		 * 呼叫转移失败
		 */
		int VOIP_FORWARD_FAILURE = 0x00000600;

		/**
		 * im 号码拨号
		 */
		int VOIP_FROM_IMMSG_NUM = 0x00000700;

		/**
		 * 视频通话去视频（保留通话）
		 */
		int VOIP_DELETE_VIDEO = 0x00000800;

		/**
		 * 保持当前session
		 */
		int VOIP_HOLD_SESSION = 0x00000900;

		/*************** 会议 **********************/

		/**
		 * 当自己加入数据会场成功后 取消会议屏幕 显示 DEFAULT 图标
		 */
		int CONF_START_SUCCED = 0x00010000;

		/**
		 * 与会人状态变更 刷新 头像
		 */
		int CONF_UPDATE_HEADS = 0x00020000;

		/**
		 * 数据会议 显示全屏状态下的 menuBar
		 */
		int CONF_SHOW_MENUBAR = 0x00030000;

		/**
		 * 主讲人变更
		 */
		int CONF_PRESENTER_CHANGE = 0x00040000;

		/**
		 * 数据会议持续时间更新
		 */
		int CONF_UPDATE_TIME = 0x00050000;

		/**
		 * 增加 或者 删除 成功
		 */
		int CONF_MEMBER_COUNT_CHANGED_SUCCED = 0x00070000;

		/**
		 * 结束会议
		 */
		int CONF_STOP = 0x00080000;

		/**
		 * 会议中主被叫主动断开连接（头像点起的退出会议、结束会议、多媒体退出按钮响应的对话框提示）
		 */
		int CONF_DISCONNECT = 0x00090000;

		/**
		 * 加入语音 会场失败
		 */
		int CONF_JOIN_AUDIO_FAILED = 0x000a0000;

		/**
		 * 加入语音会场成功
		 */
		int CONF_JOIN_AUDIO_SEECCED = 0x000b0000;

		/**
		 * 加入数据会场失败
		 */
		int CONF_JOIN_DATA_FAILED = 0x000c0000;

		/**
		 * 加入数据会场成功
		 */
		int CONF_JOIN_DATA_SEECCED = 0x000d0000;
		/**
		 * 离开语音会场
		 */
		int CONF_LEAVE_AUDIO_CONF = 0x000e0000;

		/**
		 * 离开数据会场
		 */
		int CONF_LEAVE_DATA_CONF = 0x000f0000;
		/**
		 * 创建语音会场成功
		 */
		int CONF_CREATE_AUDIO_SEECCED = 0x00100000;

		/**
		 * 创建语音会场失败
		 */
		int CONF_CREATE_AUDIO_FAILED = 0x00110000;

		/**
		 * 创建数据会场成功
		 */
		int CONF_CREATE_DATA_SEECCED = 0x00120000;

		/**
		 * 创建数据会场失败
		 */
		int CONF_CREATE_DATA_FAILED = 0x00130000;

		/**
		 * 创建会场失败
		 */
		int CONF_CREATE_FAILED = 0x00130001;

		/**
		 * 取消会议
		 */
		int CONF_VIDEO_CANCEL = 0x00140000;

		/**
		 * 会议中当主叫离开会场时 被叫就退出会议
		 */
		int CONF_HOST_LEAVE_STOPCONFERENCE = 0x00150000;

		/**
		 * 会议正在退出中，请稍后再试
		 */
		int CONF_TERMINATING = 0x00160000;

		/**
		 * 做被叫 会议主叫和被叫同时挂接时被叫方处理
		 */
		int CONF_HANG_UP_AND_ACCEPT = 0x00170000;

		/**
		 * 会议头像菜单显示（主要为同步而做）
		 */
		int CONF_SHOW_MEM_OPTIONVIEW = 0x00180000;

		/**
		 * 数据会议没有权限
		 */
		int CONF_NO_PRIORITY = 0x00190000;

		/**
		 * 语音转多媒体
		 */
		int AUDIO_TO_DATA_CONF = 0x001a0000;

		/**
		 * 华为UC数据会议中转移成功
		 */
		int HWUC_DATA_CONF_REFER_SUCCEED = 0x001b0000;

		/**
		 * 数据会议转移号码不合法
		 */
		int CONF_FORWARD_INPUT_ERROR = 0x001c0000;

		/********************** 视频消息 ***********************/

		/** 发送打开本地摄像头请求 */
		int V_SEND_OPEN = 0x001f0000;

		/** 摄像头切换，打开或者关闭 */
		int V_SWITCH = 0x00200000;

		/** 发送打开其他人的摄像头请求 */
		int V_SEND_OPEN_OTHER = 0x00210000;

		/** 重置远端视频层次 */
		int V_SEND_RESET_REMOTE = 0x00210001;

		/** 操作自己的设备 */
		int VIDEO_OPERATE_SELF = 0x00210002;

		/** 操作别人的设备 */
		int VIDEO_OPERATE_OTHER = 0x00210003;

		/** 设备视频绑定操作 */
		int VIDEO_TACH_OPERATION = 0x00210004;

		/** 融合会议选看会场 */
		int VIDEO_SELECT_SITE = 0x00210005;

		/*************************** 会议共享部分 ********************************/
		/**
		 * 开始会议共享
		 */
		int CONF_SHARE_BEGIN = 0x00220000;

		/**
		 * 会议共享数据
		 */
		int CONF_SHARE_DATA = 0x00230000;

		/**
		 * 停止会议共享，恢复到默认的电视机画面
		 */
		int CONF_SHARE_END = 0x00060000;

		/**
		 * 开始白板共享
		 */
		int CONF_START_WHITE_BOARD_SHARE = 0x00240000;

		/**
		 * 接收白板共享数据
		 */
		int CONF_RECEIVE_WHITE_BOARD_DATA = 0x00250000;

		/**
		 * 发送涂鸦
		 */
		int IM_SKETCH_PICTURE = 0x00270000;

		/**
		 * 发送图片
		 */
		int UM_PICTURE_SEND = 0x00290000;

		/**
		 * 更新视频网络状态
		 */
		int VIDEO_UPDATE_SINGLE = 0x00300000;
	}

	/**
	 * 类名称：VIEDIO_MENU 作者： LiGao 创建时间：2011-10-31 类描述：会议菜单 版权声明 : Copyright (C)
	 * 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd) 修改时间：下午7:22:29
	 *
	 */
	public static interface VIDEO_MENU {
		/**
		 * 加联络人
		 */
		int CONF_ADDCONTACT = 201;
	}

	/**
	 * 类名称：Constant.java 作者： Administrator 创建时间：2011-10-24 类描述： Voip Dial retrun
	 * 版权声明 : Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 修改时间：下午4:55:03
	 *
	 */
	public static interface VOIP_RESULT {
		/**
		 * Wifi 不可用
		 */
		int NO_WIFI = 1;

		/**
		 * 没有Voip能力
		 */
		int NO_ABLITY = 2;

		/**
		 * 号码不符合规范
		 */
		int BAD_NUMBER = 3;

		/**
		 * 成功发出Voip呼叫请求
		 */
		int DIAL_SUCCESS = 0;

		/**
		 * 呼叫失败
		 */
		int DIAL_ERROR = -1;

	}

	/**
	 * 类描述： 聊天对象类型
	 */
	public static interface CHAT_CONTACT_TYPE {
		/**
		 * 好友
		 */
		byte FRIEND = 1;

		/**
		 * 陌生人
		 */
		byte STRANGER = 2;

		/**
		 * 陌生号码
		 */
		byte STRANGE_NUMBER = 3;

		/**
		 * 自定义联系人
		 */
		byte CUSTOM_CONTACT = 4;
	}

	/**
	 * 类名称：Constant.java 作者： l00186254 创建时间：2011-11-30 类描述： LoginActivity 消息处理
	 * 版权声明 : Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 修改时间：下午7:34:25
	 *
	 */
	public static interface LOGINACTIVITY_MSG {
		/**
		 * 收到 服务器推送 back_to_loginView 广播 退出程序
		 */
		int ON_BACK_TO_LOGINVIEW = 0;

		/**
		 * 接收到广播事件
		 */
		int BROADCAST_EVENT = 1;

		/**
		 * 复位LoginView
		 */
		int RESTORE_LOGINVIEW = 2;

		/**
		 * 弹出取消强制登录消息
		 */
		int FORCE_LOGIN_CANCEL = 5;

		/**
		 * 刷新计时
		 */
		int REFRESH_LOGINBTN = 6;
		/**
		 * 解锁登录
		 */
		int UNLOCK_LOGIN = 7;
	}

	/**
	 * 类名称：Constant.java 作者： YinZefeng 创建时间：2011-11-30 类描述： LoginActivity 消息处理
	 * 版权声明 : Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 修改时间：下午7:34:25
	 */
	public static interface HOME_SLIDE_CARD_DATETYPE {
		/**
		 * 今天
		 */
		int TYPE_TODAY = 0;

		/**
		 * 昨天
		 */
		int TYPE_LASTDAY = 1;

		/**
		 * 最近
		 */
		int TYPE_RECENTLY = 2;

		/**
		 * 今天以后
		 */
		int TYPE_NEXT = 3;
	}

	/**
	 * 类名称：Constant.java 作者： lKF55032 创建时间：2012-2-4 类描述：UC2.0 刷新 版权声明 :
	 * Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd) 修改时间：下午4:05:40
	 *
	 */
	public static interface UCTWO_CONF_UPDATE_ACTION {

		/**
		 * 语音会议停会刷页面
		 */
		int AUDIO_CONF_STOP_RES = 300;

		/**
		 * 多媒体停会刷页面
		 */
		int DATA_CONF_STOP_RES = 301;

		/**
		 * 停止会议刷新页面
		 */
		int CONF_STOP_RES = 302;

		/**
		 * uc2.0退会进度条
		 */
		int SHOW_CONF_STOP_PRO = 303;
	}

	/**
	 * 类描述：服务器类型
	 */
	public static interface SERVER_TYPE {
		String HUAWEI_UC = "HUAWEIUC";
		String UC_2_0 = "UC2.0";
		String UC_1_0 = "UC1.0";
	}

	/**
	 * 类描述:呼叫转移标志
	 */
	public static interface VOIP_REFRE_FLAG {
		/**
		 * 呼叫转移成功标志
		 */
		int FLAG_TWO_THOUSOND = 200;
		/**
		 * 呼叫转移失败标志
		 */
		int FLAG_THREE_THOUSOND = 300;
	}

	/**
	 * 一键入会在5s内是否接听
	 */
	public static final int CONF_ONKEY_TIME = 5 * 1000;

	/**
	 * 语音会议中的成员状态
	 */
	public static interface AUDIO_CONF_MEMBER_STATUS {
		/**
		 * 拨号中
		 */
		int DIALING = 0;

		/**
		 * 已接听
		 */
		int ANSWERED = 1;

		/**
		 * 失败，没接听
		 */
		int FAILED_NOANSWER = 2;

		/**
		 * 失败，忙
		 */
		int FAILED_BUSY = 3;

		/**
		 * 挂断
		 */
		int HANGOFF = 4;

		/**
		 * 语音通话已连接
		 */
		int CONNECTED = 5;

		/**
		 * 用户发言
		 */
		int SPEAK = 6;

		/**
		 * 默认状态
		 */
		int DEFAULT = -1;

	}

	public static interface DATA_CONF_MUTE_STATUS {
		// MUTETYPE_LOCAL_UNMUTE = 0, MUTETYPE_LOCAL_MUTE = 1,
		// MUTETYPE_SERVER_UNMUTE = 2, MUTETYPE_SERVER_MUTE = 3
		/**
		 * 本地取消静音
		 */
		int LOCAL_UNMUTE = 0;

		/**
		 * 本地静音
		 */
		int LOCAL_MUTE = 1;

		/**
		 * 服务器取消静音
		 */
		int SERVER_UNMUTE = 2;

		/**
		 * 服务器静音
		 */
		int SERVER_MUTE = 3;
	}

	public static interface CHARACTER_MARK {
		/**
		 * 引号QUOTATION_MARK
		 */
		String QUOTATION_MARK = "'";

		/**
		 * 左方括号QUOTATION_MARK
		 */
		String LEFT_SQUARE_BRACKET_MARK = "[";

		/**
		 * 右方括号QUOTATION_MARK
		 */
		String RIGHT_SQUARE_BRACKET_MARK = "]";

		/**
		 * 左圆括号QUOTATION_MARK
		 */
		String LEFT_PARENTHESIS_MARK = "(";

		/**
		 * 右圆括号QUOTATION_MARK
		 */
		String RIGHT_PARENTHESIS_MARK = ")";

		/**
		 * 右斜杠QUOTATION_MARK
		 */
		String RIGHT_SLASH_MARK = "/";

		/**
		 * 竖线QUOTATION_MARK
		 */
		String VERTICAL_MARK = "|";

		/**
		 * 横线QUOTATION_MARK
		 */
		String HORIZONTAL_MARK = "-";

		/**
		 * 空格QUOTATION_MARK
		 */
		String BLANK_MARK = " ";

		/**
		 * 逗号QUOTATION_MARK
		 */
		String COMMA_MARK = ",";

		/**
		 * 星号QUOTATION_MARK
		 */
		String STAR_MARK = "*";

		/**
		 * 冒号QUOTATION_MARK
		 */
		String COLON_MARK = ":";

		/**
		 * 点号QUOTATION_MARK
		 */
		String POINT_MARK = ".";

		/**
		 * 波浪号QUOTATION_MARK
		 */
		String WAVE_MARK = "~";

	}

	public static interface FILTER_CARD_TYPE {
		/**
		 * 全部通话
		 */
		int FILTER_ALL_RECORD = 0;
		/**
		 * 未读即时消息
		 */
		int FILTER_UNREAD_IM = 1;
		/**
		 * 部门通知
		 */
		int FILTER_DEPARTMENT_NOTICE = 2;
	}

	/**
	 * @kKF53966 呼叫前转
	 */
	public static interface FORWARD_CALL_STATUS {
		// type=0 0:离线 1:在线 3:忙碌 4:离开
		/**
		 * 离线
		 */
		int OFFLINE_STATUS = 0;

		/**
		 * 在线
		 */
		int ONLINE_STATUS = 1;
		/**
		 * 忙碌
		 */
		int BUSY_STATUS = 3;

		/**
		 * 离开
		 */
		int AWAY_STATUS = 4;
	}

	/**
	 * @kKF53966 来电前转
	 */
	public static interface FORWARD_INCOMING_STATUS {
		// call状态 0:Unconditional 1:Busy 2: no Reply 3:Offline
		/**
		 * 无条件
		 */
		int CALL_UNCONDITIONAL_STATUS = 0;
		/**
		 * 遇忙
		 */
		int CALL_BUSY_STATUS = 1;
		/**
		 * 无响应
		 */
		int CALL_NOREPLY_STATUS = 2;
		/**
		 * 无注册
		 */
		int CALL_OFFLINE_STATUS = 3;

	}

	/**
	 * 前转类型
	 */
	public static interface BEFORE_FORWARD_TYPE {
		/**
		 * 来电转接联系人状态类型
		 */
		int PRESENCE_TYPE = 0;

		/**
		 * 来电转接call状态类型
		 */
		int FORWARD_TYPE = 1;
	}

	public static interface VideoNotifyMsgConst {
		int XRESOLUTION = 352;
		int YRESOLUTION = 288;
		int NFRAMERATE = 15;
		int NBITRATE = 0;
		int NRAWTYPE = 0;
	}

	/**
	 * 
	 * 添加联系人标签响应 kKF53966
	 */
	public static interface AddContactLabelResp {
		/**
		 * 添加标签失败
		 */
		int ADD_NEW_LABEL_FAIL = 5;

		/**
		 * 添加标签成功
		 */
		int ADD_NEW_LABEL_SUCESS = 4;
	}

	/**
	 * 
	 * 呼叫方式
	 */

	public static interface CallMode {
		/**
		 * CTD
		 */
		int CTD_CALL_MODE = 0;

		/**
		 * VoIP
		 */
		int VOIP_CALL_MODE = 1;
	}

	/**
	 * 呼叫带宽 Copyright (C) 2008-2013 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 
	 * @since 1.1
	 * @history 2013-10-18 v1.0.0 l00208218 create
	 */
	public static interface CallBandWidth {
		/**
		 * 64
		 */
		int CALL_BANDWIDTH_64 = 64;

		/**
		 * 128
		 */
		int CALL_BANDWIDTH_128 = 128;

		/**
		 * 256
		 */
		int CALL_BANDWIDTH_256 = 256;

		/**
		 * 384
		 */
		int CALL_BANDWIDTH_384 = 384;

		/**
		 * 512
		 */
		int CALL_BANDWIDTH_512 = 512;

		/**
		 * 768
		 */
		int CALL_BANDWIDTH_768 = 768;
	}

	/**
	 * 视频模式 Copyright (C) 2008-2013 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 
	 * @since 1.1
	 * @history 2013-8-27 v1.0.0 l00208218 create
	 */
	public static interface VideoMode {
		/**
		 * 流畅优先
		 */
		int VIDEO_PROCESS_MODE = 1;

		/**
		 * 画质优先
		 */
		int VIDEO_QUALITY_MODE = 0;
	}

	/**
	 * 
	 * 安全传输协议
	 */

	/**
	 * 安全传输协议 Copyright (C) 2008-2013 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 
	 * @since 1.1
	 * @history 2013-8-27 v1.0.0 l00208218 create
	 */
	public static interface SecureEncryptMode {
		/**
		 * 加密
		 */
		int SECURE_ENCRYPT_YES_MODE = 2;

		/**
		 * 非强制性加密
		 */
		int SECURE_ENCRYPT_AUTO_MODE = 3;

		/**
		 * 不加密
		 */
		int SECURE_ENCRYPT_NO_MODE = 1;
	}

	/**
	 * 聊天头像蒙版类型
	 */
	public static interface HeadStatus {
		/** 隐藏 **/
		int HEAD_STATUS_HIDE = 0;

		/** 语音通话 **/
		int HEAD_STATUS_CALL = 1;

		/** 视频通话 **/
		int HEAD_STATUS_VIDEO = 2;

		/** 多媒体会议 **/
		int HEAD_STATUS_MEDIA = 3;

		/** 语音会议 **/
		int HEAD_STATUS_AUDIO = 4;
	}

	/**
	 * 动态标签名
	 */
	public static interface LabelName {
		/**
		 * 账号
		 **/
		String LABEL_NAME_UCNO = "ucNo";

		/**
		 * 部门
		 **/
		String LABEL_NAME_DEPTDESC = "deptDesc";

		/**
		 * 工号
		 **/
		String LABEL_NAME_STAFFNO = "staffNo";

		/**
		 * 姓名
		 **/
		String LABEL_NAME_STAFFNAME = "nativeName";

		/**
		 * 拼音姓名
		 **/
		String LABEL_NAME_PINYINNAME = "name";

		/**
		 * 外文姓名
		 **/
		String LABEL_NAME_FOREIGNNAME = "foreignName";

		/**
		 * 手机
		 **/
		String LABEL_NAME_MOBILEPHONE = "mobileList";

		/**
		 * 邮箱
		 **/
		String LABEL_NAME_EMAIL = "email";

		/**
		 * 用户传真号码
		 **/
		String LABEL_NAME_FAX = "faxList";

		/**
		 * 邮编
		 **/
		String LABEL_NAME_ZIP = "postalcode";

		/**
		 * 办公地点
		 **/
		String LABEL_NAME_ADDR = "ad";

		/**
		 * 用户性别
		 **/
		String LABEL_NAME_SEX = "sex";

		/**
		 * 用户职位
		 **/
		String LABEL_NAME_OCCUPATION = "position";

		/**
		 * 个人签名
		 **/
		String LABEL_NAME_SI = "si";

		/**
		 * 电话
		 **/
		String LABEL_NAME_OFFICEPHONE = "phoneList";

		/**
		 * 内线
		 **/
		String LABEL_NAME_OFFICEINTERPHONE = "interPhoneList";

		/**
		 * 座位号
		 **/
		String LABEL_NAME_SEATNUM = "room";

		/**
		 * 时区
		 **/
		String LABEL_NAME_TIMEZONE = "timezone";

		/**
		 * 秘书
		 **/
		String LABEL_NAME_ASSISTANT = "assistantList";

		/**
		 * 出差联系说明
		 **/
		String LABEL_NAME_CONTACT = "contact";

		/**
		 * 显示姓名
		 **/
		String LABEL_NAME_DISPLAYNAME = "displayName";

		/**
		 * VoIP
		 **/
		String LABEL_NAME_VOIP = "voipList";

		/**
		 * 绑定号
		 **/
		String LABEL_NAME_CLIENTNUMBER = "bd";

		/**
		 * 其他信息
		 **/
		String LABEL_NAME_OTHERINFO = "otherInfo";

		/**
		 * NOTES邮箱地址
		 **/
		String LABEL_NAME_NOTESMAIL = "notesMail";

		/**
		 * 个人主页
		 **/
		String LABEL_NAME_HOMEPAGE = "homepage";

		/**
		 * 家庭电话
		 **/
		String LABEL_NAME_HOMEPHONE = "homePhone";

	}

	/**
	 * voiceMail界面使用语音信息类型
	 */
	public static interface VoiceMail {
		/**
		 * 显示所有语音信息
		 */
		short GET_VOICE_MAIL_LIMIT = 100;

		/**
		 * 显示所有语音信息
		 */
		int VOICE_MAIL_SHOW_TYPE_ALL = 0;

		/**
		 * 显示未读语音信息
		 */
		int VOICE_MAIL_SHOW_TYPE_UNREAD = 1;

		/**
		 * 显示紧急语音信息
		 */
		int VOICE_MAIL_SHOW_TYPE_EMERGENCY = 2;

		/**
		 * 紧急语音信息标志(与服务器一致)
		 */
		int VOICE_MAIL_EMERGENCY = 0;

		/**
		 * 前进、后退的偏移量6秒
		 */
		int CONTROL_OFFSET = 6000;

		/**
		 * 语音留言控制：后退
		 */
		int CONTROL_BACKWARD = 1;

		/**
		 * 语音留言控制：前进
		 */
		int CONTROL_FORWARD = 3;

		/**
		 * 语音留言控制：暂停
		 */
		int CONTROL_PAUSE = 2;

	}

	/**
	 * 
	 * 呼叫相关常量定义 Copyright (C) 2008-2013 华为技术有限公司(Huawei Tech.Co.,Ltd)
	 * 
	 * @since 1.1
	 * @history 2013-8-27 v1.0.0 l00211010 create
	 */
	public static interface CallConstant {
		/**
		 * 网络信号
		 */
		int VOIP_UPDATE_SINGLE = 1;

		/**
		 * 通知通话界面刷新
		 */
		int VOIP_UPDATE_UI = 2;

		/**
		 * 通知界面通话（视频/音频）变更
		 */
		int VOIP_CALL_MODIFY = 3;

		/**
		 * 通知主界面呼叫挂断
		 */
		int VOIP_CALL_HANG_UP = 4;

		/**
		 * 通知通话界面刷新
		 */
		int VOIP_CALL_RECORD = 5;

		/**
		 * 通知PDF刷新界面
		 */
		int VOIP_PDF_UPDATE_UI = 6;

		/**
		 * 系统静音
		 */
		int SLIENT_VOICE = 7;

		/**
		 * 关闭摄像头
		 */
		int CLOSE_CAMERA = 8;

		/**
		 * 显示呼出界面
		 */
		int SHOW_CALL_LAYOUT = 9;

		/**
		 * 会话标识，用于Intent传递数据标识
		 */
		String VOIP_CALLID = "callID";
		/**
		 * 对侧号码，呼叫号码，用于Intent传递数据标识
		 */
		String VOIP_CALLNUMBER = "callInNumber";

		/**
		 * 来电的匿称
		 */
		String VOIP_CALL_DISPLAY_NAME = "callInDisplayname";

	}

	public static interface MsgCallFragment {
		/**
		 * 视频
		 */
		int MSG_SHOW_VIDEOVIEW = 0x000001;

		/**
		 * 音频
		 */
		int MSG_SHOW_AUDIOVIEW = 0x000002;

		/**
		 * 结束呼叫
		 */
		int MSG_CALL_END_REQUEST = 0x000003;

		/**
		 * 呼叫中界面刷新
		 */
		int MSG_CALL_UPDATE_UI = 0X000005;

		/**
		 * 呼叫变更通知
		 */
		int MSG_CALL_MODIFY_UI = 0X000006;

		/**
		 * 发起视频呼叫
		 */
		int MSG_DIALCALL_VIDEO = 0X000007;
		/**
		 * 发起音频呼叫
		 */
		int MSG_DIALCALL_AUDIO = 0X000008;

		/**
		 * 刷新音频路由
		 */
		int MSG_AUDIO_ROUTE_UPDATE = 0X000009;

		/**
		 * 刷新视频VIEW
		 */
		int MSG_REFRESH_VIEW = 0X00000A;

		/**
		 * 升级视频请求超时
		 */
		int MSG_ADD_VIDEO_TIME_OUT = 0X00000B;

		/**
		 * 辅流解码成功
		 */
		int MSG_DATA_DECODE_SUCCESS = 0X00000C;

		/**
		 * 关闭视频失败
		 */
		int MSG_CLOSE_VIDEO_FAIL = 0X00000D;

		/**
		 * 从后台恢复
		 */
		int MSG_DO_FROM_BACKGROUND = 0X00000E;

		// start by c00349133 reason: 会话保持时，通信界面弹出“通话被保持”
		int MSG_SHOW_SESSION_HOLD = 0X000014;
		// end by c00349133 reason: 会话保持时，通信界面弹出“通话被保持”

		/**
		 * 对端开启或关闭视频
		 */
		int MSG_REMOTE_VIDEO_UPDATE = 0X000AAD;

		/**
		 * 低带宽升级失败
		 */
		int MSG_LOW_BW_UPDATE_FAIL = 0X0000EE;

		/**
		 * 低带宽协商不能视频
		 */
		int MSG_LOW_BW_AUDIO_NEGO_FAIL = 0X00000F;

		/**
		 * 三星手机的特殊判断
		 */
		int MSG_SVMSUNG_PHONE_ADD_VIEW = 0X000011;

		/**
		 * 三星手机的特殊判断
		 */
		int MSG_SVMSUNG_PHONE_REMOVE_VIEW = 0X000012;

		int MSG_NOTIFY_CONF_CONTROL_ENABLE = 0X000013;

		/**
		 * 来电界面销毁
		 */
		int MSG_NOTIFY_CALLCOMING_DESTORY = 0X000019;
	}
	
}
