Name:		mythpodcaster
Version:	0.0.5
Release:	1%{?dist}
Summary:	A Java web application that transcodes MythTV recordings and publishes them through RSS feeds.

Group:		Applications/Multimedia
License:	GPLv3
URL:		http://code.google.com/p/mythpodcaster/
Source0:	http://mythpodcaster.googlecode.com/files/mythpodcaster-0.0.5-src.tar.gz
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
mkdir -p $RPM_BUILD_ROOT/usr/share/mythpodcaster/rss
mkdir -p $RPM_BUILD_ROOT/var/www/html/mythpodcaster
cd $RPM_BUILD_ROOT/var/www/html/mythpodcaster
ln -s /usr/share/mythpodcaster/rss

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
%{_datadir}/mythpodcaster
%{_datadir}/tomcat6/webapps/MythPodcaster
%{_localstatedir}/www/html/mythpodcaster/rss

%doc

%changelog
