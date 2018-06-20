package com.wf.gts.core.lb;
import java.security.MessageDigest;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import com.wf.gts.remoting.protocol.LiveManageInfo;



public class Selector {

  private final int hashcode;


  /**
   * 虚拟节点
   */
  private final TreeMap<Long, LiveManageInfo> virtualNodes;


  public Selector(List<LiveManageInfo> actualNodes) {
    this(actualNodes, actualNodes.hashCode());
  }


  public Selector(List<LiveManageInfo> actualNodes, int hashcode) {
      this.hashcode = hashcode;
      // 创建虚拟节点环 （默认一个provider共创建128个虚拟节点，较多比较均匀）
      this.virtualNodes = new TreeMap<Long, LiveManageInfo>();
      int num = 128;
      for (LiveManageInfo liveManageInfo : actualNodes) {
          for (int i = 0; i < num / 4; i++) {
              byte[] digest = messageDigest(liveManageInfo.getGtsManageLiveAddr().getGtsManageAddr() + liveManageInfo.getGtsManageLiveAddr().getGtsManageId() + i);
              for (int h = 0; h < 4; h++) {
                long m = hash(digest, h);
                virtualNodes.put(m, liveManageInfo);
              }
          }
      }
  }

  
  /**
   * 功能描述: <br>
   * @author: chenjy
   * @date: 2018年6月19日 下午3:36:44 
   * @param ip
   * @return
   */
  public LiveManageInfo select(String clientIp) {
    byte[] digest = messageDigest(clientIp);
    return sekectForKey(hash(digest, 0));
  }

  
  
  /**
   * 功能描述: <br>
   * @author: chenjy
   * @date: 2018年6月19日 下午3:32:52 
   * @param hash
   * @return
   */
  private LiveManageInfo sekectForKey(long hash) {
      LiveManageInfo liveManageInfo = virtualNodes.get(hash);
      if (liveManageInfo == null) {
        SortedMap<Long, LiveManageInfo> tailMap = virtualNodes.tailMap(hash);
        if (tailMap.isEmpty()) {
          hash = virtualNodes.firstKey();
        } else {
          hash = tailMap.firstKey();
        }
        liveManageInfo = virtualNodes.get(hash);
      }
      return liveManageInfo;
  }

  
  
  /**
   * 功能描述: 生成16个byte数组
   * @author: chenjy
   * @date: 2018年6月19日 下午3:31:24 
   * @param value
   * @return
   */
  private byte[] messageDigest(String value) {
    MessageDigest md5;
    try {
        md5 = MessageDigest.getInstance("MD5");
        md5.update(value.getBytes("UTF-8"));
        return md5.digest();
    } catch (Exception e) {
        throw new RuntimeException();
    }
  }


  
  /**
   * 功能描述: 非负整数
   * @author: chenjy
   * @date: 2018年6月19日 下午3:31:44 
   * @param digest
   * @param index
   * @return
   */
  private long hash(byte[] digest, int index) {
    long f = ((long) (digest[3 + index * 4] & 0xFF) << 24) | ((long) (digest[2 + index * 4] & 0xFF) << 16)| ((long) (digest[1 + index * 4] & 0xFF) << 8) | (digest[index * 4] & 0xFF);
    return f & 0xFFFFFFFFL;
  }


  public int getHashCode() {
    return hashcode;
  }

}
