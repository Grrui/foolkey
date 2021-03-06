package foolkey.controller.course.Reward;

import foolkey.controller.AbstractController;
import foolkey.pojo.root.bo.Reward.RewardBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.vo.assistObject.*;
import foolkey.pojo.root.vo.dto.RewardDTO;
import foolkey.pojo.root.vo.dto.StudentDTO;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ustcg on 2017/5/6.
 */
@Controller(value = "RewardUpdate")
@RequestMapping(value = "/aes/reward/update")
public class UpdateController extends AbstractController{

    @Autowired
    private StudentInfoBO studentInfoBO;
    @Autowired
    private RewardBO courseStudentBO;


    @RequestMapping
    public void execute(
            HttpServletRequest request,
//            @RequestParam("id")Long id,
//            @RequestParam("technicTagEnum")TechnicTagEnum technicTagEnum,
//            @RequestParam("topic")String topic,
//            @RequestParam("description")String description ,
//            @RequestParam("price")Double price,
//            @RequestParam("courseTimeDayEnum")CourseTimeDayEnum courseTimeDayEnum,
//            @RequestParam("teachMethodEnum")TeachMethodEnum teachMethodEnum,
//            @RequestParam("teacherRequirementEnum")TeacherRequirementEnum teacherRequirementEnum,
//            @RequestParam("studentBaseEnum")StudentBaseEnum studentBaseEnum,
            HttpServletResponse response
    ) throws Exception {
        try {
            //获取-解析JSON明文数据
            String clearText = request.getAttribute("clearText").toString();
            JSONObject clearJSON = JSONObject.fromObject(clearText);

            String token =clearJSON.getString("token");
            Long id = clearJSON.getLong("rewardId");
            String technicTagStr = clearJSON.getString("technicTagEnum");
            TechnicTagEnum technicTagEnum = TechnicTagEnum.valueOf(technicTagStr);
            String topic = clearJSON.getString("topic");
            String description = clearJSON.getString("description");
            Double price = clearJSON.getDouble("price");
            String courseTimeDayStr = clearJSON.getString("courseTimeDayEnum");
            CourseTimeDayEnum courseTimeDayEnum = CourseTimeDayEnum.valueOf(courseTimeDayStr);
            String teachMethodStr = clearJSON.getString("teachMethodEnum");
            TeachMethodEnum teachMethodEnum = TeachMethodEnum.valueOf(teachMethodStr);
            String teacherRequirementStr = clearJSON.getString("teacherRequirementEnum");
            TeacherRequirementEnum teacherRequirementEnum = TeacherRequirementEnum.valueOf(teacherRequirementStr);
            String studentBaseStr = clearJSON.getString("studentBaseEnum");
            StudentBaseEnum studentBaseEnum = StudentBaseEnum.valueOf(studentBaseStr);


            //根据id获取旧的悬赏信息
            RewardDTO courseStudentDTO = courseStudentBO.getCourseStudentDTOById(id);

            //对悬赏赋新值
            StudentDTO studentDTO = studentInfoBO.getStudentDTO(token);
            courseStudentDTO.setCreatorId(studentDTO.getId());
//            courseStudentDTO.setCreatorId(20001L);

            courseStudentDTO.setTechnicTagEnum(technicTagEnum);
            courseStudentDTO.setTopic(topic);
            courseStudentDTO.setDescription(description);
            courseStudentDTO.setPrice(price);
            courseStudentDTO.setCourseTimeDayEnum(courseTimeDayEnum);
            courseStudentDTO.setTeachMethodEnum(teachMethodEnum);
            courseStudentDTO.setTeacherRequirementEnum(teacherRequirementEnum);
            courseStudentDTO.setStudentBaseEnum(studentBaseEnum);

            courseStudentBO.update(courseStudentDTO);

            //封装-传送JSON
            jsonObject.put("result","success");
            jsonObject.put("courseStudentDTO",courseStudentDTO);
            jsonHandler.sendJSON(jsonObject,response);
        }catch (Exception e){
            e.printStackTrace();
            jsonHandler.sendFailJSON(response);
        }
    }
}
