package foolkey.controller.course.CourseStudent;

import foolkey.controller.AbstractController;
import foolkey.pojo.root.bo.CourseStudent.CourseStudentBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.vo.cacheDTO.CourseStudentPopularDTO;
import foolkey.pojo.root.vo.dto.CourseStudentDTO;
import foolkey.tool.StaticVariable;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Created by ustcg on 2017/5/8.
 */
@Controller
@RequestMapping(value = "/courseStudent")
public class GetMyPublishCourseStudentController extends AbstractController{

    @Autowired
    private StudentInfoBO studentInfoBO;
    @Autowired
    private CourseStudentBO courseStudentBO;

    @RequestMapping(value = "/getMyPublishCourseStudent")
    public void execute(
            HttpServletRequest request,
//            @RequestParam("pageNo")Integer pageNo,
//            @RequestParam("studentId")Long studentId,
            HttpServletResponse response
    ){
        try{
            // 获取-分析JSON传递的明文信息
            String clearText = request.getParameter("clearText");
            JSONObject clearJSON = JSONObject.fromObject(clearText);

            Integer pageNo = clearJSON.getInt("pageNo");
            String token = clearJSON.getString("token");
            Long studentId = studentInfoBO.getStudentDTO(token).getId();

            //获取我的所有悬赏
            ArrayList<CourseStudentDTO> courseStudentDTOS = courseStudentBO.getMyCourseStudentDTO(studentId, pageNo, StaticVariable.pageSize);
            for (CourseStudentDTO courseStudentDTO : courseStudentDTOS) {
                System.out.println("课程：" + courseStudentDTO + "--id:" + courseStudentDTO.getId());

            }

            //封装-传送json
            jsonObject.put("result","success");
            jsonObject.put("courseStudentDTOS",courseStudentDTOS);
            jsonHandler.sendJSON(jsonObject,response);


        }catch(Exception e){
            e.printStackTrace();
            jsonHandler.sendFailJSON(response);
        }
    }
}
