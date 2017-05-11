package foolkey.controller.course.Reward;

import foolkey.controller.AbstractController;
import foolkey.handler.application.AcceptRewardApplicationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by geyao on 2017/5/4.
 */
@Controller
@RequestMapping("/acceptRewardApplication")
public class AcceptRewardApplicationController extends AbstractController{

    @Autowired
    private AcceptRewardApplicationHandler handler;

    @RequestMapping
    public void execute(
            HttpServletRequest request,
            HttpServletResponse response
    )throws Exception{
        handler.execute(request, response, jsonObject);
    }
}
