# mythpodcaster
Transcoder & RSS Publisher for MythTV Recordings

The MythTV Digital Video Recorder (DVR) is a great platform for recording and watching video content at home. But wouldn't it be great to access your recordings from your mobile device or computer when you're away?

The MythPodcaster application allows you to transcode (convert) your MythTV recordings into any number of audio and video formats automatically. The recordings are published in standard RSS feeds that can be imported into most media players, such as iTunes. Recordings are also published in customizable HTML5 web pages that you can access with any browser.

MythPodcaster is a Java application that runs in a Docker container.  Do you use Myhtbuntu?  A custom Fedora installation?  No problem!  The magic of Docker makes it easy to install MythPodcaster.  The Docker image includes the MythPodcaster application and all of the tools (FFmpeg, etc) needed to transcode your recordings.

## Installation

1. [Install Docker](https://docs.docker.com/installation/ubuntulinux/)
2. Download the latest MythPodcaster Docker image: ```sudo docker pull urlgrey/mythpodcaster:latest```
3. Create local directories for MythPodcaster settings:
```shell
sudo mkdir /var/mythpodcaster
sudo mkdir /var/mythpodcaster/rss
sudo mkdir /var/mythpodcaster/config
sudo mkdir /var/mythpodcaster/log
```
1. Add the following configuration files:
```shell
cd /var/mythpodcaster/config
sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/mythpodcaster.properties
sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/transcoding_profiles.xml
sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/log4j.xml
sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/feed_file_transformation.xslt
sudo wget https://raw.githubusercontent.com/urlgrey/mythpodcaster/master/src/main/conf/subscriptions.xml
```
