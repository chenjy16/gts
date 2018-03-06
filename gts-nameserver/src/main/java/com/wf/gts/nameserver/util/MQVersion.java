package com.wf.gts.nameserver.util;

public class MQVersion {
  
  public static final int CURRENT_VERSION = Version.V4_2_0_SNAPSHOT.ordinal();

  public static String getVersionDesc(int value) {
      int length = Version.values().length;
      if (value >= length) {
          return Version.values()[length - 1].name();
      }

      return Version.values()[value].name();
  }

  public static Version value2Version(int value) {
      int length = Version.values().length;
      if (value >= length) {
          return Version.values()[length - 1];
      }

      return Version.values()[value];
  }

  public enum Version {
      V3_0_11,
      V4_2_0_SNAPSHOT,
      V4_2_0,

      HIGHER_VERSION
      
  }

}
