package com.weilt.eshopproduct.controller.backend;

import com.google.common.collect.Maps;
import com.weilt.common.dto.Const;
import com.weilt.common.dto.ResponseCode;
import com.weilt.common.dto.ServerResponse;
import com.weilt.common.entity.Product;
import com.weilt.common.entity.User;
import com.weilt.common.service.IFileService;
import com.weilt.eshopproduct.service.IProductService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author weilt
 * @com.weilt.eshopproduct.controller.backend
 * @date 2018/8/23 == 12:12
 */
@RestController
@RequestMapping(value = "/manage/product")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @PostMapping(value = "/saveproduct")
    public ServerResponse productSave(HttpSession session,Product product) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            return iProductService.saveOrUpdateProduct(product);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping(value = "/setproductstatus")
    public ServerResponse setProductStatus(HttpSession session,Integer productId,Integer status) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            return iProductService.setProductStatus(productId,status);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping(value = "/getproductlist")
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            return iProductService.getProductList(pageNum,pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping(value = "/serarch")
    public ServerResponse serarchProduct(HttpSession session, String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            return iProductService.secrchProduct(productName,productId,pageNum,pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping(value = "/uploadimg")
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){

        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperties().getStringProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作！！");
        }
    }

    @PostMapping("/richtext_img_upload")
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //校验是否管理员
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //是管理员
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperties().getStringProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
        else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
