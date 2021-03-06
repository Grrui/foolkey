package foolkey.handler.question;

import foolkey.pojo.root.bo.AbstractBO;
import foolkey.pojo.root.bo.message.MessageBO;
import foolkey.pojo.root.bo.order.OrderInfoBO;
import foolkey.pojo.root.bo.question.QuestionBO;
import foolkey.pojo.root.bo.student.StudentInfoBO;
import foolkey.pojo.root.vo.assistObject.QuestionStateEnum;
import foolkey.pojo.root.vo.assistObject.TechnicTagEnum;
import foolkey.pojo.root.vo.dto.OrderAskQuestionDTO;
import foolkey.pojo.root.vo.dto.QuestionAnswerDTO;
import foolkey.pojo.root.vo.dto.StudentDTO;
import foolkey.tool.Time;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 提问的handler
 * <p>
 * 参数：
 * token：用户标识：String
 * answerId：回答者的id：Long
 * price：设置的这个问题的价格：Double
 * title：问题的主题：String
 * questionContent：问题的内容：String
 * couponId：优惠券的id：Long
 * technicTagEnum：问题所属的技术类别：String
 * <p>
 * 返回：
 * result：结果：success、fail(String)
 * questionAnswerDTO：问题的DTO：QuestionAnswerDTO
 * orderAskQuestionDTO：提问的订单DTO：OrderAskQuestionDTO
 * <p>
 * 步骤：
 * 1. 存储问题DTO
 * 2. 存储订单DTO
 * 3. 给回答者发消息
 * Created by GR on 2017/5/20.
 */
@Service
public class CreateQuestionHandler extends AbstractBO {

    @Autowired
    private QuestionBO questionBO;
    @Autowired
    private StudentInfoBO studentInfoBO;
    @Autowired
    private OrderInfoBO orderInfoBO;
    @Autowired
    private MessageBO messageBO;

    public void execute(
            HttpServletRequest request,
            HttpServletResponse response,
            JSONObject jsonObject
    ) throws Exception {
        //读取客户端传入参数
        String clearText = request.getAttribute("clearText").toString();
        JSONObject clearJSON = JSONObject.fromObject(clearText);

        //获取原始信息
        String token = clearJSON.getString("token");

        StudentDTO studentDTO = studentInfoBO.getStudentDTO(token);
        Long askerId = studentDTO.getId();

        Long answerId = clearJSON.getLong("answerId");
        Double price = clearJSON.getDouble("price");
        String title = clearJSON.getString("title");
        String questionContent = clearJSON.getString("questionContent");
        Long couponId = clearJSON.getLong("couponId");

        String technicTagEnumStr = clearJSON.getString("technicTagEnum");
        TechnicTagEnum technicTagEnum = TechnicTagEnum.valueOf(technicTagEnumStr);


        //创建一个问题DTO
        QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
        questionAnswerDTO.setAskerId(askerId);
        questionAnswerDTO.setAnswerId(answerId);
        questionAnswerDTO.setCreatedTime(Time.getCurrentDate());
        questionAnswerDTO.setInvalidTime(Time.getAskQuestioninValidDate());
        questionAnswerDTO.setLastUpdateTime(Time.getCurrentDate());
        questionAnswerDTO.setOnlookerNumber(0);
        questionAnswerDTO.setPrice(price);
        questionAnswerDTO.setQuestionContent(questionContent);
        questionAnswerDTO.setTitle(title);
        questionAnswerDTO.setQuestionStateEnum(QuestionStateEnum.待付款);
        questionAnswerDTO.setTechnicTagEnum(technicTagEnum);
        //  1. 存储问题DTO
        questionBO.createQuestionAnswer(questionAnswerDTO);
        //  2.存储订单DTO
        OrderAskQuestionDTO orderAskQuestionDTO = orderInfoBO.createOrderAsk(studentDTO, questionAnswerDTO, couponId);
        //返回result
        jsonObject.put("result", "success");
        jsonObject.put("questionAnswerDTO", questionAnswerDTO);
        jsonObject.put("orderAskQuestionDTO", orderAskQuestionDTO);
        jsonHandler.sendJSON(jsonObject, response);
        //  3.给回答者发消息，有人提了问题
        StudentDTO answererDTO = studentInfoBO.getStudentDTO(answerId);
        messageBO.sendToAnswererOfAsk(answererDTO);
    }

}
