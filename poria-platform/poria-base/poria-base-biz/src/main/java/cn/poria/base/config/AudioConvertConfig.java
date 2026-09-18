package cn.poria.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author qiaodi
 * date 2025/10/14 10:08
 * version 6.7.3
 * description
 */
@Configuration
public class AudioConvertConfig {

    @Bean
    public AudioConverter audioConverter() {
        return new FfmpegAudioConverter();
    }

}

