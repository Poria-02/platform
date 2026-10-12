package cn.poria.message.enums;

public interface WebSocketConstants {

    String CACHE_KEY = "WEBSOCKET_SESSION:";

    // 信息来源
    String TYPE_HEART        = "13000";  //心跳
    String TYPE_ORDER        = "13001";  //订单
    String TYPE_HIS_BOOK     = "13002";  //HIS_APP预约
    String TYPE_SYS          = "13003";  //运营系统
    String TYPE_BEHAVIOR     = "13004";  //用户行为数据分析
    String TYPE_ERROR        = "13005";  //错误信息收集
    String TYPE_HOME_CARE    = "13006";  //医护上门订单

    // 信息通用状态
    String READ_N            = "13010";  //未读
    String READ_Y            = "13011";  //已读

    String STATU_NONE        = "13012";  //未知
    String STATU_INSERT      = "13013";  //新增
    String STATU_UPDATE      = "13014";  //修改


    // 订单状态APP那边获取(消息队列获取)
    String STATU_ORDER_CANCEL            = "-1";  //已取消
    String STATU_ORDER_WAIT_TO_PAID      = "0" ;  //待支付
    String STATU_ORDER_TO_BE_SERVICE     = "11";  //待服务
    String STATU_ORDER_WAIT_SHIPPING     = "12";  //待发货
    String STATU_ORDER_WAIT_RECEIVED     = "13";  //待收货
    String STATU_ORDER_FINISH            = "30";  //订单已完成
    String STATU_ORDER_IS_REFUNDING      = "51";  //退款中
    String STATU_ORDER_REFUNDDED         = "52";  //已退款


}
