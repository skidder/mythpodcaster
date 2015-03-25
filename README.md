# MythPodcaster
Transcoder & RSS Publisher for MythTV Recordings

The MythTV Digital Video Recorder (DVR) is a great platform for recording and watching video content at home. But wouldn't it be great to access your recordings from your mobile device or computer when you're away?

The MythPodcaster application allows you to transcode (convert) your MythTV recordings into any number of audio and video formats automatically. The recordings are published in standard RSS feeds that can be imported into most media players, such as iTunes. Recordings are also published in customizable HTML5 web pages that you can access with any browser.

MythPodcaster is a Java application that runs in a Docker container.  Do you use Mythbuntu?  A custom Fedora installation?  Unfamiliar with installing and running Java applications?  No problem!  The magic of Docker makes it easy to install MythPodcaster.  The Docker image includes the MythPodcaster application and all of the tools (FFmpeg, etc) needed to transcode your recordings.

## Installation

1. [Install Docker](https://docs.docker.com/installation/ubuntulinux/)
2. Download the latest MythPodcaster Docker image:

    ```sudo docker pull urlgrey/mythpodcaster:1af14b593c05732c12d2d9de23f695a4a8cf20c6```
3. Create local directories for MythPodcaster settings:

    ```shell
    sudo mkdir /var/mythpodcaster
    sudo mkdir /var/mythpodcaster/rss
    sudo mkdir /var/mythpodcaster/config
    sudo mkdir /var/mythpodcaster/log
    ```
4. Add the following configuration files:

    ```shell
    cd /var/mythpodcaster/config
    sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/mythpodcaster.properties
    sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/transcoding_profiles.xml
    sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/log4j.xml
    sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/feed_file_transformation.xslt
    sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/subscriptions.xml
    ```
5. Edit the ```/var/mythpodcaster/config/mythpodcaster.properties``` file to include the database connection information.  Example:

    ```properties
    hibernate.connection.driver_class=com.mysql.jdbc.Driver
    hibernate.connection.url=jdbc:mysql://192.168.1.100/mythconverg
    hibernate.connection.username=mythtv
    hibernate.connection.password=mythtv
    hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
    ```
6. Run the Docker image with mapped volumes pointing to the directories created earlier, as well as the directories containing your MythTV recording (e.g. /mnt/media, /mnt/media2, ...).  

    ```shell
    sudo docker run -d -P -v /var/mythpodcaster/config:/etc/mythpodcaster -v /var/mythpodcaster/log:/var/log/mythpodcaster -v /var/mythpodcaster/rss:/var/mythpodcaster/rss -v /mnt/media:/mnt/media -v /mnt/media2:/mnt/media2 -v /mnt/media3:/mnt/media3 -v /mnt/media4:/mnt/media4 -p 8080:8080 urlgrey/mythpodcaster:1af14b593c05732c12d2d9de23f695a4a8cf20c6
    ```
7. Tail the MythPodcaster log to verify that it starts up without errors:

    ```tail -f /var/mythpodcaster/log/mythpodcaster.log```
8. Access the web interface to configure your recording rules:

    ```http://<IP or hostname of MythTV machine running Docker>:8080/mythpodcaster```
