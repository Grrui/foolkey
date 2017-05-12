package foolkey.handler.course.haveClass;

import foolkey.pojo.root.bo.AbstractBO;
import foolkey.pojo.root.bo.Course.CourseBO;
import foolkey.pojo.root.bo.coupon.CouponInfoBO;
import foolkey.pojo.root.bo.order_course.OrderInfoBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.bo.teacher.TeacherInfoBO;
import foolkey.pojo.root.vo.assistObject.OrderStateEnum;
import foolkey.pojo.root.vo.dto.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * aes加密，下课
 * 需要token，orderId，studentId，hourNum（Float类型）
 * 修改订单状态，老师的授课时长、人次，学生的学习时长、上课次数
 * 删除订单里的优惠券
 *
 * 通过aop来进行老师的认证
 *
 * 给老师发钱放在评价完成后进行
 * Created by geyao on 2017/5/6.
 */
@Service
@Transactional
public class EndClassHandler extends AbstractBO {

    @Autowired
    private StudentInfoBO studentInfoBO;
    @Autowired
    private TeacherInfoBO teacherInfoBO;

    @Autowired
    private OrderInfoBO orderInfoBO;
    @Autowired
    private CouponInfoBO couponInfoBO;
    @Autowired
    private CourseBO courseTeacherBO;

    public void execute(
            HttpServletRequest request,
            HttpServletResponse response,
            JSONObject jsonObject
    )throws Exception{
        String clearText = request.getParameter("clearText").toString();
        JSONObject clearJSON = JSONObject.fromObject(clearText);

        //获取原始数据
        String token = clearJSON.getString("token");
        Long orderId = clearJSON.getLong("orderId");
        Long studentId = clearJSON.getLong("studentId");
        Float hourNum = Float.parseFloat( clearJSON.getString("hourNum") );

        //获取DTO
            //老师
        StudentDTO studentDTO = studentInfoBO.getStudentDTO(token);
        TeacherDTO teacherDTO = teacherInfoBO.getTeacherDTO( studentDTO.getId() );
            //学生
        StudentDTO student = studentInfoBO.getStudentDTO(studentId);
            //订单
        OrderBuyCourseDTO orderDTO = orderInfoBO.getCourseOrder(orderId + "");
        CouponDTO couponDTO = couponInfoBO.getCouponDTO(orderDTO.getCouponId());


        //修改订单状态
        orderDTO.setOrderStateEnum(OrderStateEnum.结束上课);
        //修改学生状态
        student.setLearningNumber( student.getLearningNumber() + 1);
        student.setLearningTime( student.getLearningTime() + hourNum );
        //修改老师状态
        teacherDTO.setTeachingNumber( teacherDTO.getTeachingNumber() + 1 );
        teacherDTO.setTeachingTime( teacherDTO.getTeachingTime() + hourNum );


        //保存订单、老师、学生
        orderInfoBO.update(orderDTO);
        studentInfoBO.updateStudent(student);
        teacherInfoBO.updateTeacherDTO(teacherDTO);

        //删除优惠券
        if (couponDTO != null){
            couponInfoBO.delete(couponDTO);
        }

        jsonObject.put("result", "success");
        jsonHandler.sendJSON(jsonObject, response);
    }
}
