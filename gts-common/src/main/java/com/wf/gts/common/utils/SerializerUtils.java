package com.wf.gts.common.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class SerializerUtils {
  
  public static byte[] serialize(Object obj) throws RuntimeException {
      try (
          ByteArrayOutputStream bos=new ByteArrayOutputStream();
          ObjectOutput objectOutput = new ObjectOutputStream(bos)){
          objectOutput.writeObject(obj);
          return bos.toByteArray();
      } catch (IOException e) {
          throw new RuntimeException("JAVA serialize error " + e.getMessage());
      }
     
  }

  
  public static <T> T deSerialize(byte[] param, Class<T> clazz) throws RuntimeException {
      try (
          ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param);
          ObjectInput input = new ObjectInputStream(arrayInputStream)){
          return (T) input.readObject();
      } catch (IOException | ClassNotFoundException e) {
          throw new RuntimeException("JAVA deSerialize error " + e.getMessage());
      }
  }

}
