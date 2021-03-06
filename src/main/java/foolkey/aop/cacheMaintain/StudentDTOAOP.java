package foolkey.aop.cacheMaintain;

import foolkey.pojo.root.CAO.userInfo.UserCAO;
import foolkey.pojo.root.vo.dto.StudentDTO;
import foolkey.tool.TokenCreator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.annotation.Resource;

/**
 * Created by geyao on 2017/4/28.
 */
@Aspect
public class StudentDTOAOP {
    @Resource(name = "userCAO")
    private UserCAO userCAO;

    /**
     * 从数据库获取 用户信息 以后，使缓存更新
     * 更新用户缓存区，以及id_token的对应区
     * @param joinPoint
     */
    @Around(value = "execution(* foolkey.pojo.root.DAO.student.GetStudentDAO.getStudentDTO(..)) ")
    public Object updateStudentDTO(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        System.out.println(this.getClass() + "切入了从数据库读取用户信息的函数");
        System.out.println("用户名：" + args[0]);
        System.out.println("密码：" + args[1]);
        String token = TokenCreator.createToken(args[0].toString(), args[1].toString());
        Object rvt = joinPoint.proceed();
        System.out.println(this.getClass());
        if (rvt != null) {
            StudentDTO studentDTO = (StudentDTO) rvt;
            userCAO.saveStudentDTO(token, studentDTO);
            userCAO.saveIdToken(token, studentDTO.getId());
            System.out.println("AOP实现了服务器 " + token + " \n缓存的更新!!");
        }else {
            System.out.println("AOP未实现用户StudentDTO更新，因为不存在该用户");
        }
        return rvt;
    }


}
