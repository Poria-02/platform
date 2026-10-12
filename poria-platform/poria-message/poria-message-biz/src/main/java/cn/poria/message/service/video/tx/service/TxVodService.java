package cn.poria.message.service.video.tx.service;

import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.service.video.tx.config.TxTRTCProperties;
import cn.poria.message.service.video.tx.config.TxVodProperties;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.MediaInfo;
import com.tencentcloudapi.vod.v20180717.models.SearchMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.SearchMediaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TxVodService {

    @Autowired
    private VodClient vodClient;

    @Autowired
    private TxTRTCProperties trtcProperties;

    @Autowired
    private TxVodProperties vodProperties ;

    /**
     * 获取文件下载地址
     */
    public String getVodDownUrl(String roomId){
        SearchMediaRequest request = new SearchMediaRequest();
        request.setTrtcRoomIds(new String[]{roomId});
        try {
            SearchMediaResponse response = vodClient.SearchMedia(request);
            MediaInfo[] mediaInfoSet = response.getMediaInfoSet();
            for (MediaInfo mediaInfo : mediaInfoSet) {
                return mediaInfo.getBasicInfo().getMediaUrl();
            }
        } catch (TencentCloudSDKException e) {
            log.info("腾讯Vod服务请求异常, Action[{}] \n message: {}","查询录屏文件地址", JSONUtil.toJsonStr(e));
            throw new ServiceException("腾讯Vod服务请求异常,错误码: " + e.getErrorCode());
        }
        return null;
    }


    /**
     * 删除录屏文件
     */
    public void delVodFile(String fileId){

        DeleteMediaRequest request = new DeleteMediaRequest();
        request.setFileId(fileId);
        request.setSubAppId(vodProperties.getSdkAppId());
        try {
            vodClient.DeleteMedia(request);
        } catch (TencentCloudSDKException e) {
            log.error("删除录屏文件失败",e);
            throw new ServiceException("腾讯Vod服务请求异常,错误码: " + e.getErrorCode());
        }
    }

}
