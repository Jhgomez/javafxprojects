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

## Set up an SSH Server on both, host and vm(https://www.geeksforgeeks.org/linux-unix/ssh-command-in-linux-with-examples/)
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

6. If for any reason this is not completely downloaded(like in my case), You can just search for "optional features" in windows search, then for ssh server in "view feature", I still got error, but checked the optional features history and found error "error 0x800F0820" which could be some corrupted files so from command line whit admin privileges I executed `sfc /scannow` and `DISM.exe /Online /Cleanup-image /Restorehealth` but you should execute `DISM.exe /Online /Cleanup-image /Restorehealth` and then `sfc /scannow` and reboot the pc, I was able to do it from the optional features screen

7. Run these commands `Start-Service sshd` and `Start-Service ssh-agent` in an powershell with admin privileges, note that I didn't set upd these service to start automatically because I don't need them to do that but you can do it with `Set-Service -Name <service_name> -StartupType 'Automatic'`

8. You can connect now from the Linux VM by executing this command `ssh "Juan Enrique@192.168.1.5"` on a terminal, your password is your outlook account password if you're logged in to an outlook account or the computer password, either or

9. You should enter the password, if it fails stop the services with the same command but replace `Start` with `Stop`, open a Notepad instance as an Administrator by rightclicking over the icon and open the file with the address `Programdata%sshsshd_config`, edit `MaxAuthTries` to a greater number.

At this point we should have everything we need to test FXLauncher, you have the source code in the repo.

