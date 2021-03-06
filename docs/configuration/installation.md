Zanata can be installed by downloading a web archive (war) file, and configuring Jboss EAP or Wildfly according to this guide, or by downloading a handy installer.

## What you need

- JBoss Enterprise Application Platform 6.3 (EAP). This is the recommended container for Zanata, and it can be [downloaded here](http://www.jboss.org/jbossas/downloads/).
- ... OR Wildfly (recommended version is 8.1.x) which can be [downloaded here](http://wildfly.org/downloads/)
- A suitable MySQL database. This is NOT included in the Zanata archive. You can [download MySQL here](http://dev.mysql.com/downloads/mysql/).
- An email (SMTP) server to perform certain notifications.
- JDK version 7 or later (7 is recommended for EAP as it is not yet certified to run against Java 8). [OpenJDK](http://openjdk.java.net/install/) is recommended, but you can also download [Oracle's JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

The following packages are optional, but recommended:

- clamav for virus protection.

## Setting up the Zanata Database

 1. Download and install MySQL 5.5 from the [MySQL download page](http://dev.mysql.com/downloads/mysql/).
 Zanata has been thoroughly tested against MySQL 5.5 and the Zanata team therefore recommends that you install and use this version with Zanata.

 1. Start MySQL service and create a database schema for Zanata.
 `CREATE DATABASE zanata /**!40100 DEFAULT CHARACTER SET utf8 **/;`

## Installing Zanata

You can run Zanata on JBoss EAP 6 or Wildfly. Just download one of the installer archives below for your platform, and then extract it on top of your JBoss or Wildfly installation.

- [Zanata for JBoss EAP](http://sourceforge.net/projects/zanata/files/installer/zanata-3.6.0-eap-6.zip/download)
- [Zanata for Wildfly](http://sourceforge.net/projects/zanata/files/installer/zanata-3.6.0-wildfly-8.1.zip/download)

## Run the installer

Zanata comes bundled with an installer that helps with some of the initial setup. Simply run the following commands on a shell terminal:

```sh
$ cd <JBOSS>/bin/zanata-installer
$ ./install.sh
```

(there's also a .bat file if you are on Windows) The installation script will start asking some configuration questions. It will also download the Zanata web application and place it in the JBoss installation.

## Some advanced configuration

Zanata does not create an admin user by default. You need to register specific users to have administrative privileges.

 1. Open the `<JBOSS>/standalone/configuration/standalone.xml` file.

 1. Locate the following line, and replace `admin` with a comma-separated list of users that require administrator privileges on the system.

```xml
<simple name="java:global/zanata/security/admin-users" value="admin"/>
```

 1. Register a user under the name "admin", and it will automatically have administrator privileges. Any number of users may be added to this list in a comma-separated format.

 1. In the same file, configure other properties to your particular setup by adding more lines if necessary. The following properties must be configured in order for Zanata to run properly: 
```xml
<simple name="java:global/zanata/email/default-from-address" value="admin@example.com"/>
```

 This is the default email address that will appear as the sender on Zanata emails.

 1. The following properties relate to the SMTP email server that Zanata uses to send emails. It defaults to a locally installed server using port 25. Add values to suit your configuration. If a particular property does not apply to the email server being used, you can comment it out or remove it completely.

```xml
<simple name="java:global/zanata/smtp/host" value="" />
<simple name="java:global/zanata/smtp/port" value="" />
<simple name="java:global/zanata/smtp/username" value="" />
<simple name="java:global/zanata/smtp/password" value="" />
<simple name="java:global/zanata/smtp/tls" value="" />
<simple name="java:global/zanata/smtp/ssl" value="" />
```

## Installing virus scanner (optional)

To prevent virus infected document being uploaded, Zanata is capable of working with clamav.
If clamav is not installed, a warning will be logged when files are uploaded.
If clamav is installed but `clamd` is not running,
Zanata may reject all uploaded files (depending on file type).  To install and run clamav:
```
# Assuming the function install_missing() is still available
if [ -e /usr/bin/systemctl ];then
    install_missing clamav-server clamav-scanner-systemd
    sudo systemctl enable clamd@scan
    sudo systemctl start clamd@scan
else
    install_missing clamd
    sudo chkconfig clamd on
    if ! service clamd status ;then
	sudo service clamd start
    fi
fi
```

You should probably also ensure that freshclam is set to run at least once per day,
to keep virus definitions up to date.
The clamav package will probably do this for you, but you can check by looking for `/etc/cron.daily/freshclam`.
To override the default behaviour above, you can set the system property `virusScanner` when running the server.
`DISABLED` means no virus scanning will be performed; all files will be assumed safe.
Any other value will be treated as the name of a virus scanner command: the command will be called with the name of a file to scan.

## Running Zanata

Go to the `<JBOSS>/bin` directory and run the `standalone.sh` (Linux, Mac) or `standalone.bat` (Windows) file. 

## Using Zanata

To start using your Zanata server, open a browser and navigate to `http://localhost:8080/zanata`

You can now upload some source strings and start translating. To get started, see [Adding Source Strings](user-guide/projects/upload-strings).
