package cn.poria.upms.handler;

import cn.poria.upms.api.dto.UserInfo;

public interface LoginHandler {
   Boolean check(String loginStr);

   String identify(String loginStr);

   UserInfo info(String identify);

   UserInfo handle(String loginStr);
}
