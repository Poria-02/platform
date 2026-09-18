FROM harbor.shanxi-ncd.cn/public/base:1.0.0

LABEL maintainer="shanxincd"
ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /unhm-gateway
WORKDIR /unhm-gateway
EXPOSE 9999

ENV SW_ENABLED=true
ENV SW_AGENT_NAME=unhm-gateway

ADD ./target/unhm-gateway.jar ./

CMD if [ "$SW_ENABLED" = "true" ]; then \
    java -Xms512m -Xmx512m \
    -javaagent:/home/skywalking/skywalking-agent.jar \
    -Dskywalking.agent.service_name=$SW_AGENT_NAME \
    -Dskywalking.collector.backend_service=$SW_AGENT_COLLECTOR_BACKEND_SERVICES \
    -Djava.security.egd=file:/dev/./urandom \
    -jar unhm-gateway.jar \
    --spring.profiles.include=$LOG; \
else \
    java -Xms512m -Xmx512m \
    -Djava.security.egd=file:/dev/./urandom \
    -jar unhm-gateway.jar \
    --spring.profiles.include=$LOG; \
fi