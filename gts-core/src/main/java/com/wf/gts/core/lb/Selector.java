package com.wf.gts.core.lb;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import com.wf.gts.core.constant.Constant;
import com.wf.gts.remoting.protocol.GtsManageLiveAddr;



public class Selector {

  
  private final int hashcode;

  /**
   * 虚拟节点
   */
  private final TreeMap<Long, GtsManageLiveAddr> virtualNodes;


  public Selector(List<GtsManageLiveAddr>  actualNodes) {
    this(actualNodes, actualNodes.hashCode());
  }

  
  
  /**
   * @param actualNodes
   * @param hashcode
   */
  public Selector(List<GtsManageLiveAddr> actualNodes, int hashcode) {
      this.hashcode = hashcode;
      // 创建虚拟节点环 （默认一个provider共创建128个虚拟节点，较多比较均匀）
      this.virtualNodes = new TreeMap<Long, GtsManageLiveAddr>();
      actualNodes.forEach(gtsManageLiveAddr->{
            for (int i = 0; i < Constant.VIRTUAL_NODES/4; i++) {
                byte[] digest = messageDigest(gtsManageLiveAddr.getGtsManageAddr() + gtsManageLiveAddr.getGtsManageId() + i);
                for (int h = 0; h < 4; h++) {
                    long m = hash(digest, h);
                    virtualNodes.put(m, gtsManageLiveAddr);
                }
            }
      });
  }

  
  /**
   * 功能描述: 根据客户端ip选择对应的服务端
   * @author: chenjy
   * @date: 2018年6月20日 下午6:01:15 
   * @param clientIp
   * @return
   */
  public GtsManageLiveAddr select(String clientIp) {
    byte[] digest = messageDigest(clientIp);
    return sekectForKey(hash(digest, 0));
  }

  
  
  /**
   * 功能描述: 根据hash取出服务器信息
   * @author: chenjy
   * @date: 2018年6月19日 下午3:32:52 
   * @param hash
   * @return
   */
  private GtsManageLiveAddr sekectForKey(long hash) {
    
      GtsManageLiveAddr gtsManageLiveAddr = virtualNodes.get(hash);
      if (Objects.isNull(gtsManageLiveAddr)) {
          SortedMap<Long, GtsManageLiveAddr> tailMap = virtualNodes.tailMap(hash);
          if (tailMap.isEmpty()) {
            hash = virtualNodes.firstKey();
          } else {
            hash = tailMap.firstKey();
          }
          gtsManageLiveAddr = virtualNodes.get(hash);
      }
      return gtsManageLiveAddr;
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
          throw new RuntimeException("md5签名异常");
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
    long f = ((long) (digest[3 + index * 4] & 0xFF) << 24) |
              ((long) (digest[2 + index * 4] & 0xFF) << 16)| 
              ((long) (digest[1 + index * 4] & 0xFF) << 8) | 
              (digest[index * 4] & 0xFF);
    return f & 0xFFFFFFFFL;
  }


  public int getHashCode() {
    return hashcode;
  }

}
