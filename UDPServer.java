import java.io.*;
import java.net.*;

class UDPServer {
   public static int offset = 0; 

   public static void main(String args[]) throws Exception {
   
      DatagramSocket serverSocket = new DatagramSocket(11045);
      String newHeader = "";
      String sentence = "";
   
      byte[] header;
      byte[] packet;
      byte[] outData = new byte[256];
      byte[] inData = new byte[256];
      int packetNum = 0; 
      
      while (true) {
         DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
         
         serverSocket.receive(inPacket);
         
         String message = new String(inPacket.getData());
      
         String[] filename = message.split(" ");
         System.out.println("Attempting to read: " + filename[1]);
         String path = "/Users/maggieblanton/Desktop/Networks/project1/" + filename[1];
         File tmpDir = new File(path);
         
         if (tmpDir.exists()) { 
            InetAddress IPAddress = inPacket.getAddress();
          
            int port = inPacket.getPort();
         
           
            RandomAccessFile read = new RandomAccessFile(filename[1], "r");
            int flag = 0;
            long size = read.length();
         
            while (flag != -1) {
             
            
               if (packetNum == 0) {
                  newHeader = "Packet " + (packetNum) + "\nChecksum: " + "00000\r\n" + "\n" + "HTTP/1.0 200 Document Follows\r\n"
                     + "Content-Type: text/plain\r\n"
                     + "Content-Length: " + size + "\r\n\r\n" + "Data";
                  header = newHeader.getBytes();
                  packet = offset(header);
               }
               
               else {
               
                  newHeader = "Packet " + (packetNum) + "\n" + "Checksum: " + "00000\r\n" + "\r\n";
                  header = newHeader.getBytes();
                  packet = offset(header);
                  int var = packet.length - header.length;
                  flag = read.read(packet, header.length, (packet.length - header.length));
               }
            
               System.out.println("Reading packet " + (packetNum) + "\n");
            
               sentence = calcSum(packet);
            
               System.out.println(sentence);
               System.out.println("\nSending packet " + (packetNum) + "\n");
            
            
               if (flag != -1) {
                  outData = sentence.getBytes();
                  DatagramPacket outPacket =
                     new DatagramPacket(outData, outData.length, IPAddress, port);
                  serverSocket.send(outPacket);
                  
               }
               
               else {
                  header[header.length - 1] = 0;
                  sentence = calcSum(header);
                  outData = sentence.getBytes();
                  DatagramPacket outPacket =
                     new DatagramPacket(outData, outData.length, IPAddress, port);
                  serverSocket.send(outPacket);
               }
               packetNum++;
            }
         }
         else { 
            System.out.println("Invalid file name or directory."); 
            break;
         }
         
      }
   }
   
   public static int checkSum(byte[] data) {
      int checkSum = 0;
      int i = 0;
      int length = data.length;
      
      while (i < length) { 
      
         checkSum = checkSum + (int) data[i];
         i++;
      }
      return checkSum;
   }
   

   public static byte[] offset(byte[] header) {
      byte[] newHeader = new byte[256];
      int set = 0;
      while (set < newHeader.length) { 
         if (set < header.length) {
            newHeader[set] = header[set];
         } else {
            newHeader[set] = 32;
         }
         set++;
      }
      return newHeader;
   }

   
   public static String calcSum(byte[] data) { 
      String sentence = new String(data);
      String checkSum = Integer.toString(checkSum(data));
    
      
      String packetInfo = new String(data);
      int index = packetInfo.indexOf(":") + 2;
      byte[] sum = checkSum.getBytes();
      int length = checkSum.length();
      
      int var1 = index + 1;
      int var2 = var1 + 1;
      int var3 = var2 + 1;
      int var4 = var3 + 1;
      
      
      if (length == 2) { 
         data[var3] = sum[0];
         data[var4] = sum[1];
      }
      else if (length == 3) {  
         data[var2] = sum[0];
         data[var3] = sum[1];
         data[var4] = sum[2];
         
      }
      else if (length == 4) { 
         data[var1] = sum[0];
         data[var2] = sum[1];
         data[var3] = sum[2];
         data[var4] = sum[3];
      }
      else {
         data[index] = sum[0];
         data[var1] = sum[1];
         data[var2] = sum[2];
         data[var3] = sum[3];
         data[var4] = sum[4];
      }
        
      
      String returnString = new String(data);
      return returnString;
   }

  
}