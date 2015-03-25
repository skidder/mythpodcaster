FROM urlgrey/docker-tomcat-ffmpeg:latest

# install application and symbolic links into Tomcat
ADD target/mythpodcaster-0.0.16 /usr/local/tomcat/webapps/mythpodcaster

# create directories
RUN mkdir -p /var/log/mythpodcaster
