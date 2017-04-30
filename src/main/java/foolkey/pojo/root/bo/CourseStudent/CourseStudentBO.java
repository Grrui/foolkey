package foolkey.pojo.root.bo.CourseStudent;

import foolkey.pojo.root.DAO.course_student.SaveCourseStudentDAO;
import foolkey.pojo.root.DAO.course_teacher.SaveCourseTeacherDAO;
import foolkey.pojo.root.vo.dto.CourseStudentDTO;
import foolkey.pojo.root.vo.dto.CourseTeacherDTO;
import foolkey.tool.cache.Cache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ustcg on 2017/4/30.
 */
@Service("courseStudentBO")
public class CourseStudentBO {

    @Resource(name = "localCache")
    private Cache cache;

    @Resource(name = "saveCourseStudentDAO")
    private SaveCourseStudentDAO saveCourseStudentDAO;

    /**
     * 发布悬赏
     * @param courseStudentDTO
     */
    public void publishCourseStudent(CourseStudentDTO courseStudentDTO){
        if(courseStudentDTO == null){
            throw new NullPointerException("课程内容为空");
        }else{
            saveCourseStudentDAO.save(courseStudentDTO);
        }
    }
}
