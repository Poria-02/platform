package cn.poria.common.data.handler;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 租户工具类
 */
@UtilityClass
public class SourceContextHolder {
	private final ThreadLocal<String> THREAD_LOCAL_SOURCE = new TransmittableThreadLocal<>();

	public void clear() {
		THREAD_LOCAL_SOURCE.remove();
	}

	public void setSource(String source){
		THREAD_LOCAL_SOURCE.set(source);
	}

	public String getSource(){
		return THREAD_LOCAL_SOURCE.get();
	}

}
