package foolkey.pojo.root.bo.course;

import foolkey.pojo.root.CAO.Course.CourseCAO;
import foolkey.pojo.root.DAO.course_teacher.DeleteCourseTeacherDAO;
import foolkey.pojo.root.DAO.course_teacher.GetCourseTeacherDAO;
import foolkey.pojo.root.DAO.course_teacher.SaveCourseTeacherDAO;
import foolkey.pojo.root.DAO.course_teacher.UpdateCourseTeacherDAO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.bo.teacher.TeacherInfoBO;
import foolkey.pojo.root.vo.assistObject.*;
import foolkey.pojo.send_to_client.TeacherAllInfoDTO;
import foolkey.pojo.root.vo.dto.CourseDTO;
import foolkey.pojo.root.vo.dto.StudentDTO;
import foolkey.pojo.root.vo.dto.TeacherDTO;
import foolkey.pojo.send_to_client.course.CourseWithTeacherSTCDTO;
import foolkey.tool.BeanFactory;
import foolkey.tool.StaticVariable;
import foolkey.tool.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/26.
 */
@Service("courseTeacherBO")
public class CourseBO {

    @Autowired
    private Cache cache;

    @Autowired
    private CourseCAO courseCAO;

    @Autowired
    private GetCourseTeacherDAO getCourseTeacherDAO;

    @Autowired
    private SaveCourseTeacherDAO saveCourseTeacherDAO;

    @Autowired
    private UpdateCourseTeacherDAO updateCourseTeacherDAO;
    @Autowired
    private DeleteCourseTeacherDAO deleteCourseTeacherDAO;

    @Autowired
    private TeacherInfoBO teacherInfoBO;
    @Autowired
    private StudentInfoBO studentInfoBO;


    /**
     * 初始化，缓存中创建每个类别课程的空间
     */
    public void initCourseCache(){
        courseCAO.initCourseCache();
    }


