import java.io.*;
import java.net.*;
import java.util.*;

/** 
*
* UDPClient 
*
* @authors Sam Haupert, Naeem Ghossein, Maggie Blanton
* @version 7.23.20
*
* Before running, change 'Maggies-MacBook-Pro.local" to the IP Address of your choosing. 
* and change port numbers accordingly. 
* 
* Insert command line arguments to test the gremlin function. 
* For example, the command 'java UDPClient ".1"' would provide an error rate of 10%.  
* 
*/


class UDPClient {
   public static int packetsDamaged = 0;
   public static int messagesReceived = 0;

   public static void main(String args[]) throws Exception {
   
   
      DatagramSocket clientSocket = new DatagramSocket();
      Double gremlinProbability;
      
   
      InetAddress IPAddress = InetAddress.getByName("Maggies-MacBook-Pro.local");
   
      byte[] sendData = new byte[256];
      String message = "";
      String content = "";
      int lastMessage = 1;
      int count = 1;
      
      if (args.length == 0) { 
         System.out.println("\nDefault gremlin probability of 0 selected.");
         System.out.println("To change, rerun with command line arguments inserted."); 
         gremlinProbability = 0.0;
      }
      else { 
         gremlinProbability = Double.parseDouble(args[0]); 
         System.out.println("\nGremlin probability of " + gremlinProbability + " selected."); 
      }
      
      int numDamagedPackets = (int) Math.rint(gremlinProbability * 46);
      System.out.println("Gremlin will damage " + numDamagedPackets + " packets."); 
      
      ArrayList<Integer> list = new ArrayList<Integer>();
      for (int i = 1; i < 47; i++) { 
         list.add(Integer.valueOf(i));
         
      } 
      Collections.shuffle(list);
      
      ArrayList<Integer> newList = new ArrayList<Integer>();
      
      if (gremlinProbability < 1) { 
      
         for (int i = 1; i < numDamagedPackets + 1; i++) {
            newList.add(Integer.valueOf(list.get(i)));
         
         }
      }
            
   
      String http = "GET TestFile.html HTTP/1.0";
      sendData = http.getBytes();
     
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 10049);
      clientSocket.send(sendPacket);
   
      
      String savedAs = "Output";
   
      while (lastMessage != 0) {
      
         byte[] receiveData = new byte[256];
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         clientSocket.receive(receivePacket);
      
         byte[] sentence = receivePacket.getData();
        
         if (newList.contains((messagesReceived - 2)/2)) { 
            System.out.println("ATTACKING: " + (messagesReceived - 2)/2);
            sentence = gremlin(gremlinProbability, sentence);
           
         }
         
         messagesReceived++;
         
         detectError(sentence);
         
      
      
         int i = 0;
         while (lastMessage != 0 && i < sentence.length - 1) {
            lastMessage = sentence[i];
            i++;
         }
         
         if (count > 1 && lastMessage != 0) {
            message = new String(sentence);
            String elimHeader = message.substring(message.indexOf(":") + 11);
            
            content = content.concat(elimHeader);
         }
         count++;
         messagesReceived++;
      }
   
      System.out.println("\nMessage received: \n" + content);
   
      clientSocket.close();
      
      try {
         PrintWriter writer = new PrintWriter("Output.txt", "UTF-8");
         writer.println(content);
         writer.close();
      } catch (IOException exception) {
         System.out.println("Could not save file.");
      }
      System.out.println("File saved as 'Output'.");
   }


   public static String getSum(byte[] data) { 
      String checkSum = "";
      boolean var;
      int test = 0;
   
      byte[] byteCheckSum = new byte[5];
      String info = new String(data);
      int index = info.indexOf(":") + 2;
      int i = info.indexOf(":") + 2;
      int j = 0;
      
      
      while (i < index + 5) { 
         byteCheckSum[j] = data[i];
         j++;
         i++;
      }
      checkSum = new String(byteCheckSum);
      
     
      while (test != 1) {
         var = checkSum.startsWith("0");
         if (var) {
            checkSum = checkSum.substring(1);
            break;
         }
         test = 1;
      }
      return checkSum;
   }


   
   public static byte[] gremlin(double damageProbability, byte[] byteArray) { 
      Random random = new Random();
      
      
      int changeNum1 = random.nextInt(2); 
      int changeNum2 = random.nextInt(9) + 1;
      int byteNum1 = random.nextInt(byteArray.length);
      int byteNum2 = random.nextInt(byteArray.length);
      int byteNum3 = random.nextInt(byteArray.length);
      
      int numDamagedPackets = (int) Math.rint(damageProbability * 46);
      
      
      if (packetsDamaged <= numDamagedPackets + 1) { 
         if (changeNum1 == 1) {
            byteArray[byteNum1+5] = (byte) ~byteArray[byteNum1];
            
         }
         else if (changeNum2 <= 3) {
            byteArray[byteNum1] = (byte) ~byteArray[byteNum1];
            byteArray[byteNum2] = (byte) ~byteArray[byteNum2];
            
         }
         else {
            byteArray[byteNum1] = (byte) ~byteArray[byteNum1];
            byteArray[byteNum2] = (byte) ~byteArray[byteNum2];
            byteArray[byteNum3] = (byte) ~byteArray[byteNum3];
           
         }
         packetsDamaged++;
         System.out.println("\nGREMLIN ATTACK NUMBER " + packetsDamaged);
         
         
      }
      return byteArray;
   }
   
   

   public static int checkSum(byte[] data) {
      int checkSum = 0;
      int i = 0;
      while (i < data.length) { 
         checkSum += data[i];
         i++;
      }
      return checkSum;
   
   }

   public static void detectError(byte[] data) {
      int checkSum;
   
      String sentence = new String(data);
      String rCheckSum = getSum(data);
      byte[] newHeader = new byte[256];
     
      String info = new String(data);
      int index = info.indexOf(":") + 1;
   
      for (int i = index + 1; i < index + 6; i++) {
         data[i] = 48;
      }
      newHeader = data;
      checkSum = checkSum(newHeader);
      
     
   
      if (rCheckSum.equals(Integer.toString(checkSum))) {
         System.out.println("\n" + sentence);
      } else {
        
         String packetInfo = new String(data);
         System.out.println("\nERROR DETECTED: ");
         System.out.println(sentence);
         
      }
     
   }
   
  
}
