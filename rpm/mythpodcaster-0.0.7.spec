Name:		mythpodcaster
Version:	0.0.7
Release:	1%{?dist}
Summary:	A Java web application that transcodes MythTV recordings and publishes them through RSS feeds.

Group:		Applications/Multimedia
License:	GPLv3
URL:		http://code.google.com/p/mythpodcaster/
Source0:	http://mythpodcaster.googlecode.com/files/mythpodcaster-0.0.7-src.tar.gz
BuildRoot:	%{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

#BuildRequires:		
Requires:	tomcat6
Requires:	httpd
Exclusiveos:	linux

%description
A Java web application that creates RSS feeds (Podcasts) of MythTV recordings. RSS feeds link to transcoded copies of MythTV recordings. Feeds can be viewed in RSS-capable web browser and in feed aggregators, including iTunes. Encoded feed entries can be played on all popular media players, and on mobile devices like the iPod Touch and iPhone.

%prep
%setup -q

%build
mvn clean package

%install
rm -rf $RPM_BUILD_ROOT

mkdir -p $RPM_BUILD_ROOT/etc/mythpodcaster
cd %{_topdir}/BUILD/%{name}-%{version}/src/main/conf 
install -D -m 664 log4j.xml $RPM_BUILD_ROOT/etc/mythpodcaster/log4j.xml;
install -D -m 664 transcoding_profiles.xml $RPM_BUILD_ROOT/etc/mythpodcaster/transcoding_profiles.xml;
install -D -m 664 subscriptions.xml $RPM_BUILD_ROOT/etc/mythpodcaster/subscriptions.xml;
install -D -m 664 mythpodcaster.properties $RPM_BUILD_ROOT/etc/mythpodcaster/mythpodcaster.properties;

mkdir -p $RPM_BUILD_ROOT/etc/httpd/conf.d
cd %{_topdir}/BUILD/%{name}-%{version}/src/main/conf/httpd
install -D -m 664 mythpodcaster.conf $RPM_BUILD_ROOT/etc/httpd/conf.d/mythpodcaster.conf;

mkdir -p $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/MythPodcaster
cd %{_topdir}/BUILD/%{name}-%{version}/target/MythPodcaster-%{version}
for file in `find . -type f`; do\
  install -D -m 664 $file \
       $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/MythPodcaster/$file;\
done

mkdir -p $RPM_BUILD_ROOT/var/log/mythpodcaster
mkdir -p $RPM_BUILD_ROOT/var/mythpodcaster/rss
mkdir -p $RPM_BUILD_ROOT/var/www/html/mythpodcaster
cd $RPM_BUILD_ROOT/var/www/html/mythpodcaster
ln -s /var/mythpodcaster/rss

%clean
rm -rf $RPM_BUILD_ROOT

%post
/etc/init.d/httpd reload

%files
%defattr(-,tomcat,tomcat,-)
%config(noreplace) %attr(664,apache,apache) /etc/httpd/conf.d/mythpodcaster.conf

%dir /etc/mythpodcaster
%config(noreplace) /etc/mythpodcaster/mythpodcaster.properties
%config(noreplace) /etc/mythpodcaster/subscriptions.xml
%config(noreplace) /etc/mythpodcaster/transcoding_profiles.xml
%config /etc/mythpodcaster/log4j.xml

%dir /var/log/mythpodcaster
%{_localstatedir}/mythpodcaster
%{_datadir}/tomcat6/webapps/MythPodcaster
%{_localstatedir}/www/html/mythpodcaster

%doc

%changelog
* Tue May 25 2010 <kidder.scott@gmail.com> - 0.0.7
Feature:
14: Add encoding mode that invokes qt-faststart following FFMPEG encoding
15: Add support for parallel transcoding jobs

* Tue Apr 27 2010 <kidder.scott@gmail.com> - 0.0.6
Feature:
10: Support Feed Thumbnails from MythTV Thumbnails
12: Support iTunes Podcast Feed Tags

* Fri Apr 2 2010 <kidder.scott@gmail.com> - 0.0.5
Feature:
1: Include a Confirmation dialog when unsubscribing 
2: Provide an RPM build for simplified installation 
3: Allow multiple output encodings for a show 
6: Segmented Encoding directory not deleted after failed encoding
7: Move the log4j configuration to the configuration directory 
8: Reduce the volume of application logs
