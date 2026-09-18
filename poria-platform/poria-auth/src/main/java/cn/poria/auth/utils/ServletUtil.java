package cn.poria.auth.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ServletRequest;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author zhangchunlei
 * @date 2023年02月15日 2:12 PM
 */
public class ServletUtil {

	public static String getBody(ServletRequest request) {
		try (final BufferedReader reader = request.getReader()) {
			return IoUtil.read(reader);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

}
