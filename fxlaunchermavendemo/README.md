docker build -t myssh .
docker run -it --rm myssh

it is not possible to ping a container in Windows, it looks like it is because it is running behind windows







arp -a

[Configure Claro router](https://www.youtube.com/watch?v=dNLlJ584cq0&t=2s): 192.168.1.1, admin, Cl@r0.

## Prerequisites

1. I used a VMWare, because it is said to be more performant than other solutions like VirtualBox. Just type in google download VMWare, note version "Workstation Pro" is design for windows and "fusion" is for Unix base systems like MAcOs or Linux I think. You'll have to register with "Broadcom"(they own VMWare, I think). Go to "My Downloads", and click "free software download here", enter the name "workstation" and click "show results", select "wmware workstation pro", choose the windows version if using windows or Linux if using Linux, choose the release version you want(I always use the latest), click on "Terms and conditions", read if you want, and close the terms & conditions window, and then check the box "I agree to terms and conditions", you can now download it.

2. Download Linux mint, Just search on google download Linux mint, enter the website an choose the version you want, right now there is three options, Cinnamon, Xfce and MATE, I use Xfce. Click "download" scroll down and choose the "location"/"mirror"(the actual server that will send you the file) you want

3. Start a VM, You should be able to find a tutorial on internet on how to do it. I always use a "VMWare ESX" version "VMware ESXI 8" for the guest operating system, you'll see these options when following the creation flow. Just remember if you use this type of OS, after the VM is created(not started yet) right click it, select "settings", click "Processors" and deselect "Virtualize Intel VT-x/EPT or AMD-V/RVI" only if you have an error when starting the VM

4. Once vm is started you should see the linux mint "ISO" file you downloaded, double click it and just go through the installation process

5. (NOTE)This is not possible to do this using Docker, it works differently, its virtualization architecture is different. An alternative is using another desktop/laptop computer, in this case you don't need any virtual machine

6. In the event you need to grow your virtual machine storage capacity, first make sure the VM is not running, and then right click it, navigate to "Hard Disk" and click "expand" after you do this, you have to manage partitions inside your VM and make the current partition to accept new free space, in this set up you just have to runt the commands `sudo cfdisk`, you'll see a screen with the partitions, just click "resize" and accept the extra space, after you do that make the filesystem recognize this space with `sudo resize2fs /dev/sda2`, "/dev/sda2" is the name of your parittion, which is visible with the "cfdisk" command

## (Optional) test your network with a REST API
We'll use golang to create a REST API using the library called "gin"

1. Update `apt` package manager, just run in a terminal `sudo apt update`

2. Install golang. You can search packages with `apt search golang-go` to confirm it exists, install it with `sudo apt install golang-go`

3. In your terminal `cd`(change directory) to any directory you want to create this code in, I did `cd ~/Documents`. Initiate a golang module `go mod init restapi`, then run `nano` text editor with `nano api.go`(this creates the file if it doesn't exists already), paste the following code 
```
package main


import (
   "net/http"

   "github.com/gin-gonic/gin"
)

func hello(c *gin.Context) {
   c.JSON(http.StatusOK, gin.H{
     "message": "world",
   })

}

func main() {
   router := gin.Default()
   router.GET("/hello", hello)

   router.Run("192.168.1.16:9090") // you should change the ip address with the ip address(IPV4) of the machine running this code
}
```

4. Download the packages with `go get` on your terminal in the same directory where you created your golang program

5. Run the code from your terminal with `go run .`, this program opens listens on port 9090

6. test it from any browser from any device connected to the same network, enter the address `192.168.1.16:9090/hello` on your broser as it was a web page. Or from a terminal in your host you can run the http request 
```
curl http://192.168.1.16:9090/hello \
   --header "Content-Type: application/json" \
   --request "GET"

or 

curl 192.168.1.16:9090/hello \
   --header "Content-Type: application/json" \
   --request "GET"

or 

curl http://192.168.1.16:9090/hello
```

## Configure VMware Virtual Machine In same network as host to be able to find them through the ISP router with network protocols like PING, HTTP requests, SSH, etc

1. Your VM network mode has to be "Bridge"

2. To make sure the bridge is configure correctly, at least in Windows 11, search for the app "Newtwork Connections"/"View network connections" right click your internet connection, it could be "Wi-Fi" or "Ethernet"(if connected with physical cable", select "Properties" and make sure "VMware Bridge Protocol" is enable

3. Find Your connections IP address, basically you can use the UI, in Windows head to the system tray bar and right click the internet icon and click "network and internet settings", "click properties" and find it under "IPv4 address", basically just find your way to "Network properties", there is lots of ways of how to get here but this is one, If you have git for windows installed you have some UNIX utilities, just open "Git Bash" using the terminal and type `ipconfig`, find the right device, it should again, ethernet if connected with a physical cable or Wi-fi if connected wireless, is the IPV4 Address. On Linux basically is similar, you have the UI option, usually by right clicking the connection icon, usually on the top right or bottom right corner and finding its properties/configurations/about, something similar, or again you have the `ipconfig` option.

4. You can test they're connected using ping command, from a terminal just execute command `ping <ip_address_of_computer_you_want_to_check_connection_with>`, you can ping it by name also, find the name with commands like `sudo nmap -sn 192.168.1.*` or `nslookup 192.168.1.16`, they will give you the name, at least on Linux these commands work, you can even just run `hostname` command in terminal in Linux and copy that name to ping, once you have the name just use the name instead of the IP address. In my setup the host is Windows 11 and the VM is latest Linux mint

## [Set up an SSH Server on both, host and vm](https://www.geeksforgeeks.org/linux-unix/ssh-command-in-linux-with-examples/)
It is made differenctly in Windows and linux

1. First lets do this in the linux vm terminal run `sudo apt install openssh-client openssh-server`

2. check the status of running server after installation we can use this command "systemctl status sshd", if not running start with `sudo systemctl start sshd`, if you get an error, check the actual service name with `systemctl list-units --type=service --all | grep ssh`, copy the right name and repeat this step again. run `sudo systemctl enable sshd` to have service start during boot process

3. check your ssh connection with `ssh [username]@[hostname or IP address]`, `ssh juan@192.168.1.16`, in my case I did it from windows

4. If you want to use ssh the way around(connect to windows computer fron linux VM), on a windows command promt run `ssh-keygen`, This will create two files in your .ssh folder: id_rsa (private key) and id_rsa.pub (public key).

5. Open an admin permission elevated PowerShell prompt, Install the OpenSSH.Server optional feature with the command below. Here is a breakdown of the command, `Get-WindowsCapability -Online` Lists all optional Windows features (capabilities) that are available on the current OS image (i.e., the running system). `Where-Object Name -like 'OpenSSH.Server*'` Filters the list to find capabilities whose names match OpenSSH.Server*, such as: `OpenSSH.Server~~~~0.0.1.0`. `Add-WindowsCapability –Online` Installs the filtered capability onto the current OS (online image)
```
Get-WindowsCapability -Online |
  Where-Object Name -like 'OpenSSH.Server*' |
  Add-WindowsCapability –Online	
```

6. If for any reason this is not completely downloaded(like in my case), You can just search for "optional features" in windows search, then for ssh server in "view feature", I still got error, but checked the optional features history and found error "error 0x800F0820" which could be some corrupted files so from command prompt with admin privileges I executed `sfc /scannow` and `DISM.exe /Online /Cleanup-image /Restorehealth` but you should execute `DISM.exe /Online /Cleanup-image /Restorehealth` first and then `sfc /scannow` and reboot the pc, after the corrupted files produced in my previous attempts, I was able to do set upt ssh from the optional features screen

7. Run these commands `Start-Service sshd` and `Start-Service ssh-agent` in an powershell with admin privileges, note that I didn't set upd these service to start automatically because I don't need them to do that but you can do it with `Set-Service -Name <service_name> -StartupType 'Automatic'`

8. You can connect now from the Linux VM by executing this command `ssh "Juan Enrique@192.168.1.5"` on a terminal, your password is your outlook account password if you're logged in to an outlook account or the computer password, either or

9. You should enter the password, if it fails with a "Too many authentication failures" stop the services with the same command but replace `Start` with `Stop`, open a Notepad instance as an Administrator by rightclicking over the icon and open the file with the address `Programdata%sshsshd_config`, edit `MaxAuthTries` to a greater number.

At this point we should have everything we need to test FXLauncher, you have the source code in the repo.

## Installing Apache server
We need this software to be able to retrieve the artificats(JARs) from our application. Execute each of the commands below
```
sudo apt update
sudo apt install apache2 -y
sudo systemctl enable apache2
sudo systemctl start apache2\
```
```
sudo systemctl status apache2
sudo chmod -R 755 /var/www/html
sudo rm /var/www/html/index.html
sudo chown <yourUsername>:<yourUsername> -R /var/www/html
```

## Copy files Using CP Manually
You can add a maven execution if you want, otherwise just run the below code from the directory you have your artifacts in and from a Unix like terminal(git bash in windows), in our case it would be
in the `target` directory of the `front` module
```
scp * juan@192.168.1.168:/var/www/html
```

You can access your articats from a web browser from any device that is connected to the same wifi network like just typing `192.168.1.16/<name_of_artifact>` on your browser search bar, for course you can
make an http request using any utility like `wget`, or maybe `curl` or anything similar

## Setting up FXLauncher
After tyring to work out with this somewhat old library, I came to conclusion that it is not longer possible to use this with a modern stack, I was using OpenJDK 21, with Java modules in a [modular Maven application](https://maven.apache.org/pom.html). I was able to solve
several configurations to adapt to this set up and got the application running however after some testing I saw that even though the jar where being loaded from a local cache directory the new implementation/changes where not being invoked, and this happens because in the original
example the context/stack at that time was different, different tools where being used to deploy applications, JavaPackager was included in the JDK, also JavaFx modules where included but this changed at some point, theyr are not part of JDK anymore, and JavaPackager was even deprecated I think, and
the JavaPackager actually produced a FatJar, a Jar that contains all its dependencies and loads them in the class path, and that is why it previously this library worked, but now tools like Jlink
are used to create custom JRE images, this tool basically reduces the size of applications, it makes it easier to distribute, etc, however this is not a Fat Jar being produced and also it works with Java Modules to avoid including unnecessary libraries in the custom JRE image that it produces,
Is usually used alongisde with Jpackage to produce native applications like executables and msi installers in windows, .deb files in linux, etc, Jlink produces an static image, is basically frozen once it is put together, you would have to rebuild it to get updates to your users. Jpackage alone could
help you produce a similar Fat Jar file but you have to make it manually at least I don't know of a plugin that can help me with this right now, this is because Jpackage has two modes, one is for modular(java modules), you declare the modules
```
jpackage \
  --type app-image \
  --module-path mods \
  --add-modules com.example.app \
  --module com.example.app/com.example.app.Main
```
The above code invokes Jlink under the covers. The other mode is a classpath approach(non-modular), this is how it was done before modules came out as an alternative
```
jpackage \
  --type app-image \
  --input myapp-dir \
  --main-jar app.jar \
  --main-class com.example.Main
```

I'm not going to explore this solution, we can leave it as a todo. Also see [this article](https://www.cgjennings.ca/articles/java-9-dynamic-jar-loading/) where you can see that the 
usage that this API as many other where giving to URLClassLoader API it mentions that although this was never documented 
as a fact, it was a reasonable assumption since that’s the class loader that knows how to deal with JAR files. But then 
came Project Jigsaw, the module system introduced with Java 9. With it came a whole new hierarchy of class loaders that
broke any app that assumed a URLClassLoader system class loader. This article also provides other alternatives 

1. Stick with Java 8 indefinitely. This is a serious option: there are multiple vendors offering free long-term support 
builds of OpenJDK 8 and most desktop Java apps these days bundle their own JRE.
2. Write your own class loader. There are a few approaches here, such as setting the system class loader with a 
`-Djava.system.class.loader=MyClassLoader` option, or installing one at startup before loading your main class. You can 
also write a separate class loader just for loading things dynamically. This can actually be an improvement because you 
may gain the ability to GC a plug-in once it is no longer needed, or replace/upgrade plug-ins at runtime. Depending on 
the assumptions of the existing code, this can be a smooth transition or it can introduce any number of errors.
3. Switch to something like OSGi for loading components at run time. This is a great option when starting a new project,
but if you have already been using addURL it can be difficult to switch. (Again, it depends on the assumptions of your existing code.)
4. Use an agent to extend the system class path in an officially supported manner. Yeah, that’s right. There has been
an official way to do this since Java 6, buried under the obscure java.lang.instrument package. The best part is that 
this method is practically identical to what you were doing before and generally requires little change to your existing code base.

## Clean, Build, Run the aplication
These are the commands I used to run the app

./mvnw clean                        deletes the `target` folder of each module that is generated with `install` or `package` lifecycle phases
./mvnw install    compiles all java code/classes in all modules, meaning it generates a jar file in `target` directory, in this case our main jar is the located in the front module target directory `front/target/front-1.0-SNAPSHOT.jar` 
./mvnw -pl front javafx:run         runs the app without building, always run either install before this goal so that the jar the front module depends on(fxlauncherlib's jar) is created

You can run them these two at once
./mvnw clean install

Use Jlink to build a custom JRE with only the modules declared in your module-info.java files, reducing the size of the JRE by excluding other JRE APIs that you don't need, maybe its similar to how in Python it is adviced to create a venv for diferent projects in which you can have different libraries/versions installed in each:
./mvnw -pl front clean install javafx:jlink
or just
./mvnw -pl front javafx:jlink

After you execute this commands you can run the app without using maven with the following command from a git bash(unix like shell), this command uses the custom image created with the JDK utility called jlink located at `front/target/imagezip/bin/java`, declares the module `front/target/front-1.0-SNAPSHOT.jar` and indicates the main class(using the package it lives under) in the module, `fxlauncher.front/fxlauncher.front.HelloApplication`:
`front/target/imagezip/bin/java --module-path front/target/front-1.0-SNAPSHOT.jar -m fxlauncher.front/fxlauncher.front.HelloApplication`

If we were not using the Jlink generated custom JRE image we would have to indicate however is running the app what dependencies they need and they would have to download, for example, the JavaFx jar files, most likely other dependencies also and run a command like the following:
```
java \
  --module-path front/target/front-1.0-SNAPSHOT.jar:fxlauncherlib/target/fxlauncherlib-1.0-SNAPSHOT.jar:/path/to/javafx-sdk/lib \
  --add-modules javafx.controls,javafx.fxml \
  -m fxlauncher.front
```

In there the `java` is used from the JDK or JRE you have installed and the `/bin` folder in this JDK/JRE, should be in your `PATH`'s env variable so you can invoke it

Note that the original fxlauncher library source code used this package for reflection `import javax.xml.bind.JAXB`, that is not par of the JDK any more and later moved to this artifact `javax.xml.bind:jaxb-api:2.3.1` which I was using but found out that this JAR is not using a module-info.java file, meaning is not modular, and the jlink plugin was giving me the error `Error: automatic module cannot be used with jlink: java.activation from file:///C:/Users/Juan%20Enrique/.m2/repository/javax/activation/javax.activation-api/1.2.0/javax.activation-api-1.2.0.jar` which was caused because jlink only knows how to build a runtime image from explicit modules (and the built‑in JDK modules). It will refuse to include an automatic module in the custom image.

* Explicit modules are JARs (or JMODs) that contain a module-info.class.
* Automatic modules are plain JARs on the module path that get an auto‑generated name (via Automatic-Module-Name or the file name).

This was solved by using an implementation of that library that produces an explicit module, in this case `org.glassfish.jaxb:jaxb-runtime:2.3.3`. also remember you have to declare you need the module with `requires org.glassfish.jaxb.runtime;`

Some other solution would have been using a plugin like [moditect](https://github.com/moditect/moditect) to generate module-info for the module and inject it in your existing JAR. This may involve you running command like `jar uf ArtifactToModify.jar module-info.java`, `jar` is a JDK utility that lives in the bin directoy of the JDK installation, `uf` is to modify and indicate the file we want to modify, if doing it directly from a terminal you'd have to locate the jar file inside the `~/.m2` directory, here is where maven caches library's artifacts and the rest of the command you'd have to figure it out how to put the module inside the right location in the jar, remember a jar is pretty much a zip file, it conatins all the files the java program, compiled from its source code, needs to run. Other options is using the plugin `org.codehaus.mojo:exec-maven-plugin` wich lets you either execute a command like a if running from a command like during a maven phase of the build process which you can specify, an example is in the pom files of this project 

## Alternatives to keep your app up to date that You could explore
In conclusion don't use this library any more in modern setups, the most realistic approach is just stick with something new and "more simple", use jlink and jpakage to produce a native executable/native app and then you can implement a service on the internet that your app
can check with to see if there is any new version and let the customer know they can go ahead and download it, with jpackage you can manage versions and install a new version on top of an old version.

The other libraries are found [here](https://java-source.net/open-source/installer-generators) where they mention the following Open Source Installers Generators in Java: IzPack, VAInstall, Packlet, Lift Off Java Installer, Mini Installer, JSmooth, Launch4J, AntInstaller, Antigen, Java Service Wrapper, update4j
. This info was found in this [stackoverflow question](https://stackoverflow.com/questions/4002462/how-can-i-write-a-java-application-that-can-update-itself-at-runtime)

## Learning
From all of this I learnt how java has changed over time, previously the class loaders allowed us to load classes directly from jars location but that
changed with the module system, by the way, this new module system also introduced the `Module` API and the concept of
"Module Layer", using it you could load the new version of a module that contains a new version of a JAR in a new module layer that is created
on top of the current module layer but this would mean that you would have to be explicit to create any instance of the
API that has a new implementation using the correct module layer which makes it very hard to maintain, so is not a feasible
option in this case.

### APIs
* ServiceLoader
* ClassLoaders
* Module API
* JAXB for serializing XML files to java objects
