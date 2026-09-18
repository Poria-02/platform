package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.dto.UserInfo;
import cn.poria.upms.api.entity.SysUser;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "remoteUserService", url = "${PORIA_UPMS:http://poria-upms:4000}")
public interface RemoteUserService {
   @GetMapping({"/user/info/{username}"})
   R<UserInfo> info(@PathVariable("username") String username, @RequestHeader("from") String from);

   @GetMapping({"/user/info/{username}/{userType}"})
   R<UserInfo> info(@PathVariable("username") String username, @PathVariable("userType") String userType, @RequestHeader("from") String from);

   @GetMapping({"/user/mobile/{mobile}"})
   R<SysUser> mobile(@PathVariable("mobile") String mobile, @RequestHeader("from") String from);

   @GetMapping({"/social/info/{inStr}"})
   R<UserInfo> social(@PathVariable("inStr") String inStr, @RequestHeader("from") String from);

   @GetMapping({"/user/ancestor/{username}"})
   R<List<SysUser>> ancestorUsers(@PathVariable("username") String username);

   @GetMapping({"/user/yxtoken/{userId}/{token}"})
   void bindYXToken(@PathVariable("userId") Integer userId, @PathVariable("token") String token, @RequestHeader("from") String from);

   @GetMapping({"/user/info/mobile/{mobile}/{userType}"})
   R<UserInfo> infoByMobile(@PathVariable("mobile") String mobile, @PathVariable("userType") String userType, @RequestHeader("from") String from);
}
