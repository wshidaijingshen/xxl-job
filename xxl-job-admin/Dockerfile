FROM stormfeng/jre8-inotify
WORKDIR /opt
ENTRYPOINT ["sh","/xxl-job-admin.sh"]
# docker build -t stormfeng/xxl-job-admin .
# docker run -it --name=xxl-job-admin -p 8780:8780 -v /home/zorin/upload/xxl-job-admin/:/opt/ stormfeng/xxl-job-admin sh

# docker start xxl-job-admin
# docker exec -it xxl-job-admin sh

# docker update --restart unless-stopped $(docker ps -q) # 自动重启所有容器（主动关闭的除外）

# docker logs -f --tail=50 xxl-job-admin

# docker stats --no-stream
# docker stop xxl-job-admin; docker update -m 250m --memory-swap 300m xxl-job-admin; docker restart xxl-job-admin; docker stats
# docker rm -vf xxl-job-admin
# docker rmi stormfeng/xxl-job-admin

# 本脚本来源，持续集成技巧，替换jar包 https://www.cnblogs.com/toutou/p/docker_springboot.html
# 持续集成 思路 https://www.cnblogs.com/zcyy/p/15661544.html
# docker 学习中文文档 https://yeasy.gitbook.io/docker_practice/image/multistage-builds
# docker 官方英文文档
# 基于 Docker 的 Jenkins 持续集成与持续部署 https://cloud.tencent.com/developer/article/1506249
