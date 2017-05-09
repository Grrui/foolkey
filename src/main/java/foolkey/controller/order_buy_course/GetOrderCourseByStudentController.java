package foolkey.controller.order_buy_course;

import foolkey.controller.AbstractController;
import foolkey.pojo.root.bo.CourseTeacher.CourseTeacherBO;
import foolkey.pojo.root.bo.order_course.OrderInfoBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.bo.teacher.TeacherInfoBO;
import foolkey.pojo.root.vo.assistObject.CourseTypeEnum;
import foolkey.pojo.root.vo.assistObject.OrderStateEnum;
import foolkey.pojo.root.vo.assistObject.TechnicTagEnum;
import foolkey.pojo.root.vo.cacheDTO.TeacherAllInfoDTO;
import foolkey.pojo.root.vo.dto.CourseTeacherDTO;
import foolkey.pojo.root.vo.dto.OrderBuyCourseDTO;
import foolkey.pojo.root.vo.dto.StudentDTO;
import foolkey.pojo.send_to_client.OrderBuyCourseSTCDTO;
import foolkey.pojo.send_to_client.OrderBuyRewardSTCDTO;
import foolkey.tool.JSONHandler;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustcg on 2017/5/6.
 */
@Controller
@RequestMapping(value = "/order")
public class GetOrderCourseByStudentController extends AbstractController {

    @Autowired
    private OrderInfoBO orderInfoBO;
    @Autowired
    private StudentInfoBO studentInfoBO;
    @Autowired
    private TeacherInfoBO teacherInfoBO;
    @Autowired
    private CourseTeacherBO courseTeacherBO;

    @RequestMapping(value = "/getOrderCourseByStudent")
    public void execute(
            HttpServletRequest request,
//            @RequestParam("token") String token,
            HttpServletResponse response
    ) {
        //获取-解析明文JSON数据
        String clearText = request.getParameter("clearText");
        JSONObject clearJSON = JSONObject.fromObject(clearText);

        String orderStateStr = clearJSON.getString("orderStateEnum");
        OrderStateEnum orderStateEnum = OrderStateEnum.valueOf(orderStateStr);
        String token = clearJSON.getString("token");
        Integer pageNo = clearJSON.getInt("pageNo");
        Integer pageSize = clearJSON.getInt("pageSize");

        /**
         unPay, payed, applyRefund, agreeRefund, refundCompete,
         cancel, agreed, onClass, endClass,
         judged
         */
        //根据token，获取当前用户的id
        StudentDTO studentDTO = studentInfoBO.getStudentDTO(token);
        Long studentId = studentDTO.getId();

        try {
            //********** 悬赏 *****************
            //按照订单状态，该学生下面的悬赏订单
            ArrayList<OrderBuyCourseDTO> orderBuyCourseDTOS_Reward = orderInfoBO.getOrderBuyCourseDTOAsStudent(studentId, CourseTypeEnum.学生悬赏,orderStateEnum,pageNo,pageSize);
            //封装悬赏订单
            ArrayList<OrderBuyRewardSTCDTO> orderBuyRewardSTCDTOS = orderInfoBO.convertOrderBuyCourseDTOIntoOrderBuyRewardSTCDTO(orderBuyCourseDTOS_Reward);

            //******** 课程 *****************
            //按照订单状态，该学生下面的课程订单
            ArrayList<OrderBuyCourseDTO> orderBuyCourseDTOS_Course = orderInfoBO.getOrderBuyCourseDTOAsStudent(studentId, CourseTypeEnum.老师课程,orderStateEnum,pageNo,pageSize);
            //封装课程订单
            ArrayList<OrderBuyCourseSTCDTO> orderBuyCourseSTCDTOS = orderInfoBO.convertOrderBuyCourseDTOIntoOrderBuyCourseSTCDTO(orderBuyCourseDTOS_Course);

            //封装、传送JSON
            jsonObject.put("result", "success");
            jsonObject.put("orderBuyRewardSTCDTOS", orderBuyRewardSTCDTOS);
            jsonObject.put("orderBuyCourseSTCDTOS", orderBuyCourseSTCDTOS);
            jsonHandler.sendJSON(jsonObject, response);
        } catch (Exception e) {
            e.printStackTrace();
            jsonHandler.sendFailJSON(response);
        }
    }

}