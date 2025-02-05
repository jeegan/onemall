package cn.iocoder.mall.order.biz.service;

import cn.iocoder.mall.order.api.OrderCommentReplyService;
import cn.iocoder.mall.order.api.bo.OrderCommentMerchantReplyBO;
import cn.iocoder.mall.order.api.bo.OrderCommentReplyCreateBO;
import cn.iocoder.mall.order.api.bo.OrderCommentReplyPageBO;
import cn.iocoder.mall.order.api.constant.OrderCommentRelpyTypeEnum;
import cn.iocoder.mall.order.api.constant.OrderReplyUserTypeEnum;
import cn.iocoder.mall.order.api.dto.OrderCommentReplyCreateDTO;
import cn.iocoder.mall.order.api.dto.OrderCommentReplyPageDTO;
import cn.iocoder.mall.order.biz.convert.OrderCommentReplyConvert;
import cn.iocoder.mall.order.biz.dao.OrderCommentReplayMapper;
import cn.iocoder.mall.order.biz.dataobject.OrderCommentReplyDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * 订单评论回复 service impl
 *
 * @author wtz
 * @time 2019-05-31 18:30
 */
@Service
@org.apache.dubbo.config.annotation.Service(validation = "true",version = "${dubbo.provider.OrderCommentReplyService.version}")
public class OrderCommentReplyServiceImpl implements OrderCommentReplyService {

    @Autowired
    private OrderCommentReplayMapper orderCommentReplayMapper;

    /**
     * 分页获取评论回复
     * @param orderCommentReplyPageDTO
     * @return
     */
    @Override
    public OrderCommentReplyPageBO getOrderCommentReplyPage(OrderCommentReplyPageDTO orderCommentReplyPageDTO) {
        OrderCommentReplyPageBO orderCommentReplyPageBO=new OrderCommentReplyPageBO();
        //评论回复总数
        Integer totalCount=orderCommentReplayMapper.selectCommentReplyTotalCountByCommentId(orderCommentReplyPageDTO.getCommentId(),
                orderCommentReplyPageDTO.getUserType());
        //分页获取评论回复
        List<OrderCommentReplyDO> orderCommentReplyDOList=orderCommentReplayMapper.selectCommentReplyPage(orderCommentReplyPageDTO);
        orderCommentReplyPageBO.setTotal(totalCount);
        orderCommentReplyPageBO.setOrderCommentReplayItems(OrderCommentReplyConvert.INSTANCE.convertOrderCommentReplayItem(orderCommentReplyDOList));
        return orderCommentReplyPageBO;
    }


    /**
     * 创建评论回复
     * @param orderCommentReplyCreateDTO
     * @return
     */
    @Override
    public OrderCommentReplyCreateBO createOrderCommentReply(OrderCommentReplyCreateDTO orderCommentReplyCreateDTO) {
        OrderCommentReplyDO orderCommentReplyDO=OrderCommentReplyConvert.INSTANCE.convert(orderCommentReplyCreateDTO);
        orderCommentReplyDO.setCreateTime(new Date());
        orderCommentReplyDO.setUpdateTime(new Date());

        Integer replyType=orderCommentReplyCreateDTO.getCommentId()==orderCommentReplyCreateDTO.getParentId()?
                OrderCommentRelpyTypeEnum.COMMENT_REPLY.getValue():OrderCommentRelpyTypeEnum.REPLY_REPLY.getValue();

        orderCommentReplyDO.setReplyType(replyType);

        orderCommentReplayMapper.insert(orderCommentReplyDO);

        return OrderCommentReplyConvert.INSTANCE.convert(orderCommentReplyDO);
    }

    /**
     * 获取商家评论回复
     * @param commentId
     * @return
     */
    @Override
    public List<OrderCommentMerchantReplyBO> getOrderCommentMerchantReply(Integer commentId) {
        List<OrderCommentReplyDO> orderCommentReplyDOList=orderCommentReplayMapper.selectCommentMerchantReplyByCommentIdAndUserType(commentId,
                OrderReplyUserTypeEnum.MERCHANT.getValue());
        return OrderCommentReplyConvert.INSTANCE.convert(orderCommentReplyDOList);
    }
}
