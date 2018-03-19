package com.wf.gts.remoting.core;


public class TopicSysFlag {

  private final static int FLAG_UNIT = 0x1 << 0;

  private final static int FLAG_UNIT_SUB = 0x1 << 1;

  public static int buildSysFlag(final boolean unit, final boolean hasUnitSub) {
      int sysFlag = 0;

      if (unit) {
          sysFlag |= FLAG_UNIT;
      }

      if (hasUnitSub) {
          sysFlag |= FLAG_UNIT_SUB;
      }

      return sysFlag;
  }

  public static int setUnitFlag(final int sysFlag) {
      return sysFlag | FLAG_UNIT;
  }

  public static int clearUnitFlag(final int sysFlag) {
      return sysFlag & (~FLAG_UNIT);
  }

  public static boolean hasUnitFlag(final int sysFlag) {
      return (sysFlag & FLAG_UNIT) == FLAG_UNIT;
  }

  public static int setUnitSubFlag(final int sysFlag) {
      return sysFlag | FLAG_UNIT_SUB;
  }

  public static int clearUnitSubFlag(final int sysFlag) {
      return sysFlag & (~FLAG_UNIT_SUB);
  }

  public static boolean hasUnitSubFlag(final int sysFlag) {
      return (sysFlag & FLAG_UNIT_SUB) == FLAG_UNIT_SUB;
  }



}
