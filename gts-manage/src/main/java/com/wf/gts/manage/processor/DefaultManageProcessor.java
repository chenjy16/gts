package com.wf.gts.manage.processor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.manage.ManageController;
import com.wf.gts.manage.domain.Address;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.header.AddTransRequestHeader;
import com.wf.gts.remoting.header.FindTransGroupStatusRequestHeader;
import com.wf.gts.remoting.header.FindTransGroupStatusResponseHeader;
import com.wf.gts.remoting.header.PreCommitRequestHeader;
import com.wf.gts.remoting.header.RollBackTransGroupRequestHeader;
import com.wf.gts.remoting.netty.NettyRequestProcessor;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;
import io.netty.channel.ChannelHandlerContext;


public class DefaultManageProcessor implements NettyRequestProcessor {
  
   private static final Logger log = LoggerFactory.getLogger(DefaultManageProcessor.class);
   
   private ManageController manageController;
   
   public DefaultManageProcessor(ManageController manageController) {
          this.manageController = manageController;
   }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
        switch (request.getCode()) {
            case RequestCode.SAVE_TRANSGROUP:
              return createGroup(ctx, request);
            case RequestCode.ADD_TRANS:
              return addTrans(ctx, request);
            case RequestCode.FIND_TRANSGROUP_STATUS:
              return getTransactionGroupStatus(ctx, request);
            case RequestCode.PRE_COMMIT_TRANS: 
              return preCommit(ctx, request);
            case RequestCode.COMMIT_TRANS:
              return completeCommit(ctx, request);
            case RequestCode.ROLLBACK_TRANSGROUP:
              return rollback(ctx, request);
            default:
                break;
        }
        return null;
    }

    
    
    /**
     * 功能描述: 创建事务组
     * @author: chenjy
     * @date: 2018年3月14日 下午2:22:54 
     * @param ctx
     * @param request
     * @return
     * @throws RemotingCommandException
     */
    private RemotingCommand createGroup(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
      RemotingCommand response = RemotingCommand.createResponseCommand(null);  
      try {
          byte[] body = request.getBody();
              TxTransactionGroup tx =RemotingSerializable.decode(body, TxTransactionGroup.class);
              if (CollectionUtils.isNotEmpty(tx.getItemList())) {
                  String modelName = ctx.channel().remoteAddress().toString();
                  //这里创建事务组的时候，事务组也作为第一条数据来存储,第二条数据才是发起方因此是get(1)
                  TxTransactionItem item = tx.getItemList().get(1);
                  item.setModelName(modelName);
                  item.setTmDomain(Address.getInstance().getDomain());
              }
              Boolean success = manageController.getTxManagerService().saveTxTransactionGroup(tx);
              if(success){
                response.setCode(ResponseCode.SUCCESS);
            
              }else{
                response.setCode(ResponseCode.SYSTEM_ERROR);
              }
        } catch (Exception e) {
            response.setCode(ResponseCode.SYSTEM_ERROR);
            log.error("创建事务组异常:{}", e);
        }
        return response;
    }
    
    
    
    
    /**
     * 功能描述: 查找事务状态
     * @author: chenjy
     * @date: 2018年3月14日 下午3:05:45 
     * @param ctx
     * @param request
     * @return
     * @throws RemotingCommandException
     */
    private RemotingCommand getTransactionGroupStatus(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
        RemotingCommand response = RemotingCommand.createResponseCommand(FindTransGroupStatusResponseHeader.class);
        try {
          FindTransGroupStatusResponseHeader resHeader=(FindTransGroupStatusResponseHeader)response.readCustomHeader();
          FindTransGroupStatusRequestHeader header=(FindTransGroupStatusRequestHeader)request.decodeCommandCustomHeader(FindTransGroupStatusRequestHeader.class);
          int status = manageController.getTxManagerService().findTxTransactionGroupStatus(header.getTxGroupId());
          resHeader.setStatus(status);
          response.setCode(ResponseCode.SUCCESS);
        } catch (Exception e) {
          response.setCode(ResponseCode.SYSTEM_ERROR);
          e.printStackTrace();
        }
        return response;
    }
    
    
    
   /**
    * 功能描述: 增加事务信息
    * @author: chenjy
    * @date: 2018年3月14日 下午3:30:53 
    * @param ctx
    * @param request
    * @return
    * @throws RemotingCommandException
    */
   private RemotingCommand addTrans(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
      RemotingCommand response = RemotingCommand.createResponseCommand(null);
      AddTransRequestHeader header=(AddTransRequestHeader)request.decodeCommandCustomHeader(AddTransRequestHeader.class);
      try {
          byte[] body = request.getBody();
          TxTransactionItem item =RemotingSerializable.decode(body, TxTransactionItem.class);
          item.setModelName(ctx.channel().remoteAddress().toString());
          item.setTmDomain(Address.getInstance().getDomain());
          Boolean success =  manageController.getTxManagerService().addTxTransaction(header.getTxGroupId(), item);
          if(success){
            response.setCode(ResponseCode.SUCCESS);
          }else{
            response.setCode(ResponseCode.SYSTEM_ERROR);
          }
      } catch (Exception e) {
        response.setCode(ResponseCode.SYSTEM_ERROR);
        e.printStackTrace();
      }
      return response;
  }
    
   
   
   /**
    * 功能描述: 通知整个事务回滚
    * @author: chenjy
    * @date: 2018年3月14日 下午3:36:31 
    * @param ctx
    * @param request
    * @return
    * @throws RemotingCommandException
    */
   private RemotingCommand rollback(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
     
     RemotingCommand response = RemotingCommand.createResponseCommand(null);
     RollBackTransGroupRequestHeader header=(RollBackTransGroupRequestHeader)request.decodeCommandCustomHeader(RollBackTransGroupRequestHeader.class);
     try {
      //????此处需要考虑一下如何更好处理
       //收到客户端的回滚通知  此通知为事务发起（start）里面通知的,发送给其它的客户端
       manageController.getTxTransactionExecutor().rollBack(header.getTxGroupId(),manageController);
       response.setCode(ResponseCode.SUCCESS);
     } catch (Exception e) {
      response.setCode(ResponseCode.SYSTEM_ERROR);
      e.printStackTrace();
    }
     return response;
   }
   
   
   
   
   
   /**
    * 功能描述: 预提交
    * @author: chenjy
    * @date: 2018年3月15日 上午9:06:22 
    * @param ctx
    * @param request
    * @return
    * @throws RemotingCommandException
    */
   private RemotingCommand preCommit(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
     RemotingCommand response = RemotingCommand.createResponseCommand(null);
     PreCommitRequestHeader header=(PreCommitRequestHeader)request.decodeCommandCustomHeader(PreCommitRequestHeader.class);
     //????此处需要考虑一下如何更好处理
     try {
       manageController.getTxTransactionExecutor().preCommit(header.getTxGroupId(),manageController);
       response.setCode(ResponseCode.SUCCESS);
    } catch (Exception e) {
      response.setCode(ResponseCode.SYSTEM_ERROR);
      e.printStackTrace();
    }
     return response;
   }
   
   
   
   /**
    * 功能描述: 提交事务
    * @author: chenjy
    * @date: 2018年3月14日 下午3:39:38 
    * @param ctx
    * @param request
    * @return
    * @throws RemotingCommandException
    */
   private RemotingCommand completeCommit(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
     RemotingCommand response = RemotingCommand.createResponseCommand(null);
     byte[] body = request.getBody();
     try {
         TxTransactionGroup tx =RemotingSerializable.decode(body, TxTransactionGroup.class);
         if (CollectionUtils.isNotEmpty(tx.getItemList())) {
           manageController.getTxManagerService().updateTxTransactionItemStatus(tx.getId(), tx.getItemList().get(0).getTaskKey(),tx.getItemList().get(0).getStatus());
         }
         response.setCode(ResponseCode.SUCCESS);
      } catch (Exception e) {
          response.setCode(ResponseCode.SYSTEM_ERROR);
          e.printStackTrace();
      }
     return response;
   }
   
   
    @Override
    public boolean rejectRequest() {
        return false;
    }

  
}
