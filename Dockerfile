FROM urlgrey/docker-tomcat-ffmpeg:tomcat-7.0.73

# install application and symbolic links into Tomcat
ADD target/mythpodcaster-0.0.17 /usr/local/tomcat/webapps/mythpodcaster

# create directories
RUN mkdir -p /var/log/mythpodcaster
