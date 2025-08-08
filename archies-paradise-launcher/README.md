## Archie's paradise launcher.

Здесь находятся все исходники. 
Проект разделён на три части:
- Приложение
- Сервер
- Античит
- Bucket плагин

## Modifications
I commented a line in `Discord.java` and put "localhost" in `launcher_config.yaml`, the to make this code run I start 
main method in `ServerMain.java` and also `Main.java`

Original repo [here](https://github.com/husker-dev/archies-paradise-launcher), btw you will need Java 8

## Vulnerability
When I added this project I got a message and the file indicated below was deleted

```
El archivo \archies-paradise-launcher\resources\mods\CustomSkinLoader_Forge.jar se ha detectado como infectado con Java.Trojan.GenericGBA.34443
```

Also got the following message

```
File archies-paradise-launcher/plugin/libs/spigot-1.12.2-R0.1-SNAPSHOT-shaded.jar is 61.05 MB; this is larger than GitHub's recommended maximum file size of 50.00 MB
```

So I deleted this jar
