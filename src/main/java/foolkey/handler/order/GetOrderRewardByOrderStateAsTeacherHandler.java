package foolkey.handler.order;

import foolkey.pojo.root.bo.AbstractBO;
import foolkey.pojo.root.bo.Reward.RewardBO;
import foolkey.pojo.root.bo.order.OrderInfoBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.vo.assistObject.CourseTypeEnum;
import foolkey.pojo.root.vo.assistObject.OrderStateEnum;
import foolkey.pojo.root.vo.dto.RewardDTO;
import foolkey.pojo.root.vo.dto.StudentDTO;
import foolkey.pojo.send_to_client.OrderBuyCourseWithStudentAsTeacherSTCDTO;
import foolkey.pojo.send_to_client.OrderBuyRewardAsTeacherSTCDTO;
import foolkey.tool.StaticVariable;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据输入的订单状态，获取悬赏订单信息
 * Created by GR on 2017/5/14.
 */
@Service
@Transactional
public class GetOrderRewardByOrderStateAsTeacherHandler extends AbstractBO {

    @Autowired
    private StudentInfoBO studentInfoBO;
    @Autowired
    private OrderInfoBO orderInfoBO;
    @Autowired
    private RewardBO rewardBO;

    public void execute(
            HttpServletRequest request,
            HttpServletResponse response,
            JSONObject jsonObject
    ){
        //获取-解析明文JSON数据
        String clearText = request.getParameter("clearText");
        JSONObject clearJSON = JSONObject.fromObject(clearText);

        String token = clearJSON.getString("token");
        Integer pageNo = clearJSON.getInt("pageNo");
        String orderStateStr = clearJSON.getString("orderStateEnum");
        OrderStateEnum orderStateEnum = OrderStateEnum.valueOf(orderStateStr);

        /**
         未付款, 已付款, 申请退款, 同意退款, 退款完成,
         取消课程, 同意上课, 上课中, 结束上课,
         上课完成
         */
        //根据token，获取当前老师的id(因为后面用到的信息在“学生”部分就可以获得，因此暂时使用学生DTO)
        StudentDTO studentDTO = studentInfoBO.getStudentDTO(token);
        Long teacherId = studentDTO.getId();

        //最后返回的东西
        List<OrderBuyRewardAsTeacherSTCDTO> orderBuyRewardAsTeacherSTCDTOS = new ArrayList<>();

        try {
            //********** 悬赏 *****************
            //1. 获得该老师下面哪些悬赏订单处于规定状态
            List<Long> rewardIdS = orderInfoBO.getOrderBuyCourseDTOAsTeacherByOrderStates(teacherId, CourseTypeEnum.学生悬赏,pageNo, StaticVariable.PAGE_SIZE, orderStateEnum);
            //2. 上面每个悬赏，获取下面的学生-订单信息
            for(Long rewardId:rewardIdS){
                OrderBuyRewardAsTeacherSTCDTO orderBuyRewardAsTeacherSTCDTO = new OrderBuyRewardAsTeacherSTCDTO();
                //      2.1获取悬赏DTO
                RewardDTO rewardDTO = rewardBO.getCourseStudentDTO(rewardId);
                orderBuyRewardAsTeacherSTCDTO.setRewardDTO(rewardDTO);
                //      2.2由悬赏id获取订单-学生DTOS
                List<OrderBuyCourseWithStudentAsTeacherSTCDTO> orderBuyCourseWithStudentAsTeacherSTCDTOS = orderInfoBO.getOrderBuyCourseWithStudentAsTeacher(rewardId, CourseTypeEnum.学生悬赏,1,4, orderStateEnum);
                orderBuyRewardAsTeacherSTCDTO.setOrderBuyCourseWithStudentAsTeacherSTCDTOS(orderBuyCourseWithStudentAsTeacherSTCDTOS);
                orderBuyRewardAsTeacherSTCDTOS.add(orderBuyRewardAsTeacherSTCDTO);
            }

            //封装、传送JSON
            jsonObject.put("result", "success");
            jsonObject.put("orderBuyRewardAsTeacherSTCDTOS", orderBuyRewardAsTeacherSTCDTOS);
            jsonHandler.sendJSON(jsonObject, response);
        } catch (Exception e) {
            e.printStackTrace();
            jsonHandler.sendFailJSON(response);
        }

    }


}
