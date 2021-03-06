package foolkey.pojo.root.DAO.course_student;

import foolkey.pojo.root.DAO.base.GetBaseDAO;
import foolkey.pojo.root.vo.assistObject.RewardStateEnum;
import foolkey.pojo.root.vo.assistObject.TechnicTagEnum;
import foolkey.pojo.root.vo.dto.RewardDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/25.
 */
@Repository("getCourseStudentDAO")
public class GetCourseStudentDAO extends GetBaseDAO<RewardDTO>{

    /**
     * 根据技术分类查询前n条已经解决、未解决记录
     * @param technicTagEnum
     * @param resultSize
     * @return
     */
    public ArrayList<RewardDTO> findByTechnicTagEnumAndResultSize(TechnicTagEnum technicTagEnum, RewardStateEnum rewardStateEnum, Integer resultSize){
        ArrayList<RewardDTO> courseStudentDTOS = new ArrayList<>();
        String hql = "select cs from RewardDTO cs where cs.technicTagEnum = ? and cs.rewardStateEnum = ? order by cs.createTime desc";
        courseStudentDTOS = findByPage(hql,1,resultSize,technicTagEnum, rewardStateEnum);
        return courseStudentDTOS;
    }
}
