FROM urlgrey:docker-tomcat-ffmpeg

# install application and symbolic links into Tomcat
ADD target/mythpodcaster-0.0.16 /data/mythpodcaster/mythpodcaster-0.0.16
RUN ln -s /data/mythpodcaster/mythpodcaster-0.0.16 /usr/local/tomcat/webapp/mythpodcaster

# create directories
RUN mkdir -p /var/log/mythpodcaster
