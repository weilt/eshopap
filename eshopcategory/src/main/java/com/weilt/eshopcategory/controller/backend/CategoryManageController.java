package com.weilt.eshopcategory.controller.backend;

import com.weilt.common.dto.Const;
import com.weilt.common.dto.ResponseCode;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.User;
import com.weilt.eshopcategory.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author weilt
 * @com.weilt.eshopcategory.controller.backend
 * @date 2018/8/24 == 23:58
 */
@RestController
@RequestMapping(value = "/manage/catagory")
public class CategoryManageController {

    @Autowired
    private ICategoryService iCategoryService;
    @PostMapping(value = "/addcategory")
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            return iCategoryService.addCategory(categoryName,parentId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping(value = "/updatecategoryname")
    public ServerResponse setCatagoryName(HttpSession session,Integer categoryId,String categoryName){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN) {
            //是管理员
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }


    //取得当前分类的子节点，不递归
    @PostMapping(value = "/getsubcategory")
    public ServerResponse getChildParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN) {
            //是管理员
            //查询子节点信息，并且不递归
            return iCategoryService.getChildParallelCategory(categoryId);

        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping(value = "/getdeepcategory")
    public ServerResponse getCategoryAndDeepChildCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN) {
            //是管理员
            //查询子节点信息，并且递归子节点的id
            return iCategoryService.selectCategoryAndDeepCategory(categoryId);

        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }
}
