package foolkey.controller.course.haveClass;

import foolkey.controller.AbstractController;
import foolkey.handler.course.haveClass.EndClassHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by geyao on 2017/5/6.
 */
@Controller
@RequestMapping("/aes/endClass")
public class EndClassController extends AbstractController{
    @Autowired
    private EndClassHandler handler;

    @RequestMapping
    public void execute(
            HttpServletRequest request,
            HttpServletResponse response
    )throws  Exception{
        handler.execute(request, response, jsonObject);
    }
}
