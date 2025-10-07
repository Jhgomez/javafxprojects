package tutorial.tutorial.examples.networking.udp;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

public record InputSetUp(DatagramPacket packet, ByteArrayInputStream byteArrayInputStream, ObjectInputStream objectInputStream) { }
