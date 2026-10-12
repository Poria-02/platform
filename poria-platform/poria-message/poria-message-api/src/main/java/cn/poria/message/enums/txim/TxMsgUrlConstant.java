package cn.poria.message.enums.txim;

/**
 * 腾讯msg url
 */
public interface TxMsgUrlConstant {

    /**
     * 版本
     */
    String MSG_VERSION = "/v4";

    /**
     * tx 消息域名
     */
    String MSG_HOST = "https://console.tim.qq.com";

    String MSG_PARAM = "?sdkappid={}&identifier={}&usersig={}&random={}&contenttype=json";


    //==============================[帐号管理]====================================//
    String ACCOUNT_CREATE = "/im_open_login_svc/account_import";
    String ACCOUNT_DELETE = "/im_open_login_svc/account_delete";
    String ACCOUNT_INFO = "/profile/portrait_get";








    //==============================[单聊消息]====================================//

    String P2P_MSG_URL = "/openim/sendmsg";
    String P2P_MSG_RECORD = "/openim/admin_getroammsg";



    //==============================[全员推送]====================================//



    //==============================[资料管理]====================================//



    //==============================[关系链管理]====================================//



    //==============================[最近联系人]====================================//


    //==============================[群组管理]====================================//
    String GROUP_CREATE = "/group_open_http_svc/create_group";
    String GROUP_UPDATE = "/group_open_http_svc/modify_group_base_info";
    String GROUP_INFO = "/group_open_http_svc/get_group_info";
    String GROUP_SEND_MSG = "/group_open_http_svc/send_group_msg";
    String GROUP_MSG_RECORD = "/group_open_http_svc/group_msg_get_simple";
    String GROUP_MEMBER_ADD = "/group_open_http_svc/add_group_member";
    String GROUP_MEMBER_DEL = "/group_open_http_svc/delete_group_member";
    String GROUP_MEMBER_UPDATE = "/group_open_http_svc/modify_group_member_info";
    String GROUP_MEMBER_INFO = "/group_open_http_svc/get_group_member_info";
    String GROUP_DESTROY = "/group_open_http_svc/destroy_group";
    String GROUP_OWN = "/group_open_http_svc/get_joined_group_list";
    String GROUP_SYSTEM_MSG = "/group_open_http_svc/send_group_system_notification";
    String GROUP_TRANSFER = "/group_open_http_svc/change_group_owner";






    //==============================[全局禁言管理]====================================//



    //==============================[运营管理]====================================//

    //==============================[多人会议管理]====================================//
    String ROOM_CREATE = "/room_engine_http_srv/create_room";

    String ROOM_DESTROY = "/room_engine_http_srv/destroy_room";

    String ROOM_GET_INFO = "/room_engine_http_srv/get_room_info";
}
