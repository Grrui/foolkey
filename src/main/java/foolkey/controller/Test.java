package foolkey.controller;

import com.alibaba.fastjson.JSON;
import foolkey.pojo.root.bo.order.OrderInfoBO;
import foolkey.pojo.root.vo.assistObject.RoleEnum;
import foolkey.pojo.root.vo.dto.StudentDTO;
import foolkey.pojo.root.vo.dto.TeacherDTO;
import foolkey.pojo.send_to_client.OrderBuyCourseAsStudentDTO;

import java.util.Date;

/**
 * Created by GR on 2017/5/13.
 */

public class Test {

    private OrderInfoBO orderInfoBO;

//    @RequestMapping
//    public void execute(HttpServletResponse response){
//        JSONHandler jsonHandler = new JSONHandler();
//        JSONObject jsonObject = new JSONObject();
//        Long time1 = System.currentTimeMillis();
//        List list = orderInfoBO.test();
//        jsonObject.put("1", orderInfoBO.test());
//        jsonHandler.sendJSON(jsonObject, response);
//    }

    public static void main(String[] args) throws Exception{
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setNickedName("昵称");
        studentDTO.setRoleEnum(RoleEnum.student);
        studentDTO.setBirthday(new Date());
        String jsonString = JSON.toJSONString(studentDTO);
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("student", studentDTO);
        System.out.println(jsonObject);
        System.out.println(jsonString);

        StudentDTO studentDTO1 = JSON.parseObject( jsonString, StudentDTO.class);
        System.out.println(studentDTO1);
        studentDTO1.setBirthday(new Date() );
        System.out.println(studentDTO1);
        System.out.println();

        OrderBuyCourseAsStudentDTO orderBuyCourseAsStudentDTO = new OrderBuyCourseAsStudentDTO();
        orderBuyCourseAsStudentDTO.setStudentDTO(studentDTO);
        orderBuyCourseAsStudentDTO.setTeacherDTO(new TeacherDTO());
        String orderStr = JSON.toJSONString(orderBuyCourseAsStudentDTO);
        System.out.println(orderBuyCourseAsStudentDTO);
        System.out.println(orderStr);
        System.out.println( JSON.parseObject(orderStr, OrderBuyCourseAsStudentDTO.class));
    }
}
