# Docker

### 一、docker安装

#### 1、centos7.9

```shell
#安装软件包
rpm -ivh container-selinux-2.119.2-1.911c772.el7_8.noarch.rpm
rpm -ivh containerd.io-1.6.33-3.1.el7.x86_64.rpm
rpm -ivh slirp4netns-0.4.3-4.el7_8.x86_64.rpm
rpm -ivh fuse3-libs-3.6.1-4.el7.x86_64.rpm
rpm -ivh fuse-overlayfs-0.7.2-6.el7_8.x86_64.rpm
rpm -ivh docker-compose-plugin-2.27.1-1.el7.x86_64.rpm
rpm -ivh docker-buildx-plugin-0.14.1-1.el7.x86_64.rpm
rpm -ivh docker-ce-cli-26.1.4-1.el7.x86_64.rpm
rpm -ivh docker-ce-rootless-extras-26.1.4-1.el7.x86_64.rpm
rpm -ivh docker-ce-26.1.4-1.el7.x86_64.rpm
#快速安装
rpm -ivh *.rpm
#启动docker引擎
systemctl enable --now docker
```

#### 2、debian11

允许root用户登录

```shell
#PermitRootLogin yes
vi /etc/ssh/sshd_config
systemctl restart sshd
```

安装docker

```shell
dpkg -i containerd.io_1.7.27-1_amd64.deb
dpkg -i docker-ce-cli_28.4.0-1~debian.11~bullseye_amd64.deb
dpkg -i docker-ce_28.4.0-1~debian.11~bullseye_amd64.deb
dpkg -i docker-buildx-plugin_0.28.0-0~debian.11~bullseye_amd64.deb
dpkg -i docker-compose-plugin_2.39.4-0~debian.11~bullseye_amd64.deb
```

### 二、docker常用命令

```shell
#查看docker配置信息
docker info
#查看docker版本
docker -v
#查看镜像
docker images
docker image list
#删除镜像
docker image rm mysql:latest
#保存镜像
docker save mysql:latest mysql.tar
#以压缩格式保存镜像
docker save mysql:8.4.6 | gzip > mysql.tar.gz
#装载镜像
docker load mysql.tar
#装载gzip压缩的镜像
gunzip -c mysql.tar.gz | docker load
#查看容器列表
docker ps #运行中的
docker ps -a #所有的
#删除容器
docker container rm 容器ID
#查看容器日志
docker logs 容器名|容器ID
#停止容器
docker stop 容器名|容器ID
#重新启动容器
docker restart 容器名|容器ID
#重新标记镜像
docker tag 源tag 新tag
#进入正在运行的容器
docker exec -it 容器ID|名称 bash
#构建镜像
docker build -f Dockerfile --rm -t %imageName%:%version% .
#运行镜像,tips:不同镜像有不同,参考镜像文档
docker run -t -p 主机端口:容器端口 -e 环境变量名=变量值 -v 主机路径:容器路径 --name 容器名称 --restart=always 镜像名:镜像tag
```

### 三、Dockerfile样例

#### 1、构建自己的基础镜像

Dockerfile

```dockerfile
FROM debian:12.10
#定义参数
ARG JDK_DIR=/jdk
ARG JDKFILE=openjdk-22.0.2_linux-x64_bin.tar.gz
ARG JDKPATH=jdk-22.0.2
COPY simsun.ttc /usr/share/fonts/
RUN apt-get update && apt-get install -y locales fontconfig && rm -rf /var/lib/apt/lists/* \
    && localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8 \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo 'Asia/Shanghai' >/etc/timezone \
    && fc-cache -fv\
    && mkdir $JDK_DIR
ADD $JDKFILE $JDK_DIR
# 设置环境变量
ENV LANG zh_CN.utf8
ENV JAVA_HOME $JDK_DIR/$JDKPATH
ENV CLASSPATH .:${JAVA_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH
```

构建命令：

```shell
docker build -f Dockerfile --rm -t openjdk-22.0.2-docker-base .
```

#### 2、应用镜像

Dockerfile

```dockerfile
FROM openjdk-22.0.2-docker-base
WORKDIR /springboot-training
VOLUME ["/springboot-training/logs"]
ADD springboot-training-1.0-250920-1558.jar /springboot-training
ADD springboot-training.sh /springboot-training
EXPOSE 9080
ENTRYPOINT ["sh","/springboot-training/springboot-training.sh"]
```

springboot-training.sh

```sh
#!/bin/sh
if [ -n "$JAVA_OPTS" ]; then
  JAVA_OPTS="$JAVA_OPTS"
else
  JAVA_OPTS="-Xms512m -Xmx1024m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
fi
java -Xbootclasspath/a:$(
  cd $(dirname $0)
  pwd
) $JAVA_OPTS -Djava.security.egd=file:/dev/urandom -jar $(
  cd $(dirname $0)
  pwd
)/springboot-training-1.0-250920-1558.jar
```

构建命令

```shell
docker build -f Dockerfile --rm -t springboot-training:1.0-250920-1558 .
```

运行命令

```shell
docker run -t -p 9080:9080 -e JAVA_OPTS='-Xms512m -Xmx1024m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m' -v /logs:/springboot-training/logs --name springboot-training --restart=always springboot-training:1.0-250920-1558
```

