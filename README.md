# fritzcontrol
A little java command line tool to manage the AVM fritz box parental controls

Usage: `java -jar <jar> -u <username> -p <password> -D <Device> -P <Profile>`

Additional Options: 
- `-url` default is `fritz.box`
- `-auth` possible options are `MD5` (default) and `PBKDF2` (newer fritz boxes)

