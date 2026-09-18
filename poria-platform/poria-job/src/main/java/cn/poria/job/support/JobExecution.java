package cn.poria.job.support;

import cn.poria.common.core.util.R;

/**
 * Makes a failed Feign call fail the XXL-Job execution, so the scheduler can retry it.
 */
public final class JobExecution {

    private JobExecution() {
    }

    public static void requireSuccess(R<?> result, String action) {
        if (result == null || !result.isSuccess()) {
            String message = result == null ? "remote service returned no response" : result.getMsg();
            throw new IllegalStateException(action + " failed: " + message);
        }
    }

}
