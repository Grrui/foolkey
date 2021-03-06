package foolkey.pojo.root.bo.pay_order;

import foolkey.pojo.root.bo.coupon.CouponInfoBO;
import foolkey.pojo.root.bo.coupon.UseCouponBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.vo.assistObject.OrderStateEnum;
import foolkey.pojo.root.vo.assistObject.PayResultEnum;
import foolkey.pojo.root.vo.dto.*;
import foolkey.tool.StaticVariable;
import foolkey.tool.constant_values.MoneyRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 从账户扣款
 * 不对订单，账户，做任何修改
 * Created by geyao on 2017/5/1.
 */
@Service
@Transactional
public class PayBO {
    @Autowired
    private StudentInfoBO studentInfoBO;
    //    @Autowired
//    private OrderInfoBO orderBO;
    @Autowired
    private CouponInfoBO couponInfoBO;
    @Autowired
    private UseCouponBO useCouponBO;

    public Boolean pay(StudentDTO studentDTO, OrderBuyCourseDTO order, CouponDTO couponDTO, Double expectPrice) {
        Double money = studentDTO.getVirtualCurrency();
        Double totalPrice = order.getAmount();

        if (order.getCouponId() != null)
            totalPrice = totalPrice - couponDTO.getValue();
        //再次计算预期价格，是否相等
        if (!totalPrice.equals(expectPrice))
            return false;
        totalPrice = totalPrice * order.getCutOffPercent();
        if (money > totalPrice) {
            money = money - totalPrice;
            if (money < 0)
                //钱不够
                return false;
            studentDTO.setVirtualCurrency(money);
            order.setOrderStateEnum(OrderStateEnum.同意上课);
            return true;
        }
        return false;
    }

    public Boolean payForReward(StudentDTO studentDTO, RewardDTO courseDTO, CouponDTO couponDTO) {
        Double money = studentDTO.getVirtualCurrency();
        Double totalPrice = courseDTO.getPrice();

        if (couponDTO != null)
            totalPrice = totalPrice - couponDTO.getValue();
        if (money > totalPrice) {
            money = money - totalPrice;
            studentDTO.setVirtualCurrency(money);
            return true;
        }
        return false;
    }

    /**
     * 提问者：对自己的提问付钱
     * 1. 判断券能用否
     * 2. 判断够不够用的级别
     * 3. 判断余额够不够
     *
     * @param studentDTO
     * @param orderAskQuestionDTO
     * @return
     */
    public PayResultEnum payForQuestionAsAsker(StudentDTO studentDTO, OrderAskQuestionDTO orderAskQuestionDTO, CouponDTO couponDTO) throws Exception {
        //余额
        Double money = studentDTO.getVirtualCurrency();
        //提问价格
        Double cost = orderAskQuestionDTO.getAmount();

        //假如用了券
        if (couponDTO != null) {
            //判断券是否可用
            if (useCouponBO.userCouponForQuestion(studentDTO, orderAskQuestionDTO, couponDTO) == null) {
                return PayResultEnum.notUseCoupon;
            } else {
                cost = cost - couponDTO.getValue();
            }
        }

        //余额不足
        if (money.compareTo(cost) < 0) {
            return PayResultEnum.notEnoughBalance;
        } else {
            money -= cost;
            //更新学生余额信息
            studentDTO.setVirtualCurrency(money);
            studentInfoBO.updateStudent(studentDTO);
            return PayResultEnum.success;
        }

    }

    /**
     * 围观者：对要围观的问题付钱
     * 1. 判断券能用否
     * 2. 判断够不够用的级别
     * 3. 判断余额够不够
     *
     * @param onlookerDTO
     * @param couponDTO
     * @return
     */
    public PayResultEnum payForAnswerAsOnlooker(StudentDTO onlookerDTO, StudentDTO askerDTO, StudentDTO answererDTO, CouponDTO couponDTO) throws Exception {
        //余额
        Double virtualCurrency = onlookerDTO.getVirtualCurrency();
        //提问价格
        Double cost = StaticVariable.ONLOOK_VIRTUAL_PRICE;

        //假如用了券
        if (couponDTO != null) {
            //判断券是否可用
            if (useCouponBO.userCouponForOnlook(onlookerDTO, couponDTO)) {
                return PayResultEnum.notUseCoupon;
            } else {
                cost = cost - couponDTO.getValue();
            }
        }

        //余额不足
        if (virtualCurrency.compareTo(cost) < 0) {
            return PayResultEnum.notEnoughBalance;
        } else {

            virtualCurrency -= cost;
            //更新围观者余额信息
            onlookerDTO.setVirtualCurrency(virtualCurrency);
            studentInfoBO.updateStudent(onlookerDTO);
            //更新提问者余额
            askerDTO.setCash(askerDTO.getCash() + cost * MoneyRate.ASKER_GET_MONEY_RATE_OF_ONLOOK / MoneyRate.VIRTUAL_REAL_RATE);
            studentInfoBO.updateStudent(askerDTO);
            //更新回答者余额
            answererDTO.setCash(answererDTO.getCash() + cost * MoneyRate.ANSWERER_GET_MONEY_RATE_OF_ONLOOK / MoneyRate.VIRTUAL_REAL_RATE);
            studentInfoBO.updateStudent(answererDTO);
            return PayResultEnum.success;
        }

    }
}
