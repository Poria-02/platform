FROM harbor.shanxi-ncd.cn/public/base:1.0.0

ENV TZ=Asia/Shanghai
ENV SW_AGENT_NAME=poria-auth

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /poria
WORKDIR poria
EXPOSE 3000

ADD ./target/poria-auth.jar ./app.jar
CMD java -Xms512m -Xmx512m -javaagent:/home/skywalking/skywalking-agent.jar -Djava.security.egd=file:/dev/./urandom -Dhttps.proxyHost=$PROXY_HOST -Dhttps.proxyPort=$PROXY_PORT -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT -Dhttp.nonProxyHosts=$NO_PROXY_HOST -jar app.jar  --spring.profiles.include=$LOG