    /**
     * 添加热门课程到缓存
     * @param technicTagEnum
     * @param size
     */
    public void fillCourseWithTeacherSTCDTOToCache(TechnicTagEnum technicTagEnum, Integer size){
        if(technicTagEnum == null || size == null || size == 0){
            throw new NullPointerException("technicTagEnum/size is null");
        }else {
            try {
                List<CourseDTO> courseDTOS = getCourseTeacherDAO.findByTechnicTagEnumAndResultSize(technicTagEnum, size);
                List<CourseWithTeacherSTCDTO> courseWithTeacherSTCDTOS = new ArrayList<>();
                for (CourseDTO courseDTO : courseDTOS) {
                    TeacherAllInfoDTO teacherAllInfoDTO = teacherInfoBO.getTeacherAllInfoDTO(courseDTO.getCreatorId());
                    System.out.println("老师的id"+courseDTO.getCreatorId());
                    System.out.println("老师的信息："+teacherAllInfoDTO);
                    CourseWithTeacherSTCDTO courseWithTeacherSTCDTO = new CourseWithTeacherSTCDTO();
                    courseWithTeacherSTCDTO.setCourseDTO(courseDTO);
                    courseWithTeacherSTCDTO.setTeacherAllInfoDTO(teacherAllInfoDTO);
                    courseWithTeacherSTCDTOS.add(courseWithTeacherSTCDTO);
                }
                if(courseDTOS.size()!=0) {
                    courseCAO.addCourseWithTeacherSToCache(technicTagEnum, courseWithTeacherSTCDTOS, DirectionEnum.tail);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取某个标签下流行的课程
     * @param technicTagEnum
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<CourseWithTeacherSTCDTO> getCourseWithTeacherSTCDTO(TechnicTagEnum technicTagEnum, Integer pageNo, Integer pageSize) throws Exception{
        //请求的内容超过内存大小
        //暂时不允许超过内存大小
        if((pageNo-1)*pageSize >= StaticVariable.CACHE_SIZE) {
            return new ArrayList<>();
        }else{
            return courseCAO.getCourseWithTeacherPopularFromCache(technicTagEnum, pageNo, pageSize);
        }
    }

    /**
     * 发布课程
     * @param courseDTO
     */
    public void publishCourseTeacher(CourseDTO courseDTO){
        if(courseDTO == null){
            throw new NullPointerException("课程内容为空");
        }else{
            saveCourseTeacherDAO.save(courseDTO);
        }
    }

    /**
     * 验证一个课程是否可以被购买
     * 课程状态、课程老师的认证、封禁状态
     * @param courseDTO
     * @return
     */
    public boolean  checkCourse(CourseDTO courseDTO){
        try {
            if (courseDTO.getCourseTeacherStateEnum()
                    .compareTo(CourseTeacherStateEnum.可上课) == 0){ // 课程
                System.out.println("课程ok " + courseDTO.getCreatorId());
                Long teacherId = courseDTO.getCreatorId();
                TeacherDTO teacherDTO = teacherInfoBO.getTeacherDTO(teacherId);
                if (teacherDTO.getVerifyState()
                        .compareTo(VerifyStateEnum.verified) == 0){ // 老师是否认证
                    System.out.println("老师认证ok");
                    StudentDTO studentDTO = studentInfoBO.getStudentDTO(teacherId);
                    if (studentDTO.getUserStateEnum()
                            .compareTo(UserStateEnum.normal) == 0 // 老师是否北封禁
                            && studentDTO.getRoleEnum()
                            .compareTo( RoleEnum.teacher) == 0){ // 学生是否是老师
                        System.out.println("老师状态ok");
                        return true;
                    }
                }else {
                    System.out.println("老师认证不对 " + teacherDTO.getVerifyState());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据课程id获取课程信息
     * @param id
     * @return
     * @throws Exception
     */
    public CourseDTO getCourseTeacherDTOById(Long id) throws Exception{
        if(id == null){
            throw new NullPointerException("id is null");
        }else{
            CourseWithTeacherSTCDTO courseWithTeacherSTCDTO = courseCAO.getCourseWithTeacherByCourseId(id);
            if(courseWithTeacherSTCDTO != null && courseWithTeacherSTCDTO.getCourseDTO() != null){
                System.out.println("缓存有！");
               return courseWithTeacherSTCDTO.getCourseDTO();
            }else{
                return getCourseTeacherDAO.get(CourseDTO.class,id);
            }
        }
    }

    public CourseDTO getCourseTeacherDTOById(String courseId) throws Exception {
        Long id = Long.parseLong(courseId);
        return getCourseTeacherDTOById(id);
    }


    /**
     * 根据课程id获取到课程-老师DTO
     * @param courseId
     * @return
     * @throws Exception
     */
    public CourseWithTeacherSTCDTO getCourseWithTeacherSTCDTOByCourseId(Long courseId) throws Exception {
        // 首先根据课程id获取课程DTO
        CourseDTO courseDTO = getCourseTeacherDTOById(courseId);
        // 获取所属老师的信息
        TeacherAllInfoDTO teacherAllInfoDTO = teacherInfoBO.getTeacherAllInfoDTO(courseDTO.getCreatorId());
        // 创建课程-老师DTO，并赋值
        CourseWithTeacherSTCDTO courseWithTeacherSTCDTO = new CourseWithTeacherSTCDTO();
        courseWithTeacherSTCDTO.setCourseDTO(courseDTO);
        courseWithTeacherSTCDTO.setTeacherAllInfoDTO(teacherAllInfoDTO);
        return courseWithTeacherSTCDTO;
    }

    /**
     * 修改缓存的课程信息
     * @param courseDTO
     * @return
     * @throws Exception
     */
    private void updateCourseTeacherCache(CourseDTO courseDTO) throws Exception {
        if(courseDTO == null){
            throw new NullPointerException("courseDTO is null");
        }else{
            //获取老师的所有信息
            TeacherAllInfoDTO teacherAllInfoDTO = BeanFactory.getBean("teacherAllInfoDTO",TeacherAllInfoDTO.class);

            //生成一个热门课程的DTO
            CourseWithTeacherSTCDTO courseWithTeacherSTCDTO = new CourseWithTeacherSTCDTO();
            courseWithTeacherSTCDTO.setCourseDTO(courseDTO);
            courseWithTeacherSTCDTO.setTeacherAllInfoDTO(teacherAllInfoDTO);

            /**
             * 要修改！暂时没动。改完cao方法在弄
             */
//            //获取要修改的课程所在的节点
//            Node node = courseCAO.getCourseTeacherPopularNodeByCourseTeacherDTOId(courseDTO.getId());
//            if(node != null) {
//                CourseWithTeacherSTCDTO oldCourseWithTeacherSTCDTO = (CourseWithTeacherSTCDTO) node.getData();
//                //判断是否修改了类别
//                if(oldCourseWithTeacherSTCDTO.getCourseTeacherDTO().getTechnicTagEnum() == courseDTO.getTechnicTagEnum()){
//                    System.out.println("没有修改技术类别---");
//                    node.setData(courseWithTeacherSTCDTO);
//                    System.out.println("秀秀秀");
//                }else{
//                    // 1. 删除当前类别的当前节点
//                    courseCAO.deleteCourseTeacherNode(courseWithTeacherSTCDTO);
//                    courseCAO.addCourseWithTeacherToCache(courseDTO.getTechnicTagEnum(),courseWithTeacherSTCDTO,DirectionEnum.head);
//                }
//
//            }

        }
    }

    /**
     * 更新课程
     * 先改数据库，再改缓存
     * @param courseDTO
     */
    public void update(CourseDTO courseDTO)throws Exception{
        updateCourseTeacherDTO( courseDTO );
        updateCourseTeacherCache( courseDTO);
    }

    /**
     * 更新数据库的内容
     * @param courseDTO
     * @throws Exception
     */
    private void updateCourseTeacherDTO(CourseDTO courseDTO) throws Exception{
        if(courseDTO == null){
            throw new NullPointerException("courseDTO is null");
        }else{
            updateCourseTeacherDAO.update(courseDTO);
        }
    }

    /**
     * 把一门课程从缓存的缓冲区添加到缓存的热门去
     * @param courseWithTeacherSTCDTO
     */
//    public void addCourseTeacherFromReserveToCache(CourseWithTeacherSTCDTO courseWithTeacherSTCDTO){
//        if(courseWithTeacherSTCDTO == null){
//            throw new NullPointerException("courseWithTeacherSTCDTO id null");
//        }else{
//            //1. 获取热门缓冲区的节点  删除
//            Node delNode = courseCAO.getCourseTeacherNodeInReserve(courseDTO);
//            courseCAO.deleteCourseTeacherNodeInReserve(courseDTO);
//            //2.添加节点到热门区
//            courseCAO.addCourseTeacherToCache(courseDTO);
//        }
//    }


    /**
     * 展示所有的热门课程
     * @param technicTagEnum
     */
    public List<CourseWithTeacherSTCDTO> getAllCourseWithTeacherSTCDTO(TechnicTagEnum technicTagEnum){
        if(technicTagEnum == null){
            throw new NullPointerException("technicTagEnum is null");
        }else{
            return courseCAO.getAllCourseWithTeacherPopularFromCache(technicTagEnum);
        }
    }

    /**
     * 把课程转变为课程-老师
     * @param courseDTOS
     * @return
     * @throws Exception
     */
    public ArrayList convertCourseDTOIntoCourseWithTeacherDTO(List<CourseDTO> courseDTOS) throws Exception{

        ArrayList<CourseWithTeacherSTCDTO> courseWithTeacherSTCDTOS = new ArrayList<>();
        for(CourseDTO courseDTO:courseDTOS){
            CourseWithTeacherSTCDTO courseWithTeacherSTCDTO = new CourseWithTeacherSTCDTO();
            // 获取-添加老师信息
            TeacherAllInfoDTO teacherAllInfoDTO = teacherInfoBO.getTeacherAllInfoDTO(courseDTO.getCreatorId());
            courseWithTeacherSTCDTO.setTeacherAllInfoDTO(teacherAllInfoDTO);
            // 课程信息赋值
            courseWithTeacherSTCDTO.setCourseDTO(courseDTO);
            courseWithTeacherSTCDTOS.add(courseWithTeacherSTCDTO);
        }
        return courseWithTeacherSTCDTOS;
    }

    /**
     * 删除课程
     * @param courseDTO
     */
    public void delete(CourseDTO courseDTO){
        deleteCourseTeacherDAO.delete(courseDTO);
    }

}
